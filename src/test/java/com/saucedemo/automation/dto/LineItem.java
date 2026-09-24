package com.saucedemo.automation.dto;

import java.math.BigDecimal;
import java.util.Objects;

/** One product line - name + price - used to compare "what's in the cart" against "what the PDF says." */
public class LineItem {

    private String name;
    private BigDecimal price;

    public LineItem() {
    }

    public LineItem(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineItem)) return false;
        LineItem lineItem = (LineItem) o;
        return Objects.equals(name, lineItem.name) && Objects.equals(price, lineItem.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}
