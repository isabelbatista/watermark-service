package isabelbatista.test.watermark.queueing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import isabelbatista.test.watermark.documents.Content;
import isabelbatista.test.watermark.queuing.Producer;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;

import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Isabel Batista on 10.05.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerTest {

    @Autowired
    private Producer producer;

    private LinkedBlockingQueue queue;
    private Ticket ticket;

    @Before
    public void setUp() {
        queue = new LinkedBlockingQueue(10);
        ticket = createDummyTicket();
    }

    @Test
    public void expectProducerIsRunning() {
        assertFalse(producer.isRunning());

        producer.init(queue, ticket);

        assertTrue(producer.isRunning());
    }

    @Test
    public void expectOneTicketInQueue() {
        producer.init(queue, ticket);
        producer.run();

        assertThat(queue.size(), is(1));
    }

    @Test
    public void expectDefinedTicketInQueue() {
        producer.init(queue, ticket);
        producer.run();

        Ticket ticketFromQueue = (Ticket) queue.iterator().next();
        assertThat(ticketFromQueue.getPublicationId(), is("1"));
        assertThat(ticketFromQueue.getContent(), is(Content.BOOK.value()));
    }

    @Test
    public void expectPendingTicketStatusWhenPushedToQueue() {
        producer.init(queue, ticket);
        producer.run();

        Ticket ticketFromQueue = (Ticket) queue.iterator().next();
        assertThat(ticketFromQueue.getStatus(), is(Status.PENDING.value()));
    }

    @Test
    public void expectTwoTicketsInQueue() {
        Ticket ticket2 = createDummyTicket();
        ticket2.setPublicationId("2");

        producer.init(queue, ticket);
        producer.run();
        producer.init(queue, ticket2);
        producer.run();

        assertThat(queue.size(), is(2));
    }

    private Ticket createDummyTicket() {
        Ticket ticket = new Ticket();
        ticket.setStatus(Status.STARTED.value());
        ticket.setContent(Content.BOOK.value());
        ticket.setPublicationId("1");
        return ticket;
    }
}
