import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class HotelManagement {
    static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_management";
    static final String USER = "root";
    static final String PASS = "Mishra*1";

    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            int choice;
            do {
                System.out.println("\n=== HOTEL MANAGEMENT SYSTEM ===");
                System.out.println("1. Book Room");
                System.out.println("2. View All Bookings");
                System.out.println("3. Checkout");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> bookRoom();
                    case 2 -> viewBookings();
                    case 3 -> checkout();
                    case 4 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice != 4);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookRoom() {
        try {
            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();
    
            // Show available room types
            System.out.println("Available Room Types:");
            String roomQuery = "SELECT DISTINCT room_type FROM rooms WHERE is_available = TRUE";
            ResultSet types = conn.createStatement().executeQuery(roomQuery);
            while (types.next()) {
                System.out.println("- " + types.getString("room_type"));
            }
    
            System.out.print("Enter Room Type to Book: ");
            String roomType = sc.nextLine();
    
            // Find available room of that type
            String availableRoomQuery = "SELECT room_no FROM rooms WHERE room_type = ? AND is_available = TRUE LIMIT 1";
            PreparedStatement pst = conn.prepareStatement(availableRoomQuery);
            pst.setString(1, roomType);
            ResultSet rs = pst.executeQuery();
    
            if (!rs.next()) {
                System.out.println("No available rooms of this type. Try a different type.");
                return;
            }
    
            int roomNo = rs.getInt("room_no");
    
            // Book room and get generated customer ID
            String insertQuery = "INSERT INTO customers (name, phone, room_no, check_in) VALUES (?, ?, ?, NOW())";
            pst = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setInt(3, roomNo);
            pst.executeUpdate();
    
            ResultSet generatedKeys = pst.getGeneratedKeys();
            int customerId = -1;
            if (generatedKeys.next()) {
                customerId = generatedKeys.getInt(1);
            }
    
            // Update room availability
            String updateRoom = "UPDATE rooms SET is_available = FALSE WHERE room_no = ?";
            pst = conn.prepareStatement(updateRoom);
            pst.setInt(1, roomNo);
            pst.executeUpdate();
    
            System.out.println("Room " + roomNo + " booked successfully!");
            System.out.println("Your Customer ID is: " + customerId + " (Please save it for checkout)");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void viewBookings() {
        try {
            String query = "SELECT * FROM customers";
            ResultSet rs = conn.createStatement().executeQuery(query);

            System.out.printf("\n%-5s %-15s %-10s %-8s %-20s %-20s %-5s %-10s\n",
                    "ID", "Name", "Phone", "Room", "Check-in", "Check-out", "Days", "Bill");

            while (rs.next()) {
                System.out.printf("%-5d %-15s %-10s %-8d %-20s %-20s %-5d %-10.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("room_no"),
                        rs.getString("check_in"),
                        rs.getString("check_out") == null ? "N/A" : rs.getString("check_out"),
                        rs.getInt("days_stayed"),
                        rs.getDouble("total_bill"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void checkout() {
        try {
            System.out.print("Enter Customer ID for Checkout: ");
            int id = sc.nextInt();
            sc.nextLine();

            // Get check-in and room info
            String getQuery = "SELECT check_in, room_no FROM customers WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(getQuery);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("Customer not found.");
                return;
            }

            Timestamp checkInTime = rs.getTimestamp("check_in");
            int roomNo = rs.getInt("room_no");

            LocalDateTime checkIn = checkInTime.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long days = ChronoUnit.DAYS.between(checkIn, now);
            if (days == 0) days = 1; // Minimum 1 day charge

            // Get price per day
            String priceQuery = "SELECT price_per_day FROM rooms WHERE room_no = ?";
            pst = conn.prepareStatement(priceQuery);
            pst.setInt(1, roomNo);
            ResultSet rs2 = pst.executeQuery();
            rs2.next();
            double price = rs2.getDouble("price_per_day");
            double totalBill = days * price;

            // Update checkout info
            String updateQuery = "UPDATE customers SET check_out = NOW(), days_stayed = ?, total_bill = ? WHERE id = ?";
            pst = conn.prepareStatement(updateQuery);
            pst.setInt(1, (int) days);
            pst.setDouble(2, totalBill);
            pst.setInt(3, id);
            pst.executeUpdate();

            // Make room available
            pst = conn.prepareStatement("UPDATE rooms SET is_available = TRUE WHERE room_no = ?");
            pst.setInt(1, roomNo);
            pst.executeUpdate();

            System.out.println("Checkout complete. Total Bill: ₹" + totalBill + " for " + days + " day(s).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
