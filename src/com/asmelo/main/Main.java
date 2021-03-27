package com.asmelo.main;

import com.asmelo.labs.Product;
import com.asmelo.labs.ProductManager;
import com.asmelo.labs.Rateable;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;

import static com.asmelo.labs.Rating.*;

public class Main {

    public static void main(String[] args) {
        ProductManager pm = new ProductManager("en-GB");

        pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
//        pm.printProductReport(101);
        pm.reviewProduct(101, FOUR_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(101, FOUR_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(101, FIVE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(101, FOUR_STAR, "Nice hot cup of Tea");
//        pm.printProductReport(101);

        pm.createProduct(102, "Tea", BigDecimal.valueOf(2.99), NOT_RATED);
//        pm.printProductReport(102);
        pm.reviewProduct(102, FOUR_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(102, TWO_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(102, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(102, ONE_STAR, "Nice hot cup of Tea");
//        pm.printProductReport(102);

        pm.createProduct(103, "Tea", BigDecimal.valueOf(2.50), NOT_RATED);
//        pm.printProductReport(103);
        pm.reviewProduct(103, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(103, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(103, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(103, ONE_STAR, "Nice hot cup of Tea");
//        pm.printProductReport(103);

        pm.createProduct(104, "Tea", BigDecimal.valueOf(2.60), NOT_RATED);
        pm.reviewProduct(104, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(104, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(104, ONE_STAR, "Nice hot cup of Tea");
        pm.reviewProduct(104, ONE_STAR, "Nice hot cup of Tea");
//        pm.printProductReport(104);

        Comparator<Product> ratingSorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
        Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
        pm.printProducts(p -> p.getPrice().floatValue() < 10, ratingSorter.thenComparing(priceSorter));

        pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + '\t' + discount));

//        pm.printProducts(priceSorter.thenComparing(ratingSorter).reversed());

//        Product p2 = productManager.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), FOUR_STAR);
//        Product p3 = productManager.createProduct(103, "Cake", BigDecimal.valueOf(3.99), FIVE_STAR, LocalDate.now().plusDays(2));
//        Product p4 = productManager.createProduct(105, "Cookie", BigDecimal.valueOf(3.99), TWO_STAR, LocalDate.now());
//        Product p5 = p3.applyRating(THREE_STAR);
//        Product p6 = productManager.createProduct(104, "Chocolate",BigDecimal.valueOf(2.99), FIVE_STAR);
//        Product p7 = productManager.createProduct(104, "Chocolate",BigDecimal.valueOf(2.99), FIVE_STAR, LocalDate.now().plusDays(2));
//        Product p8 = p4.applyRating(FIVE_STAR);
//        Product p9 = p1.applyRating(TWO_STAR);
//
//        System.out.println(p1.getBestBefore());
//        System.out.println(p3.getBestBefore());
//
//        System.out.println(p1);
//        System.out.println(p2);
//        System.out.println(p3);
//        System.out.println(p4);
//        System.out.println(p5);
//        System.out.println(p6);
//        System.out.println(p7);
//        System.out.println(p8);
//        System.out.println(p9);
//
//        System.out.println(p6.equals(p7));

        //        printProduct(p1);
//        printProduct(p2);
//        printProduct(p3);
//        printProduct(p4);
//        printProduct(p5);
    }

    public static void printProduct(Product product) {
        System.out.println(product.getId() + " " + product.getName() + " " + product.getPrice() + " " + product.getRating().getStars());
    }
}
