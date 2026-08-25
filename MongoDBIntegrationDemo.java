import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Program 16: MongoDB Integration with Java
 * 
 * Demonstrates NoSQL database integration concepts, BSON document modeling,
 * CRUD operations, indexing, and aggregation pipelines in Java.
 * 
 * Includes both standard MongoDB Java Driver API reference structures and a
 * standalone in-memory BSON document database engine for zero-dependency
 * execution.
 * 
 * Time Complexity: O(1) for indexed primary key lookup.
 * O(N) for unindexed document scans and collection filtering.
 * Space Complexity: O(N) for storing collections of N documents.
 */
public class MongoDBIntegrationDemo {

    /**
     * BSON Document representation (similar to org.bson.Document in MongoDB
     * Driver).
     */
    public static class Document {
        private final Map<String, Object> fields = new LinkedHashMap<>();

        public Document() {
        }

        public Document(String key, Object value) {
            fields.put(key, value);
        }

        public Document append(String key, Object value) {
            fields.put(key, value);
            return this;
        }

        public Object get(String key) {
            return fields.get(key);
        }

        public String getString(String key) {
            Object v = fields.get(key);
            return v != null ? v.toString() : null;
        }

        public Integer getInteger(String key) {
            Object v = fields.get(key);
            if (v instanceof Number) {
                return ((Number) v).intValue();
            }
            return null;
        }

        public Double getDouble(String key) {
            Object v = fields.get(key);
            if (v instanceof Number) {
                return ((Number) v).doubleValue();
            }
            return null;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public boolean containsKey(String key) {
            return fields.containsKey(key);
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder("{ ");
            int count = 0;
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (count++ > 0)
                    sb.append(", ");
                sb.append("\"").append(entry.getKey()).append("\": ");
                Object val = entry.getValue();
                if (val instanceof String) {
                    sb.append("\"").append(val).append("\"");
                } else if (val instanceof Document) {
                    sb.append(((Document) val).toJson());
                } else if (val instanceof List) {
                    sb.append(val.toString());
                } else {
                    sb.append(val);
                }
            }
            sb.append(" }");
            return sb.toString();
        }

        @Override
        public String toString() {
            return toJson();
        }
    }

    /**
     * Simulated MongoCollection managing CRUD, Indexing, and Aggregation pipelines.
     */
    public static class MongoCollection {
        private final String collectionName;
        private final List<Document> storage = new ArrayList<>();
        private final Map<String, Map<Object, List<Document>>> indexes = new HashMap<>();

        public MongoCollection(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getCollectionName() {
            return collectionName;
        }

        // CREATE: Insert One
        public void insertOne(Document doc) {
            if (!doc.containsKey("_id")) {
                doc.append("_id", UUID.randomUUID().toString().substring(0, 8));
            }
            storage.add(doc);
            updateIndexesForDocument(doc);
            System.out.println("  [INSERT] Inserted document with _id: " + doc.get("_id"));
        }

        // CREATE: Insert Many
        public void insertMany(List<Document> docs) {
            for (Document doc : docs) {
                insertOne(doc);
            }
        }

        // READ: Find all
        public List<Document> find() {
            return new ArrayList<>(storage);
        }

        // READ: Find by Filter
        public List<Document> find(Document filter) {
            List<Document> result = new ArrayList<>();
            for (Document doc : storage) {
                if (matchesFilter(doc, filter)) {
                    result.add(doc);
                }
            }
            return result;
        }

        // UPDATE: Update One
        public boolean updateOne(Document filter, Document updateDoc) {
            for (Document doc : storage) {
                if (matchesFilter(doc, filter)) {
                    applyUpdate(doc, updateDoc);
                    rebuildIndexes();
                    return true;
                }
            }
            return false;
        }

        // UPDATE: Update Many
        public int updateMany(Document filter, Document updateDoc) {
            int modifiedCount = 0;
            for (Document doc : storage) {
                if (matchesFilter(doc, filter)) {
                    applyUpdate(doc, updateDoc);
                    modifiedCount++;
                }
            }
            if (modifiedCount > 0) {
                rebuildIndexes();
            }
            return modifiedCount;
        }

        // DELETE: Delete One
        public boolean deleteOne(Document filter) {
            for (int i = 0; i < storage.size(); i++) {
                if (matchesFilter(storage.get(i), filter)) {
                    Document removed = storage.remove(i);
                    rebuildIndexes();
                    System.out.println("  [DELETE] Removed document with _id: " + removed.get("_id"));
                    return true;
                }
            }
            return false;
        }

        // DELETE: Delete Many
        public int deleteMany(Document filter) {
            List<Document> toRemove = new ArrayList<>();
            for (Document doc : storage) {
                if (matchesFilter(doc, filter)) {
                    toRemove.add(doc);
                }
            }
            storage.removeAll(toRemove);
            if (!toRemove.isEmpty()) {
                rebuildIndexes();
            }
            return toRemove.size();
        }

        // INDEXING: Create Single Field Index
        public void createIndex(String fieldName) {
            Map<Object, List<Document>> indexMap = new HashMap<>();
            for (Document doc : storage) {
                Object val = doc.get(fieldName);
                if (val != null) {
                    indexMap.computeIfAbsent(val, k -> new ArrayList<>()).add(doc);
                }
            }
            indexes.put(fieldName, indexMap);
            System.out.println("  [INDEX] Created index on field: '" + fieldName + "'");
        }

        // Fast Lookup using Index
        public List<Document> findByIndex(String fieldName, Object value) {
            if (indexes.containsKey(fieldName)) {
                System.out.println("  [INDEX LOOKUP] Using index on '" + fieldName + "' = " + value);
                return indexes.get(fieldName).getOrDefault(value, Collections.emptyList());
            }
            // Fallback to table scan
            return find(new Document(fieldName, value));
        }

        // AGGREGATION: Simple Aggregation Pipeline ($match, $group, $sort)
        public List<Document> aggregate(List<Document> pipeline) {
            List<Document> currentStageDocs = new ArrayList<>(storage);

            for (Document stage : pipeline) {
                if (stage.containsKey("$match")) {
                    Document matchFilter = (Document) stage.get("$match");
                    List<Document> filtered = new ArrayList<>();
                    for (Document doc : currentStageDocs) {
                        if (matchesFilter(doc, matchFilter)) {
                            filtered.add(doc);
                        }
                    }
                    currentStageDocs = filtered;
                } else if (stage.containsKey("$group")) {
                    Document groupSpec = (Document) stage.get("$group");
                    String groupByField = groupSpec.getString("_id");
                    if (groupByField != null && groupByField.startsWith("$")) {
                        groupByField = groupByField.substring(1);
                    }

                    Map<Object, List<Document>> groupedMap = new LinkedHashMap<>();
                    for (Document doc : currentStageDocs) {
                        Object keyVal = groupByField != null ? doc.get(groupByField) : "ALL";
                        groupedMap.computeIfAbsent(keyVal, k -> new ArrayList<>()).add(doc);
                    }

                    List<Document> aggregatedResults = new ArrayList<>();
                    for (Map.Entry<Object, List<Document>> entry : groupedMap.entrySet()) {
                        Document aggDoc = new Document("_id", entry.getKey());

                        for (Map.Entry<String, Object> opEntry : groupSpec.getFields().entrySet()) {
                            String opKey = opEntry.getKey();
                            if (opKey.equals("_id"))
                                continue;

                            Document opDetail = (Document) opEntry.getValue();
                            if (opDetail.containsKey("$sum")) {
                                Object sumTarget = opDetail.get("$sum");
                                if (sumTarget instanceof Integer && (Integer) sumTarget == 1) {
                                    aggDoc.append(opKey, entry.getValue().size());
                                } else if (sumTarget instanceof String && ((String) sumTarget).startsWith("$")) {
                                    String fName = ((String) sumTarget).substring(1);
                                    double sum = 0;
                                    for (Document d : entry.getValue()) {
                                        Double val = d.getDouble(fName);
                                        if (val != null)
                                            sum += val;
                                    }
                                    aggDoc.append(opKey, sum);
                                }
                            } else if (opDetail.containsKey("$avg")) {
                                String fName = ((String) opDetail.get("$avg")).substring(1);
                                double sum = 0;
                                int count = 0;
                                for (Document d : entry.getValue()) {
                                    Double val = d.getDouble(fName);
                                    if (val != null) {
                                        sum += val;
                                        count++;
                                    }
                                }
                                aggDoc.append(opKey, count > 0 ? (sum / count) : 0.0);
                            }
                        }
                        aggregatedResults.add(aggDoc);
                    }
                    currentStageDocs = aggregatedResults;
                } else if (stage.containsKey("$sort")) {
                    Document sortSpec = (Document) stage.get("$sort");
                    for (Map.Entry<String, Object> sortEntry : sortSpec.getFields().entrySet()) {
                        String sortField = sortEntry.getKey();
                        int direction = (Integer) sortEntry.getValue();
                        currentStageDocs.sort((d1, d2) -> {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> v1 = (Comparable<Object>) d1.get(sortField);
                            @SuppressWarnings("unchecked")
                            Comparable<Object> v2 = (Comparable<Object>) d2.get(sortField);
                            if (v1 == null || v2 == null)
                                return 0;
                            return direction >= 0 ? v1.compareTo(v2) : v2.compareTo(v1);
                        });
                    }
                }
            }
            return currentStageDocs;
        }

        private boolean matchesFilter(Document doc, Document filter) {
            for (Map.Entry<String, Object> entry : filter.getFields().entrySet()) {
                String key = entry.getKey();
                Object targetVal = entry.getValue();

                if (targetVal instanceof Document) {
                    Document opDoc = (Document) targetVal;
                    Object docVal = doc.get(key);
                    if (opDoc.containsKey("$gt")) {
                        double limit = ((Number) opDoc.get("$gt")).doubleValue();
                        if (docVal == null || ((Number) docVal).doubleValue() <= limit)
                            return false;
                    }
                    if (opDoc.containsKey("$gte")) {
                        double limit = ((Number) opDoc.get("$gte")).doubleValue();
                        if (docVal == null || ((Number) docVal).doubleValue() < limit)
                            return false;
                    }
                    if (opDoc.containsKey("$lt")) {
                        double limit = ((Number) opDoc.get("$lt")).doubleValue();
                        if (docVal == null || ((Number) docVal).doubleValue() >= limit)
                            return false;
                    }
                } else {
                    if (!Objects.equals(doc.get(key), targetVal)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void applyUpdate(Document doc, Document updateDoc) {
            if (updateDoc.containsKey("$set")) {
                Document setFields = (Document) updateDoc.get("$set");
                for (Map.Entry<String, Object> entry : setFields.getFields().entrySet()) {
                    doc.append(entry.getKey(), entry.getValue());
                }
            }
            if (updateDoc.containsKey("$inc")) {
                Document incFields = (Document) updateDoc.get("$inc");
                for (Map.Entry<String, Object> entry : incFields.getFields().entrySet()) {
                    String k = entry.getKey();
                    Number incVal = (Number) entry.getValue();
                    Number currVal = (Number) doc.get(k);
                    if (currVal == null)
                        currVal = 0;
                    if (incVal instanceof Double || currVal instanceof Double) {
                        doc.append(k, currVal.doubleValue() + incVal.doubleValue());
                    } else {
                        doc.append(k, currVal.intValue() + incVal.intValue());
                    }
                }
            }
        }

        private void updateIndexesForDocument(Document doc) {
            for (Map.Entry<String, Map<Object, List<Document>>> idxEntry : indexes.entrySet()) {
                String field = idxEntry.getKey();
                Object val = doc.get(field);
                if (val != null) {
                    idxEntry.getValue().computeIfAbsent(val, k -> new ArrayList<>()).add(doc);
                }
            }
        }

        private void rebuildIndexes() {
            List<String> fieldNames = new ArrayList<>(indexes.keySet());
            indexes.clear();
            for (String field : fieldNames) {
                createIndex(field);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     PROGRAM 16: JAVA MONGODB INTEGRATION DEMO   ");
        System.out.println("=================================================\n");

        System.out.println("--- Standard MongoDB Driver API Usage Reference ---");
        System.out.println("  MongoClient mongoClient = MongoClients.create(\"mongodb://localhost:27017\");");
        System.out.println("  MongoDatabase database = mongoClient.getDatabase(\"electronics_store\");");
        System.out.println("  MongoCollection<Document> collection = database.getCollection(\"products\");\n");

        System.out.println("--- Executing Document Store Operations ---");
        MongoCollection productsCollection = new MongoCollection("products");

        // 1. CREATE: Insert Documents
        System.out.println("\n1. Inserting Sample Product Documents:");
        Document p1 = new Document("_id", "PROD101").append("name", "Gaming Laptop")
                .append("category", "Electronics").append("price", 1250.00).append("stock", 15);
        Document p2 = new Document("_id", "PROD102").append("name", "Wireless Mouse")
                .append("category", "Electronics").append("price", 29.99).append("stock", 120);
        Document p3 = new Document("_id", "PROD103").append("name", "Mechanical Keyboard")
                .append("category", "Electronics").append("price", 89.50).append("stock", 45);
        Document p4 = new Document("_id", "PROD104").append("name", "Ergonomic Chair")
                .append("category", "Furniture").append("price", 249.00).append("stock", 20);
        Document p5 = new Document("_id", "PROD105").append("name", "Standing Desk")
                .append("category", "Furniture").append("price", 399.00).append("stock", 8);

        productsCollection.insertMany(Arrays.asList(p1, p2, p3, p4, p5));

        // 2. CREATE INDEX
        System.out.println("\n2. Indexing Collection:");
        productsCollection.createIndex("category");

        // 3. READ: Querying Documents
        System.out.println("\n3. Querying All Documents:");
        for (Document doc : productsCollection.find()) {
            System.out.println("  " + doc.toJson());
        }

        System.out.println("\n4. Filtering Documents (Category = 'Electronics'):");
        List<Document> electronics = productsCollection.findByIndex("category", "Electronics");
        for (Document doc : electronics) {
            System.out.println("  " + doc.toJson());
        }

        System.out.println("\n5. Filtering Documents with Comparison Query (Price > 100.0):");
        Document priceFilter = new Document("price", new Document("$gt", 100.0));
        List<Document> expensiveItems = productsCollection.find(priceFilter);
        for (Document doc : expensiveItems) {
            System.out.println("  " + doc.toJson());
        }

        // 6. UPDATE: Updating Documents
        System.out.println("\n6. Updating Document (PROD101 Price drop & Stock increase):");
        Document filterProd101 = new Document("_id", "PROD101");
        Document updateProd101 = new Document("$set", new Document("price", 1199.99))
                .append("$inc", new Document("stock", 5));
        productsCollection.updateOne(filterProd101, updateProd101);

        List<Document> updatedP1 = productsCollection.find(filterProd101);
        System.out.println("  Updated Doc: " + updatedP1.get(0).toJson());

        // 7. AGGREGATION PIPELINE ($match, $group, $sort)
        System.out.println("\n7. Executing MongoDB Aggregation Pipeline:");
        System.out.println(
                "   [Pipeline: $group by category -> calc count, avg(price), total stock -> $sort by totalStock desc]");

        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$category")
                        .append("count", new Document("$sum", 1))
                        .append("avgPrice", new Document("$avg", "$price"))
                        .append("totalStock", new Document("$sum", "$stock"))),
                new Document("$sort", new Document("totalStock", -1)));

        List<Document> aggResults = productsCollection.aggregate(pipeline);
        System.out.println("   Aggregation Results:");
        for (Document res : aggResults) {
            System.out.printf("   Category: %-12s | Count: %d | Avg Price: $%.2f | Total Stock: %d\n",
                    res.get("_id"), res.getInteger("count"), res.getDouble("avgPrice"), res.getInteger("totalStock"));
        }

        // 8. DELETE: Delete Document
        System.out.println("\n8. Deleting Document (PROD102):");
        productsCollection.deleteOne(new Document("_id", "PROD102"));

        System.out.println("\nRemaining Total Documents Count: " + productsCollection.find().size());
        System.out.println("=================================================");
    }
}
