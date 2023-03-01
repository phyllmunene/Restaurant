import java.sql.*;
import java.util.Scanner;

public class Restaurant {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    private static Connection conn;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            connect();
            boolean running = true;
            while (running) {
                System.out.println("\n--- Restaurant Menu ---");
                System.out.println("1. Show Menu");
                System.out.println("2. Add Item to Menu");
                System.out.println("3. Order Food and Drink");
                System.out.println("4. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        showMenu();
                        break;
                    case 2:
                        addItem();
                        break;
                    case 3:
                        orderFoodAndDrink();
                        break;
                    case 4:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            disconnect();
        }
    }

    private static void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showMenu() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM menu");

        System.out.println("\n--- Menu ---");
        System.out.println("Name\t\tPrice");
        while (rs.next()) {
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            System.out.printf("%s\t\t%.2f\n", name, price);
        }

        rs.close();
        stmt.close();
    }

    private static void addItem() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter name of item: ");
        String name = scanner.nextLine();

        System.out.print("Enter price of item: ");
        double price = scanner.nextDouble();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO menu (name, price) VALUES (?, ?)");
        stmt.setString(1, name);
        stmt.setDouble(2, price);
        stmt.executeUpdate();

        System.out.println("Item added successfully");

        stmt.close();
        scanner.close();
    }

    private static void orderFoodAndDrink() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the food item you want to order: ");
        String foodName = scanner.nextLine();

        System.out.println("Enter the name of the drink you want to order: ");
        String drinkName = scanner.nextLine();

        PreparedStatement foodStmt = conn.prepareStatement("SELECT price FROM menu WHERE name = ?");
        foodStmt.setString(1, foodName);
        ResultSet foodRs = foodStmt.executeQuery();
        double foodPrice = 0;
        if (foodRs.next()) {
            foodPrice = foodRs.getDouble("price");
        } else {
            System.out.println("Food item not found in the menu");
            foodRs.close();
            foodStmt.close();
            scanner.close();
            return;
        }

        PreparedStatement drinkStmt = conn.prepareStatement("SELECT price FROM menu WHERE name = ?");
        drinkStmt.setString(1, drinkName);
        ResultSet drinkRs = drinkStmt.executeQuery();
        double drink
