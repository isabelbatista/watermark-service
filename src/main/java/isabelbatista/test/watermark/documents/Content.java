package isabelbatista.test.watermark.documents;

/**
 * Enumeration for setting the publication to a specified type (book or journal).
 *
 * Created by Isabel Batista on 06.05.17.
 */
public enum Content {
    BOOK("book"), JOURNAL("journal");

    private String value;

    Content(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
