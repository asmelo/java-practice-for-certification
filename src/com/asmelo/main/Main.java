package com.asmelo.main;

import com.asmelo.labs.Product;
import com.asmelo.labs.ProductManager;
import com.asmelo.labs.Rateable;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import static com.asmelo.labs.Rating.*;

public class Main {

    public static void main(String[] args) {
        ProductManager pm = new ProductManager(Locale.UK);

        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
        pm.printProductReport();
        p1 = pm.reviewProduct(p1, FOUR_STAR, "Nice hot cup of Tea");
        p1 = pm.reviewProduct(p1, TWO_STAR, "Nice hot cup of Tea");
        p1 = pm.reviewProduct(p1, ONE_STAR, "Nice hot cup of Tea");
        p1 = pm.reviewProduct(p1, ONE_STAR, "Nice hot cup of Tea");
        pm.printProductReport();
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
