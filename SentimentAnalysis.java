import java.util.Scanner;

public class SentimentAnalysis {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] positive = { "good", "rich" };
        String[] negative = { "bad", "poor" };

        int score = 0;

        System.out.print("Enter a Sentence: ");
        String text = sc.nextLine().toLowerCase();

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
            System.out.println("Sentiment : Negative");
        else
            System.out.println("Sentiment : Neutral");

        sc.close();
    }
}