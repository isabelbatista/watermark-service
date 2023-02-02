package isabelbatista.test.watermark;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import isabelbatista.test.watermark.watermarking.Ticket;

/**
 * ElasticSearch Ticket repository interface to persist elements of type @see Ticket.
 *
 * Defines a ticket repository that stores elements of type @see Ticket. ID is stored as String.
 *
 * Created by Isabel Batista on 06.05.17.
 */
@Repository
public interface ESTicketRepository extends ElasticsearchRepository<Ticket, String> {
}
