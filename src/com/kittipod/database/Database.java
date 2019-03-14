package com.kittipod.database;

import com.kittipod.models.BuyingDate;
import com.kittipod.models.Person;
import com.kittipod.models.Product;
import com.kittipod.models.Transaction;

import java.sql.*;
import java.util.*;

public class Database {

    private static final String DB_NAME = "TransactionsDataBase.db";

    //TODO: Change directory of the database
    private static final String CONNECTION_STRING = "jdbc:sqlite:/Users/kittipodpungcharoenkul/IdeaProjects/" +
            "quardshopping/src/com/kittipod/database/" + DB_NAME;

    private static final String TABLE_MEMBER = "member";
    private static final String COLUMN_MEMBER_ID = "id";
    private static final String COLUMN_MEMBER_NAME = "Name";
    private static final String COLUMN_MEMBER_SPENDING = "Spending";
    private static final int INDEX_MEMBER_ID = 1;
    private static final int INDEX_MEMBER_NAME = 2;
    private static final int INDEX_MEMBER_SPENDING = 3;

    private static final String TABLE_TRANSACTION = "record";
    private static final String COLUMN_TRANSACTION_YEAR = "Year";
    private static final String COLUMN_TRANSACTION_MONTH = "Month";
    private static final String COLUMN_TRANSACTION_DAY = "Day";
    private static final String COLUMN_TRANSACTION_PRODUCT = "Product";
    private static final String COLUMN_TRANSACTION_PRICE = "Price";
    private static final String COLUMN_TRANSACTION_MEMBER_ID = "Member_id";
    private static final int INDEX_TRANSACTION_YEAR = 1;
    private static final int INDEX_TRANSACTION_MONTH = 2;
    private static final int INDEX_TRANSACTION_DAY = 3;
    private static final int INDEX_TRANSACTION_PRODUCT = 4;
    private static final int INDEX_TRANSACTION_PRICE = 5;
    private static final int INDEX_TRANSACTION_MEMBER_ID = 6;


    private static final String QUERY_MEMBER_BY_NAME = "SELECT " + COLUMN_MEMBER_ID + " FROM " +
            TABLE_MEMBER + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
    private static final String ADD_MEMBER = "INSERT INTO " + TABLE_MEMBER +
            " (" + COLUMN_MEMBER_NAME + ", " + COLUMN_MEMBER_SPENDING + ") VALUES(?,0)";
    private static final String QUERY_TRANSACTION_BY_BUYER = "SELECT * FROM " + TABLE_TRANSACTION +
            " WHERE " + COLUMN_TRANSACTION_MEMBER_ID + " = ?";
    private static final String ADD_TRANSACTION = "INSERT INTO " + TABLE_TRANSACTION +
            " (" + COLUMN_TRANSACTION_YEAR + ", " + COLUMN_TRANSACTION_MONTH + ", " +
            COLUMN_TRANSACTION_DAY + ", " + COLUMN_TRANSACTION_PRODUCT + ", " + COLUMN_TRANSACTION_PRICE +
            ", " + COLUMN_TRANSACTION_MEMBER_ID + ") VALUES(?,?,?,?,?,?)";
    private static final String GET_TOTAL_SPENDING = "SELECT " + COLUMN_TRANSACTION_PRICE + " FROM " +
            TABLE_TRANSACTION;
    private static final String GET_ALL_MEMBERS = "SELECT * FROM " + TABLE_MEMBER + " ORDER BY " +
            TABLE_MEMBER + "." + COLUMN_MEMBER_NAME + " COLLATE NOCASE ASC";
    private static final String GET_ALL_TRANSACTIONS = "SELECT * FROM " + TABLE_TRANSACTION + " ORDER BY " +
            TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_YEAR + ", " +
            TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_MONTH + ", " +
            TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_DAY + " COLLATE NOCASE ASC";
    private static final String QUERY_MEMBERS_BY_ID = "SELECT " + COLUMN_MEMBER_NAME + " FROM " +
            TABLE_MEMBER + " WHERE " + TABLE_MEMBER + "." + COLUMN_MEMBER_ID + " = ?";
    private static final String UPDATE_MEMBERS_SPENDING = "UPDATE " + TABLE_MEMBER +
            " SET " + COLUMN_MEMBER_SPENDING + " = ? WHERE " + COLUMN_MEMBER_ID + " = ?";
    private static final String DELETE_TRANSACTION = "DELETE FROM " + TABLE_TRANSACTION +
            " WHERE " + COLUMN_TRANSACTION_YEAR + " = ? AND " + COLUMN_TRANSACTION_MONTH + " = ? AND " +
            COLUMN_TRANSACTION_DAY + " = ? AND " + COLUMN_TRANSACTION_PRODUCT + " = ? AND " +
            COLUMN_TRANSACTION_PRICE + " = ? AND " + COLUMN_TRANSACTION_MEMBER_ID + " = ?";
    private static final String RESET_MEMBERS = "DELETE FROM " + TABLE_MEMBER;
    private static final String RESET_RECORDS = "DELETE FROM " + TABLE_TRANSACTION;


    private static Connection conn;
    private static PreparedStatement queryMember;
    private static PreparedStatement addMember;
    private static PreparedStatement queryTransactionByBuyer;
    private static PreparedStatement addTransaction;
    private static PreparedStatement queryMemberByID;
    private static PreparedStatement updateMemberSpending;
    private static PreparedStatement deleteTransaction;

    // MODIFIES: This
    // EFFECT: Start all connections to the database
    public static boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            queryMember = conn.prepareStatement(QUERY_MEMBER_BY_NAME);
            addMember = conn.prepareStatement(ADD_MEMBER);
            queryTransactionByBuyer = conn.prepareStatement(QUERY_TRANSACTION_BY_BUYER);
            addTransaction = conn.prepareStatement(ADD_TRANSACTION);
            queryMemberByID = conn.prepareStatement(QUERY_MEMBERS_BY_ID);
            updateMemberSpending = conn.prepareStatement(UPDATE_MEMBERS_SPENDING);
            deleteTransaction = conn.prepareStatement(DELETE_TRANSACTION);
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
            return false;
        }
    }

    // MODIFIES: This
    // EFFECT: Close all connections to the database
    public static boolean close() {
        try {
            closeStatement(queryMember);
            closeStatement(addMember);
            closeStatement(queryTransactionByBuyer);
            closeStatement(addTransaction);
            closeStatement(queryMemberByID);
            closeStatement(updateMemberSpending);
            closeStatement(deleteTransaction);
            if (conn != null) {
                conn.close();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Could not close the connection: " + e.getMessage());
            return false;
        }
    }

    private static void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    // EFFECT: Delete the given transaction from the database
    public static void deleteTransaction(Transaction transaction) {
        try {
            conn.setAutoCommit(false);
            BuyingDate date = transaction.getBuyingDate();
            Calendar calendar = date.getDate();
            deleteTransaction.setInt(1, calendar.get(Calendar.YEAR));
            deleteTransaction.setInt(2, calendar.get(Calendar.MONTH));
            deleteTransaction.setInt(3, calendar.get(Calendar.DAY_OF_MONTH));
            Product product = transaction.getProduct();
            deleteTransaction.setString(4, product.getName());
            deleteTransaction.setDouble(5, product.getPrice());
            Person buyer = transaction.getBuyer();
            deleteTransaction.setInt(6, buyer.getId());
            int affectedRow = deleteTransaction.executeUpdate();
            if (affectedRow == 0) {
                System.out.println("No transaction found.");
            } else if (affectedRow != 1) {
                throw new SQLException("More than one transaction was deleted.");
            } else {
                updateMemberSpending(buyer, buyer.getTotalSpent() - product.getPrice());
                conn.commit();
                System.out.println("Transaction removed.");
            }
        } catch (SQLException e) {
            System.out.println("Couldn't remove transaction: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // EFFECT: Query the members in the database by the give name if not exist return null
    public static Person queryMember(String name) {
        try {
            queryMember.setString(1, name);
            ResultSet results = queryMember.executeQuery();
            if (results.next()) {
                int id = results.getInt(1);
                return new Person(name, id);
            }
        } catch (SQLException e) {
            System.out.println("Couldn't query member: " + e.getMessage());
        }
        return null;
    }

    // EFFECT: Query the members in the database by the give id if not exist return null
    public static Person queryMemberById(int id) {
        try {
            queryMemberByID.setInt(1, id);
            ResultSet results = queryMemberByID.executeQuery();
            if (results.next()) {
                String name = results.getString(1);
                return new Person(name, id);
            }
        } catch (SQLException e) {
            System.out.println("Couldn't query member: " + e.getMessage());
        }
        return null;
    }

    // EFFECT: Add new member to the database if not already existing
    // and return the new member object. If an exception is thrown, return null.
    public static int addMember(String name) {
        Person person = queryMember(name);
        if (person == null) {
            try {
                addMember.setString(1, name);
                int affectedRow = addMember.executeUpdate();
                if (affectedRow != 1) {
                    throw new SQLException("More than one member was added.");
                }
                ResultSet generatedKey = addMember.getGeneratedKeys();
                if (generatedKey.next()) {
                    return generatedKey.getInt(1);
                } else {
                    throw new SQLException("Couldn't get id for member");
                }
            } catch (SQLException e) {
                System.out.println("Couldn't add member: " + e.getMessage());
            }
        } else {
            System.out.println("This person is already a member.");
        }
        return 0;

    }

    // EFFECT: Update the member data in the database with the new balance
    public static void updateMemberSpending(Person person, double newBalance) throws SQLException {
        updateMemberSpending.setDouble(1, newBalance);
        updateMemberSpending.setInt(2, person.getId());
        int affectedRow = updateMemberSpending.executeUpdate();
        if (affectedRow != 1) {
            throw new SQLException("More than one member data was changed.");
        }
        System.out.println("Member data updated.");
    }


    // EFFECT: Query the transactions in the database by the buyer if not exist return null
    public static List<Transaction> queryTransactionByBuyer(Person person) {
        try {
            queryTransactionByBuyer.setInt(1, person.getId());
            ResultSet results = queryTransactionByBuyer.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (results.next()) {
                BuyingDate date = new BuyingDate();
                date.setYear(results.getInt(COLUMN_TRANSACTION_YEAR));
                date.setMonth(results.getInt(COLUMN_TRANSACTION_MONTH));
                date.setDay(results.getInt(COLUMN_TRANSACTION_DAY));
                Product product = new Product(results.getString(COLUMN_TRANSACTION_PRODUCT),
                        results.getDouble(COLUMN_TRANSACTION_PRICE));
                Transaction transaction = new Transaction(date, product, person);
                transactions.add(transaction);
            }
            return transactions;
        } catch (SQLException e) {
            System.out.println("Couldn't query transaction: " + e.getMessage());
        }
        return null;
    }

    // EFFECT: Add the given transaction into the database
    public static void addTransaction(Transaction transaction) {
        BuyingDate date = transaction.getBuyingDate();
        Calendar calendar = date.getDate();
        Product product = transaction.getProduct();
        Person person = transaction.getBuyer();
        try {
            conn.setAutoCommit(false);
            addTransaction.setInt(INDEX_TRANSACTION_YEAR, calendar.get(Calendar.YEAR));
            addTransaction.setInt(INDEX_TRANSACTION_MONTH, calendar.get(Calendar.MONTH));
            addTransaction.setInt(INDEX_TRANSACTION_DAY, calendar.get(Calendar.DAY_OF_MONTH));
            addTransaction.setString(INDEX_TRANSACTION_PRODUCT, product.getName());
            addTransaction.setDouble(INDEX_TRANSACTION_PRICE, product.getPrice());
            addTransaction.setInt(INDEX_TRANSACTION_MEMBER_ID, person.getId());
            int affectedRow = addTransaction.executeUpdate();
            if (affectedRow != 1) {
                throw new SQLException("Affected row more than one");
            } else {
                updateMemberSpending(person,person.getTotalSpent() + product.getPrice());
                System.out.println("Transaction added.");
            }
        } catch (SQLException e) {
            System.out.println("Couldn't insert new transaction: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // EFFECT: Return all transaction in the database. If an exception is thrown, return null;
    public static List<Transaction> queryAllTransaction() {
        try {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(GET_ALL_TRANSACTIONS);
            List<Transaction> transactions = new ArrayList<>();
            while (results.next()) {
                BuyingDate date = new BuyingDate();
                date.setYear(results.getInt(COLUMN_TRANSACTION_YEAR));
                date.setMonth(results.getInt(COLUMN_TRANSACTION_MONTH));
                date.setDay(results.getInt(COLUMN_TRANSACTION_DAY));
                Product product = new Product(results.getString(COLUMN_TRANSACTION_PRODUCT),
                        results.getDouble(COLUMN_TRANSACTION_PRICE));
                Person person = queryMemberById(results.getInt(COLUMN_TRANSACTION_MEMBER_ID));
                Transaction transaction = new Transaction(date, product, person);
                transactions.add(transaction);
            }
            statement.close();
            return transactions;
        } catch (SQLException e) {
            System.out.println("Couldn't query transaction: " + e.getMessage());
        }
        return null;
    }


    // EFFECT: Get total spending form the transaction database. If an exception is thrown, return -1;
    public static double getTotal() {
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(GET_TOTAL_SPENDING);
            double total = 0;
            while (result.next()) {
                total += result.getDouble(1);
            }
            statement.close();
            return total;
        } catch (SQLException e) {
            System.out.println("Couldn't query total spending: " + e.getMessage());
            return -1;
        }
    }

    // EFFECT: Return a list of all members. If an exception is thrown, return null;
    public static Map<String, Person> getAllMembers() {
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(GET_ALL_MEMBERS);
            Map<String, Person> members = new HashMap<>();
            while (result.next()) {
                Person member = new Person(result.getString(INDEX_MEMBER_NAME), result.getInt(INDEX_MEMBER_ID));
                member.setTotalSpent(result.getDouble(INDEX_MEMBER_SPENDING));
                members.put(member.getName(), member);
            }
            statement.close();
            return members;
        } catch (SQLException e) {
            System.out.println("Couldn't query total spending: " + e.getMessage());
            return null;
        }
    }

    // EFFECT: Delete all data in the member table
    public static void resetMembers() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(RESET_MEMBERS);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't reset member list: " + e.getMessage());
        }
    }

    // EFFECT: Delete all data in the record table
    public static void resetRecords() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(RESET_RECORDS);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't reset record: " + e.getMessage());
        }
    }
}
