package com.kittipod.models;

import java.util.Calendar;
import java.util.Objects;

public class BuyingDate {

    private Calendar date;

    public BuyingDate() {
        this.date = Calendar.getInstance();
    }

    // REQUIRES: 1 <= month <= 12
    public void setMonth(int month) {
        date.set(Calendar.MONTH,month);
    }

    // REQUIRES: 1 <= day <= 31
    public void setDay(int day) {
        date.set(Calendar.DAY_OF_MONTH,day);
    }

    public void setYear(int year) {
        date.set(Calendar.YEAR,year);
    }

    public Calendar getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuyingDate that = (BuyingDate) o;
        return (date.get(Calendar.YEAR)== that.getDate().get(Calendar.YEAR))
        && (date.get(Calendar.DAY_OF_YEAR) == that.getDate().get(Calendar.DAY_OF_YEAR));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date.get(Calendar.YEAR), date.get(Calendar.DAY_OF_YEAR));
    }

    @Override
    public String toString() {
        return date.get(Calendar.YEAR) + "/" + String.format("%02d",date.get(Calendar.MONTH)) +
                "/" + String.format("%02d",date.get(Calendar.DAY_OF_MONTH));
    }
}
