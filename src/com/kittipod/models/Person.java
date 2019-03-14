package com.kittipod.models;


import java.util.Objects;

public class Person {
    private final String name;
    private double totalSpent =0;
    private int id;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public String getName() {
        return name;
    }

    // MODIFIES: This
    // EFFECT: Add the total spending by the input
    public void addTotalSpent(double totalSpent) {
        this.totalSpent += totalSpent;
    }

    // REQUIRES: The total spending >= input
    // MODIFIES: This
    // EFFECT: Deduct the total spending by the input
    public void minusTotalSpent(double totalSpent) {
        this.totalSpent -= totalSpent;
    }

    public int getId() {
        return id;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
