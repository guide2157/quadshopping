package com.kittipod;

public class Product implements Comparable<Product> {
    private final String name;
    private final double price;
    private final int day;
    private final int month;
    private final String buyer;

    public Product(String name, double price, int day, int month, String buyer) {
        this.name = name;
        this.price = price;
        this.day = day;
        this.month = month;
        this.buyer = buyer;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }


    public String getBuyer() {
        return buyer;
    }

    @Override
    public int compareTo(Product o) {

        if (this.month < o.getMonth()) {
            return -1;
        } else if (this.month == o.getMonth()) {
            if (this.day < o.getDay()) {
                return -1;
            } else if (this.day == o.getDay()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }

    }
}
