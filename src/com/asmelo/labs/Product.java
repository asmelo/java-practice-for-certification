package com.asmelo.labs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import static com.asmelo.labs.Rating.*;

public abstract class Product implements Rateable<Product> {

    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(5);

    private int id;
    private String name;
    private BigDecimal price;
    private Rating rating;

    Product() {
        this(0,"no_name", BigDecimal.ZERO, NOT_RATED);
    }

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    Product(int id, String name, BigDecimal price) {
        this(id, name, price, NOT_RATED);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public Rating getRating() {
        return rating;
    }

    public BigDecimal getDiscount() {
        return this.price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

//    public abstract Product applyRating(Rating rating);

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discount=" + getDiscount() +
                ", rating=" + rating.getStars() +
                ", bestBefore=" + getBestBefore() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id == product.id; //&& Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
