import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private final List<String> tickets;
    private final int maxCapacity;
    private int ticketCounter;
    private int totalTicketsProduced;
    private final int totalTickets;

    public TicketPool(int maxCapacity, int totalTickets) {
        this.tickets = new LinkedList<>();
        this.maxCapacity = maxCapacity;
        this.ticketCounter = 0;
        this.totalTicketsProduced = 0;
        this.totalTickets = totalTickets;
    }

    public synchronized void addTickets(int vendorId, int count) {
        // Ensure we don't add more tickets than the total tickets
        count = Math.min(count, totalTickets - totalTicketsProduced);

        while (tickets.size() + count > maxCapacity) {
            try {
                System.out.println("Vendor " + vendorId + " waiting to add tickets");
                wait(); // Wait if adding exceeds max capacity
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        for (int i = 0; i < count; i++) {
            String ticket = "Ticket " + (++ticketCounter) + " (Vendor " + vendorId + ")";
            tickets.add(ticket);
            totalTicketsProduced++;
        }

        System.out.println("Vendor " + vendorId + " added " + count + " tickets. Total tickets: " + tickets.size());
        notifyAll(); // Notify waiting threads
    }

    public synchronized String removeTicket(int customerId) {
        // Wait until tickets are available
        while (tickets.isEmpty()) {
            try {
                System.out.println("Consumer " + customerId + " waiting");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        // Remove the first ticket
        String ticket = tickets.remove(0);
        System.out.println("Customer " + customerId + " purchased: " + ticket + ". Remaining tickets: " + tickets.size());

        notifyAll(); // Notify waiting threads
        return ticket;
    }
    // Method to check if all tickets have been produced and consumed
    public synchronized boolean isComplete() {
        return totalTicketsProduced >= totalTickets && tickets.isEmpty();
    }

    public synchronized int getRemainingTickets() {
        return tickets.size();
    }
}
