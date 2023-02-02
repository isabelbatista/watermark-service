package isabelbatista.test.watermark.watermarking;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Abstract watermark that is extended by the watermark classes @see BookWatermark and @see JournalWatermark
 * for @see Book and @see Journal.
 *
 * Created by Isabel Batista on 06.05.17.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookWatermark.class),
        @JsonSubTypes.Type(value = JournalWatermark.class)
})
public abstract class Watermark {

    @Field(type= FieldType.String)
    private String content;

    @Field(type= FieldType.String)
    private String title;

    @Field(type= FieldType.String)
    private String author;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
