package isabelbatista.test.watermark.documents;

/**
 * Enumeration for book topics.
 *
 * Created by Isabel Batista on 11.05.17.
 */
public enum Topic {
    BUSINESS ("Business"), SCIENCE ("Science"), MEDIA ("Media");

    private String value;

    Topic(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
