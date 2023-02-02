package isabelbatista.test.watermark.queuing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import isabelbatista.test.watermark.ESTicketRepository;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.documents.PublicationNotFoundException;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;
import isabelbatista.test.watermark.watermarking.WatermarkCompositor;
import isabelbatista.test.watermark.watermarking.WatermarkingException;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Responsible to remove one @see Ticket element after the other from the queue and start the process of
 * adding a @see Watermark to the @see Publication that is related to the @see Ticket.
 *
 * Created by Isabel Batista on 07.05.17.
 */
@Component
public class RemovingConsumer implements Runnable {

    final Logger logger = LoggerFactory.getLogger(RemovingConsumer.class);

    @Autowired
    private WatermarkCompositor compositor;

    private LinkedBlockingQueue queue;
    private Producer producer;

    @Autowired
    private ESTicketRepository ticketRepository;

    @Autowired
    private WatermarkService watermarkService;

    public void init(LinkedBlockingQueue queue, Producer producer) {
        this.queue = queue;
        this.producer = producer;
    }

    @Override
    public void run() {
        Ticket ticket = null;
        try {
            ticket = (Ticket) queue.take();
            logger.info("Ticket with ID {} was removed from queue to watermark the related publication.", ticket.getId());
            compositor.updatePublicationWithWatermark(ticket);
            Thread.sleep(2000); // for simulation of the processing
        } catch (InterruptedException e) {
            logger.error("Ticket consumer has encountered an error during removing ticket with ID {} from queue.", ticket.getId(), e.getMessage());
            watermarkService.setTicketStatus(ticket, Status.FAILED.value());
            e.printStackTrace();
        } catch (PublicationNotFoundException e) {
            logger.error("Given publication couldn't be found.", e.getMessage());
            watermarkService.setTicketStatus(ticket, Status.FAILED.value());
            e.printStackTrace();
        } catch (WatermarkingException e) {
            logger.error("Something went wrong during watermarking. ", e.getMessage());
            watermarkService.setTicketStatus(ticket, Status.FAILED.value());
            e.printStackTrace();
        }
    }
}
