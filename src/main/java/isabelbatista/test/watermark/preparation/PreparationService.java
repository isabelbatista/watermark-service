package isabelbatista.test.watermark.preparation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import isabelbatista.test.watermark.ESBookRepository;
import isabelbatista.test.watermark.ESJournalRepository;
import isabelbatista.test.watermark.ESTicketRepository;
import isabelbatista.test.watermark.WatermarkService;
import isabelbatista.test.watermark.documents.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service methods to prepare the application and persistence layer.
 *
 * Created by Isabel Batista on 07.05.17.
 */
@Service
public class PreparationService {

    final Logger logger = LoggerFactory.getLogger(PreparationService.class);

    @Autowired
    private ESBookRepository bookRepository;

    @Autowired
    private ESJournalRepository journalRepository;

    @Autowired
    private ESTicketRepository ticketRepository;

    @Autowired
    private WatermarkService watermarkService;

    private static final String FILENAME_AND_PATH = "src/main/resources/publications.csv";
    private static final String FILE_COLUMNS_DELIMITER = ";";

    public List<Publication> importExampleData() throws InvalidEnumException, PreparationException {

        List<Publication> publications = getPublicationsFromCsv();

        List<Publication> savedPublications = new ArrayList<>();

        if(publications != null && publications.size() > 0) {

            for(Publication publication : publications) {
                if(publication.getContent().equals(Content.BOOK.value())) {
                    savedPublications.add(savePublication((Book) publication));
                } else if (publication.getContent().equals(Content.JOURNAL.value())) {
                    savedPublications.add(savePublication((Journal) publication));
                } else {
                    logger.error("Import failed. Publication of unknown content type {} found.", publication.getContent());
                    throw new PreparationException("Import failed. Publication of unknown content type " + publication.getContent() + " found.");
                }
            }
        }
        return savedPublications;
    }

    /**
     * Helper method to persist publications.
     *
     * @param publication               Publication to persist.
     * @return                          Saved publication with set ID.
     * @throws InvalidEnumException     Exception thrown if the given content type of the publication is not set or unknown.
     */
    public Publication savePublication(Publication publication) throws InvalidEnumException {

        final String content = publication.getContent();
        Publication savedPublication = null;

        if(content == null) {
            logger.error("Content type of publication must not be null.");
            throw new InvalidEnumException("Content type of publication must not be null.");
        }

        if(content.equals(Content.BOOK.value())) {
            logger.info("Save the book with title '{}'", publication.getTitle());
            savedPublication = bookRepository.save((Book) publication);
        } else if (content.equals(Content.JOURNAL.value())) {
            logger.info("Save the journal with title '{}'", publication.getTitle());
            savedPublication = journalRepository.save((Journal) publication);
        } else {
            logger.error("The publication with content type {} is not valid. Has to be BOOK or JOURNAL.", content);
            throw new InvalidEnumException("The publication with content type " +  content + " is not valid. Has to be BOOK or JOURNAL.");
        }

        return savedPublication;
    }

    private List<Publication> getPublicationsFromCsv() {

        logger.info("Read csv file {} to get publication samples.", FILENAME_AND_PATH);

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;

        List<Publication> publications = new ArrayList<>();

        try {
            fileReader = new FileReader(FILENAME_AND_PATH);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            int counter = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(counter > 0) {
                    publications.add(extractPublicationFromLine(line));
                }
                counter++;
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return publications;
    }

    private Publication extractPublicationFromLine(String line) throws IOException {

        if(line == null) {
            logger.error("File couldn't be read. Line is empty.");
            throw new IOException("File couldn't be read. Line is empty.");
        }

        Publication publication = null;

        String[] lineTokens = line.split(FILE_COLUMNS_DELIMITER);
        if(lineTokens.length > 3 && lineTokens[0].equals(Content.BOOK.value())) {
            logger.info("Create book from line {}", line);
            Book book = new Book();
            book.setContent(Content.BOOK.value());
            book.setTopic(lineTokens[3]);
            publication = (Book) book;
        } else {
            logger.info("Create journal from line {}", line);
            Journal journal = new Journal();
            journal.setContent(Content.JOURNAL.value());
            publication = (Journal) journal;
        }

        publication.setTitle(lineTokens[1]);
        publication.setAuthor(lineTokens[2]);
        return publication;
    }
}
