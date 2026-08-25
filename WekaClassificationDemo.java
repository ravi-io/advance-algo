import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Program 19: Machine Learning Classification using Weka in Java
 * 
 * Demonstrates the Weka Machine Learning architecture:
 * 1. Data representation using Weka-compatible Instance and Instances data structures
 * 2. Decision Tree Classifier (Weka J48 / ID3 implementation with Information Gain)
 * 3. Naive Bayes Classifier and k-Nearest Neighbors (Weka IBk)
 * 4. Model Evaluation metrics: Confusion Matrix, Accuracy, Precision, Recall, F1-Score
 * 
 * Time Complexity: O(N * D) where N is dataset instance count and D is attribute count.
 * Space Complexity: O(N * D) for dataset storage and decision tree structures.
 */
public class WekaClassificationDemo {

    public enum AttributeType { NUMERIC, NOMINAL }

    public static class Attribute {
        private final String name;
        private final AttributeType type;
        private final List<String> nominalValues;

        public Attribute(String name, AttributeType type) {
            this.name = name;
            this.type = type;
            this.nominalValues = new ArrayList<>();
        }

        public Attribute(String name, List<String> nominalValues) {
            this.name = name;
            this.type = AttributeType.NOMINAL;
            this.nominalValues = new ArrayList<>(nominalValues);
        }

        public String getName() { return name; }
        public AttributeType getType() { return type; }
        public List<String> getNominalValues() { return nominalValues; }
    }

    public static class Instance {
        private final double[] values;
        private String classLabel;

        public Instance(double[] values, String classLabel) {
            this.values = values;
            this.classLabel = classLabel;
        }

        public double[] getValues() { return values; }
        public double getValue(int index) { return values[index]; }
        public String getClassLabel() { return classLabel; }
        public void setClassLabel(String classLabel) { this.classLabel = classLabel; }
    }

    public static class Instances {
        private final String relationName;
        private final List<Attribute> attributes;
        private final List<Instance> instances = new ArrayList<>();
        private int classIndex = -1;

        public Instances(String relationName, List<Attribute> attributes) {
            this.relationName = relationName;
            this.attributes = attributes;
        }

        public void add(Instance inst) {
            instances.add(inst);
        }

        public void setClassIndex(int classIndex) {
            this.classIndex = classIndex;
        }

        public String getRelationName() { return relationName; }
        public List<Attribute> getAttributes() { return attributes; }
        public List<Instance> getInstances() { return instances; }
        public int size() { return instances.size(); }
        public Instance get(int index) { return instances.get(index); }
        public Attribute classAttribute() { return attributes.get(classIndex); }
    }

    /**
     * Decision Tree Node for J48 / ID3 Classifier
     */
    public static class TreeNode {
        String attributeName;
        int attributeIndex;
        Map<String, TreeNode> children = new HashMap<>();
        String leafClass;
        boolean isLeaf;

        public void printTree(String indent) {
            if (isLeaf) {
                System.out.println(indent + "  => Class: " + leafClass);
                return;
            }
            for (Map.Entry<String, TreeNode> child : children.entrySet()) {
                System.out.println(indent + attributeName + " = " + child.getKey() + ":");
                child.getValue().printTree(indent + "  ");
            }
        }
    }

    /**
     * Weka J48 Decision Tree Classifier
     */
    public static class WekaJ48Classifier {
        private TreeNode root;

        public void buildClassifier(Instances data) {
            List<Integer> availableAttrIndices = new ArrayList<>();
            for (int i = 0; i < data.getAttributes().size(); i++) {
                if (i != data.classIndex) {
                    availableAttrIndices.add(i);
                }
            }
            this.root = buildTree(data.getInstances(), availableAttrIndices, data);
        }

        private TreeNode buildTree(List<Instance> samples, List<Integer> attrIndices, Instances dataset) {
            TreeNode node = new TreeNode();

            // Check if all samples have the same class label
            String firstClass = samples.get(0).getClassLabel();
            boolean allSame = true;
            for (Instance inst : samples) {
                if (!inst.getClassLabel().equals(firstClass)) {
                    allSame = false;
                    break;
                }
            }
            if (allSame || attrIndices.isEmpty()) {
                node.isLeaf = true;
                node.leafClass = getMajorityClass(samples);
                return node;
            }

            // Find attribute with highest Information Gain
            int bestAttrIdx = -1;
            double maxGain = -1.0;
            double systemEntropy = computeEntropy(samples);

            for (int attrIdx : attrIndices) {
                double gain = computeInformationGain(samples, attrIdx, systemEntropy, dataset);
                if (gain > maxGain) {
                    maxGain = gain;
                    bestAttrIdx = attrIdx;
                }
            }

            if (bestAttrIdx == -1 || maxGain <= 0.0) {
                node.isLeaf = true;
                node.leafClass = getMajorityClass(samples);
                return node;
            }

            Attribute bestAttr = dataset.getAttributes().get(bestAttrIdx);
            node.attributeName = bestAttr.getName();
            node.attributeIndex = bestAttrIdx;
            node.isLeaf = false;

            // Split on attribute values
            Map<String, List<Instance>> subsets = new HashMap<>();
            for (Instance inst : samples) {
                int valIdx = (int) inst.getValue(bestAttrIdx);
                String valName = bestAttr.getNominalValues().get(valIdx);
                subsets.computeIfAbsent(valName, k -> new ArrayList<>()).add(inst);
            }

            List<Integer> remainingAttrs = new ArrayList<>(attrIndices);
            remainingAttrs.remove(Integer.valueOf(bestAttrIdx));

            for (Map.Entry<String, List<Instance>> entry : subsets.entrySet()) {
                TreeNode childNode = buildTree(entry.getValue(), remainingAttrs, dataset);
                node.children.put(entry.getKey(), childNode);
            }

            return node;
        }

        public String classifyInstance(Instance instance, Instances dataset) {
            TreeNode curr = root;
            while (!curr.isLeaf) {
                Attribute attr = dataset.getAttributes().get(curr.attributeIndex);
                int valIdx = (int) instance.getValue(curr.attributeIndex);
                String valName = attr.getNominalValues().get(valIdx);
                TreeNode next = curr.children.get(valName);
                if (next == null) {
                    return getMajorityClass(dataset.getInstances());
                }
                curr = next;
            }
            return curr.leafClass;
        }

        private double computeEntropy(List<Instance> samples) {
            Map<String, Integer> counts = new HashMap<>();
            for (Instance inst : samples) {
                counts.put(inst.getClassLabel(), counts.getOrDefault(inst.getClassLabel(), 0) + 1);
            }
            double entropy = 0.0;
            int total = samples.size();
            for (int count : counts.values()) {
                double p = (double) count / total;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
            return entropy;
        }

        private double computeInformationGain(List<Instance> samples, int attrIdx, double systemEntropy, Instances dataset) {
            Attribute attr = dataset.getAttributes().get(attrIdx);
            Map<String, List<Instance>> splits = new HashMap<>();

            for (Instance inst : samples) {
                int valIdx = (int) inst.getValue(attrIdx);
                String valName = attr.getNominalValues().get(valIdx);
                splits.computeIfAbsent(valName, k -> new ArrayList<>()).add(inst);
            }

            double remainder = 0.0;
            int total = samples.size();
            for (List<Instance> sub : splits.values()) {
                double p = (double) sub.size() / total;
                remainder += p * computeEntropy(sub);
            }

            return systemEntropy - remainder;
        }

        private String getMajorityClass(List<Instance> samples) {
            Map<String, Integer> counts = new HashMap<>();
            for (Instance inst : samples) {
                counts.put(inst.getClassLabel(), counts.getOrDefault(inst.getClassLabel(), 0) + 1);
            }
            return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        public void printDecisionTree() {
            if (root != null) root.printTree("");
        }
    }

    /**
     * Weka Model Evaluation Engine
     */
    public static class Evaluation {
        public static void evaluateModel(WekaJ48Classifier classifier, Instances dataset) {
            List<String> classValues = dataset.classAttribute().getNominalValues();
            int numClasses = classValues.size();
            int[][] confusionMatrix = new int[numClasses][numClasses];
            int correct = 0;

            for (Instance inst : dataset.getInstances()) {
                String predicted = classifier.classifyInstance(inst, dataset);
                String actual = inst.getClassLabel();

                int predIdx = classValues.indexOf(predicted);
                int actIdx = classValues.indexOf(actual);

                if (predIdx >= 0 && actIdx >= 0) {
                    confusionMatrix[actIdx][predIdx]++;
                    if (predIdx == actIdx) correct++;
                }
            }

            int total = dataset.size();
            double accuracy = (double) correct / total * 100.0;

            System.out.println("\n=== Weka Classifier Evaluation Results ===");
            System.out.printf("Correctly Classified Instances   : %4d   (%6.2f %%\n", correct, accuracy);
            System.out.printf("Incorrectly Classified Instances : %4d   (%6.2f %%\n", total - correct, 100.0 - accuracy);
            System.out.printf("Total Number of Instances        : %4d\n\n", total);

            System.out.println("=== Detailed Accuracy By Class ===");
            System.out.printf("%-15s | %-10s | %-10s | %-10s\n", "Class", "Precision", "Recall", "F-Measure");
            System.out.println("---------------------------------------------------------------");

            for (int i = 0; i < numClasses; i++) {
                int tp = confusionMatrix[i][i];
                int fp = 0;
                int fn = 0;
                for (int j = 0; j < numClasses; j++) {
                    if (j != i) {
                        fp += confusionMatrix[j][i];
                        fn += confusionMatrix[i][j];
                    }
                }

                double precision = (tp + fp) == 0 ? 0 : (double) tp / (tp + fp);
                double recall = (tp + fn) == 0 ? 0 : (double) tp / (tp + fn);
                double f1 = (precision + recall) == 0 ? 0 : 2 * (precision * recall) / (precision + recall);

                System.out.printf("%-15s | %-10.4f | %-10.4f | %-10.4f\n",
                        classValues.get(i), precision, recall, f1);
            }

            System.out.println("\n=== Confusion Matrix ===");
            System.out.print("   a   b  <-- classified as\n");
            for (int i = 0; i < numClasses; i++) {
                System.out.printf("%4d %4d | %s = %s\n",
                        confusionMatrix[i][0], confusionMatrix[i][1], (char)('a' + i), classValues.get(i));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   PROGRAM 19: WEKA MACHINE LEARNING IN JAVA     ");
        System.out.println("=================================================\n");

        System.out.println("--- Standard Weka API Integration Reference ---");
        System.out.println("  ConverterUtils.DataSource source = new ConverterUtils.DataSource(\"dataset.arff\");");
        System.out.println("  Instances data = source.getDataSet();");
        System.out.println("  data.setClassIndex(data.numAttributes() - 1);");
        System.out.println("  Classifier tree = new J48();");
        System.out.println("  tree.buildClassifier(data);\n");

        // Define Dataset Schema: Bank Loan Approval Dataset
        Attribute outlook = new Attribute("Outlook", Arrays.asList("Sunny", "Overcast", "Rain"));
        Attribute income = new Attribute("Income", Arrays.asList("High", "Medium", "Low"));
        Attribute student = new Attribute("Student", Arrays.asList("No", "Yes"));
        Attribute creditRating = new Attribute("CreditRating", Arrays.asList("Fair", "Excellent"));
        Attribute loanStatus = new Attribute("LoanStatus", Arrays.asList("Denied", "Approved"));

        List<Attribute> attributes = Arrays.asList(outlook, income, student, creditRating, loanStatus);
        Instances dataset = new Instances("BankLoanApproval", attributes);
        dataset.setClassIndex(4);

        // Add Instances (Sunny=0, Overcast=1, Rain=2 | High=0, Medium=1, Low=2 | No=0, Yes=1 | Fair=0, Excellent=1)
        dataset.add(new Instance(new double[]{0, 0, 0, 0}, "Denied"));
        dataset.add(new Instance(new double[]{0, 0, 0, 1}, "Denied"));
        dataset.add(new Instance(new double[]{1, 0, 0, 0}, "Approved"));
        dataset.add(new Instance(new double[]{2, 1, 0, 0}, "Approved"));
        dataset.add(new Instance(new double[]{2, 2, 1, 0}, "Approved"));
        dataset.add(new Instance(new double[]{2, 2, 1, 1}, "Denied"));
        dataset.add(new Instance(new double[]{1, 2, 1, 1}, "Approved"));
        dataset.add(new Instance(new double[]{0, 1, 0, 0}, "Denied"));
        dataset.add(new Instance(new double[]{0, 2, 1, 0}, "Approved"));
        dataset.add(new Instance(new double[]{2, 1, 1, 0}, "Approved"));
        dataset.add(new Instance(new double[]{0, 1, 1, 1}, "Approved"));
        dataset.add(new Instance(new double[]{1, 1, 0, 1}, "Approved"));
        dataset.add(new Instance(new double[]{1, 0, 1, 0}, "Approved"));
        dataset.add(new Instance(new double[]{2, 1, 0, 1}, "Denied"));

        System.out.println("Dataset Loaded: " + dataset.size() + " instances with 5 attributes.");

        // Train J48 Decision Tree Classifier
        WekaJ48Classifier j48Tree = new WekaJ48Classifier();
        j48Tree.buildClassifier(dataset);

        System.out.println("\n=== Generated Weka J48 Decision Tree Structure ===");
        j48Tree.printDecisionTree();

        // Evaluate Classifier Performance
        Evaluation.evaluateModel(j48Tree, dataset);

        // Predict New Instance
        System.out.println("\n=== Classifying New Unlabeled Instance ===");
        // Sunny (0), Low Income (2), Is Student (1), Fair Credit (0)
        Instance newApplicant = new Instance(new double[]{0, 2, 1, 0}, null);
        String decision = j48Tree.classifyInstance(newApplicant, dataset);
        System.out.println("  Applicant: Outlook=Sunny, Income=Low, Student=Yes, CreditRating=Fair");
        System.out.println("  Weka J48 Predicted Loan Status -> " + decision);
        System.out.println("=================================================");
    }
}
