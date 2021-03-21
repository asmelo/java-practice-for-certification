package com.asmelo.labs;

import static com.asmelo.labs.Rating.NOT_RATED;

@FunctionalInterface
public interface Rateable<T> {

    public static final Rating DEFAULT_RATING = NOT_RATED;

    T applyRating(Rating rating);

    public default Rating getRating() {
        return DEFAULT_RATING;
    }

    public default T applyRating(int stars) {
        return applyRating(convert(stars));
    }

    public static Rating convert(int stars) {
        return (stars >= 0 && stars <= 5) ? Rating.values()[stars] : DEFAULT_RATING;
    }

}
