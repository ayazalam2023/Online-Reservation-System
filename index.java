import java.io.*;
import java.util.*;

public class OnlineReservationSystem {

    // Map to store user credentials (username -> password)
    private static final Map<String, String> users = new HashMap<>();

    // File to store reservations
    private static final String RESERVATIONS_FILE = "reservations.txt";

    // Scanner for user input
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize users directly in the code
        initializeUsers();

        // Welcome message and login verification
        System.out.println("Welcome to the Online Reservation System!");
        if (!authenticateUser()) {
            System.out.println("Login failed. Please try again.");
            return;
        }

        // Main menu loop for the reservation system
        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Make a Reservation");
            System.out.println("2. Cancel a Reservation");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left by nextInt()

            switch (choice) {
                case 1:
                    makeReservation();
                    break;
                case 2:
                    cancelReservation();
                    break;
                case 3:
                    System.out.println("Thank you for using the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please select again.");
            }
        }
    }

    /**
     * Initializes a list of users and their credentials directly within the code.
     */
    private static void initializeUsers() {
        // Adding some sample users (username -> password)
        users.put("admin", "password123");
        users.put("user1", "password456");
        users.put("user2", "pass789");
        // You can add more users here
    }

    /**
     * Handles user authentication by checking login details from the in-memory user list.
     * 
     * @return true if login is successful, false otherwise
     */
    private static boolean authenticateUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Verify user credentials from the users map
        if (users.containsKey(username) && users.get(username).equals(password)) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Incorrect username or password.");
            return false;
        }
    }

    /**
     * Handles the process of making a new reservation by collecting user inputs and saving it to the 'reservations.txt' file.
     */
    private static void makeReservation() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATIONS_FILE, true))) {
            System.out.print("Enter Train Number: ");
            String trainNumber = scanner.nextLine();
            System.out.print("Enter Class Type (AC, Sleeper, etc.): ");
            String classType = scanner.nextLine();
            System.out.print("Enter Date of Journey (DD-MM-YYYY): ");
            String dateOfJourney = scanner.nextLine();
            System.out.print("Enter Source Station: ");
            String source = scanner.nextLine();
            System.out.print("Enter Destination Station: ");
            String destination = scanner.nextLine();

            // Generate a unique PNR number
            String pnr = UUID.randomUUID().toString();
            writer.write(pnr + "," + trainNumber + "," + classType + "," + dateOfJourney + "," + source + "," + destination);
            writer.newLine();

            System.out.println("Reservation confirmed! Your PNR number is: " + pnr);
        } catch (IOException e) {
            System.out.println("Error saving the reservation: " + e.getMessage());
        }
    }

    /**
     * Handles the process of cancelling a reservation by removing the reservation entry from 'reservations.txt'.
     */
    private static void cancelReservation() {
        System.out.print("Enter your PNR number: ");
        String pnr = scanner.nextLine();

        File inputFile = new File(RESERVATIONS_FILE);
        File tempFile = new File("temp_reservations.txt");

        boolean reservationFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] reservationDetails = line.split(",");
                // Only write back reservations that do not match the PNR for cancellation
                if (!reservationDetails[0].equals(pnr)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    reservationFound = true;
                }
            }

        } catch (IOException e) {
            System.out.println("Error processing the cancellation: " + e.getMessage());
        }

        // If the reservation was found and deleted, replace the original file
        if (reservationFound) {
            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                System.out.println("Reservation with PNR " + pnr + " has been successfully canceled.");
            } else {
                System.out.println("Error updating the reservations file.");
            }
        } else {
            System.out.println("No reservation found with the provided PNR.");
            tempFile.delete(); // Delete the temp file if nothing was changed
        }
    }
}