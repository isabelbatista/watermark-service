package isabelbatista.test.watermark.watermarking;

/**
 * Throw if a given @see Ticket or ticket ID cannot be found in the persistence layer.
 *
 * Created by Isabel Batista on 06.05.17.
 */
public class TicketNotFoundException extends Exception {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
