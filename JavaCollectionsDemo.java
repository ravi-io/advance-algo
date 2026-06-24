import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaCollectionsDemo {

    public static void main(String[] args) {
        // List: O(1) average insertion at end, O(n) for search
        List<String> names = new ArrayList<>();
        names.add("Ravi");
        names.add("Asha");
        names.add("Ravi");
        System.out.println("List: " + names);

        // Set: O(1) average insert/search, stores unique values
        Set<String> uniqueNames = new HashSet<>(names);
        System.out.println("Set: " + uniqueNames);

        // Map: O(1) average lookup by key
        Map<String, Integer> marks = new HashMap<>();
        marks.put("Ravi", 90);
        marks.put("Asha", 85);
        marks.put("Kiran", 88);
        System.out.println("Map: " + marks);
        System.out.println("Ravi's marks: " + marks.get("Ravi"));
    }
}
