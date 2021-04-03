package com.asmelo.main;

import com.asmelo.labs.Product;
import com.asmelo.labs.ProductManager;
import com.asmelo.labs.Rateable;
import com.asmelo.labs.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.asmelo.labs.Rating.*;

public class Main {

    public static void main(String[] args) {

        AtomicInteger clientCount = new AtomicInteger();

        Callable<String> client = () -> {
            ProductManager pm = ProductManager.getInstance();
            String clientId = "Client " + clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt(4);

            String languageTag = ProductManager.getSupportedLanguages()
                    .stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst()
                    .get();

            StringBuilder log = new StringBuilder();
            log.append(clientId + " " + threadName + "\n-\tstart of log\t-\n");

            log.append(pm.getDiscounts(languageTag)
            .entrySet()
            .stream()
            .map(entry -> entry.getKey() + "\t" + entry.getValue())
            .collect(Collectors.joining("\n")));

            Product product = pm.reviewProduct(productId, FOUR_STAR, "Yest another review");
            log.append((product != null)
                    ? "\nProduct " + product.getId() + " reviewed\n"
                    : "\nProduct " + product.getId() + " not reviewed");

            pm.printProductReport(productId, languageTag, clientId);
            log.append(clientId + " generated report for " + productId + " product");

            log.append("\n-\tend of log\t-\n");

            return log.toString();
        };

        List<Callable<String>> clients = Stream.generate(() -> client).limit(5).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            List<Future<String>> results = executorService.invokeAll(clients);
            executorService.shutdown();
            results.stream().forEach(result -> {
                try{
                    System.out.println(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error retrieving client log");
                }

            });
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error invoking clients");
        }


//        pm.createProduct(164, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
//        pm.reviewProduct(164, FOUR_STAR, "Nice hot cup of Tea");
//        pm.reviewProduct(164, FOUR_STAR, "Nice hot cup of Tea");
//        pm.reviewProduct(164, FIVE_STAR, "Nice hot cup of Tea");
//        pm.reviewProduct(164, FOUR_STAR, "Nice hot cup of Tea");
//
////        pm.dumpData();
////        pm.restoreData();
//
//        pm.printProductReport(101);
//        pm.printProductReport(102);
//        pm.printProductReport(103);
//        pm.printProductReport(164);
//
//        pm.printProducts(p -> p.getPrice().floatValue() < 10, (p1, p2)->p1.getRating().ordinal()-p1.getRating().ordinal());
//        pm.getDiscounts().forEach((rating, discout) -> System.out.println(rating + '\n' + discout));

    }

    public static void printProduct(Product product) {
        System.out.println(product.getId() + " " + product.getName() + " " + product.getPrice() + " " + product.getRating().getStars());
    }
}
