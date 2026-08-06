import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessingDemo {

    static class Student {
        String name;
        String email;
        String program;
        double gpa;

        Student(String name, String email, String program, double gpa) {
            this.name = name;
            this.email = email;
            this.program = program;
            this.gpa = gpa;
        }

        @Override
        public String toString() {
            return name + ", email=" + email + ", program=" + program + ", gpa=" + gpa;
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
                if (parts.length >= 5) {
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String program = parts[3].trim();
                    double gpa = Double.parseDouble(parts[4].trim());
                    students.add(new Student(name, email, program, gpa));
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
