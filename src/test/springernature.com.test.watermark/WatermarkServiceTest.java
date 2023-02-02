package isabelbatista.test.watermark;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import isabelbatista.test.watermark.documents.*;
import isabelbatista.test.watermark.queuing.QueueForWatermarking;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;
import isabelbatista.test.watermark.watermarking.TicketNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


/**
 * Created by Isabel Batista on 11.05.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WatermarkServiceTest {

    @InjectMocks
    private WatermarkService watermarkService;

    @Mock
    private ESBookRepository bookRepositoryMock;

    @Mock
    private ESJournalRepository journalRepositoryMock;

    @Mock
    private ESTicketRepository ticketRepositoryMock;

    @Mock
    private QueueForWatermarking queueForWatermarkingMock;

    private Book book;
    private Journal journal;
    private Ticket bookTicket;
    private Ticket journalTicket;

    @Before
    public void setUp() {
        book = createDummyBook();
        journal = createDummyJournal();
        bookTicket = createDummyTicket(book);
        journalTicket = createDummyTicket(journal);
    }

    @Test
    public void shouldReturnGeneratedTicket() throws PublicationNotFoundException {

        Ticket spyTicket = spy(bookTicket);
        spyTicket.setId("1");

        when(bookRepositoryMock.findOne(book.getId())).thenReturn(book);
        when(ticketRepositoryMock.save(bookTicket)).thenReturn(spyTicket);

        Ticket generatedTicket = watermarkService.getTicketForPollingStatus(book.getId());

        assertThat(bookTicket.getPublicationId(), is(spyTicket.getPublicationId()));
        assertNull(bookTicket.getId());
        assertNotNull(spyTicket.getId());
    }

    @Test
    public void shouldReturnBookByGivenTicketId() {

        when(ticketRepositoryMock.findOne(bookTicket.getId())).thenReturn(bookTicket);
        when(bookRepositoryMock.findOne(bookTicket.getPublicationId())).thenReturn(book);

        final Publication publication = watermarkService.getPublicationById(book.getId());

        assertNotNull(publication);
        assertThat(publication.getId(), is(book.getId()));
        assertThat(publication.getTitle(), is(book.getTitle()));
    }

    @Test
    public void shouldReturnJournalByGivenTicketId() {

        when(ticketRepositoryMock.findOne(journalTicket.getId())).thenReturn(journalTicket);
        when(journalRepositoryMock.findOne(journalTicket.getPublicationId())).thenReturn(journal);

        final Publication publication = watermarkService.getPublicationById(journal.getId());

        assertNotNull(publication);
        assertThat(publication.getId(), is(journal.getId()));
        assertThat(publication.getTitle(), is(journal.getTitle()));
    }

    @Test
    public void shouldReturnBookByGivenPublicationId() {

        when(bookRepositoryMock.findOne(book.getId())).thenReturn(book);

        final Publication publication = watermarkService.getPublicationById(book.getId());

        assertNotNull(publication);
        assertThat(publication.getTitle(), is(book.getTitle()));
    }

    @Test
    public void shouldReturnJournalByGivenPublicationId() {

        when(journalRepositoryMock.findOne(journal.getId())).thenReturn(journal);

        final Publication publication = watermarkService.getPublicationById(journal.getId());

        assertNotNull(publication);
        assertThat(publication.getTitle(), is(journal.getTitle()));
    }

    @Test
    public void shouldReturnStatusOfGivenTicket() throws TicketNotFoundException {

        when(ticketRepositoryMock.findOne(bookTicket.getId())).thenReturn(bookTicket);

        String status = watermarkService.getTicketStatus(bookTicket.getId());

        assertNotNull(status);
        assertThat(status, is(Status.STARTED.value()));
    }

    @Test(expected = TicketNotFoundException.class)
    public void shouldThrowExceptionIfTicketNotFound() throws TicketNotFoundException {
        String status = watermarkService.getTicketStatus(bookTicket.getId());
    }

    @Test
    public void shouldSetTicketStatusFailed() {

        when(ticketRepositoryMock.save(bookTicket)).thenReturn(bookTicket);

        Ticket updatedTicket = watermarkService.setTicketStatus(bookTicket, Status.FAILED.value());

        assertNotNull(updatedTicket);
        assertThat(updatedTicket.getStatus(), is(Status.FAILED.value()));
    }

    private Book createDummyBook() {

        final Book book = new Book();
        book.setContent(Content.BOOK.value());
        book.setId("1");
        book.setTitle("The Dark Code");
        book.setAuthor("Bruce Wayne");
        book.setTopic(Topic.SCIENCE.value());

        return book;
    }

    private Journal createDummyJournal() {
        Journal journal = new Journal();
        journal.setId("1");
        journal.setAuthor("Clark Kent");
        journal.setTitle("Journal of human flight routes");
        journal.setContent(Content.JOURNAL.value());
        return journal;
    }

    private Ticket createDummyTicket(Publication publication) {
        final Ticket ticket = new Ticket();
        ticket.setContent(publication.getContent());
        ticket.setPublicationId(publication.getId());
        ticket.setStatus(Status.STARTED.value());
        return ticket;
    }
}
