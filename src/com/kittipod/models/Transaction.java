package com.kittipod.models;

import java.util.Calendar;
import java.util.Objects;

public class Transaction implements Comparable<Transaction>{

    private BuyingDate buyingDate;
    private Product product;
    private Person buyer;

    public Transaction(BuyingDate buyingDate, Product product, Person buyer) {
        this.buyingDate = buyingDate;
        this.product = product;
        this.buyer = buyer;
    }


    public BuyingDate getBuyingDate() {
        return buyingDate;
    }

    public Product getProduct() {
        return product;
    }

    public Person getBuyer() {
        return buyer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(buyingDate, that.buyingDate) &&
                Objects.equals(product, that.product) &&
                Objects.equals(buyer, that.buyer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyingDate, product, buyer);
    }

    @Override
    public int compareTo(Transaction o) {
        if (this.buyingDate.getDate().get(Calendar.YEAR) != o.buyingDate.getDate().get(Calendar.YEAR)) {
            return this.buyingDate.getDate().get(Calendar.YEAR) - o.buyingDate.getDate().get(Calendar.YEAR);
        }
        return this.buyingDate.getDate().get(Calendar.DAY_OF_YEAR) - o.buyingDate.getDate().get(Calendar.DAY_OF_YEAR);
    }
}
