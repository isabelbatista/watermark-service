package isabelbatista.test.watermark.queuing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import isabelbatista.test.watermark.watermarking.Ticket;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue for getting watermark requests that can be consumed. Singleton.
 *
 * Created by Isabel Batista on 07.05.17.
 */
@Component
public class QueueForWatermarking {

    final Logger logger = LoggerFactory.getLogger(QueueForWatermarking.class);
    private LinkedBlockingQueue queue;

    /*
    @Value("${queue.size}")
    private int queueSize;
    */

    @Autowired
    ObservingConsumer observingConsumer;

    @Autowired
    RemovingConsumer removingConsumer;

    @Autowired
    Producer producer;

    @Autowired
    QueueForWatermarking() {
        this.queue = new LinkedBlockingQueue(10);
    }

    public void startQueuing(Ticket ticket) {

        logger.info("Start queueing for the given ticket with ID {}", ticket.getId());

        producer.init(queue, ticket);
        observingConsumer.init(queue, producer);
        removingConsumer.init(queue, producer);

        Thread producerThread = new Thread(producer);
        Thread obsConsumerThread = new Thread(observingConsumer);
        Thread remConsumerThread = new Thread(removingConsumer);

        producerThread.start();
        obsConsumerThread.start();
        remConsumerThread.start();
    }
}
