package isabelbatista.test.watermark.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import isabelbatista.test.watermark.watermarking.Watermark;

/**
 * Abstract publication that is extended by multiple publication types (book and journal).
 *
 * Created by Isabel Batista on 06.05.17.
 */
public abstract class Publication {

    @Id
    String id;

    @Field(type=FieldType.String)
    String content;

    @Field(type=FieldType.String)
    String title;

    @Field(type=FieldType.String)
    String author;

    @Field(type=FieldType.Object)
    Watermark watermark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Watermark getWatermark() {
        return watermark;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }
}