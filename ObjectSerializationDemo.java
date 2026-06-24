import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializationDemo {

    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final int id;

        Employee(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Employee{id=" + id + ", name='" + name + "'}";
        }
    }

    // Serialization and deserialization of a single object.
    // Time complexity: O(1) for one object, Space complexity: O(1) excluding file storage.
    public static void serializeEmployee(String filePath, Employee employee) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(employee);
        }
    }

    public static Employee deserializeEmployee(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Employee) in.readObject();
        }
    }

    public static void main(String[] args) {
        String filePath = "employee.ser";
        Employee employee = new Employee("Ravi", 101);

        try {
            serializeEmployee(filePath, employee);
            Employee restored = deserializeEmployee(filePath);
            System.out.println("Serialized object: " + employee);
            System.out.println("Deserialized object: " + restored);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Serialization failed: " + e.getMessage());
        }
    }
}
