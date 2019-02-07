package com.kittipod.ui;


import com.kittipod.exceptions.*;
import com.kittipod.objects.Person;
import com.kittipod.objects.Product;

import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    private static ArrayList<Person> people = new ArrayList<>();
    private static ArrayList<Product> products = new ArrayList<>();

    public static void main(String[] args) {
        Setup();
        Program();
    }

    //EFFECT: Display the instructions
    private static void displayInstruction() {
        String[] instruction = {"Add item", "Remove item", "Calculate total spending", "Assign price to each member", "Display spending history", "Print member list", "Add member", "Quit"};
        for (int i = 1; i <= instruction.length; i++) {
            System.out.println(i + " : " + instruction[i - 1]);

        }
    }

    //MODIFIES: This
    //EFFECT: Add initial members to the people list
    private static void Setup() {
        System.out.println("Welcome to spending calculator!");
        System.out.println("Please enter number of members : ");
        int memberNum = scanner.nextInt();
        while (true) {
            if (memberNum <= 15) {
                break;
            } else {
                System.out.println("Number out of range. Please enter a number which is less than 15.");
                System.out.println("Please enter number of members : ");
                memberNum = scanner.nextInt();
            }
        }
        scanner.nextLine();
        for (int i = 1; i <= memberNum; i++) {
            System.out.println("Please enter " + orderTranslate(i) + " member's name : ");
            String name = scanner.nextLine();
            Person member = new Person(name.toLowerCase());
            people.add(member);
        }
        System.out.println("Program initialization completed.");
    }

    private static void Program() {
        boolean continueProgram = true;
        while (continueProgram) {
            displayInstruction();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    try {
                        addItem();
                    } catch (InvalidMonthException e) {
                        System.out.println("Invalid month was entered.");
                    } catch (InvalidDayException e) {
                        System.out.println("Entered day not in range.");
                    } catch (InvalidYearException e) {
                        System.out.println("Enter year out of range.");
                    } catch (InvalidBuyerException e) {
                        System.out.println("Buyer not found in member list.");
                    } catch (FutureDateException e) {
                        System.out.println("Sorry, the product must have already been purchased." + '\n' + "The entered date is in the future.");
                    }
                    break;
                case 2:
                    removeItem();
                    break;
                case 3:
                    calculateTotal();
                    break;
                case 4:
                    assignPrice();
                    break;
                case 5:
                    displayHistory();
                    break;
                case 6:
                    printMemberList();
                    break;
                case 7:
                    addMember();
                    break;
                case 8:
                    continueProgram = false;
                    System.out.println("Program ended.");
                    break;

                default:
                    break;

            }
        }
    }

    //MODIFIES: This
    //EFFECT: Add new members to the people list
    private static void addMember() {
        scanner.nextLine();
        System.out.println("Please enter a new member name :");
        String name = scanner.nextLine().toLowerCase();
        Person person = new Person(name);
        try {
            int personFound = Collections.binarySearch(people, person, null);
            if (personFound >= 0) {
                throw new PersonAlreadyAddedException();
            }
        } catch (PersonAlreadyAddedException e) {
            System.out.println("This person is already a member.");
            System.out.println("Please enter another name or enter q to cancel.");
        }
        people.add(person);
        System.out.println("New member has been added.");
    }

    private static void assignPrice() {
        double totalPrice = 0;
        for (Product p : products) {
            totalPrice += p.getPrice();
        }
        double averagePrice = totalPrice / people.size();
        for (Person person : people) {
            double difference = averagePrice - person.getTotalSpent();
            System.out.println(person.getName() + " paid $" + person.getTotalSpent() + ".");
            if (difference >= 0) {
                String displayDifference = String.format("$%,.2f", difference);
                System.out.println(person.getName() + " have to pay " + displayDifference + " more.");
            } else {
                String displayDifference = String.format("$%,.2f", difference);
                System.out.println(person.getName() + " have to get " + displayDifference + " back.");
            }
            System.out.println("===================================");
        }

    }

    private static void displayHistory() {
        Collections.sort(products);
        for (Product product : products) {
            String price = String.format("$%,.2f", product.getPrice());
            String month = String.format("%02d", product.getMonth());
            String day = String.format("%02d", product.getDay());
            System.out.println(month + "/" + day + " : " + price + " : " + product.getName() + " : " + product.getBuyer());
        }
        System.out.println("===================================");
    }

    private static void calculateTotal() {
        double totalPrice = 0;
        for (Product p : products) {
            totalPrice += p.getPrice();
        }
        System.out.println("Total spending is $" + totalPrice);
        String averagePrice = String.format("$%,.2f", totalPrice / people.size());
        System.out.println(averagePrice + " per person");
    }

    private static void removeItem() {
        String name;
        int day;
        String buyer;
        scanner.nextLine();
        System.out.println("Enter item name: ");
        name = scanner.nextLine();
        System.out.println("Enter day: ");
        day = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter buyer: ");
        buyer = scanner.nextLine().toLowerCase();
        Person buyer1 = new Person(buyer);
        boolean found = false;
        List<Product> toRemove = new ArrayList<>();
        for (Product product : products) {
            if ((product.getName().equals(name)) && (product.getDay() == day) && (product.getBuyer().equals(buyer))) {
                found = true;
                toRemove.add(product);
            }
        }
        if (found) {
            int foundPerson = Collections.binarySearch(people, buyer1, null);
            people.get(foundPerson).removeItem(toRemove.get(0));
            products.removeAll(toRemove);
        } else {
            System.out.println("Product not found");
        }

    }

    private static void addItem() throws InvalidMonthException, InvalidDayException, InvalidYearException, InvalidBuyerException, FutureDateException {
        String name;
        double price;
        Calendar cal;
        String buyer;
        scanner.nextLine();
        System.out.println("Enter item name: ");
        name = scanner.nextLine().toLowerCase();
        System.out.println("Enter price: ");
        price = scanner.nextDouble();
        cal = addDate();
        scanner.nextLine();
        System.out.println("Enter buyer: ");
        buyer = scanner.nextLine().toLowerCase();
        Person buyer1 = new Person(buyer);
        int foundPerson = findBuyer(buyer1);
        Product product = new Product(name, price, cal, buyer);
        products.add(product);
        people.get(foundPerson).addItem(product);
    }

    private static int findBuyer(Person buyer) throws InvalidBuyerException {
        int foundPerson = Collections.binarySearch(people, buyer, null);
        if (foundPerson < 0) {
            throw new InvalidBuyerException();
        }
        return foundPerson;
    }

    private static String orderTranslate(int number) {
        Map<Integer, String> order = new HashMap<>();
        order.put(1, "first");
        order.put(2, "second");
        order.put(3, "third");
        order.put(4, "fourth");
        order.put(5, "fifth");
        order.put(6, "sixth");
        order.put(7, "seventh");
        order.put(8, "eighth");
        order.put(9, "ninth");
        order.put(10, "tenth");
        order.put(11, "eleventh");
        order.put(12, "twelve");
        order.put(13, "thirteen");
        order.put(14, "fourteen");
        order.put(15, "fifteen");
        return order.get(number);

    }

    private static void printMemberList() {
        System.out.println("This group is consisted of " + people.size() + " members.");
        for (Person m : people) {
            System.out.println(m.getName());
        }
        System.out.println("===================================");
    }

    private static Calendar addDate() throws InvalidMonthException, InvalidDayException, InvalidYearException {
        Calendar cal = Calendar.getInstance();
        int year = addYear();
        int month = addMonth();
        int day = addDay(month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, day);
        return cal;
    }

    private static int addMonth() throws InvalidMonthException {
        int month;
        System.out.println("Enter month: ");
        month = scanner.nextInt();
        if (!((month >= 1) && (month <= 12))) {
            throw new InvalidMonthException();
        }
        return month;
    }

    private static int addDay(int month) throws InvalidDayException {
        int day;
        System.out.println("Enter day: ");
        day = scanner.nextInt();
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (!(day >= 1 && day <= 31)) {
                    throw new InvalidDayException();
                }

                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (!(day >= 1 && day <= 30)) {
                    throw new InvalidDayException();
                }
                break;
            case 2:
                if (!(day >= 1 && day <= 29)) {
                    throw new InvalidDayException();
                }
                break;

        }
        return day;
    }

    private static int addYear() throws InvalidYearException {
        int year;
        System.out.println("Enter year: ");
        year = scanner.nextInt();
        if (year <= 2000 || year >= 2040) {
            throw new InvalidYearException();
        }
        return year;
    }

}



