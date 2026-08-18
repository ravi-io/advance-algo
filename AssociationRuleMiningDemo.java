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
 * Program 13: Association Rule Mining using Apriori Algorithm
 * 
 * Mines frequent itemsets and generates strong association rules (A => B)
 * from transactional data based on Minimum Support and Minimum Confidence.
 * Computes Support, Confidence, and Lift ratio metrics.
 * 
 * Time Complexity: O(2^D * N) worst case, where D is maximum transaction length
 *                  and N is number of transactions. Pruning reduces candidate space significantly.
 * Space Complexity: O(2^D) for candidate itemset storage.
 */
public class AssociationRuleMiningDemo {

    // Represents an Association Rule A => B
    static class AssociationRule {
        Set<String> antecedent; // A
        Set<String> consequent; // B
        double support;         // Support(A U B)
        double confidence;      // Confidence(A => B) = Support(A U B) / Support(A)
        double lift;            // Lift(A => B) = Confidence(A => B) / Support(B)

        AssociationRule(Set<String> antecedent, Set<String> consequent, double support, double confidence, double lift) {
            this.antecedent = antecedent;
            this.consequent = consequent;
            this.support = support;
            this.confidence = confidence;
            this.lift = lift;
        }

        @Override
        public String toString() {
            return String.format("%s => %s | Support: %.2f | Confidence: %.2f | Lift: %.2f",
                    antecedent, consequent, support, confidence, lift);
        }
    }

    public static class Apriori {
        private List<Set<String>> transactions;
        private double minSupportRatio;
        private double minConfidenceRatio;
        private int totalTransactions;
        private int minSupportCount;

        public Apriori(List<Set<String>> transactions, double minSupportRatio, double minConfidenceRatio) {
            this.transactions = transactions;
            this.minSupportRatio = minSupportRatio;
            this.minConfidenceRatio = minConfidenceRatio;
            this.totalTransactions = transactions.size();
            this.minSupportCount = (int) Math.ceil(minSupportRatio * totalTransactions);
        }

        public void mine() {
            System.out.printf("Total Transactions: %d | Min Support: %.1f%% (>= %d count) | Min Confidence: %.1f%%\n\n",
                    totalTransactions, minSupportRatio * 100, minSupportCount, minConfidenceRatio * 100);

            // Step 1: Find Frequent 1-Itemsets (L1)
            Map<Set<String>, Integer> frequentItemsets = new HashMap<>();
            Map<Set<String>, Integer> currentCandidatesCount = getCandidate1Counts();

            List<Set<String>> currentFrequent = filterByMinSupport(currentCandidatesCount, frequentItemsets);
            int k = 1;

            System.out.println("-------------------------------------------------");
            System.out.printf("Frequent %d-Itemsets (L%d):\n", k, k);
            printItemsets(currentFrequent, frequentItemsets);

            // Step 2: Iteratively find Frequent k-Itemsets (Lk)
            while (!currentFrequent.isEmpty()) {
                k++;
                List<Set<String>> candidateK = generateCandidateK(currentFrequent, k);
                if (candidateK.isEmpty()) {
                    break;
                }

                Map<Set<String>, Integer> candidateKCounts = countSupport(candidateK);
                List<Set<String>> frequentK = filterByMinSupport(candidateKCounts, frequentItemsets);

                if (!frequentK.isEmpty()) {
                    System.out.printf("Frequent %d-Itemsets (L%d):\n", k, k);
                    printItemsets(frequentK, frequentItemsets);
                    currentFrequent = frequentK;
                } else {
                    break;
                }
            }

            // Step 3: Generate Association Rules from Frequent Itemsets with size >= 2
            System.out.println("-------------------------------------------------");
            System.out.println("Generated Association Rules (A => B):");
            System.out.println("-------------------------------------------------");

            List<AssociationRule> rules = generateRules(frequentItemsets);
            if (rules.isEmpty()) {
                System.out.println("No association rules satisfied the minimum confidence threshold.");
            } else {
                for (AssociationRule rule : rules) {
                    System.out.println("  " + rule);
                }
            }
        }

        private Map<Set<String>, Integer> getCandidate1Counts() {
            Map<Set<String>, Integer> counts = new HashMap<>();
            for (Set<String> t : transactions) {
                for (String item : t) {
                    Set<String> itemset = new HashSet<>();
                    itemset.add(item);
                    counts.put(itemset, counts.getOrDefault(itemset, 0) + 1);
                }
            }
            return counts;
        }

        private Map<Set<String>, Integer> countSupport(List<Set<String>> candidates) {
            Map<Set<String>, Integer> counts = new HashMap<>();
            for (Set<String> candidate : candidates) {
                int count = 0;
                for (Set<String> t : transactions) {
                    if (t.containsAll(candidate)) {
                        count++;
                    }
                }
                counts.put(candidate, count);
            }
            return counts;
        }

        private List<Set<String>> filterByMinSupport(Map<Set<String>, Integer> candidateCounts,
                                                     Map<Set<String>, Integer> frequentItemsets) {
            List<Set<String>> frequent = new ArrayList<>();
            for (Map.Entry<Set<String>, Integer> entry : candidateCounts.entrySet()) {
                if (entry.getValue() >= minSupportCount) {
                    frequent.add(entry.getKey());
                    frequentItemsets.put(entry.getKey(), entry.getValue());
                }
            }
            return frequent;
        }

        private List<Set<String>> generateCandidateK(List<Set<String>> previousFrequent, int k) {
            List<Set<String>> candidates = new ArrayList<>();
            int n = previousFrequent.size();

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Set<String> set1 = previousFrequent.get(i);
                    Set<String> set2 = previousFrequent.get(j);

                    Set<String> union = new TreeSet<>(set1);
                    union.addAll(set2);

                    if (union.size() == k && !candidates.contains(union)) {
                        candidates.add(union);
                    }
                }
            }
            return candidates;
        }

        private List<AssociationRule> generateRules(Map<Set<String>, Integer> frequentItemsets) {
            List<AssociationRule> rules = new ArrayList<>();

            for (Set<String> itemset : frequentItemsets.keySet()) {
                if (itemset.size() < 2) continue;

                int itemsetSupportCount = frequentItemsets.get(itemset);
                double itemsetSupport = (double) itemsetSupportCount / totalTransactions;

                // Generate non-empty proper subsets
                List<Set<String>> subsets = generateSubsets(itemset);
                for (Set<String> antecedent : subsets) {
                    if (antecedent.isEmpty() || antecedent.size() == itemset.size()) continue;

                    Set<String> consequent = new TreeSet<>(itemset);
                    consequent.removeAll(antecedent);

                    int antecedentCount = frequentItemsets.getOrDefault(antecedent, 0);
                    if (antecedentCount == 0) continue;

                    double confidence = (double) itemsetSupportCount / antecedentCount;

                    if (confidence >= minConfidenceRatio) {
                        int consequentCount = frequentItemsets.getOrDefault(consequent, 0);
                        double consequentSupport = (double) consequentCount / totalTransactions;
                        double lift = consequentSupport == 0 ? 0 : confidence / consequentSupport;

                        rules.add(new AssociationRule(antecedent, consequent, itemsetSupport, confidence, lift));
                    }
                }
            }

            // Sort rules by Confidence descending
            rules.sort((r1, r2) -> Double.compare(r2.confidence, r1.confidence));
            return rules;
        }

        private List<Set<String>> generateSubsets(Set<String> set) {
            List<String> list = new ArrayList<>(set);
            List<Set<String>> subsets = new ArrayList<>();
            int n = list.size();

            for (int i = 0; i < (1 << n); i++) {
                Set<String> subset = new TreeSet<>();
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) != 0) {
                        subset.add(list.get(j));
                    }
                }
                subsets.add(subset);
            }
            return subsets;
        }

        private void printItemsets(List<Set<String>> itemsets, Map<Set<String>, Integer> counts) {
            for (Set<String> set : itemsets) {
                int count = counts.get(set);
                double supp = (double) count / totalTransactions;
                System.out.printf("  Itemset: %-25s | Count: %d | Support: %.2f\n", set, count, supp);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   PROGRAM 13: ASSOCIATION RULE MINING (APRIORI) ");
        System.out.println("=================================================\n");

        // Sample Market Basket Transactions
        List<Set<String>> transactions = new ArrayList<>();
        transactions.add(new HashSet<>(Arrays.asList("Milk", "Bread", "Butter")));
        transactions.add(new HashSet<>(Arrays.asList("Milk", "Bread")));
        transactions.add(new HashSet<>(Arrays.asList("Milk", "Diaper", "Beer", "Bread")));
        transactions.add(new HashSet<>(Arrays.asList("Bread", "Butter")));
        transactions.add(new HashSet<>(Arrays.asList("Milk", "Diaper", "Beer")));
        transactions.add(new HashSet<>(Arrays.asList("Milk", "Bread", "Butter", "Diaper")));

        System.out.println("Market Basket Transactions Database:");
        for (int i = 0; i < transactions.size(); i++) {
            System.out.printf("  T%-2d: %s\n", (i + 1), transactions.get(i));
        }
        System.out.println();

        // Run Apriori with minSupport = 40% (0.40) and minConfidence = 60% (0.60)
        double minSupport = 0.40;
        double minConfidence = 0.60;

        Apriori apriori = new Apriori(transactions, minSupport, minConfidence);
        apriori.mine();

        System.out.println("=================================================");
    }
}
