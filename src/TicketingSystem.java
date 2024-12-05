import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TicketingSystem {
    private static void saveToFile(Configuration config, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(config, writer);
            System.out.println("Configuration saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Configure Real-Time Ticketing System");

        // Get totalTickets with validation
        int totalTickets;
        while (true) {
            System.out.print("Enter total tickets available: ");
            try {
                totalTickets = scanner.nextInt();
                if (totalTickets > 0)
                    break;
                System.out.println("Please enter a positive integer.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }

        // Get ticketReleaseRate with validation
        int ticketReleaseRate;
        while (true) {
            System.out.print("Enter ticket release rate (tickets per second): ");
            try {
                ticketReleaseRate = scanner.nextInt();
                if (ticketReleaseRate > 0)
                    break;
                System.out.println("Please enter a positive integer.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }

        // Get customerRetrievalRate with validation
        int customerRetrievalRate;
        while (true) {
            System.out.print("Enter customer retrieval rate (tickets per second): ");
            try {
                customerRetrievalRate = scanner.nextInt();
                if (customerRetrievalRate > 0)
                    break;
                System.out.println("Please enter a positive integer.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }

        // Get maxTicketCapacity with validation and check against totalTickets
        int maxTicketCapacity;
        while (true) {
            System.out.print("Enter max ticket capacity: ");
            try {
                maxTicketCapacity = scanner.nextInt();
                if (maxTicketCapacity >= totalTickets) {
                    break;
                } else {
                    System.out.println("Max ticket capacity must be greater than or equal to total tickets.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }

        // Create and save configuration
        Configuration config = new Configuration(totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
        // Save configuration to a JSON file
        saveToFile(config, "configurations.json");

        // Initialize TicketPool
        TicketPool ticketPool = new TicketPool(maxTicketCapacity, totalTickets);

        // Calculate tickets per vendor
        int ticketsPerVendor = totalTickets / 5;
        int remainingTickets = totalTickets % 5;

        // Create and start Vendor threads (5 vendors)
        Thread[] vendorThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            int vendorTickets = ticketsPerVendor + (i < remainingTickets ? 1 : 0);
            Vendor vendor = new Vendor(ticketPool, ticketReleaseRate, vendorTickets, i + 1);
            vendorThreads[i] = new Thread(vendor);
            vendorThreads[i].start();
        }

        // Create and start Consumer threads (5 customers)
        Thread[] consumerThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            Consumer consumer = new Consumer(ticketPool, customerRetrievalRate, i + 1);
            consumerThreads[i] = new Thread(consumer);
            consumerThreads[i].start();
        }

        while (true) {
            if (ticketPool.isComplete()) {
                System.out.println("===== Ticket Operation Finished =====");
                System.out.println("All tickets have been produced and consumed successfully.");
                break;
            }
            try {
                Thread.sleep(3000); // Poll every
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        int remainingllTickets = config.getMaxTicketCapacity() - config.getTotalTickets();
        System.out.println("Remaining tickets in the Ticket pool: " + remainingllTickets);

        try {
            // Wait for all vendor threads to complete
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
            }
            // Wait for all consumer threads to complete
            for (Thread consumerThread : consumerThreads) {
                consumerThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            scanner.close();
        }
        // Ticket operation completed message
    }

}