package isabelbatista.test.watermark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import isabelbatista.test.watermark.documents.*;
import isabelbatista.test.watermark.preparation.PreparationService;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * Integration Tests for the Watermark Rest Controller.
 *
 * Created by Isabel Batista on 05.05.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK)
@ComponentScan
@ActiveProfiles("test")
public class WatermarkRestControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    PreparationService preparationService;

    @Autowired
    WatermarkService watermarkService;

    private MockMvc mvc;

    private MockRestServiceServer mockRestServiceServer;

    private Book book;
    private Journal journal;

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockRestServiceServer = MockRestServiceServer.createServer(new RestTemplate());

        createAndSaveDummyPublications();
    }

    /**
     * Test for /watermark/watermarkPublication/?id=
     *
     * Scenario: Starting the watermarking process returns a ticket ID.
     *
     * @throws Exception
     */
    @Test
    public void shouldGetTicketForWatermarkingPublicationRequest() throws Exception {
        final MvcResult watermarkingResult = mvc.perform(get("/watermark/watermarkPublication/?id=" + book.getId()).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final String ticketId = watermarkingResult.getResponse().getContentAsString();
        notNull(ticketId, "The ticket id must not be null");
    }

    /**
     * Test for /watermark/ticketStatus/?ticketId=
     *
     * Scenario: Ticket Status Finished is returned after successful watermarking.
     *
     * @throws Exception
     */
    @Test
    public void shouldGetStatusFinishedForSuccessfullyWatermarkedPublication() throws Exception {
        final MvcResult watermarkingResult = mvc.perform(get("/watermark/watermarkPublication/?id=" + book.getId()).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread.sleep(500); // sleep, otherwise we are too fast to check the real status

        // request for the status of the ticket --> should be finished
        final String ticketId = watermarkingResult.getResponse().getContentAsString();
        final MvcResult statusResult = mvc.perform(get("/watermark/ticketStatus/?ticketId=" + ticketId).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final String foundStatus = statusResult.getResponse().getContentAsString();
        isTrue(foundStatus.equals(Status.FINISHED.value()), "Status 'Finished' is expected but it is '" + foundStatus + "'");

        // reset Ticket Status to Started
        Ticket ticket = watermarkService.getTicketById(ticketId);
        watermarkService.setTicketStatus(ticket, Status.STARTED.value());
    }

    /**
     * Test for /watermark/getPublication/?ticketId=
     *
     * Scenario: Watermark is set on the returned publication of content type @see Book.
     *
     * @throws Exception
     */
    @Test
    public void shouldGetWatermarkedBook() throws Exception {
        final MvcResult watermarkingResult = mvc.perform(get("/watermark/watermarkPublication/?id=" + book.getId()).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread.sleep(700); // sleep, otherwise we are too fast to check the set watermark (is not set yet)

        final String ticketId = watermarkingResult.getResponse().getContentAsString();
        mvc.perform(get("/watermark/getPublication/?ticketId=" + ticketId).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watermark.content", is("book")))
                .andExpect(jsonPath("$.watermark.title", is(book.getTitle())))
                .andExpect(jsonPath("$.watermark.author", is(book.getAuthor())))
                .andExpect(jsonPath("$.watermark.topic", is(Topic.SCIENCE.value())))
                .andReturn();
    }

    /**
     * Test for /watermark/getPublication/?ticketId=
     *
     * Scenario: Watermark is set on the returned publication of content type @see Journal.
     *
     * @throws Exception
     */
    @Test
    public void shouldGetWatermarkedJournal() throws Exception {
        final MvcResult watermarkingResult = mvc.perform(get("/watermark/watermarkPublication/?id=" + journal.getId()).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Thread.sleep(700); // sleep, otherwise we are too fast to check the set watermark (is not set yet)

        final String ticketId = watermarkingResult.getResponse().getContentAsString();
        mvc.perform(get("/watermark/getPublication/?ticketId=" + ticketId).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watermark.content", is("journal")))
                .andExpect(jsonPath("$.watermark.title", is(journal.getTitle())))
                .andExpect(jsonPath("$.watermark.author", is(journal.getAuthor())))
                .andReturn();
    }

    private void createAndSaveDummyPublications() throws InvalidEnumException {
        book = new Book();
        book.setContent(Content.BOOK.value());
        book.setTitle("The Dark Code");
        book.setAuthor("Bruce Wayne");
        book.setTopic(Topic.SCIENCE.value());
        book = (Book) preparationService.savePublication(book);

        journal = new Journal();
        journal.setContent(Content.JOURNAL.value());
        journal.setTitle("Journal of human flight routes");
        journal.setAuthor("Clark Kent");
        journal = (Journal) preparationService.savePublication(journal);
    }
}