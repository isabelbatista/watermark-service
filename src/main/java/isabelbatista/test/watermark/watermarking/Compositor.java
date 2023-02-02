package isabelbatista.test.watermark.watermarking;

import isabelbatista.test.watermark.documents.Book;
import isabelbatista.test.watermark.documents.Journal;
import isabelbatista.test.watermark.documents.Publication;
import isabelbatista.test.watermark.documents.PublicationNotFoundException;

/**
 * Compositor interface to generate and include a watermark to a given publication (book or journal).
 *
 * Created by Isabel Batista on 10.05.17.
 */
public interface Compositor {

    /**
     * Generates a watermark for the given publication of content type @see Book.
     *
     * @param book  Book that contains information for the watermark to generate.
     * @return      Watermark for books with all information taken from the given @see Book.
     */
    public BookWatermark generateBookWatermark(Book book);

    /**
     * Generates a watermark for the given publication of content type @see Journal.
     *
     * @param journal   Journal that contains information for the watermark to generate.
     * @return          Watermark for journals with all information taken from the given @see Journal.
     */
    public JournalWatermark generateJournalWatermark(Journal journal);

    /**
     * Triggers generation of a watermark related to the given publication (differently for book or journal)
     * and updates the publication with the new watermark.
     *
     * @param ticket                        Ticket to update the related publication with watermark.
     * @return                              Watermarked publication.
     * @throws PublicationNotFoundException Exception if the given publication cannot be found in the persistence layer.
     * @throws WatermarkingException        Exception if something unspecific went wrong during watermarking.
     */
    public Publication updatePublicationWithWatermark(final Ticket ticket) throws PublicationNotFoundException, WatermarkingException;
}
