package isabelbatista.test.watermark.queueing;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import isabelbatista.test.watermark.ESTicketRepository;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.documents.Book;
import isabelbatista.test.watermark.documents.Content;
import isabelbatista.test.watermark.documents.PublicationNotFoundException;
import isabelbatista.test.watermark.queuing.Producer;
import isabelbatista.test.watermark.queuing.RemovingConsumer;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;
import isabelbatista.test.watermark.watermarking.WatermarkCompositor;
import isabelbatista.test.watermark.watermarking.WatermarkingException;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Isabel Batista on 10.05.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
//@RunWith(MockitoJUnitRunner.class)
public class RemovingConsumerTest {

    @InjectMocks
    private RemovingConsumer removingConsumer;

    @Autowired
    Producer producer;

    @Mock
    WatermarkCompositor compositorMock;

    @Mock
    WatermarkService serviceMock;

    @Mock
    ESTicketRepository ticketRepositoryMock;

    private LinkedBlockingQueue queue;
    private List<Ticket> tickets;

    @Before
    public void setUp() {
        queue = new LinkedBlockingQueue(10);
        tickets = createDummyTickets();
        queue.addAll(tickets);
    }

    @Test
    public void expectGetAndRemoveTicketFromQueue() throws PublicationNotFoundException, WatermarkingException {

        assertEquals(queue.size(), 2);

        removingConsumer.init(queue, producer);
        when(compositorMock.updatePublicationWithWatermark(tickets.get(0))).thenReturn(new Book());

        removingConsumer.run();

        assertThat(queue.size(), is(1));
    }

    @Test
    public void expectExceptionIfPublicationNotFound() throws PublicationNotFoundException, WatermarkingException {

        removingConsumer.init(queue, producer);

        when(serviceMock.getPublicationById(tickets.get(0).getPublicationId())).thenReturn(null);
        when(compositorMock.updatePublicationWithWatermark(tickets.get(0))).thenThrow(PublicationNotFoundException.class);

        removingConsumer.run();

        verify(compositorMock).updatePublicationWithWatermark(tickets.get(0));
    }

    @Test
    public void expectTicketStatusFailedIfNoPublicationFound() throws PublicationNotFoundException, WatermarkingException {

        removingConsumer.init(queue, producer);

        when(compositorMock.updatePublicationWithWatermark(tickets.get(0))).thenThrow(PublicationNotFoundException.class);
        when(serviceMock.getPublicationById(tickets.get(0).getPublicationId())).thenReturn(null);
        when(serviceMock.setTicketStatus(tickets.get(0), Status.FAILED.value())).thenReturn(tickets.get(0));

        removingConsumer.run();

        verify(serviceMock).setTicketStatus(tickets.get(0), Status.FAILED.value());
    }

    private List<Ticket> createDummyTickets() {
        Ticket ticketOne = new Ticket();
        ticketOne.setId("1");
        ticketOne.setContent(Content.BOOK.value());
        ticketOne.setStatus(Status.PENDING.value());
        ticketOne.setPublicationId("1");

        Ticket ticketTwo = new Ticket();
        ticketTwo.setId("2");
        ticketTwo.setContent(Content.JOURNAL.value());
        ticketTwo.setStatus(Status.PENDING.value());
        ticketTwo.setPublicationId("2");

        return Lists.newArrayList(ticketOne, ticketTwo);
    }
}
