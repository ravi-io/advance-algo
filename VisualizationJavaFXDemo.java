import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Program 15: Data Visualization using JavaFX
 * 
 * Creates an interactive JavaFX Graphical Interface visualizing algorithm benchmark statistics:
 * 1. BarChart: Execution speed comparison (in milliseconds) across algorithms.
 * 2. LineChart: Growth curve comparison (Complexity scaling vs Dataset size N).
 * 
 * Time Complexity: O(N) for chart rendering and dataset binding.
 * Space Complexity: O(N) for storing UI node hierarchies and series data.
 * 
 * Note: Compile and Run with OpenJFX modules:
 *   javac --module-path /path/to/javafx/lib --add-modules javafx.controls VisualizationJavaFXDemo.java
 *   java --module-path /path/to/javafx/lib --add-modules javafx.controls VisualizationJavaFXDemo
 */
public class VisualizationJavaFXDemo extends Application {

    // Sample Benchmark Data: Algorithm Name -> Execution Time (ms)
    private static final Map<String, Double> BENCHMARK_DATA = new LinkedHashMap<>();
    
    static {
        BENCHMARK_DATA.put("Quick Sort", 12.4);
        BENCHMARK_DATA.put("Merge Sort", 14.8);
        BENCHMARK_DATA.put("Heap Sort", 18.2);
        BENCHMARK_DATA.put("Insertion Sort", 145.0);
        BENCHMARK_DATA.put("Bubble Sort", 230.5);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Advanced Algorithms - JavaFX Data Visualization");

        // 1. Dashboard Header
        Label header = new Label("📊 Advanced Algorithms Benchmark & Visualization");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#1E293B"));
        header.setPadding(new Insets(10, 0, 15, 0));

        // 2. Build Bar Chart (Algorithm Execution Speed)
        CategoryAxis xAxisBar = new CategoryAxis();
        xAxisBar.setLabel("Algorithm");

        NumberAxis yAxisBar = new NumberAxis();
        yAxisBar.setLabel("Execution Time (ms)");

        BarChart<String, Number> barChart = new BarChart<>(xAxisBar, yAxisBar);
        barChart.setTitle("Execution Time Comparison (N = 100,000)");
        barChart.setLegendVisible(false);
        barChart.setPrefWidth(480);
        barChart.setPrefHeight(380);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : BENCHMARK_DATA.entrySet()) {
            barSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(barSeries);

        // 3. Build Line Chart (Algorithm Complexity Growth Curve)
        NumberAxis xAxisLine = new NumberAxis(0, 100, 20);
        xAxisLine.setLabel("Dataset Size N (x10^3)");

        NumberAxis yAxisLine = new NumberAxis(0, 10000, 2000);
        yAxisLine.setLabel("Operations Count");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxisLine, yAxisLine);
        lineChart.setTitle("Growth Rate Scaling O(N log N) vs O(N^2)");
        lineChart.setPrefWidth(480);
        lineChart.setPrefHeight(380);

        // Series 1: O(N log N) - QuickSort / MergeSort
        XYChart.Series<Number, Number> nLogNSeries = new XYChart.Series<>();
        nLogNSeries.setName("O(N log N) - Fast Sorts");
        for (int n = 10; n <= 100; n += 10) {
            double ops = n * (Math.log(n) / Math.log(2)) * 10;
            nLogNSeries.getData().add(new XYChart.Data<>(n, ops));
        }

        // Series 2: O(N^2) - BubbleSort / InsertionSort
        XYChart.Series<Number, Number> nSquareSeries = new XYChart.Series<>();
        nSquareSeries.setName("O(N^2) - Simple Sorts");
        for (int n = 10; n <= 100; n += 10) {
            double ops = n * n;
            nSquareSeries.getData().add(new XYChart.Data<>(n, ops));
        }

        lineChart.getData().add(nLogNSeries);
        lineChart.getData().add(nSquareSeries);

        // 4. Layout Assembly
        HBox chartsLayout = new HBox(20, barChart, lineChart);
        chartsLayout.setAlignment(Pos.CENTER);
        chartsLayout.setPadding(new Insets(10));

        VBox root = new VBox(15, header, chartsLayout);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F8FAFC;");

        Scene scene = new Scene(root, 1020, 520);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Prints ASCII visual representations in standard console
    private static void printConsoleSummary() {
        System.out.println("=================================================");
        System.out.println("     PROGRAM 15: VISUALIZATION USING JAVAFX      ");
        System.out.println("=================================================\n");
        System.out.println("Data Summary (Algorithm Execution Time in ms):");
        System.out.println("-------------------------------------------------");

        double maxVal = 250.0;
        for (Map.Entry<String, Double> entry : BENCHMARK_DATA.entrySet()) {
            String name = entry.getKey();
            double val = entry.getValue();
            int barLength = (int) ((val / maxVal) * 35);
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < barLength; i++) {
                bar.append("█");
            }
            System.out.printf("  %-15s | %-35s %6.1f ms\n", name, bar.toString(), val);
        }
        System.out.println("-------------------------------------------------");
        System.out.println("Attempting to launch JavaFX GUI Application Window...");
    }

    public static void main(String[] args) {
        printConsoleSummary();
        try {
            Application.launch(args);
        } catch (NoClassDefFoundError | UnsupportedOperationException e) {
            System.out.println("\n[Note] JavaFX graphical environment runtime is not available in this environment.");
            System.out.println("  Reason: " + e.getMessage());
            System.out.println("  The console text visualization above displays the benchmark dataset.");
        } catch (Exception e) {
            System.out.println("\nJavaFX execution note: " + e.getMessage());
        }
    }
}
