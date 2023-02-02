package isabelbatista.test.watermark.watermarking;

/**
 * Enumeration for the different @see Ticket status.
 *
 * Created by Isabel Batista on 06.05.17.
 */
public enum Status {
    /**
     * Status when the user asks for watermarking a publication and gets back the @see Ticket ID.
     */
    STARTED("Started"),

    /**
     * Status when the @see Ticket was pushed to the queue and is waiting for execution.
     */
    PENDING("Pending"),

    /**
     * Status when the @see Ticket was taken from the queue and watermarking is executing.
     */
    IN_PROGRESS("In Progress"),

    /**
     * Status when something went wrong during the watermarking process or queueing.
     */
    FAILED ("Failed"),

    /**
     * Status when watermarking was successful and the publication can be requested by @see Ticket ID.
     */
    FINISHED ("Finished");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
