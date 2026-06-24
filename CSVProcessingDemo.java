import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessingDemo {

    static class Student {
        String name;
        int age;
        String course;

        Student(String name, int age, String course) {
            this.name = name;
            this.age = age;
            this.course = course;
        }

        @Override
        public String toString() {
            return name + ", age=" + age + ", course=" + course;
        }
    }

    // Reads a CSV file and converts each row into a Student object.
    // Time complexity: O(n) for n rows, Space complexity: O(n) for storing records.
    public static List<Student> readStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    students.add(new Student(parts[0], Integer.parseInt(parts[1]), parts[2]));
                }
            }
        }

        return students;
    }

    public static void main(String[] args) {
        String filePath = "students.csv";

        try {
            List<Student> students = readStudents(filePath);
            System.out.println("Students loaded from CSV:");
            for (Student student : students) {
                System.out.println(student);
            }
        } catch (IOException e) {
            System.out.println("CSV read failed: " + e.getMessage());
        }
    }
}
