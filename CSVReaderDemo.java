import java.io.*;

public class CSVReaderDemo {

    public static void main(String[] args) {

        String file = "students.csv";
        String line;

        int totalMarks = 0;
        int count = 0;

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));

            // Skip header
            br.readLine();

            System.out.println("Student Details");
            System.out.println("--------------------------------");

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                int id = Integer.parseInt(data[0]);
                String name = data[1];
                int marks = Integer.parseInt(data[2]);

                System.out.println("ID    : " + id);
                System.out.println("Name  : " + name);
                System.out.println("Marks : " + marks);
                System.out.println("--------------------------------");

                totalMarks += marks;
                count++;
            }

            br.close();

            if (count > 0) {
                double average = (double) totalMarks / count;

                System.out.println("Total Students : " + count);
                System.out.println("Total Marks    : " + totalMarks);
                System.out.println("Average Marks  : " + average);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}