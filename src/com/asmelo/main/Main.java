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

//        pm.printProductReport(101);
//        pm.printProductReport(102);
//        pm.printProductReport(103);

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
