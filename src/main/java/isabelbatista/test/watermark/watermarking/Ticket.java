package isabelbatista.test.watermark.watermarking;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Ticket that is generated when the user requests watermarking a publication.
 * The Ticket will be returned to the user to poll for the watermarking process status.
 *
 * Marked as elastic search document for index "tickets" and of type "ticket".
 *
 * Created by Isabel Batista on 06.05.17.
 */
@Document(indexName = "tickets", type = "ticket", shards = 1, replicas = 0)
public class Ticket {

    @Id
    String id;

    @Field(type= FieldType.String)
    String content;

    @Field(type= FieldType.String)
    String publicationId;

    @Field(type=FieldType.String)
    String status;

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

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
