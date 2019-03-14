package com.kittipod.ui;

import com.kittipod.database.Database;
import com.kittipod.exceptions.InvalidBuyerException;
import com.kittipod.exceptions.InvalidDayException;
import com.kittipod.exceptions.InvalidMonthException;
import com.kittipod.exceptions.InvalidYearException;
import com.kittipod.models.BuyingDate;
import com.kittipod.models.Person;
import com.kittipod.models.Product;
import com.kittipod.models.Transaction;

import java.util.*;

public class MenuUsingDatabase {
    private static Scanner scanner = new Scanner(System.in);
//
//    private static Set<Transaction> transactions = new HashSet<>();
    private static Map<String,Person> people;

    public static void main(String[] args) {
        System.out.println("Welcome to the quad spending calculator!");
        Database.open();
        people = Database.getAllMembers();
        Program();
    }

    //EFFECT: Display the instructions
    private static void displayInstruction() {
        String[] instruction = {"Add transaction", "Remove transaction",
                "Calculate total spending", "Assign price to each member",
                "Display spending history", "Display individual spending",
                "Print member list", "Add member","Reset transaction","Reset program","Quit"};
        for (int i = 1; i <= instruction.length; i++) {
            System.out.println(i + " : " + instruction[i - 1]);

        }
    }


    private static void Program() {
        boolean continueProgram = true;
        while (continueProgram) {
            displayInstruction();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    try {
                        addTransaction();
                    } catch (InvalidMonthException e) {
                        System.out.println("Invalid month was entered.");
                    } catch (InvalidDayException e) {
                        System.out.println("Entered day not in range.");
                    } catch (InvalidYearException e) {
                        System.out.println("Entered year out of range.");
                    } catch (InvalidBuyerException e) {
                        System.out.println("Buyer not found in member list.");
                    }
                    break;
                case 2:
                    try {
                        removeTransaction();
                    } catch (InvalidMonthException e) {
                        System.out.println("Invalid month was entered.");
                    } catch (InvalidDayException e) {
                        System.out.println("Entered day not in range.");
                    } catch (InvalidYearException e) {
                        System.out.println("Entered year out of range.");
                    } catch (InvalidBuyerException e) {
                        System.out.println("Buyer not found in member list.");
                    }
                    break;
                case 3:
                    calculateTotal();
                    break;
                case 4:
                    assignPayment();
                    break;
                case 5:
                    displayHistory();
                    break;
                case 6:
                    displayIndividualHistory();
                    break;
                case 7:
                    printMemberList();
                    break;
                case 8:
                    addMember();
                    break;
                case 9:
                    Database.resetRecords();
                    System.out.println("All transactions deleted.");
                    System.out.println("===================================");
                    break;
                case 10:
                    Database.resetRecords();
                    Database.resetMembers();
                    people.clear();
                    System.out.println("Program reset!");
                    System.out.println("===================================");
                    break;
                case 11:
                    continueProgram = false;
                    System.out.println("Program ended.");
                    Database.close();
                    break;
                default:
                    break;

            }
        }
    }

    //MODIFIES: This
    //EFFECT: Add new members to the member list.
    private static void addMember() {
        scanner.nextLine();
        System.out.println("Please enter a new member name :");
        String name = scanner.nextLine();
        int id = Database.addMember(name);
        people.put(name,new Person(name,id));
        System.out.println("New member added.");
    }


    // EFFECT: Print out the amount which each member has to pay.
    private static void assignPayment() {
        double total = Database.getTotal();
        double averagePrice = total / people.size();
        for (Person person : people.values()) {
            double difference = averagePrice - person.getTotalSpent();
            System.out.println(person.getName() + " paid $" + person.getTotalSpent() + ".");
            if (difference >= 0) {
                String displayDifference = String.format("$%,.2f", difference);
                System.out.println(person.getName() + " have to pay " + displayDifference + " more.");
            } else {
                String displayDifference = String.format("$%,.2f", Math.abs(difference));
                System.out.println(person.getName() + " have to get " + displayDifference + " back.");
            }
            System.out.println("===================================");
        }

    }

    // EFFECT: Print out all transactions
    private static void displayHistory() {
        List<Transaction> transactionsList = Database.queryAllTransaction();
        if (transactionsList == null || transactionsList.isEmpty()) {
            System.out.println("No transaction recorded.");
            System.out.println("===================================");
            return;
        }
        for (Transaction transaction : transactionsList) {
            String price = String.format("$%,.2f", transaction.getProduct().getPrice());
            System.out.println(transaction.getBuyingDate() + " : " + price + " : " +
                    transaction.getProduct().getName() + " : " + transaction.getBuyer());
        }
        System.out.println("===================================");
    }

    // EFFECT: Print out the total amount of spending
    private static void calculateTotal() {
        double totalPrice = Database.getTotal();
        System.out.println("Total spending is $" + totalPrice);
        String averagePrice = String.format("$%,.2f", totalPrice / people.size());
        System.out.println(averagePrice + " per person");
    }


    // EFFECT: Print out all transactions done by the indicated person
    private static void displayIndividualHistory() {
        scanner.nextLine();
        System.out.println("Please enter member's name :");
        String name = scanner.nextLine();
        if (people.keySet().contains(name)) {
            List<Transaction> transactions = Database.queryTransactionByBuyer(people.get(name));
            for (Transaction transaction : transactions) {
                String price = String.format("$%,.2f", transaction.getProduct().getPrice());
                System.out.println(transaction.getBuyingDate() + " : " + price + " : " +
                        transaction.getProduct().getName());
            }
            System.out.println("===================================");
        } else {
            System.out.println("Couldn't find the member.");
        }
    }

    // EFFECT: remove an existing transaction from the transaction list
    private static void removeTransaction() throws InvalidMonthException, InvalidDayException, InvalidYearException, InvalidBuyerException {
        scanner.nextLine();
        System.out.println("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.println("Enter item price: ");
        double price = scanner.nextDouble();
        Product product = new Product(itemName,price);
        BuyingDate buyingDate = addDate();
        Person buyer = getBuyer();
        Transaction transaction = new Transaction(buyingDate,product,buyer);
        Database.deleteTransaction(transaction);
        people.get(buyer.getName()).minusTotalSpent(price);
    }

    // MODIFIES: This
    // EFFECT: Add a new transaction to the transaction list
    private static void addTransaction() throws InvalidMonthException, InvalidDayException, InvalidYearException, InvalidBuyerException {
        scanner.nextLine();
        System.out.println("Enter item name: ");
        String name = scanner.nextLine().toLowerCase();
        System.out.println("Enter price: ");
        double price = scanner.nextDouble();
        BuyingDate date = addDate();
        scanner.nextLine();
        Person buyer = getBuyer();
        Product product = new Product(name,price);
        Transaction transaction = new Transaction(date,product,buyer);
        Database.addTransaction(transaction);
        people.get(buyer.getName()).addTotalSpent(price);
        System.out.println("===================================");
    }


    // EFFECT: Print out all members' name
    private static void printMemberList() {
        System.out.println("This group is consisted of " + people.size() + " members.");
        List<String> names = new ArrayList<>(people.keySet());
        Collections.sort(names);
        for (String name : names) {
            System.out.println(name);
        }
        System.out.println("===================================");
    }

    // EFFECT: Get the buyer from the member list
    // if the input member is not in the list, throw an InvalidBuyerException
    private static Person getBuyer() throws InvalidBuyerException{
        System.out.println("Enter buyer: ");
        String buyerName = scanner.nextLine().toLowerCase();
        if (buyerName.equals("")) {
            buyerName = scanner.nextLine().toLowerCase();
        }
        if (people.containsKey(buyerName)) {
            return people.get(buyerName);
        } else {
            throw new InvalidBuyerException();
        }
    }

    // EFFECT: Create a buyingDate object from the input
    private static BuyingDate addDate() throws InvalidMonthException, InvalidDayException, InvalidYearException {
        BuyingDate buyingDate = new BuyingDate();
        addYear(buyingDate);
        addMonth(buyingDate);
        addDay(buyingDate);
        return buyingDate;
    }

    private static void addMonth(BuyingDate buyingDate) throws InvalidMonthException {
        int month;
        System.out.println("Enter month: ");
        month = scanner.nextInt();
        if (!((month >= 1) && (month <= 12))) {
            throw new InvalidMonthException();
        }
        buyingDate.setMonth(month);
    }

    private static void addDay(BuyingDate buyingDate) throws InvalidDayException {
        int month = buyingDate.getDate().get(Calendar.MONTH);
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
        buyingDate.setDay(day);
    }

    private static void addYear(BuyingDate buyingDate) throws InvalidYearException {
        int year;
        System.out.println("Enter year: ");
        year = scanner.nextInt();
        if (year <= 2000 || year >= 2040) {
            throw new InvalidYearException();
        }
        buyingDate.setYear(year);
    }

}




