# ☕ Advanced Java Lab Programs

A professional and well-structured Java lab repository for MCA Semester 2, covering core advanced Java concepts and advanced algorithms with clean code, comments, and complexity notes.

![Java](https://img.shields.io/badge/Java-21%2B-orange?logo=java)
![GitHub](https://img.shields.io/badge/GitHub-Repository-black?logo=github)
![License](https://img.shields.io/badge/License-Free-green)

---

## 📌 Project Overview

This repository contains **15 practical Java programs** focused on:

- File Handling
- Sorting Algorithms
- Searching Techniques
- Matrix Operations
- Exception Handling
- Multithreading
- Collections Framework
- JDBC Connectivity
- CSV Processing
- Serialization and Deserialization
- Regression Analysis
- Clustering Algorithms (K-Means)
- Association Rule Mining (Apriori Algorithm)
- Text Mining (TF-IDF & Cosine Similarity)
- Data Visualization using JavaFX

---

## 📁 Program List

| No. | Topic                    | File                                                               |
| --- | ------------------------ | ------------------------------------------------------------------ |
| 1   | File Handling            | [FileHandling.java](FileHandling.java)                             |
| 2   | Sorting Algorithms       | [SortingAlgorithms.java](SortingAlgorithms.java)                   |
| 3   | Searching Techniques     | [SearchingTechniques.java](SearchingTechniques.java)               |
| 4   | Matrix Operations        | [MatrixOperations.java](MatrixOperations.java)                     |
| 5   | Exception Handling       | [ExceptionHandlingDemo.java](ExceptionHandlingDemo.java)           |
| 6   | Multithreading           | [MultithreadingDemo.java](MultithreadingDemo.java)                 |
| 7   | Java Collections         | [JavaCollectionsDemo.java](JavaCollectionsDemo.java)               |
| 8   | JDBC Connectivity        | [JDBCConnectivityDemo.java](JDBCConnectivityDemo.java)             |
| 9   | CSV Processing           | [CSVProcessingDemo.java](CSVProcessingDemo.java)                   |
| 10  | Object Serialization     | [ObjectSerializationDemo.java](ObjectSerializationDemo.java)       |
| 11  | Regression Analysis      | [RegressionAnalysisDemo.java](RegressionAnalysisDemo.java)         |
| 12  | Clustering               | [ClusteringDemo.java](ClusteringDemo.java)                         |
| 13  | Association Rule Mining  | [AssociationRuleMiningDemo.java](AssociationRuleMiningDemo.java)   |
| 14  | Text Mining              | [TextMiningDemo.java](TextMiningDemo.java)                         |
| 15  | JavaFX Data Visualization| [VisualizationJavaFXDemo.java](VisualizationJavaFXDemo.java)       |

---

## ▶️ How to Run

### 1. Compile programs

Standard Java programs (1–14):
```bash
javac FileHandling.java SortingAlgorithms.java SearchingTechniques.java MatrixOperations.java ExceptionHandlingDemo.java MultithreadingDemo.java JavaCollectionsDemo.java JDBCConnectivityDemo.java CSVProcessingDemo.java ObjectSerializationDemo.java RegressionAnalysisDemo.java ClusteringDemo.java AssociationRuleMiningDemo.java TextMiningDemo.java
```

JavaFX Visualization program (15):
```bash
# Standard compilation:
javac VisualizationJavaFXDemo.java

# If OpenJFX modules are configured separately:
javac --module-path /path/to/javafx/lib --add-modules javafx.controls VisualizationJavaFXDemo.java
```

### 2. Run any program

```bash
java FileHandling
java SortingAlgorithms
java SearchingTechniques
java MatrixOperations
java ExceptionHandlingDemo
java MultithreadingDemo
java JavaCollectionsDemo
java JDBCConnectivityDemo
java CSVProcessingDemo
java ObjectSerializationDemo
java RegressionAnalysisDemo
java ClusteringDemo
java AssociationRuleMiningDemo
java TextMiningDemo
java VisualizationJavaFXDemo
```

---

## 🧠 Program Details & Algorithm Notes

1. **Regression Analysis ([RegressionAnalysisDemo.java](RegressionAnalysisDemo.java))**:
   - Calculates Simple Linear Regression equation ($y = m \cdot x + c$), Slope ($m$), Intercept ($c$), Pearson Correlation Coefficient ($r$), Coefficient of Determination ($R^2$), and Mean Squared Error (MSE).
   - Time Complexity: $O(N)$, Space Complexity: $O(N)$.

2. **Clustering ([ClusteringDemo.java](ClusteringDemo.java))**:
   - Implements K-Means Clustering for 2D spatial points with iterative centroid updates, convergence checking, and Within-Cluster Sum of Squares (WCSS / Inertia) calculation.
   - Time Complexity: $O(K \cdot N \cdot I)$, Space Complexity: $O(N + K)$.

3. **Association Rule Mining ([AssociationRuleMiningDemo.java](AssociationRuleMiningDemo.java))**:
   - Implements the Apriori Algorithm for mining frequent itemsets and generating association rules ($A \Rightarrow B$) based on Minimum Support and Minimum Confidence thresholds.
   - Computes Support, Confidence, and Lift metrics.
   - Time Complexity: $O(2^D \cdot N)$ worst-case (bounded by candidate pruning), Space Complexity: $O(2^D)$.

4. **Text Mining ([TextMiningDemo.java](TextMiningDemo.java))**:
   - Implements text preprocessing (tokenization, stopword removal), Term Frequency (TF), Inverse Document Frequency (IDF), TF-IDF vector space matrix, and Cosine Similarity search.
   - Time Complexity: $O(D \cdot W)$ preprocessing, $O(V \cdot D)$ matrix indexing, Space Complexity: $O(V \cdot D)$.

5. **Data Visualization ([VisualizationJavaFXDemo.java](VisualizationJavaFXDemo.java))**:
   - JavaFX GUI application rendering interactive `BarChart` (algorithm execution speeds) and `LineChart` (time complexity growth curves $O(N \log N)$ vs $O(N^2)$). Includes console summary fallback.
   - Time Complexity: $O(N)$, Space Complexity: $O(N)$.

---

## ✨ Author

Ravi Karmakar ❤️
