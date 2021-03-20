package com.asmelo.main;

import com.asmelo.Drink;
import com.asmelo.Food;
import com.asmelo.Product;
import com.asmelo.ProductManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.asmelo.Rating.*;

public class Main {

    public static void main(String[] args) {
        ProductManager productManager = new ProductManager();

        Product p1 = productManager.createProduct(101, "Tea", BigDecimal.valueOf(1.99), THREE_STAR);
        Product p2 = productManager.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), FOUR_STAR);
        Product p3 = productManager.createProduct(103, "Cake", BigDecimal.valueOf(3.99), FIVE_STAR, LocalDate.now().plusDays(2));
        Product p4 = productManager.createProduct(105, "Cookie", BigDecimal.valueOf(3.99), TWO_STAR, LocalDate.now());
        Product p5 = p3.applyingRating(THREE_STAR);
        Product p6 = productManager.createProduct(104, "Chocolate",BigDecimal.valueOf(2.99), FIVE_STAR);
        Product p7 = productManager.createProduct(104, "Chocolate",BigDecimal.valueOf(2.99), FIVE_STAR, LocalDate.now().plusDays(2));
        Product p8 = p4.applyingRating(FIVE_STAR);
        Product p9 = p1.applyingRating(TWO_STAR);

        System.out.println(p1.getBestBefore());
        System.out.println(p3.getBestBefore());

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);
        System.out.println(p5);
        System.out.println(p6);
        System.out.println(p7);
        System.out.println(p8);
        System.out.println(p9);

        System.out.println(p6.equals(p7));

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
