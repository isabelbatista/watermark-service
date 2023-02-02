package isabelbatista.test.watermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import isabelbatista.test.watermark.documents.Publication;
import isabelbatista.test.watermark.documents.PublicationNotFoundException;
import isabelbatista.test.watermark.preparation.PreparationService;
import isabelbatista.test.watermark.watermarking.TicketNotFoundException;

/**
 * Created by Isabel Batista on 05.05.17.
 *
 * Rest Service for watermarking publications like books or journals.
 *
 * Entry path is "/watermark".
 */
@RestController
@RequestMapping("/watermark")
public class WatermarkRestController {

    final Logger logger = LoggerFactory.getLogger(WatermarkRestController.class);

    @Autowired
    private WatermarkService service;

    @Autowired
    private PreparationService preparationService;

    /**
     * Request for watermarking a given publication (book or journal).
     * You will get back a ticket ID for polling the status of the watermarking process.
     * If the watermarking has finished (Status: Finished) you can receive
     * the watermarked publication with the appropriate method "getWatermarkedPublication".
     *
     * @param publicationId                 ID of the publication (book or journal) as it is saved in the persistence layer.
     * @return                              Ticket ID for getting the status of the process, and later on the watermarked publication.
     * @throws PublicationNotFoundException Exception if the publication with the given ID was not found in the persistence layer.
     */
    @RequestMapping("/watermarkPublication")
    @ResponseStatus(HttpStatus.OK)
    public String watermarkPublication(@RequestParam(value="id") final String publicationId) throws PublicationNotFoundException {
        logger.info("Incoming request to watermark publication with ID {}", publicationId);
        return service.watermarkPublication(publicationId).getId();
    }

    /**
     * Request for returning the current state of a ticket with given ticketId.
     *
     * @param ticketId                  Ticket ID of the ticket to check for current status.
     * @return                          Status name of the ticket, for a valid status list @see Status
     * @throws TicketNotFoundException  Exception if the ticket with the given ID was not found in the persistence layer.
     */
    @RequestMapping("/ticketStatus")
    @ResponseStatus(HttpStatus.OK)
    public String getTicketStatus(@RequestParam(value="ticketId") final String ticketId) throws TicketNotFoundException {
        logger.info("Incoming request to get the current ticket status for ticket ID {}", ticketId);
        return service.getTicketStatus(ticketId);
    }

    /**
     * Request for getting the watermarked Publication.
     *
     * @param ticketId                      Ticket ID that was given by starting the watermark process.
     * @return                              Publication (book or journal) that was successfully watermarked.
     * @throws PublicationNotFoundException Exception if the publication related to the ticket was not found.
     */
    @RequestMapping("/getPublication")
    @ResponseStatus(HttpStatus.OK)
    public Publication getWatermarkedPublication(@RequestParam(value="ticketId") final String ticketId) throws PublicationNotFoundException {
        logger.info("Incoming request to get the publication by ticket ID {}", ticketId);
        return service.getPublicationByTicketId(ticketId);
    }

    // ###################### EXCEPTION HANDLERS ######################

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private String handleNotFoundException(PublicationNotFoundException exception) {
        logger.error("Not found: {}", exception.getMessage());
        exception.printStackTrace();
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private String handleNotFoundException(TicketNotFoundException exception) {
        logger.error("Not found: {}", exception.getMessage());
        exception.printStackTrace();
        return exception.getMessage();
    }
}