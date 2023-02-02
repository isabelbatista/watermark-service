package isabelbatista.test.watermark.queuing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Observer for the consumer to check the elements that are pushed to the queue by the @see Producer.
 *
 * Created by Isabel Batista on 07.05.17.
 */
@Component
public class ObservingConsumer implements Runnable {

    final Logger logger = LoggerFactory.getLogger(ObservingConsumer.class);

    private LinkedBlockingQueue queue;
    private Producer producer;

    public void init(LinkedBlockingQueue queue, Producer producer) {
        this.producer = producer;
        this.queue = queue;
    }

    @Override
    public void run() {


        while (producer.isRunning()) {
            logger.info("Observing queue. Current elements in the queue: {}", queue);

            try {
                logger.info("Sleeping for 2000 ms in continuous observation.");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("Observing the queue failed. " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("Observing-Consumer Completed.");
        logger.info("Final elements in the queue {}", queue);
    }
}
