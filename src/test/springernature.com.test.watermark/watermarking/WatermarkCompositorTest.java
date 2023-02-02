package isabelbatista.test.watermark.watermarking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import isabelbatista.test.watermark.ESBookRepository;
import isabelbatista.test.watermark.ESJournalRepository;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.documents.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by Isabel Batista on 06.05.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WatermarkCompositorTest {

    @InjectMocks
    private WatermarkCompositor compositor;

    @Mock
    WatermarkService serviceMock;

    @Mock
    ESBookRepository bookRepositoryMock;

    @Mock
    ESJournalRepository journalRepositoryMock;

    private Map<String, Book> books;
    private Journal journal;

    @Before
    public void setUp() {
        books = createDummyBooks();
        journal = createDummyJournal();
    }

    @Test
    public void shouldReturnExpectedBookWatermark() {
        final BookWatermark bookWatermark = compositor.generateBookWatermark(books.get("1"));

        assertNotNull(bookWatermark);
        assertThat(bookWatermark.getContent(), is(Content.BOOK.value()));
        assertThat(bookWatermark.getTitle(), is("The Dark Code"));
        assertThat(bookWatermark.getAuthor(), is("Bruce Wayne"));
        assertThat(bookWatermark.getTopic(), is(Topic.SCIENCE.value()));
    }

    @Test
    public void shouldReturnExpectedJournalWatermark() {
        final JournalWatermark journalWatermark = compositor.generateJournalWatermark(journal);

        assertNotNull(journalWatermark);
        assertThat(journalWatermark.getContent(), is(Content.JOURNAL.value()));
        assertThat(journalWatermark.getTitle(), is("Journal of human flight routes"));
        assertThat(journalWatermark.getAuthor(), is("Clark Kent"));
    }

    @Test
    public void shouldUpdateBookWithCorrectWatermark() throws PublicationNotFoundException, WatermarkingException {
        final Book book = books.get("1");

        final Ticket ticket = new Ticket();
        ticket.setId("1");
        ticket.setStatus(Status.PENDING.value());
        ticket.setContent(book.getContent());
        ticket.setPublicationId(book.getId());

        when(serviceMock.getPublicationById(ticket.getPublicationId())).thenReturn(book);
        when(bookRepositoryMock.save(book)).thenReturn(book);

        Publication publication = compositor.updatePublicationWithWatermark(ticket);

        verify(serviceMock).setTicketStatus(ticket, Status.IN_PROGRESS.value());
        verify(serviceMock).setTicketStatus(ticket, Status.FINISHED.value());

        assertNotNull(publication);
        assertNotNull(publication.getWatermark());
        assertThat(publication.getWatermark().getTitle(), is(book.getTitle()));
        assertThat(publication.getContent(), is(Content.BOOK.value()));
        assertThat(publication.getWatermark().getContent(), is(Content.BOOK.value()));
    }

    @Test
    public void shouldUpdateJournalWithCorrectWatermark() throws PublicationNotFoundException, WatermarkingException {

        Ticket ticket = createPendingDummyTicket(journal);

        when(serviceMock.getPublicationById(ticket.getPublicationId())).thenReturn(journal);
        when(journalRepositoryMock.save(journal)).thenReturn(journal);

        Publication publication = compositor.updatePublicationWithWatermark(ticket);

        verify(serviceMock).setTicketStatus(ticket, Status.IN_PROGRESS.value());
        verify(serviceMock).setTicketStatus(ticket, Status.FINISHED.value());

        assertNotNull(publication);
        assertNotNull(publication.getWatermark());
        assertThat(publication.getWatermark().getTitle(), is(journal.getTitle()));
        assertThat(publication.getContent(), is(Content.JOURNAL.value()));
        assertThat(publication.getWatermark().getContent(), is(Content.JOURNAL.value()));
    }

    private Map<String, Book> createDummyBooks() {
        final Book book = new Book();
        book.setContent(Content.BOOK.value());
        book.setId("1");
        book.setTitle("The Dark Code");
        book.setAuthor("Bruce Wayne");
        book.setTopic(Topic.SCIENCE.value());

        final Book book2 = new Book();
        book2.setContent(Content.BOOK.value());
        book2.setId("2");
        book2.setTitle("How to make money");
        book2.setAuthor("Dr. Evil");
        book2.setTopic(Topic.BUSINESS.value());

        Map<String, Book> booksMap = new HashMap<>();
        booksMap.put(book.getId(), book);
        booksMap.put(book2.getId(), book2);

        return booksMap;
    }

    private Journal createDummyJournal() {
        Journal journal = new Journal();
        journal.setId("1");
        journal.setAuthor("Clark Kent");
        journal.setTitle("Journal of human flight routes");
        journal.setContent(Content.JOURNAL.value());
        return journal;
    }

    private Ticket createPendingDummyTicket(Publication publication) {
        final Ticket ticket = new Ticket();
        ticket.setId("1");
        ticket.setStatus(Status.PENDING.value());
        ticket.setContent(publication.getContent());
        ticket.setPublicationId(publication.getId());
        return ticket;
    }
}
