package com.kittipod;

import java.util.ArrayList;

public class Person implements Comparable<Person>{
    private final String name;
    private double totalSpent =0;
    private ArrayList<Product> spentProducts = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public String getName() {
        return name;
    }

    public void addItem(Product product){
        spentProducts.add(product);
        totalSpent+=product.getPrice();
        System.out.println(product.getName()+" : $"+product.getPrice()+" is added to "+this.name+" account.");
    }

    public void removeItem(Product product){
        spentProducts.remove(product);
        System.out.println(product.getName()+" : $"+product.getPrice()+" is removed from "+this.name+" account.");
    }

    @Override
    public int compareTo(Person person) {
        return this.name.compareToIgnoreCase(person.getName());
    }
}
