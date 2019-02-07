package com.kittipod.objects;

import com.kittipod.exceptions.FutureDateException;

import java.util.Calendar;

public class Product implements Comparable<Product> {
    private final String name;
    private final double price;
    private final Calendar date;
    private final String buyer;

    public Product(String name, double price, Calendar date, String buyer) throws FutureDateException {
        Calendar current = Calendar.getInstance();
        if (current.before(date)) {
            throw new FutureDateException();
        }
        this.name = name;
        this.price = price;
        this.date = date;
        this.buyer = buyer;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getDay() {
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return date.get(Calendar.MONTH);
    }

    public Calendar getDate() {
        return date;
    }

    public String getBuyer() {
        return buyer;
    }

    @Override
    public int compareTo(Product o) {
        boolean sameYear = date.get(Calendar.YEAR) == o.getDate().get(Calendar.YEAR);
        boolean sameDay = date.get(Calendar.DAY_OF_YEAR) == o.getDate().get(Calendar.DAY_OF_YEAR);
        boolean sameName = name.equals(o.getName());
        boolean samePrice = price == o.getPrice();
        if (sameDay && sameYear && sameName && samePrice) {
            return 0;
        } else {
            return 1;
        }
    }
}
