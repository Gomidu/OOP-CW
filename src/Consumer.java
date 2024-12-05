public class Consumer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerRetrievalRate;
    private final int customerId;

    public Consumer(TicketPool ticketPool, int customerRetrievalRate, int customerId) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        while (true) {
            // Break condition will be handled by the TicketPool
            for (int i = 0; i < customerRetrievalRate; i++) {
                String ticket = ticketPool.removeTicket(customerId);
                if (ticket == null) {
                    return; // Exit if no more tickets
                }
            }
            try {
                Thread.sleep(1000); // Simulate retrieval rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}