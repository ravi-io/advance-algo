import java.util.Arrays;

public class SortingAlgorithms {

    // Merge Sort: O(n log n) time, O(n) space
    public static void mergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    private static void merge(int[] arr, int l, int mid, int r) {
        int n1 = mid - l + 1, n2 = r - mid;
        int[] L = Arrays.copyOfRange(arr, l, mid + 1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, r + 1);
        int i = 0, j = 0, k = l;

        while (i < n1 && j < n2) {
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        }

        while (i < n1) {
            arr[k++] = L[i++];
        }

        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    // Quick Sort: O(n log n) avg, O(log n) space
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high], i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }

        int t = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = t;
        return i + 1;
    }

    // Heap Sort: O(n log n) time, O(1) space
    public static void heapSort(int[] arr) {
        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            int t = arr[0];
            arr[0] = arr[i];
            arr[i] = t;
            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;

        if (l < n && arr[l] > arr[largest]) {
            largest = l;
        }

        if (r < n && arr[r] > arr[largest]) {
            largest = r;
        }

        if (largest != i) {
            int t = arr[i];
            arr[i] = arr[largest];
            arr[largest] = t;
            heapify(arr, n, largest);
        }
    }

    public static void main(String[] args) {
        int[] data = {64, 25, 12, 22, 11, 90, 45, 7};
        System.out.println("Original: " + Arrays.toString(data));

        int[] a1 = data.clone();
        mergeSort(a1, 0, a1.length - 1);
        System.out.println("Merge Sort: " + Arrays.toString(a1));

        int[] a2 = data.clone();
        quickSort(a2, 0, a2.length - 1);
        System.out.println("Quick Sort: " + Arrays.toString(a2));

        int[] a3 = data.clone();
        heapSort(a3);
        System.out.println("Heap Sort: " + Arrays.toString(a3));
    }
}