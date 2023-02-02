package isabelbatista.test.watermark;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import isabelbatista.test.watermark.documents.Book;

/**
 * ElasticSearch Book repository interface to persist publications of content type @see Book.
 *
 * Defines a book repository that stores elements of type @see Book. ID is stored as String.
 *
 * Created by Isabel Batista on 05.05.17.
 */
@Repository
public interface ESBookRepository extends ElasticsearchRepository<Book, String> {
}
