package isabelbatista.test.watermark.documents;

import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Journal that inherits from @see Publication.
 *
 * Marked as Elastic Search document on index "publications" with type "journal".
 *
 * Created by Isabel Batista on 05.05.17.
 */
@Document(indexName = "publications", type = "journal", shards = 1, replicas = 0)
public class Journal extends Publication {
}
