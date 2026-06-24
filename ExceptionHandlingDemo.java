public class ExceptionHandlingDemo {

    // Custom exception for invalid age input
    static class InvalidAgeException extends Exception {
        public InvalidAgeException(String message) {
            super(message);
        }
    }

    // Validation logic: O(1) time, O(1) space
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be 18 or above.");
        }
    }

    public static void main(String[] args) {
        try {
            int numerator = 10;
            int denominator = 0;
            int result = numerator / denominator; // ArithmeticException
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Caught arithmetic error: " + e.getMessage());
        } finally {
            System.out.println("This block always executes.");
        }

        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
        }
    }
}
