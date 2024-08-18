import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RobotDelivery {

    public static final int ROUTE_LENGTH = 100;
    public static final int NUM_ROUTES = 1000;
    public static final String COMMANDS = "RLRFR";

    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_ROUTES);

        for (int i = 0; i < NUM_ROUTES; i++) {
            executor.submit(() -> {
                String route = generateRoute(COMMANDS, ROUTE_LENGTH);
                int rCount = (int) route.chars().filter(ch -> ch == 'R').count();

                synchronized (sizeToFreq) {
                    sizeToFreq.put(rCount, sizeToFreq.getOrDefault(rCount, 0) + 1);
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Ждем завершения всех потоков
        }

        int maxFrequency = sizeToFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
        int maxFrequencyCount = sizeToFreq.get(maxFrequency);

        System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + maxFrequencyCount + " раз)");
        System.out.println("Другие размеры:");
        sizeToFreq.entrySet().stream()
                .filter(entry -> entry.getKey() != maxFrequency)
                .forEach(entry -> System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)"));
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
