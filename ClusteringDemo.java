import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Program 12: Clustering using Java Libraries
 * 
 * Implements the K-Means Clustering Algorithm in Java.
 * Segregates 2D data points into K clusters based on Euclidean distance,
 * iteratively recomputing cluster centroids until convergence.
 * 
 * Time Complexity: O(K * N * I) where K is number of clusters, N is number of points, 
 *                  and I is maximum iterations until convergence.
 * Space Complexity: O(N + K) for storing points and centroids.
 */
public class ClusteringDemo {

    static class Point {
        double x;
        double y;
        int id;

        Point(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public double distanceTo(Point other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        @Override
        public String toString() {
            return String.format("P%d(%.2f, %.2f)", id, x, y);
        }
    }

    static class Cluster {
        int id;
        Point centroid;
        List<Point> points;

        Cluster(int id, Point initialCentroid) {
            this.id = id;
            this.centroid = new Point(-1, initialCentroid.x, initialCentroid.y);
            this.points = new ArrayList<>();
        }

        void clear() {
            points.clear();
        }

        void addPoint(Point p) {
            points.add(p);
        }

        // Recalculates centroid position based on mean of assigned points
        boolean updateCentroid() {
            if (points.isEmpty()) {
                return false;
            }

            double sumX = 0;
            double sumY = 0;
            for (Point p : points) {
                sumX += p.x;
                sumY += p.y;
            }

            double newX = sumX / points.size();
            double newY = sumY / points.size();

            // Check if centroid shifted significantly
            double shift = Math.sqrt(Math.pow(centroid.x - newX, 2) + Math.pow(centroid.y - newY, 2));
            centroid.x = newX;
            centroid.y = newY;

            return shift > 1e-4; // Return true if centroid changed
        }

        // Computes Within-Cluster Sum of Squares (WCSS / Inertia)
        double computeWCSS() {
            double wcss = 0;
            for (Point p : points) {
                double dist = p.distanceTo(centroid);
                wcss += dist * dist;
            }
            return wcss;
        }
    }

    public static class KMeans {
        private int k;
        private int maxIterations;
        private List<Cluster> clusters;

        public KMeans(int k, int maxIterations) {
            this.k = k;
            this.maxIterations = maxIterations;
            this.clusters = new ArrayList<>();
        }

        public List<Cluster> fit(List<Point> data) {
            if (data.size() < k) {
                throw new IllegalArgumentException("Number of points must be >= K.");
            }

            // Step 1: Initialize K centroids (using deterministic spread for reproducibility)
            clusters.clear();
            for (int i = 0; i < k; i++) {
                int initIndex = (i * data.size()) / k;
                Point initPoint = data.get(initIndex);
                clusters.add(new Cluster(i + 1, initPoint));
            }

            int iteration = 0;
            boolean centroidsMoved = true;

            // Step 2 & 3: Iterate Assignment and Update phases
            while (centroidsMoved && iteration < maxIterations) {
                iteration++;

                // Clear previous point assignments
                for (Cluster cluster : clusters) {
                    cluster.clear();
                }

                // Assign each point to nearest cluster centroid
                for (Point point : data) {
                    Cluster nearestCluster = null;
                    double minDistance = Double.MAX_VALUE;

                    for (Cluster cluster : clusters) {
                        double dist = point.distanceTo(cluster.centroid);
                        if (dist < minDistance) {
                            minDistance = dist;
                            nearestCluster = cluster;
                        }
                    }

                    if (nearestCluster != null) {
                        nearestCluster.addPoint(point);
                    }
                }

                // Update centroids and verify convergence
                centroidsMoved = false;
                for (Cluster cluster : clusters) {
                    if (cluster.updateCentroid()) {
                        centroidsMoved = true;
                    }
                }
            }

            System.out.printf("K-Means converged after %d iterations.\n", iteration);
            return clusters;
        }

        public double calculateTotalWCSS() {
            double totalWCSS = 0;
            for (Cluster cluster : clusters) {
                totalWCSS += cluster.computeWCSS();
            }
            return totalWCSS;
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      PROGRAM 12: K-MEANS CLUSTERING IN JAVA     ");
        System.out.println("=================================================\n");

        // Generate sample 2D dataset with 3 distinct spatial groupings
        List<Point> dataset = new ArrayList<>();
        // Group 1: Low values (Customer Group A - Budget)
        dataset.add(new Point(1, 1.0, 1.5));
        dataset.add(new Point(2, 1.5, 2.0));
        dataset.add(new Point(3, 2.0, 1.0));
        
        // Group 2: Medium values (Customer Group B - Standard)
        dataset.add(new Point(4, 5.0, 6.0));
        dataset.add(new Point(5, 5.5, 5.0));
        dataset.add(new Point(6, 6.0, 5.5));
        dataset.add(new Point(7, 6.5, 6.5));

        // Group 3: High values (Customer Group C - Premium)
        dataset.add(new Point(8, 9.0, 9.5));
        dataset.add(new Point(9, 9.5, 8.5));
        dataset.add(new Point(10, 10.0, 9.0));

        System.out.println("Input Dataset (Total Points: " + dataset.size() + "):");
        for (Point p : dataset) {
            System.out.println("  " + p);
        }

        int K = 3;
        int maxIter = 100;

        System.out.printf("\nExecuting K-Means Clustering (K = %d)...\n", K);
        System.out.println("-------------------------------------------------");

        KMeans kMeans = new KMeans(K, maxIter);
        List<Cluster> finalClusters = kMeans.fit(dataset);

        System.out.println("\n-------------------------------------------------");
        System.out.println("Clustering Results Summary:");
        System.out.println("-------------------------------------------------");
        for (Cluster cluster : finalClusters) {
            System.out.printf("Cluster %d | Centroid: (%.2f, %.2f) | Points Count: %d\n",
                    cluster.id, cluster.centroid.x, cluster.centroid.y, cluster.points.size());
            System.out.print("  Members: ");
            for (Point p : cluster.points) {
                System.out.print(p + " ");
            }
            System.out.printf("\n  Within-Cluster Sum of Squares (WCSS): %.4f\n\n", cluster.computeWCSS());
        }

        System.out.printf("Total WCSS (Inertia): %.4f\n", kMeans.calculateTotalWCSS());
        System.out.println("=================================================");
    }
}
