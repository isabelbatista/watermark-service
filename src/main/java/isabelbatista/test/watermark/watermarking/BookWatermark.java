package isabelbatista.test.watermark.watermarking;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Watermark for publication type @see Book that inherits from @see Watermark and has additional fields to set.
 *
 * Created by Isabel Batista on 05.05.17.
 */
public class BookWatermark extends Watermark {

    @Field(type= FieldType.String)
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
