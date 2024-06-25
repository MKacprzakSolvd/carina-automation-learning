package com.solvd.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.function.BiPredicate;

// TODO consider making it into record
@Getter
@Builder
public class Product {
    // TODO add sensible equals
    private String name;
    private BigDecimal price;
    private Integer rating;
    private Integer reviewNumber;
    private Boolean inStock;
    // TODO add avaliable colors and sizes

    /**
     * Returns true, if there are any different field values
     * for filed pairs that are both not null. If other is null, it will return true.
     * <p>
     * I.e. if this.name is "foo" && other.name is "bar" it will return true,
     * but if this.name is "foo" && other.name is null it will return false.
     * (assuming there are no other differences),
     * Warning: this method does not constitute an equivalence relationship
     */
    public boolean hasDifferences(Product other) {
        if (other == null) {
            return true;
        }
        if (differsInValue(getName(), other.getName())) return true;
        if (differsInValue(getPrice(), other.getPrice(), (a, b) -> a.compareTo(b) == 0)) return true;
        if (differsInValue(getRating(), other.getRating())) return true;
        if (differsInValue(getReviewNumber(), other.getReviewNumber())) return true;
        if (differsInValue(getInStock(), other.getInStock())) return true;
        return false;
    }


    /**
     * Works like hasDifferences but for values.
     * Helper function.
     */
    private static <T> boolean differsInValue(T valA, T valB) {
        // TODO consider moving this method to utils
        if (valA != null && valB != null &&
                !valA.equals(valB)) {
            return true;
        }
        return false;
    }

    /**
     * Works like hasDifferences but for values.
     * Helper function.
     */
    private static <T> boolean differsInValue(T valA, T valB, BiPredicate<T, T> equalityChecker) {
        // TODO consider moving this method to utils
        if (valA != null && valB != null
                && !equalityChecker.test(valA, valB)) {
            return true;
        }
        return false;
    }

    public static int compareByNameAToZ(Product product1, Product product2) {
        return product1.getName().compareTo(product2.getName());
    }

    public static int compareByNameZToA(Product product1, Product product2) {
        return product2.getName().compareTo(product1.getName());
    }

    public static int compareByPriceAscending(Product product1, Product product2) {
        return product1.getPrice().compareTo(product2.getPrice());
    }

    public static int compareByPriceDescending(Product product1, Product product2) {
        return product2.getPrice().compareTo(product1.getPrice());
    }
}
