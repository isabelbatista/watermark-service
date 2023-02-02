package isabelbatista.test.watermark.watermarking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import isabelbatista.test.watermark.ESBookRepository;
import isabelbatista.test.watermark.ESJournalRepository;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.documents.*;

/**
 * Implementation of the Compositor interface to generate and include a watermark to a given publication (book or journal).
 *
 * Created by Isabel Batista on 06.05.17.
 */
@Component
public class WatermarkCompositor implements Compositor {

    final Logger logger = LoggerFactory.getLogger(WatermarkCompositor.class);

    @Autowired
    private WatermarkService service;

    @Autowired
    private ESBookRepository bookRepository;

    @Autowired
    private ESJournalRepository journalRepository;

    /**
     * {@inheritDoc}
     */
    public BookWatermark generateBookWatermark(Book book) {
        logger.info("Generate book watermark for the book with ID {}", book.getId());
        BookWatermark watermark = new BookWatermark();
        watermark.setContent(book.getContent());
        watermark.setTitle(book.getTitle());
        watermark.setAuthor(book.getAuthor());
        watermark.setTopic(book.getTopic());
        return watermark;
    }

    /**
     * {@inheritDoc}
     */
    public JournalWatermark generateJournalWatermark(Journal journal) {
        logger.info("Generate book watermark for the journal with ID {}", journal.getId());
        JournalWatermark watermark = new JournalWatermark();
        watermark.setContent(journal.getContent());
        watermark.setTitle(journal.getTitle());
        watermark.setAuthor(journal.getAuthor());
        return watermark;
    }

    /**
     * {@inheritDoc}
     */
    public Publication updatePublicationWithWatermark(final Ticket ticket) throws PublicationNotFoundException, WatermarkingException {
        final Publication publication = service.getPublicationById(ticket.getPublicationId());
        if(publication == null) {
            service.setTicketStatus(ticket, Status.FAILED.value());
            logger.error("Watermarking failed. The publication with ID {} does not exist.", ticket.getPublicationId());
            throw new PublicationNotFoundException("Watermarking failed. The publication with ID '" + ticket.getPublicationId() + "' does not exist.");
        }
        service.setTicketStatus(ticket, Status.IN_PROGRESS.value());

        final Watermark watermark = generateWatermarkRelatedToPublication(publication);
        final Publication watermarkedPublication = addWatermarkToPublication(publication, watermark);
        if(watermarkedPublication != null && watermarkedPublication.getWatermark() != null) {
            service.setTicketStatus(ticket, Status.FINISHED.value());
        } else {
            logger.error("No watermark added to the publication with ID {}", publication.getId());
            service.setTicketStatus(ticket, Status.FAILED.value());
            throw new WatermarkingException("No watermark added to the publication with ID " + publication.getId());
        }
        return watermarkedPublication;
    }

    private Publication addWatermarkToPublication(Publication publication, Watermark watermark) throws PublicationNotFoundException {

        Publication watermarkedPublication = null;
        String content = publication.getContent();

        logger.debug("Add {} watermark to the publication with ID {}", content, publication.getId());

        if(content.equals(Content.BOOK.value())) {
            publication.setWatermark(((BookWatermark) watermark));
            watermarkedPublication = bookRepository.save(((Book) publication));
        } else if (content.equals(Content.JOURNAL.value())) {
            publication.setWatermark(((JournalWatermark) watermark));
            watermarkedPublication = journalRepository.save(((Journal) publication));
        } else {
            logger.error("Watermarking failed. Given content type '{}' of the publication with ID {} not valid.", content, publication.getId());
            throw new PublicationNotFoundException("Watermarking failed. Given content type of the publication not valid: '" + content + "'");
        }
        return watermarkedPublication;
    }

    private Watermark generateWatermarkRelatedToPublication(final Publication publication) throws PublicationNotFoundException {
        final String content = publication.getContent();
        Watermark watermark = null;
        if(content.equals(Content.BOOK.value())) {
            logger.debug("Generate appropriate watermark for a {}", content);
            watermark = (BookWatermark) generateBookWatermark(((Book) publication));
        } else if (content.equals(Content.JOURNAL.value())) {
            logger.debug("Generate appropriate watermark for a {}", content);
            watermark = (JournalWatermark) generateJournalWatermark(((Journal) publication));
        } else {
            logger.error("Watermarking failed. Given content type '{}' of the publication with ID {} not valid.", content, publication.getId());
            throw new PublicationNotFoundException("Watermarking failed. Given content type of the publication not valid: '" + content + "'");
        }
        return watermark;
    }
}
