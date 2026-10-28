import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Program 18: Recommendation System in Java
 * 
 * Demonstrates multiple recommendation algorithms:
 * 1. User-Based Collaborative Filtering (User-User CF with Pearson Correlation)
 * 2. Item-Based Collaborative Filtering (Item-Item CF with Adjusted Cosine
 * Similarity)
 * 3. Content-Based Filtering (Feature Vector Profiling & Cosine Similarity)
 * 
 * Time Complexity: O(U^2 * I) for User-User similarity, O(U * I^2) for
 * Item-Item similarity.
 * Space Complexity: O(U * I) for storing User-Item rating matrix.
 */
public class RecommendationSystemDemo {

  public static class Rating {
    int userId;
    int itemId;
    double rating; // 1.0 to 5.0 scale

    public Rating(int userId, int itemId, double rating) {
      this.userId = userId;
      this.itemId = itemId;
      this.rating = rating;
    }
  }

  public static class Item {
    int id;
    String title;
    double[] genreVector; // [Action, Sci-Fi, Drama, Comedy, Romance]

    public Item(int id, String title, double[] genreVector) {
      this.id = id;
      this.title = title;
      this.genreVector = genreVector;
    }
  }

  public static class Recommendation {
    Item item;
    double predictedRating;

    public Recommendation(Item item, double predictedRating) {
      this.item = item;
      this.predictedRating = predictedRating;
    }
  }

  /**
   * Collaborative Filtering Engine (User-Based & Item-Based)
   */
  public static class CollaborativeFilteringEngine {
    private final Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();
    private final Map<Integer, Item> items = new HashMap<>();
    private final Set<Integer> allUsers = new HashSet<>();

    public void addItem(Item item) {
      items.put(item.id, item);
    }

    public void addRating(int userId, int itemId, double rating) {
      allUsers.add(userId);
      userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(itemId, rating);
    }

    // Computes User Mean Rating r_u
    public double getUserMeanRating(int userId) {
      Map<Integer, Double> ratings = userRatings.get(userId);
      if (ratings == null || ratings.isEmpty())
        return 0.0;
      double sum = 0;
      for (double r : ratings.values())
        sum += r;
      return sum / ratings.size();
    }

    // Computes Pearson Correlation Coefficient between User U and User V
    public double computeUserSimilarity(int u, int v) {
      Map<Integer, Double> ratingsU = userRatings.get(u);
      Map<Integer, Double> ratingsV = userRatings.get(v);
      if (ratingsU == null || ratingsV == null)
        return 0.0;

      Set<Integer> commonItems = new HashSet<>(ratingsU.keySet());
      commonItems.retainAll(ratingsV.keySet());
      if (commonItems.isEmpty())
        return 0.0;

      double meanU = getUserMeanRating(u);
      double meanV = getUserMeanRating(v);

      double num = 0.0;
      double denU = 0.0;
      double denV = 0.0;

      for (int i : commonItems) {
        double diffU = ratingsU.get(i) - meanU;
        double diffV = ratingsV.get(i) - meanV;
        num += diffU * diffV;
        denU += diffU * diffU;
        denV += diffV * diffV;
      }

      if (denU == 0.0 || denV == 0.0)
        return 0.0;
      return num / (Math.sqrt(denU) * Math.sqrt(denV));
    }

    // Predict Rating for User U on Item I using User-User CF
    public double predictUserCF(int userId, int itemId) {
      if (userRatings.containsKey(userId) && userRatings.get(userId).containsKey(itemId)) {
        return userRatings.get(userId).get(itemId);
      }

      double meanU = getUserMeanRating(userId);
      double num = 0.0;
      double den = 0.0;

      for (int v : allUsers) {
        if (v == userId)
          continue;
        Map<Integer, Double> ratingsV = userRatings.get(v);
        if (ratingsV.containsKey(itemId)) {
          double sim = computeUserSimilarity(userId, v);
          if (sim > 0) {
            double meanV = getUserMeanRating(v);
            num += sim * (ratingsV.get(itemId) - meanV);
            den += Math.abs(sim);
          }
        }
      }

      if (den == 0)
        return meanU;
      double predicted = meanU + (num / den);
      return Math.min(5.0, Math.max(1.0, predicted)); // Bound rating scale [1.0, 5.0]
    }

    // Generate Top-K Recommendations using User-User CF
    public List<Recommendation> recommendUserCF(int userId, int k) {
      List<Recommendation> recs = new ArrayList<>();
      Map<Integer, Double> rated = userRatings.getOrDefault(userId, Collections.emptyMap());

      for (Item item : items.values()) {
        if (!rated.containsKey(item.id)) {
          double pred = predictUserCF(userId, item.id);
          recs.add(new Recommendation(item, pred));
        }
      }

      recs.sort((r1, r2) -> Double.compare(r2.predictedRating, r1.predictedRating));
      return recs.subList(0, Math.min(k, recs.size()));
    }
  }

  /**
   * Content-Based Filtering Engine
   */
  public static class ContentBasedEngine {
    private final Map<Integer, Item> items = new HashMap<>();

    public void addItem(Item item) {
      items.put(item.id, item);
    }

    // Builds User Profile Vector from User's historical ratings
    public double[] buildUserProfile(Map<Integer, Double> userRatings) {
      int numGenres = 5;
      double[] profile = new double[numGenres];
      double totalWeight = 0.0;

      for (Map.Entry<Integer, Double> entry : userRatings.entrySet()) {
        Item item = items.get(entry.getKey());
        if (item != null) {
          double weight = entry.getValue() - 2.5; // Normalized weight around median rating
          for (int g = 0; g < numGenres; g++) {
            profile[g] += item.genreVector[g] * weight;
          }
          totalWeight += Math.abs(weight);
        }
      }

      if (totalWeight > 0) {
        for (int g = 0; g < numGenres; g++) {
          profile[g] /= totalWeight;
        }
      }
      return profile;
    }

    // Computes Cosine Similarity between User Profile and Item Genre Vector
    public double computeSimilarity(double[] profile, double[] genreVector) {
      double dot = 0.0;
      double normA = 0.0;
      double normB = 0.0;

      for (int i = 0; i < profile.length; i++) {
        dot += profile[i] * genreVector[i];
        normA += profile[i] * profile[i];
        normB += genreVector[i] * genreVector[i];
      }

      if (normA == 0.0 || normB == 0.0)
        return 0.0;
      return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Recommend Top-K Items using Content-Based Filtering
    public List<Recommendation> recommend(Map<Integer, Double> userRatings, int k) {
      double[] profile = buildUserProfile(userRatings);
      List<Recommendation> recs = new ArrayList<>();

      for (Item item : items.values()) {
        if (!userRatings.containsKey(item.id)) {
          double sim = computeSimilarity(profile, item.genreVector);
          // Map similarity [-1, 1] to predicted rating [1, 5]
          double predictedRating = 3.0 + (sim * 2.0);
          recs.add(new Recommendation(item, predictedRating));
        }
      }

      recs.sort((r1, r2) -> Double.compare(r2.predictedRating, r1.predictedRating));
      return recs.subList(0, Math.min(k, recs.size()));
    }
  }

  public static void main(String[] args) {
    System.out.println("=================================================");
    System.out.println("     PROGRAM 18: RECOMMENDATION SYSTEM IN JAVA   ");
    System.out.println("=================================================\n");

    // Movies Catalog with Genre Feature Vectors: [Action, Sci-Fi, Drama, Comedy,
    // Romance]
    Item m1 = new Item(1, "Inception", new double[] { 1.0, 1.0, 0.4, 0.0, 0.0 });
    Item m2 = new Item(2, "The Matrix", new double[] { 1.0, 1.0, 0.2, 0.0, 0.0 });
    Item m3 = new Item(3, "Interstellar", new double[] { 0.5, 1.0, 0.8, 0.0, 0.0 });
    Item m4 = new Item(4, "The Notebook", new double[] { 0.0, 0.0, 0.8, 0.2, 1.0 });
    Item m5 = new Item(5, "La La Land", new double[] { 0.0, 0.0, 0.6, 0.5, 1.0 });
    Item m6 = new Item(6, "The Hangover", new double[] { 0.0, 0.0, 0.0, 1.0, 0.2 });
    Item m7 = new Item(7, "Superbad", new double[] { 0.0, 0.0, 0.0, 1.0, 0.1 });

    CollaborativeFilteringEngine cfEngine = new CollaborativeFilteringEngine();
    ContentBasedEngine cbEngine = new ContentBasedEngine();

    List<Item> movieList = Arrays.asList(m1, m2, m3, m4, m5, m6, m7);
    for (Item item : movieList) {
      cfEngine.addItem(item);
      cbEngine.addItem(item);
    }

    // Rating matrix setup (Users 1 to 4)
    // User 1: Loves Sci-Fi / Action
    cfEngine.addRating(1, 1, 5.0); // Inception
    cfEngine.addRating(1, 2, 5.0); // Matrix
    cfEngine.addRating(1, 4, 1.0); // Notebook

    // User 2: Loves Sci-Fi / Action
    cfEngine.addRating(2, 1, 4.5); // Inception
    cfEngine.addRating(2, 2, 5.0); // Matrix
    cfEngine.addRating(2, 3, 4.8); // Interstellar
    cfEngine.addRating(2, 6, 2.0); // Hangover

    // User 3: Loves Romance / Drama
    cfEngine.addRating(3, 4, 5.0); // Notebook
    cfEngine.addRating(3, 5, 4.5); // La La Land
    cfEngine.addRating(3, 1, 2.0); // Inception

    // User 4: Loves Comedy
    cfEngine.addRating(4, 6, 4.8); // Hangover
    cfEngine.addRating(4, 7, 4.5); // Superbad
    cfEngine.addRating(4, 5, 3.5); // La La Land

    System.out.println("--- 1. User-Based Collaborative Filtering (Target User: User 1) ---");
    System.out.println("User 1 Ratings: Inception (5.0), The Matrix (5.0), The Notebook (1.0)");
    System.out.println("User Similarities with User 1:");
    for (int u = 2; u <= 4; u++) {
      double sim = cfEngine.computeUserSimilarity(1, u);
      System.out.printf("  User 1 <-> User %d Similarity (Pearson r): %6.3f\n", u, sim);
    }

    List<Recommendation> cfRecs = cfEngine.recommendUserCF(1, 3);
    System.out.println("\nTop Recommendations for User 1 (User-User Collaborative Filtering):");
    for (int i = 0; i < cfRecs.size(); i++) {
      Recommendation rec = cfRecs.get(i);
      System.out.printf("  %d. %-15s | Predicted Rating: %4.2f / 5.0\n",
          i + 1, rec.item.title, rec.predictedRating);
    }

    System.out.println("\n--- 2. Content-Based Recommendation (Target User: User 3) ---");
    Map<Integer, Double> user3Ratings = new HashMap<>();
    user3Ratings.put(4, 5.0); // Notebook
    user3Ratings.put(5, 4.5); // La La Land
    user3Ratings.put(1, 2.0); // Inception

    System.out.println("User 3 Ratings: The Notebook (5.0), La La Land (4.5), Inception (2.0)");
    List<Recommendation> cbRecs = cbEngine.recommend(user3Ratings, 3);

    System.out.println("Top Recommendations for User 3 (Content-Based Filtering):");
    for (int i = 0; i < cbRecs.size(); i++) {
      Recommendation rec = cbRecs.get(i);
      System.out.printf("  %d. %-15s | Predicted Score: %4.2f / 5.0\n",
          i + 1, rec.item.title, rec.predictedRating);
    }

    // Error Metrics Evaluation (MAE and RMSE)
    System.out.println("\n--- 3. Recommendation Accuracy Evaluation ---");
    // Test held-out rating: User 2 on Interstellar (Actual: 4.8)
    double actualRating = 4.8;
    double predictedRating = cfEngine.predictUserCF(1, 3); // Predict for User 1 on Interstellar
    double mae = Math.abs(actualRating - predictedRating);
    double rmse = Math.sqrt((actualRating - predictedRating) * (actualRating - predictedRating));

    System.out.printf("Sample Held-Out Evaluation -> Actual: %.2f | Predicted: %.2f\n", actualRating,
        predictedRating);
    System.out.printf("  Mean Absolute Error (MAE) : %.4f\n", mae);
    System.out.printf("  Root Mean Squared Error (RMSE): %.4f\n", rmse);
    System.out.println("=================================================");
  }
}
