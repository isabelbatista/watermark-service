package isabelbatista.test.watermark.documents;

/**
 * Exception that is thrown if a specified publication cannot be found.
 *
 * Created by Isabel Batista on 06.05.17.
 */
public class PublicationNotFoundException extends Exception {
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
