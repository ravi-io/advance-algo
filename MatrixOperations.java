import java.util.Arrays;

public class MatrixOperations {

    // Matrix addition: O(m*n) time, O(m*n) space
    public static int[][] addMatrices(int[][] a, int[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        int rows = a.length;
        int cols = a[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    // Matrix subtraction: O(m*n) time, O(m*n) space
    public static int[][] subtractMatrices(int[][] a, int[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        int rows = a.length;
        int cols = a[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    // Matrix multiplication: O(p*q*r) time, O(p*r) space for p x q and q x r matrices
    public static int[][] multiplyMatrices(int[][] a, int[][] b) {
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Column count of first matrix must match row count of second matrix.");
        }

        int rows = a.length;
        int cols = b[0].length;
        int inner = a[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < inner; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    // Matrix transpose: O(m*n) time, O(m*n) space
    public static int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] transposed = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        int[][] a = {{1, 2, 3}, {4, 5, 6}};
        int[][] b = {{7, 8}, {9, 10}, {11, 12}};

        System.out.println("Matrix A:");
        printMatrix(a);
        System.out.println("\nMatrix B:");
        printMatrix(b);

        System.out.println("\nMatrix Addition:");
        printMatrix(addMatrices(new int[][]{{1, 2}, {3, 4}}, new int[][]{{5, 6}, {7, 8}}));

        System.out.println("\nMatrix Multiplication:");
        printMatrix(multiplyMatrices(a, b));

        System.out.println("\nTranspose of Matrix A:");
        printMatrix(transposeMatrix(a));
    }
}
