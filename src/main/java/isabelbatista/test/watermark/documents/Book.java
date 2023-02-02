package isabelbatista.test.watermark.documents;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Book that inherits from @see Publication and has additional fields to set.
 *
 * Marked as Elastic Search document on index "publications" with type "book".
 *
 * Created by Isabel Batista on 05.05.17.
 */
@Document(indexName = "publications", type = "book", shards = 1, replicas = 0)
public class Book extends Publication {

    @Field(type=FieldType.String)
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


}
