package staf.kafkautilities.clients;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import staf.kafkautilities.config.KafkaConfig;

/**
 * This class contains method of Kafka Producer/Consumer client
 * This class contains methods for creating Kafka Producer/Consumer client.
 */
public class KafkaClients {
    private static final Logger log = LoggerFactory.getLogger(KafkaClients.class);
    private KafkaConfig kafkaConfig = new KafkaConfig();

    /**
     * This method creates Kafka Producer client
     * @param path        - path of vault secret path or property file name
     * @return producer   - producer object or null
     */
    public Producer<String, String> producer(String path){
        Producer<String, String> producer = null;
        try {
            producer = new KafkaProducer(kafkaConfig.getProducerConfigs(path));
            log.info("Kafka producer created..");
        } catch(Exception e) {
            log.error("Some issues with Kafka producer creation.. :{}", e.getMessage());
        }
        return producer;
    }

    /**
     * This method creates Kafka Consumer client
     *
     * @param path - path of accepts vault secret path or property file name
     * @return consumer  - object or null
     * @throws Exception - Throws execution if there are issues in kafka consumer instance
     */
    public Consumer<String, String> consumer(String path) {
        Consumer<String, String> consumer = null;
        try {
            consumer = new KafkaConsumer(kafkaConfig.getConsumerConfigs(path));
            log.info("Kafka consumer created..");
        } catch (Exception e) {
            log.error("Some issues with Kafka consumer creation..: {}", e.getMessage());
        }
        return consumer;
    }
}
