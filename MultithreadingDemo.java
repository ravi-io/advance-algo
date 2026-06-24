public class MultithreadingDemo {

    // Each thread performs a simple loop; time complexity is O(n) per thread, space O(1)
    static class Task implements Runnable {
        private final String name;
        private final int count;

        public Task(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 1; i <= count; i++) {
                System.out.println(name + " -> " + i);
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new Task("Thread-A", 5));
        Thread t2 = new Thread(new Task("Thread-B", 5));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        System.out.println("Multithreading demo completed.");
    }
}
