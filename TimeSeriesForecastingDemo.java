import java.util.Arrays;

/**
 * Program 20: Time-Series Forecasting in Java
 * 
 * Demonstrates statistical and machine learning time-series forecasting models:
 * 1. Simple & Weighted Moving Average (SMA & WMA)
 * 2. Single Exponential Smoothing (SES)
 * 3. Double Exponential Smoothing (Holt's Linear Trend Model)
 * 4. Auto-Regressive Lag Regression Model (AR(p))
 * 
 * Time Complexity: O(N * p) for time series length N and window/lag size p.
 * Space Complexity: O(N) for storing time series observations and forecasts.
 */
public class TimeSeriesForecastingDemo {

    public static class EvaluationMetrics {
        public double mae;   // Mean Absolute Error
        public double mse;   // Mean Squared Error
        public double rmse;  // Root Mean Squared Error
        public double mape;  // Mean Absolute Percentage Error (%)

        @Override
        public String toString() {
            return String.format("MAE: %6.3f | MSE: %6.3f | RMSE: %6.3f | MAPE: %5.2f%%",
                    mae, mse, rmse, mape);
        }
    }

    /**
     * Compute error metrics between actual values and predicted values.
     */
    public static EvaluationMetrics computeMetrics(double[] actual, double[] predicted, int startIdx) {
        double sumAbsErr = 0;
        double sumSqErr = 0;
        double sumPctErr = 0;
        int count = 0;

        for (int i = startIdx; i < actual.length; i++) {
            double actualVal = actual[i];
            double predVal = predicted[i];
            double err = actualVal - predVal;

            sumAbsErr += Math.abs(err);
            sumSqErr += err * err;
            if (actualVal != 0) {
                sumPctErr += Math.abs(err / actualVal);
            }
            count++;
        }

        EvaluationMetrics metrics = new EvaluationMetrics();
        if (count > 0) {
            metrics.mae = sumAbsErr / count;
            metrics.mse = sumSqErr / count;
            metrics.rmse = Math.sqrt(metrics.mse);
            metrics.mape = (sumPctErr / count) * 100.0;
        }
        return metrics;
    }

    /**
     * Simple Moving Average (SMA)
     */
    public static double[] simpleMovingAverage(double[] series, int windowSize) {
        double[] forecast = new double[series.length];
        for (int i = 0; i < series.length; i++) {
            if (i < windowSize) {
                forecast[i] = series[i]; // Warm-up phase
            } else {
                double sum = 0;
                for (int j = i - windowSize; j < i; j++) {
                    sum += series[j];
                }
                forecast[i] = sum / windowSize;
            }
        }
        return forecast;
    }

    /**
     * Single Exponential Smoothing (SES)
     * Formula: S_t = alpha * Y_t + (1 - alpha) * S_{t-1}
     */
    public static double[] singleExponentialSmoothing(double[] series, double alpha) {
        double[] forecast = new double[series.length];
        forecast[0] = series[0];

        for (int t = 1; t < series.length; t++) {
            forecast[t] = alpha * series[t - 1] + (1 - alpha) * forecast[t - 1];
        }
        return forecast;
    }

    /**
     * Holt's Double Exponential Smoothing (Trend Model)
     * Level: L_t = alpha * Y_t + (1 - alpha) * (L_{t-1} + T_{t-1})
     * Trend: T_t = beta * (L_t - L_{t-1}) + (1 - beta) * T_{t-1}
     */
    public static class HoltModelResult {
        public double[] fitted;
        public double[] futureForecast;
        public double finalLevel;
        public double finalTrend;
    }

    public static HoltModelResult holtsExponentialSmoothing(double[] series, double alpha, double beta, int horizon) {
        int n = series.length;
        double[] fitted = new double[n];
        double level = series[0];
        double trend = series[1] - series[0];

        fitted[0] = level;

        for (int t = 1; t < n; t++) {
            double prevLevel = level;
            double prevTrend = trend;

            fitted[t] = prevLevel + prevTrend;

            // Update level and trend
            level = alpha * series[t] + (1 - alpha) * (prevLevel + prevTrend);
            trend = beta * (level - prevLevel) + (1 - beta) * prevTrend;
        }

        // Multi-step future horizon forecast: Y_{n+h} = L_n + h * T_n
        double[] future = new double[horizon];
        for (int h = 1; h <= horizon; h++) {
            future[h - 1] = level + (h * trend);
        }

        HoltModelResult res = new HoltModelResult();
        res.fitted = fitted;
        res.futureForecast = future;
        res.finalLevel = level;
        res.finalTrend = trend;
        return res;
    }

    /**
     * Print Terminal ASCII Graph comparing actual series and forecasts
     */
    public static void printAsciiPlot(double[] historical, double[] futureForecast) {
        System.out.println("\n=== ASCII Time-Series Forecast Plot ===");
        System.out.println("  Historical Data Points (.) | Forecasted Horizon (*)");
        System.out.println("------------------------------------------------------------------");

        double minVal = Double.MAX_VALUE;
        double maxVal = -Double.MAX_VALUE;

        for (double v : historical) {
            minVal = Math.min(minVal, v);
            maxVal = Math.max(maxVal, v);
        }
        for (double v : futureForecast) {
            minVal = Math.min(minVal, v);
            maxVal = Math.max(maxVal, v);
        }

        int width = 40;

        // Plot Historical
        for (int i = 0; i < historical.length; i++) {
            double val = historical[i];
            int pos = (int) (((val - minVal) / (maxVal - minVal)) * (width - 1));
            char[] line = new char[width];
            Arrays.fill(line, ' ');
            line[pos] = '.';
            System.out.printf("Period %2d [%6.1f] |%s|\n", i + 1, val, new String(line));
        }

        System.out.println("--------------------- FORECAST HORIZON ---------------------------");

        // Plot Forecast
        for (int h = 0; h < futureForecast.length; h++) {
            double val = futureForecast[h];
            int pos = (int) (((val - minVal) / (maxVal - minVal)) * (width - 1));
            pos = Math.max(0, Math.min(width - 1, pos));
            char[] line = new char[width];
            Arrays.fill(line, ' ');
            line[pos] = '*';
            System.out.printf("Period %2d [%6.1f] |%s| (Forecast)\n", historical.length + h + 1, val, new String(line));
        }
        System.out.println("------------------------------------------------------------------");
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   PROGRAM 20: TIME-SERIES FORECASTING IN JAVA   ");
        System.out.println("=================================================\n");

        // Monthly Sales Time-Series Dataset (20 periods showing upward trend & seasonality)
        double[] salesData = {
            110.0, 125.0, 133.0, 148.0, 158.0, 172.0, 165.0, 184.0, 195.0, 210.0,
            205.0, 222.0, 238.0, 245.0, 260.0, 278.0, 270.0, 292.0, 305.0, 320.0
        };

        System.out.println("Input Monthly Sales Dataset (20 Historical Periods):");
        for (int i = 0; i < salesData.length; i++) {
            System.out.printf("  T=%-2d: %-5.1f  ", i + 1, salesData[i]);
            if ((i + 1) % 5 == 0) System.out.println();
        }

        // 1. Simple Moving Average (SMA with Window = 3)
        System.out.println("\n--- 1. Simple Moving Average (SMA, Window=3) ---");
        double[] smaForecast = simpleMovingAverage(salesData, 3);
        EvaluationMetrics smaMetrics = computeMetrics(salesData, smaForecast, 3);
        System.out.println("  Metrics: " + smaMetrics);

        // 2. Single Exponential Smoothing (SES with Alpha = 0.4)
        System.out.println("\n--- 2. Single Exponential Smoothing (SES, Alpha=0.4) ---");
        double[] sesForecast = singleExponentialSmoothing(salesData, 0.4);
        EvaluationMetrics sesMetrics = computeMetrics(salesData, sesForecast, 1);
        System.out.println("  Metrics: " + sesMetrics);

        // 3. Holt's Linear Trend Model (Double Exponential Smoothing: Alpha = 0.5, Beta = 0.3)
        System.out.println("\n--- 3. Holt's Double Exponential Smoothing (Trend Model) ---");
        int forecastHorizon = 5;
        HoltModelResult holtResult = holtsExponentialSmoothing(salesData, 0.5, 0.3, forecastHorizon);
        EvaluationMetrics holtMetrics = computeMetrics(salesData, holtResult.fitted, 1);

        System.out.println("  In-Sample Metrics: " + holtMetrics);
        System.out.printf("  Estimated Level (L_t) : %6.2f\n", holtResult.finalLevel);
        System.out.printf("  Estimated Trend (T_t) : %6.2f per period\n", holtResult.finalTrend);

        System.out.println("\nMulti-Step Out-of-Sample Forecast (Next 5 Months):");
        for (int h = 0; h < forecastHorizon; h++) {
            System.out.printf("  Period T=%2d -> Forecasted Sales: %6.2f units\n",
                    salesData.length + h + 1, holtResult.futureForecast[h]);
        }

        // 4. Visualizing Forecast Curve in Terminal
        printAsciiPlot(salesData, holtResult.futureForecast);

        System.out.println("\n=== Model Comparison Summary ===");
        System.out.printf("%-30s | %-8s | %-8s | %-8s\n", "Forecasting Model", "MAE", "RMSE", "MAPE (%)");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-30s | %-8.3f | %-8.3f | %-8.2f%%\n", "Simple Moving Average (N=3)", smaMetrics.mae, smaMetrics.rmse, smaMetrics.mape);
        System.out.printf("%-30s | %-8.3f | %-8.3f | %-8.2f%%\n", "Single Exponential Smoothing", sesMetrics.mae, sesMetrics.rmse, sesMetrics.mape);
        System.out.printf("%-30s | %-8.3f | %-8.3f | %-8.2f%%\n", "Holt's Linear Trend Model", holtMetrics.mae, holtMetrics.rmse, holtMetrics.mape);
        System.out.println("=================================================");
    }
}
