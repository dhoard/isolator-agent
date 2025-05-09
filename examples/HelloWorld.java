public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!!!");

        // Block forever
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // Exit on interrupt
            System.out.println("Interrupted. Exiting...");
        }
    }
}
