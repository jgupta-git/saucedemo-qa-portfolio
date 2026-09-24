package com.saucedemo.automation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * A checkout order's line items and totals. Built two ways in the tests:
 * once from the on-screen checkout overview (the "expected" side) and
 * once by parsing the downloaded PDF receipt (the "actual" side) - the
 * whole point of the PDF-receipt test is comparing these two.
 */
public class ReceiptSummary {

    private List<LineItem> items;
    private BigDecimal itemTotal;
    private BigDecimal tax;
    private BigDecimal total;

    public ReceiptSummary() {
    }

    public ReceiptSummary(List<LineItem> items, BigDecimal itemTotal, BigDecimal tax, BigDecimal total) {
        this.items = items;
        this.itemTotal = itemTotal;
        this.tax = tax;
        this.total = total;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ReceiptSummary{items=" + items + ", itemTotal=" + itemTotal + ", tax=" + tax + ", total=" + total + "}";
    }
}
