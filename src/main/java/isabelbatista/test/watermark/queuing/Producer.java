package isabelbatista.test.watermark.queuing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Producer that pushes new elements of type @see Ticket to the queue for later processing by the @see RemovingConsumer.
 *
 * Created by Isabel Batista on 06.05.17.
 */
@Component
public class Producer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    private LinkedBlockingQueue queue;
    private boolean running;
    private Ticket ticket;

    @Autowired
    private WatermarkService service;

    public void init(LinkedBlockingQueue queue, Ticket ticket) {
        this.queue = queue;
        running = true;
        this.ticket = ticket;
    }

    /**
     * Returns the status of the producer thread.
     *
     * @return  The status of the producer thread as boolean (true = running, false = stopped).
     */
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {

        if(ticket == null) {
            logger.error("Put ticket to the queue failed. Ticket must not be null.");
        } else {
            try {
                queue.put(ticket);
                logger.info("Put ticket to the queue. Ticket Id: '" + ticket.getId() + "'");
                service.setTicketStatus(ticket, Status.PENDING.value());
            } catch (InterruptedException e) {
                logger.error("Couldn't put ticket with ID {} to the queue.", ticket.getId(), e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("Producer Completed.");
        running = false;
    }
}
