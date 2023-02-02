package isabelbatista.test.watermark.preparation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import isabelbatista.test.watermark.documents.InvalidEnumException;

/**
 * Initializer that runs after application is in state "ready".
 *
 * Starts the import of example data sets of publications from a given CSV.
 *
 * Created by Isabel Batista on 11.05.17.
 */
@Component
public class InitializeElasticSearch implements ApplicationListener<ApplicationReadyEvent> {

    final Logger logger = LoggerFactory.getLogger(InitializeElasticSearch.class);

    @Autowired
    private PreparationService preparationService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            preparationService.importExampleData();
        } catch (InvalidEnumException e) {
            logger.error("Couldn't import example data to the persistence layer.", e.getMessage());
            e.printStackTrace();
        } catch (PreparationException e) {
            logger.error("Couldn't import example data to the persistence layer.", e.getMessage());
            e.printStackTrace();
        }
    }
}
