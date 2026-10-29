import java.util.Arrays;

public class SearchDemo {
    public static void main(String[] args) {
        int[] arr = { 10, 20, 30, 40, 50 };
        int target = 30;

        // 1. Linear Search
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target)
                System.out.println("Linear: Found at index " + i);
        }

        // 2. Binary Search (built-in)
        int index = Arrays.binarySearch(arr, target);
        System.out.println("Binary: Found at index " + index);
    }
}