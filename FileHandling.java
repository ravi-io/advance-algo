import java.io.*;
import java.nio.file.*;

public class FileHandling {
    // Write content to a file
    public static void writeFile(String path, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(content);
        }
    }

    // Read entire file and return content
    public static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }

    // Append content to an existing file
    public static void appendFile(String path, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(content);
        }
    }

    // Copy file using NIO
    public static void copyFile(String src, String dest) throws IOException {
        Files.copy(Path.of(src), Path.of(dest), StandardCopyOption.REPLACE_EXISTING);
    }

    // Delete a file safely
    // public static boolean deleteFile(String path) {
    //     return new File(path).delete();
    // }

    public static void main(String[] args) {
        String fileName = "demo.txt";
        try {
            // Write
            writeFile(fileName, "Hello, File Handling in Java!\nLine 2: Java I/O");
            System.out.println("[WRITE] File written successfully.");

            // Read
            System.out.println("[READ] Content:\n" + readFile(fileName));

            // Append
            appendFile(fileName, "\nLine 3: Appended text.");
            System.out.println("[APPEND] Content after append:\n" + readFile(fileName));

            // // Copy
            copyFile(fileName, "demo_copy.txt");
            System.out.println("[COPY] File copied to demo_copy.txt");

            // Delete
            // System.out.println("[DELETE] demo.txt deleted: " + deleteFile(fileName));
            // System.out.println("[DELETE] demo_copy.txt deleted: " + deleteFile("demo_copy.txt"));
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}