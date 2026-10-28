import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Program 17: Sentiment Analysis with Java
 * 
 * Implements two Natural Language Processing (NLP) sentiment analysis engines:
 * 1. Rule-Based Lexicon Sentiment Engine with Negation Handling & Intensifier
 * Boosting
 * 2. Multinomial Naive Bayes Machine Learning Classifier
 * 
 * Time Complexity: O(N * W) for preprocessing and classification of N texts
 * averaging W words.
 * Space Complexity: O(V + N) where V is total vocabulary size and N is sample
 * corpus size.
 */
public class SentimentAnalysisDemo {

  public enum SentimentClass {
    POSITIVE, NEGATIVE, NEUTRAL
  }

  // Standard English stop words
  private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
      "a", "an", "the", "and", "or", "but", "if", "because", "as", "until", "while",
      "of", "at", "by", "for", "with", "about", "against", "between", "into", "through",
      "during", "before", "after", "above", "below", "to", "from", "up", "down", "in",
      "out", "on", "off", "over", "under", "again", "further", "then", "once", "here",
      "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more",
      "most", "other", "some", "such", "own", "same", "so", "than", "too", "very"));

  // Negation terms that invert sentiment polarity
  private static final Set<String> NEGATION_WORDS = new HashSet<>(Arrays.asList(
      "not", "no", "never", "none", "neither", "nor", "cannot", "cant", "dont",
      "doesnt", "didnt", "isnt", "arent", "wasnt", "werent", "havent", "hasnt", "hadnt",
      "without", "hardly", "barely", "scarcely"));

  // Intensifier terms that boost sentiment strength
  private static final Map<String, Double> INTENSIFIERS = new HashMap<>();
  static {
    INTENSIFIERS.put("very", 1.5);
    INTENSIFIERS.put("extremely", 2.0);
    INTENSIFIERS.put("super", 1.6);
    INTENSIFIERS.put("really", 1.4);
    INTENSIFIERS.put("incredibly", 1.8);
    INTENSIFIERS.put("absolutely", 1.7);
    INTENSIFIERS.put("highly", 1.5);
    INTENSIFIERS.put("slightly", 0.5);
    INTENSIFIERS.put("somewhat", 0.7);
  }

  /**
   * Lexicon-Based Sentiment Analyzer Engine
   */
  public static class LexiconSentimentAnalyzer {
    private final Map<String, Double> lexicon = new HashMap<>();

    public LexiconSentimentAnalyzer() {
      loadDefaultLexicon();
    }

    private void loadDefaultLexicon() {
      // Positive terms (+1.0 to +4.0)
      lexicon.put("excellent", 3.5);
      lexicon.put("great", 3.0);
      lexicon.put("awesome", 3.5);
      lexicon.put("good", 2.0);
      lexicon.put("best", 3.8);
      lexicon.put("wonderful", 3.2);
      lexicon.put("fantastic", 3.5);
      lexicon.put("love", 3.2);
      lexicon.put("loved", 3.2);
      lexicon.put("likes", 2.0);
      lexicon.put("like", 1.8);
      lexicon.put("happy", 2.5);
      lexicon.put("enjoyed", 2.5);
      lexicon.put("enjoy", 2.2);
      lexicon.put("brilliant", 3.4);
      lexicon.put("perfect", 3.6);
      lexicon.put("superb", 3.5);
      lexicon.put("recommend", 2.4);
      lexicon.put("fast", 1.5);
      lexicon.put("smooth", 1.8);
      lexicon.put("satisfied", 2.2);
      lexicon.put("outstanding", 3.7);
      lexicon.put("delightful", 3.0);
      lexicon.put("impressive", 2.8);

      // Negative terms (-1.0 to -4.0)
      lexicon.put("bad", -2.5);
      lexicon.put("terrible", -3.5);
      lexicon.put("horrible", -3.8);
      lexicon.put("poor", -2.0);
      lexicon.put("worst", -3.9);
      lexicon.put("awful", -3.5);
      lexicon.put("hate", -3.2);
      lexicon.put("hated", -3.2);
      lexicon.put("slow", -1.8);
      lexicon.put("broken", -2.8);
      lexicon.put("defective", -3.0);
      lexicon.put("disappointed", -2.6);
      lexicon.put("disappointing", -2.6);
      lexicon.put("useless", -3.0);
      lexicon.put("waste", -2.8);
      lexicon.put("error", -1.5);
      lexicon.put("crash", -2.5);
      lexicon.put("fail", -2.2);
      lexicon.put("failed", -2.2);
      lexicon.put("ugly", -2.0);
      lexicon.put("frustrating", -2.7);
    }

    public static class SentimentResult {
      public double positiveScore;
      public double negativeScore;
      public double neutralScore;
      public double compoundScore; // Normalized between -1.0 and +1.0
      public SentimentClass predictedClass;

      @Override
      public String toString() {
        return String.format("Class: %-8s | Compound: %6.3f | Pos: %.2f, Neg: %.2f, Neu: %.2f",
            predictedClass, compoundScore, positiveScore, negativeScore, neutralScore);
      }
    }

    public SentimentResult analyze(String text) {
      String cleanText = text.toLowerCase().replaceAll("[^a-z0-9'\\s]", " ");
      String[] tokens = cleanText.split("\\s+");

      double totalPos = 0.0;
      double totalNeg = 0.0;
      int totalNeuCount = 0;
      double netScore = 0.0;

      boolean negationActive = false;
      int negationDistance = 0;
      double currentMultiplier = 1.0;

      for (String token : tokens) {
        if (token.isEmpty())
          continue;

        if (NEGATION_WORDS.contains(token)) {
          negationActive = true;
          negationDistance = 0;
          continue;
        }

        if (negationActive) {
          negationDistance++;
          if (negationDistance > 2) { // Negation window of 2 words
            negationActive = false;
          }
        }

        if (INTENSIFIERS.containsKey(token)) {
          currentMultiplier = INTENSIFIERS.get(token);
          continue;
        }

        if (lexicon.containsKey(token)) {
          double score = lexicon.get(token) * currentMultiplier;
          if (negationActive) {
            score = -score * 0.8; // Invert polarity with slight attenuation
          }

          if (score > 0) {
            totalPos += score;
          } else if (score < 0) {
            totalNeg += Math.abs(score);
          }
          netScore += score;
          currentMultiplier = 1.0; // Reset multiplier after applying
        } else {
          totalNeuCount++;
        }
      }

      SentimentResult result = new SentimentResult();
      double sum = totalPos + totalNeg + totalNeuCount;
      result.positiveScore = sum == 0 ? 0 : totalPos / sum;
      result.negativeScore = sum == 0 ? 0 : totalNeg / sum;
      result.neutralScore = sum == 0 ? 1 : totalNeuCount / sum;

      // Normalize compound score using VADER-style formula: net / sqrt(net^2 + alpha)
      double alpha = 15.0;
      result.compoundScore = netScore / Math.sqrt((netScore * netScore) + alpha);

      if (result.compoundScore >= 0.05) {
        result.predictedClass = SentimentClass.POSITIVE;
      } else if (result.compoundScore <= -0.05) {
        result.predictedClass = SentimentClass.NEGATIVE;
      } else {
        result.predictedClass = SentimentClass.NEUTRAL;
      }

      return result;
    }
  }

  /**
   * Machine Learning Naive Bayes Sentiment Classifier
   */
  public static class NaiveBayesSentimentClassifier {
    private final Map<SentimentClass, Double> classPriors = new HashMap<>();
    private final Map<SentimentClass, Map<String, Integer>> wordCounts = new HashMap<>();
    private final Map<SentimentClass, Integer> totalWordsPerClass = new HashMap<>();
    private final Set<String> vocabulary = new HashSet<>();

    public void train(List<String> documents, List<SentimentClass> labels) {
      int totalDocs = documents.size();
      Map<SentimentClass, Integer> docCounts = new HashMap<>();

      for (SentimentClass sc : SentimentClass.values()) {
        docCounts.put(sc, 0);
        wordCounts.put(sc, new HashMap<>());
        totalWordsPerClass.put(sc, 0);
      }

      for (int i = 0; i < totalDocs; i++) {
        String doc = documents.get(i);
        SentimentClass label = labels.get(i);

        docCounts.put(label, docCounts.get(label) + 1);
        List<String> tokens = tokenize(doc);

        for (String token : tokens) {
          vocabulary.add(token);
          Map<String, Integer> classMap = wordCounts.get(label);
          classMap.put(token, classMap.getOrDefault(token, 0) + 1);
          totalWordsPerClass.put(label, totalWordsPerClass.get(label) + 1);
        }
      }

      // Calculate priors P(C)
      for (SentimentClass sc : SentimentClass.values()) {
        classPriors.put(sc, (double) docCounts.get(sc) / totalDocs);
      }
    }

    public SentimentClass classify(String text) {
      List<String> tokens = tokenize(text);
      SentimentClass bestClass = SentimentClass.NEUTRAL;
      double maxLogProb = -Double.MAX_VALUE;

      int vocabSize = vocabulary.size();

      for (SentimentClass sc : SentimentClass.values()) {
        double prior = classPriors.getOrDefault(sc, 0.0);
        if (prior == 0.0)
          continue;

        double logProb = Math.log(prior);
        int totalWordsInC = totalWordsPerClass.get(sc);

        for (String token : tokens) {
          int count = wordCounts.get(sc).getOrDefault(token, 0);
          // Laplace Smoothing (+1)
          double wordProb = (double) (count + 1) / (totalWordsInC + vocabSize);
          logProb += Math.log(wordProb);
        }

        if (logProb > maxLogProb) {
          maxLogProb = logProb;
          bestClass = sc;
        }
      }
      return bestClass;
    }

    private List<String> tokenize(String text) {
      String clean = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
      String[] raw = clean.split("\\s+");
      List<String> filtered = new ArrayList<>();
      for (String t : raw) {
        if (!t.isEmpty() && !STOP_WORDS.contains(t)) {
          filtered.add(t);
        }
      }
      return filtered;
    }
  }

  public static void main(String[] args) {
    System.out.println("=================================================");
    System.out.println("      PROGRAM 17: SENTIMENT ANALYSIS IN JAVA     ");
    System.out.println("=================================================\n");

    // Sample Test Sentences covering Positive, Negative, and Negation cases
    List<String> sampleTexts = Arrays.asList(
        "The product is absolutely fantastic! Fast delivery and great build quality.",
        "Terrible customer service and broken product. I am very disappointed.",
        "It is an average device. Works as expected, nothing extraordinary.",
        "The performance is not bad, but the screen quality is poor.",
        "I love the sleek design and impressive performance of this application!",
        "Horrible experience, useless software and constant crashes.");

    System.out.println("--- 1. Lexicon-Based Sentiment Analysis Pipeline ---");
    LexiconSentimentAnalyzer lexiconAnalyzer = new LexiconSentimentAnalyzer();

    for (int i = 0; i < sampleTexts.size(); i++) {
      String text = sampleTexts.get(i);
      LexiconSentimentAnalyzer.SentimentResult result = lexiconAnalyzer.analyze(text);
      System.out.printf("Text %d: \"%s\"\n", i + 1, text);
      System.out.println("        Result -> " + result + "\n");
    }

    System.out.println("--- 2. Training Machine Learning Naive Bayes Classifier ---");
    List<String> trainingCorpus = Arrays.asList(
        "Great product, excellent quality and super fast shipping",
        "Awesome customer support, very happy with my purchase",
        "Best laptop ever, brilliant battery life and smooth display",
        "Terrible build quality, cheap materials and broke on day one",
        "Worst customer service experience, horrible delay and useless agent",
        "Bad performance, constant crashes and frustrating lag",
        "It is okay, normal experience with average features",
        "Standard product, performs fine according to specifications");

    List<SentimentClass> trainingLabels = Arrays.asList(
        SentimentClass.POSITIVE, SentimentClass.POSITIVE, SentimentClass.POSITIVE,
        SentimentClass.NEGATIVE, SentimentClass.NEGATIVE, SentimentClass.NEGATIVE,
        SentimentClass.NEUTRAL, SentimentClass.NEUTRAL);

    NaiveBayesSentimentClassifier nbClassifier = new NaiveBayesSentimentClassifier();
    nbClassifier.train(trainingCorpus, trainingLabels);
    System.out.println("Naive Bayes Model Trained successfully on " + trainingCorpus.size() + " samples.");

    System.out.println("\n--- 3. Evaluating Machine Learning Predictions ---");
    int correct = 0;
    int total = sampleTexts.size();

    // Ground truth for evaluation
    List<SentimentClass> groundTruth = Arrays.asList(
        SentimentClass.POSITIVE, SentimentClass.NEGATIVE, SentimentClass.NEUTRAL,
        SentimentClass.NEGATIVE, SentimentClass.POSITIVE, SentimentClass.NEGATIVE);

    System.out.printf("%-55s | %-12s | %-12s\n", "Input Text", "Predicted", "Actual");
    System.out.println("-------------------------------------------------------------------------------------");

    for (int i = 0; i < total; i++) {
      String text = sampleTexts.get(i);
      SentimentClass predicted = nbClassifier.classify(text);
      SentimentClass actual = groundTruth.get(i);

      if (predicted == actual)
        correct++;
      String shortText = text.length() > 52 ? text.substring(0, 49) + "..." : text;
      System.out.printf("%-55s | %-12s | %-12s\n", shortText, predicted, actual);
    }

    double accuracy = (double) correct / total * 100.0;
    System.out.println("-------------------------------------------------------------------------------------");
    System.out.printf("Model Classification Accuracy: %.2f%%\n", accuracy);
    System.out.println("=================================================");
  }
}
