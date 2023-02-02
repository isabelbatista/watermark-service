package isabelbatista.test.watermark.preparation;

/**
 * Exception thrown if the preparation of the application and persistence layer failed.
 *
 * Created by Isabel Batista on 12.05.17.
 */
public class PreparationException extends Exception {
    public PreparationException(String message) {
        super(message);
    }
}
