package isabelbatista.test.watermark;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import isabelbatista.test.watermark.documents.Journal;

/**
 *  ElasticSearch Journal repository interface to persist publications of content type @see Journal.
 *
 *  Defines a journal repository that stores elements of type @see Journal. ID is stored as String.
 *
 * Created by Isabel Batista on 05.05.17.
 */
@Repository
public interface ESJournalRepository extends ElasticsearchRepository<Journal, String> {
}
