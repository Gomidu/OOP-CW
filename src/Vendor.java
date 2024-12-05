public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;
    private final int totalTickets;
    private final int vendorId;

    public Vendor(TicketPool ticketPool, int releaseRate, int totalTickets, int vendorId) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
        this.totalTickets = totalTickets;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        int ticketsProduced = 0;

        while (ticketsProduced < totalTickets) {
            int ticketsToAdd = Math.max(releaseRate, totalTickets - ticketsProduced);
            ticketPool.addTickets(vendorId, ticketsToAdd);
            ticketsProduced += ticketsToAdd;

            try {
                Thread.sleep(1000); // Simulate release rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Vendor " + vendorId + " finished producing tickets.");
    }
}
