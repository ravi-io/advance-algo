public class SearchingTechniques {

    // Linear Search: O(n) time, O(1) extra space
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // Binary Search: O(log n) time, O(1) extra space
    // Works only on sorted arrays.
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] data = {10, 20, 30, 40, 50};

        System.out.println("Linear Search -> 30 found at index: " + linearSearch(data, 30));
        System.out.println("Binary Search -> 40 found at index: " + binarySearch(data, 40));
        System.out.println("Binary Search -> 99 found at index: " + binarySearch(data, 99));
    }
}
