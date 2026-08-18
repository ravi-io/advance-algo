import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Program 14: Text Mining with Java
 * 
 * Demonstrates NLP and Text Mining pipeline:
 * 1. Preprocessing (Tokenization, Lowercasing, Stopword Removal)
 * 2. Feature Extraction: Term Frequency (TF), Inverse Document Frequency (IDF), and TF-IDF
 * 3. Information Retrieval: Cosine Similarity matching for search queries
 * 
 * Time Complexity: O(D * W) for preprocessing and indexing where D is document count, W is average words per document.
 *                  O(V * D) for TF-IDF matrix creation where V is unique vocabulary size.
 * Space Complexity: O(V * D) for storing term frequencies and vector spaces.
 */
public class TextMiningDemo {

    // Standard English stop words set
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he",
        "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "were", "with"
    ));

    // Document representation
    static class Document {
        int id;
        String rawContent;
        List<String> tokens;

        Document(int id, String rawContent) {
            this.id = id;
            this.rawContent = rawContent;
            this.tokens = preprocess(rawContent);
        }

        private List<String> preprocess(String text) {
            List<String> filteredTokens = new ArrayList<>();
            // Clean text: lowercase and remove non-alphanumeric characters
            String cleanText = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
            String[] rawTokens = cleanText.split("\\s+");

            for (String token : rawTokens) {
                token = token.trim();
                if (!token.isEmpty() && !STOP_WORDS.contains(token) && token.length() > 1) {
                    filteredTokens.add(token);
                }
            }
            return filteredTokens;
        }
    }

    public static class TFIDFVectorSpace {
        private List<Document> documents;
        private Set<String> vocabulary;
        private Map<String, Double> idfMap;

        public TFIDFVectorSpace(List<Document> documents) {
            this.documents = documents;
            this.vocabulary = new TreeSet<>();
            this.idfMap = new HashMap<>();
            buildVocabularyAndIDF();
        }

        private void buildVocabularyAndIDF() {
            // Collect all unique vocabulary terms across all documents
            for (Document doc : documents) {
                vocabulary.addAll(doc.tokens);
            }

            int N = documents.size();
            // Calculate IDF for each term in vocabulary
            for (String term : vocabulary) {
                int docCountWithTerm = 0;
                for (Document doc : documents) {
                    if (doc.tokens.contains(term)) {
                        docCountWithTerm++;
                    }
                }
                // IDF formula: ln( (N + 1) / (docCount + 1) ) + 1.0 (Smoothed IDF)
                double idf = Math.log((double) (N + 1) / (docCountWithTerm + 1)) + 1.0;
                idfMap.put(term, idf);
            }
        }

        // Computes Term Frequency (TF) of term in document
        public double computeTF(String term, Document doc) {
            if (doc.tokens.isEmpty()) return 0;
            int count = 0;
            for (String t : doc.tokens) {
                if (t.equals(term)) count++;
            }
            return (double) count / doc.tokens.size();
        }

        // Computes TF-IDF vector for a given document
        public double[] getTFIDFVector(Document doc) {
            double[] vector = new double[vocabulary.size()];
            int index = 0;
            for (String term : vocabulary) {
                double tf = computeTF(term, doc);
                double idf = idfMap.getOrDefault(term, 1.0);
                vector[index++] = tf * idf;
            }
            return vector;
        }

        // Computes Cosine Similarity between query vector and document vector
        public double computeCosineSimilarity(double[] vecA, double[] vecB) {
            double dotProduct = 0.0;
            double normA = 0.0;
            double normB = 0.0;

            for (int i = 0; i < vecA.length; i++) {
                dotProduct += vecA[i] * vecB[i];
                normA += vecA[i] * vecA[i];
                normB += vecB[i] * vecB[i];
            }

            if (normA == 0.0 || normB == 0.0) return 0.0;
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }

        public void printTFIDFTable() {
            System.out.println("TF-IDF Matrix (Vocabulary size: " + vocabulary.size() + "):");
            System.out.println("------------------------------------------------------------------");
            System.out.printf("%-18s", "Term / Doc");
            for (Document doc : documents) {
                System.out.printf(" | Doc %d ", doc.id);
            }
            System.out.println(" | IDF");
            System.out.println("------------------------------------------------------------------");

            for (String term : vocabulary) {
                System.out.printf("%-18s", term);
                for (Document doc : documents) {
                    double tf = computeTF(term, doc);
                    double tfidf = tf * idfMap.get(term);
                    System.out.printf(" | %-6.3f", tfidf);
                }
                System.out.printf(" | %-6.3f\n", idfMap.get(term));
            }
            System.out.println("------------------------------------------------------------------");
        }

        public void search(String query) {
            System.out.println("\nQuery: \"" + query + "\"");
            Document queryDoc = new Document(-1, query);
            double[] queryVector = getTFIDFVector(queryDoc);

            System.out.println("Search Results (Cosine Similarity Ranking):");
            System.out.println("------------------------------------------------------------------");

            List<Map.Entry<Document, Double>> results = new ArrayList<>();
            for (Document doc : documents) {
                double[] docVector = getTFIDFVector(doc);
                double sim = computeCosineSimilarity(queryVector, docVector);
                results.add(new HashMap.SimpleEntry<>(doc, sim));
            }

            // Sort by similarity descending
            results.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

            for (Map.Entry<Document, Double> entry : results) {
                System.out.printf("  Doc %d (Score: %.4f) -> %s\n",
                        entry.getKey().id, entry.getValue(), entry.getKey().rawContent);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("        PROGRAM 14: TEXT MINING WITH JAVA        ");
        System.out.println("=================================================\n");

        // Input Document Corpus
        List<String> rawCorpus = Arrays.asList(
            "Java is a popular object-oriented programming language for enterprise applications.",
            "Algorithms and data structures are essential for software development and performance optimization.",
            "Text mining and natural language processing extract knowledge and insights from text data.",
            "Machine learning algorithms improve automatic pattern recognition in large datasets using Java."
        );

        List<Document> corpus = new ArrayList<>();
        System.out.println("Document Corpus:");
        for (int i = 0; i < rawCorpus.size(); i++) {
            Document doc = new Document(i + 1, rawCorpus.get(i));
            corpus.add(doc);
            System.out.printf("  Doc %d: %s\n", doc.id, doc.rawContent);
            System.out.printf("         Tokens: %s\n\n", doc.tokens);
        }

        // Build TF-IDF Model
        TFIDFVectorSpace vectorSpace = new TFIDFVectorSpace(corpus);
        vectorSpace.printTFIDFTable();

        // Perform Information Retrieval Queries
        vectorSpace.search("Java algorithms programming");
        vectorSpace.search("Text mining natural language");

        System.out.println("\n=================================================");
    }
}
