
import java.util.*;

public class Searching {
    public static void main(String[] args) {
        int[] a = { 10, 20, 30, 40, 50 };
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter element: ");
        int key = sc.nextInt();

        // Linear Search
        int i;
        for (i = 0; i < a.length; i++)
            if (a[i] == key)
                break;

        System.out.println(i < a.length ? "Linear: Found" : "Linear: Not Found");

        // Binary Search
        int pos = Arrays.binarySearch(a, key);
        System.out.println(pos >= 0 ? "Binary: Found" : "Binary: Not Found");

        sc.close();
    }
}