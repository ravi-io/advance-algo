import java.util.Scanner;

public class SentimentAnalysis {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] positive = { "good", "nice", "rich", "happy", "love" };
        String[] negative = { "bad", "poor", "sad", "hate" };

        System.out.print("Enter a sentence: ");
        String text = sc.nextLine().toLowerCase();

        int score = 0;

        for (String word : positive) {
            if (text.contains(word))
                score++;
        }

        for (String word : negative) {
            if (text.contains(word))
                score--;
        }

        if (score > 0)
            System.out.println("Sentiment : Positive");
        else if (score < 0)
            System.out.println("Sentment : Negative");
        else
            System.out.println("Sentiment : Neutral");

        sc.close();
    }
}