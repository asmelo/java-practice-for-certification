package com.asmelo.labs;

import java.io.*;
import java.lang.annotation.Repeatable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {

    private static final ProductManager pm = new ProductManager();

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());
    private final ResourceBundle config = ResourceBundle.getBundle("com.asmelo.labs.config");
    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    private Map<Product, List<Review>> products = new HashMap();


    private static Map<String, ResourceFormatter> formatters =
            Map.of(
                    "en-GB", new ResourceFormatter(Locale.UK),
                    "en-US", new ResourceFormatter(Locale.US),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "zn-CN", new ResourceFormatter(Locale.CHINA)
            );

    private ProductManager() {
        loadAllData();
    }

    public static ProductManager getInstance() {
        return pm;
    }

    public static Set<String> getSupportedLanguages() {
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Food(id, name, price, rating, bestBefore);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception e) {

        } finally {
            writeLock.unlock();
        }
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Drink(id, name, price, rating);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception e) {

        } finally {
            writeLock.unlock();
        }

        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        try {
            writeLock.lock();
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException ex) {
            logger.log(Level.INFO, ex.getMessage());
        } finally {
            writeLock.unlock();
        }
        return null;
    }

    private Product reviewProduct(Product product, Rating rating, String comments) {

        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));

        product = product.applyRating(
                Rateable.convert(
                        (int)Math.round(
                                reviews.stream()
                                .mapToInt(r -> r.getRating().ordinal())
                                .average()
                                .orElse(0)
                        )
                )
        );

        products.put(product, reviews);
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException {
        try {
            readLock.lock();
            return products.keySet().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));
        } finally {
            readLock.unlock();
        }

    }

    public void printProductReport(int id, String languageTag, String client) {
        try {
            readLock.lock();
            printProductReport(findProduct(id), languageTag, client);
        } catch (ProductManagerException ex) {
            logger.log(Level.INFO, ex.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error printing product report " + e.getMessage());
        } finally {
            readLock.unlock();
        }
    }

    private void printProductReport(Product product, String languageTag, String client) throws IOException {
        ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE), "UTF-8"))) {
            out.append(formatter.formatProduct(product) + System.lineSeparator());

            if (reviews.isEmpty()) {
                out.append(formatter.getText("no.reviews") + System.lineSeparator());
            } else {
                out.append(reviews.stream()
                        .map(r -> formatter.formatReviews(r) + System.lineSeparator())
                        .collect(Collectors.joining()));
            }
        }

    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
            StringBuilder txt = new StringBuilder();

            products.keySet().stream()
                    .filter(filter)
                    .sorted(sorter)
                    .forEach(p -> txt.append(formatter.formatProduct(p) + '\n'));

            System.out.println(txt);
        } finally {
            readLock.unlock();
        }
    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review " + text);
        }
        return review;
    }

    private Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);

            int id = Integer.parseInt((String)values[1]);
            String name = (String)values[2];
            BigDecimal price = BigDecimal.valueOf(Double.valueOf((String)values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));

            switch ((String)values[0]) {
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String)values[5]);
                    product = new Food(id, name, price, rating, bestBefore);
            }

        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            logger.log(Level.WARNING, "Error parsing product " + text + " " + e.getMessage());
        }
        return product;
    }

    private void loadAllData() {
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(file -> loadProduct(file))
                    .filter(product -> product!=null)
                    .collect(Collectors.toMap(product -> product,
                            product -> loadReviews(product)));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data " + e.getMessage(), e);
        }
    }

    private List<Review> loadReviews(Product product) {
        List<Review> reviews = null;
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try {
                reviews = Files.lines(file, Charset.forName("UTF-8"))
                        .map(text -> parseReview(text))
                        .filter(review -> review != null)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error loading reviews " + e.getMessage());
            }
        }

        return reviews;
    }

    public Product loadProduct(Path file) {
        Product product = null;

        try {
            product = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading product " + e.getMessage());
        }

        return product;
    }

    private void dumpData() {
        try {

            if (Files.notExists(tempFolder)) {
                Files.createDirectory(tempFolder);
            }
            Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Instant.now().getEpochSecond()));

            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(tempFile, StandardOpenOption.CREATE)
            )) {
                out.writeObject(products);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error dumping data " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreData() {
        try {

            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith("tmp"))
                    .findFirst().orElseThrow();

            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
                products = (HashMap)in.readObject();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error restoring data " + e.getMessage(), e);
        }
    }

    public Map<String, String> getDiscounts(String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
            return products.keySet()
                    .stream()
                    .collect(
                            Collectors.groupingBy(
                                    product -> product.getRating().getStars(),
                                    Collectors.collectingAndThen(
                                            Collectors.summingDouble(product -> product.getDiscount().doubleValue()),
                                            discount -> formatter.moneyFormat.format(discount))));
        } finally {
            readLock.lock();
        }
    }

    public static class ResourceFormatter {

        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            resources = ResourceBundle.getBundle("com.asmelo.labs.resources", locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(resources.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));
        }

        private String formatReviews(Review review) {
            return MessageFormat.format(resources.getString("review"),
                    review.getRating().getStars(),
                    review.getComments());
        }

        private String getText(String key) {
            return resources.getString(key);
        }

    }

}
