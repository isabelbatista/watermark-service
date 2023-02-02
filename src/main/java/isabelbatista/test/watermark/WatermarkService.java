package isabelbatista.test.watermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import isabelbatista.test.watermark.documents.*;
import isabelbatista.test.watermark.queuing.QueueForWatermarking;
import isabelbatista.test.watermark.watermarking.Status;
import isabelbatista.test.watermark.watermarking.Ticket;
import isabelbatista.test.watermark.watermarking.TicketNotFoundException;
import isabelbatista.test.watermark.watermarking.WatermarkCompositor;

/**
 * Implements the service interface that provides necessary methods to watermark publications etc.
 *
 * Created by Isabel Batista on 05.05.17.
 */

@Service
public class WatermarkService implements isabelbatista.test.watermark.Service {

    final Logger logger = LoggerFactory.getLogger(WatermarkService.class);

    @Autowired
    private ESBookRepository bookRepository;

    @Autowired
    private ESJournalRepository journalRepository;

    @Autowired
    private ESTicketRepository ticketRepository;

    @Autowired
    private QueueForWatermarking queueForWatermarking;

    @Autowired
    private WatermarkCompositor generator;

    /**
     * {@inheritDoc}
     */
    public Ticket watermarkPublication(final String publicationId) throws PublicationNotFoundException {

        final Ticket ticket = getTicketForPollingStatus(publicationId);

        logger.debug("Send ticket with ID {} to the queue.", ticket.getId());
        queueForWatermarking.startQueuing(ticket);

        return ticket;
    }

    /**
     * {@inheritDoc}
     */
    public Publication getPublicationByTicketId(final String ticketId) throws PublicationNotFoundException {

        final Ticket ticket = ticketRepository.findOne(ticketId);
        Publication publication = getPublicationById(ticket.getPublicationId());
        return publication;
    }

    /**
     * {@inheritDoc}
     */
    public String getTicketStatus(final String ticketId) throws TicketNotFoundException {
        final Ticket ticket = ticketRepository.findOne(ticketId);
        if(ticket == null) {
            logger.error("Ticket ID does not exist.", ticketId);
            throw new TicketNotFoundException("The ticket with ID '" + ticketId + "' does not exist. Please check existing tickets.");
        }
        return ticket.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    public Ticket setTicketStatus(Ticket ticket, final String status) {
        logger.info("Set status of ticket ID {} to {}", ticket.getId(), status);
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    /**
     * {@inheritDoc}
     */
    public Publication getPublicationById(final String publicationId) {
        Publication publication = (Book) bookRepository.findOne(publicationId);
        if(publication == null) {
            publication = (Journal) journalRepository.findOne(publicationId);
        }
        return publication;
    }

    /**
     * Returns a new generated @see Ticket for polling the watermarking process status and saves it to the persistence layer.
     *
     * @param publicationId                     ID of the @see Publication (book or journal) that has to be watermarked
     * @return                                  Ticket with a set ID to poll the status of the watermarking process.
     * @throws PublicationNotFoundException     Exception if no @see Publication can be found to the given publication ID.
     */
    public Ticket getTicketForPollingStatus(final String publicationId) throws PublicationNotFoundException {

        logger.debug("Generate and save ticket for publication with ID {}", publicationId);

        // method needs the publication to set the content type to the ticket
        final Publication publication = getPublicationById(publicationId);
        if(publication == null) {
            throw new PublicationNotFoundException("The publication with ID '" + publicationId + "' cannot be found.");
        }

        Ticket ticket = generateInitialTicket(publication);

        logger.debug("Persist new generated ticket for publication with ID {}", publicationId);
        return ticketRepository.save(ticket);
    }

    /**
     * Returns the ticket by the given ticket ID.
     *
     * @param ticketId      Ticket ID of the searched persisted @see Ticket.
     * @return              Ticket object that is persisted.
     */
    public Ticket getTicketById(String ticketId) {
        return ticketRepository.findOne(ticketId);
    }

    private Ticket generateInitialTicket(Publication publication) {

        logger.info("Generate ticket for publication with ID {}", publication.getId());

        Ticket ticket = new Ticket();
        ticket.setContent(publication.getContent());
        ticket.setPublicationId(publication.getId());
        ticket.setStatus(Status.STARTED.value());
        return ticket;
    }
}