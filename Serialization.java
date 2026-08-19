import java.io.*;

// Serializable Student class
class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String course;
    private double marks;

    // Constructor
    public Student(int id, String name, String course, double marks) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.marks = marks;
    }

    // Display student details
    public void display() {
        System.out.println("Student ID : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Course     : " + course);
        System.out.println("Marks      : " + marks);
    }
}

public class Serialization {

    public static void main(String[] args) {

        String fileName = "student.ser";

        // ---------------- SERIALIZATION ----------------
        Student Student = new Student(
                101,
                "Jhn",
                "MCA",
                89.5
        );

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(fileName);

            ObjectOutputStream objectOut =
                    new ObjectOutputStream(fileOut);

            // Convert object into byte stream
            objectOut.writeObject(Student);

            objectOut.close();
            fileOut.close();

            System.out.println("Object Serialization Successful!");
            System.out.println("Object saved in: " + fileName);

        } catch (IOException e) {
            System.out.println("Serialization Error: "
                    + e.getMessage());
        }

        // ---------------- DESERIALIZATION ----------------
        try {
            FileInputStream fileIn =
                    new FileInputStream(fileName);

            ObjectInputStream objectIn =
                    new ObjectInputStream(fileIn);

            // Convert byte stream back into object
            Student restoredStudent =
                    (Student) objectIn.readObject();

            objectIn.close();
            fileIn.close();

            System.out.println("\nObject Deserialization Successful!");
            System.out.println("\nRestored Student Details:");
            System.out.println("-------------------------");

            restoredStudent.display();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Deserialization Error: "
                    + e.getMessage());
        }
    }
}