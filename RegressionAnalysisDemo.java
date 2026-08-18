import java.util.ArrayList;
import java.util.List;

/**
 * Program 11: Linear Regression Analysis
 * 
 * Performs Simple Linear Regression (y = m * x + c) on a given dataset.
 * Calculates slope (m), y-intercept (c), Pearson correlation coefficient (r),
 * coefficient of determination (R^2), and Mean Squared Error (MSE).
 * 
 * Time Complexity: O(N) for dataset size N.
 * Space Complexity: O(N) for storing dataset points.
 */
public class RegressionAnalysisDemo {

    static class DataPoint {
        double x; // Independent variable (e.g., Study Hours)
        double y; // Dependent variable (e.g., Exam Score)

        DataPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    public static class LinearRegressionModel {
        private double slope;       // m
        private double intercept;   // c
        private double r;           // Pearson correlation coefficient
        private double rSquared;    // R^2
        private double mse;         // Mean Squared Error

        public LinearRegressionModel(List<DataPoint> data) {
            fit(data);
        }

        private void fit(List<DataPoint> data) {
            int n = data.size();
            if (n == 0) {
                throw new IllegalArgumentException("Dataset cannot be empty.");
            }

            double sumX = 0;
            double sumY = 0;
            double sumXY = 0;
            double sumX2 = 0;
            double sumY2 = 0;

            for (DataPoint dp : data) {
                sumX += dp.x;
                sumY += dp.y;
                sumXY += dp.x * dp.y;
                sumX2 += dp.x * dp.x;
                sumY2 += dp.y * dp.y;
            }

            double meanX = sumX / n;
            double meanY = sumY / n;

            // Slope m = (N * sum(XY) - sum(X)*sum(Y)) / (N * sum(X^2) - (sum(X))^2)
            double denominator = (n * sumX2) - (sumX * sumX);
            if (denominator == 0) {
                throw new ArithmeticException("Vertical line regression is undefined (zero variance in X).");
            }

            this.slope = ((n * sumXY) - (sumX * sumY)) / denominator;
            this.intercept = meanY - (this.slope * meanX);

            // Pearson correlation coefficient (r)
            double numR = (n * sumXY) - (sumX * sumY);
            double denR = Math.sqrt(((n * sumX2) - (sumX * sumX)) * ((n * sumY2) - (sumY * sumY)));
            this.r = denR == 0 ? 0 : numR / denR;
            this.rSquared = this.r * this.r;

            // Mean Squared Error (MSE)
            double sumSquaredError = 0;
            for (DataPoint dp : data) {
                double predictedY = predict(dp.x);
                double error = dp.y - predictedY;
                sumSquaredError += error * error;
            }
            this.mse = sumSquaredError / n;
        }

        public double predict(double x) {
            return (slope * x) + intercept;
        }

        public double getSlope() {
            return slope;
        }

        public double getIntercept() {
            return intercept;
        }

        public double getR() {
            return r;
        }

        public double getRSquared() {
            return rSquared;
        }

        public double getMse() {
            return mse;
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      PROGRAM 11: REGRESSION ANALYSIS IN JAVA    ");
        System.out.println("=================================================\n");

        // Sample Dataset: Study Hours (X) vs. Exam Score (Y)
        List<DataPoint> dataset = new ArrayList<>();
        dataset.add(new DataPoint(1.0, 45.0));
        dataset.add(new DataPoint(2.0, 50.0));
        dataset.add(new DataPoint(3.0, 65.0));
        dataset.add(new DataPoint(4.0, 70.0));
        dataset.add(new DataPoint(5.0, 78.0));
        dataset.add(new DataPoint(6.0, 85.0));
        dataset.add(new DataPoint(7.0, 88.0));
        dataset.add(new DataPoint(8.0, 95.0));

        System.out.println("Training Dataset (Hours Studied -> Exam Score):");
        for (DataPoint dp : dataset) {
            System.out.printf("  Hours: %4.1f | Score: %5.1f\n", dp.x, dp.y);
        }

        // Train Linear Regression Model
        LinearRegressionModel model = new LinearRegressionModel(dataset);

        System.out.println("\n-------------------------------------------------");
        System.out.println("Model Parameters & Performance Metrics:");
        System.out.println("-------------------------------------------------");
        System.out.printf("  Regression Equation       : y = %.4f * x + %.4f\n", model.getSlope(), model.getIntercept());
        System.out.printf("  Slope (m)                 : %.4f\n", model.getSlope());
        System.out.printf("  Y-Intercept (c)           : %.4f\n", model.getIntercept());
        System.out.printf("  Pearson Correlation (r)   : %.4f\n", model.getR());
        System.out.printf("  R-Squared (R^2)           : %.4f (%.2f%% variance explained)\n", 
                model.getRSquared(), model.getRSquared() * 100);
        System.out.printf("  Mean Squared Error (MSE)  : %.4f\n", model.getMse());

        System.out.println("\n-------------------------------------------------");
        System.out.println("Predictions for New Input Data:");
        System.out.println("-------------------------------------------------");
        double[] testHours = { 2.5, 4.5, 6.5, 9.0, 10.0 };
        for (double hours : testHours) {
            double predictedScore = model.predict(hours);
            // Cap score at 100 max for practical interpretation
            double boundedScore = Math.min(100.0, predictedScore);
            System.out.printf("  Study Hours: %4.1f hrs --> Predicted Score: %5.2f (Bounded: %5.2f)\n", 
                    hours, predictedScore, boundedScore);
        }
        System.out.println("=================================================");
    }
}
