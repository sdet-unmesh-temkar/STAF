package staf.kafkautilities.kafkaMethods;

import generalutilities.ReportAndLogging;
import generalutilities.TestContext;
import generalutilities.ThreadLocalRegistry;
import io.restassured.path.json.JsonPath;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * This class contains methods related to Kafka Producer/Consumer.
 */
public class KafkaCommonMethods {

    private static final ThreadLocal<KafkaCommonMethods> instance = ThreadLocal.withInitial(KafkaCommonMethods::new);

    private static final Logger LOG = LoggerFactory.getLogger(KafkaCommonMethods.class);
    TestContext<Object> testContext = TestContext.getInstance();
    private final List<ConsumerRecords<String, String>> kafkaConsumerRecords = new ArrayList<>();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private String strMessage = "Message: ";
    private String schemaUrl = "schema.registry.url";
    private String consumerClient = "kafkaConsumer";
    private String producerClient = "kafkaProducer";

    /**
     * Private constructor to prevent direct instantiation in other class.
     */

    private KafkaCommonMethods() {
        super();
        ThreadLocalRegistry.register(instance);
    }

    /**
     * This method is used to get the instance of this class.
     *
     * @return KafkaCommonMethods - instance
     */
    public static KafkaCommonMethods getInstance() {
        return instance.get();
    }


    /**
     * This method is used to cleanup the thread local instance.
     */
    public void unload() {
        instance.remove();
    }

    /**
     * This method is used to create Producer client with message that we are going to produce.
     *
     * @param topicName - for which topic message needs to produce
     * @param message   - in string format
     * @throws InterruptedException - this is an exception throws when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity
     */
    public void producerClient(String topicName, String message) {
        Producer<String, String
                > producer = (Producer<String, String>) testContext.getProperty(producerClient);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, message);
        try {
            RecordMetadata metadata = producer.send(producerRecord).get();
            LOG.info("Record sent with key {} to partition {}  with offset {}", producerRecord, metadata.partition(), metadata.offset());
        } catch (ExecutionException e) {
            LOG.error("Error in sending record", e);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException on producerClient method: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method is used to create Producer client with message that we are going to produce on specified partition.
     *
     * @param topicName - for which topic message needs to produce
     * @param partition - on partition event needs to produce
     * @param message   - in string format
     * @throws InterruptedException - this is an exception throws when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity
     */
    public void producerClientWithPartition(String topicName,int partition, String message) throws InterruptedException {
        Producer<String, String
                > producer = (Producer<String, String>) testContext.getProperty(producerClient);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName,partition,"key", message);
        try {
            RecordMetadata metadata = producer.send(producerRecord).get();
            LOG.info("Record sent with key {} to partition {}  with offset {}", producerRecord, metadata.partition(), metadata.offset());
        } catch (ExecutionException e) {
            LOG.error("Error in sending record", e);
        }
    }


    /**
     * This method is used to retrieve Kafka events using consumer client.
     *
     * @param topicName       - for which topic message needs to consume
     * @param pollingTime     - duration of polling in milli seconds
     * @param pollingAttempts - number of polling attempts
     */
    public void retrieveKafkaEvents(String topicName, long pollingTime, int pollingAttempts) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        kConsumer.subscribe(Collections.singletonList(topicName));
        int counter = 0;
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kConsumer.poll(Duration.ofMillis(pollingTime));
                if (consumerRecords.count() == 0) {
                    counter++;
                    if (counter > pollingAttempts) {
                        break;// If no message found count is reached to threshold exit loop.
                    }
                }
                for (ConsumerRecord record:consumerRecords) {
                    LOG.info("retrieved record :: {} ", record);
                }
                kafkaConsumerRecords.add(consumerRecords);
            }
        } finally {
            kConsumer.close();
        }
    }

    /**
     * This method is used to retrieve Kafka events using consumer client.
     *
     * @param topicName - for which topic message needs to consume
     */
    public void retrieveKafkaEvents(String topicName) {
        retrieveKafkaEvents(topicName, 500, 25);
    }

    /**
     * This method is used to retrieve Kafka filtered events using consumer client
     *
     * @param topicName       - for which topic message needs to consume
     * @param filter          - filter values needs to be apply while consuming message
     * @param pollingTime     - duration of polling in milli seconds
     * @param pollingAttempts - number of polling attempts
     */
    public void retrieveKafkaEvents(String topicName, List<Map<String, String>> filter, long pollingTime, int pollingAttempts) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        kConsumer.subscribe(Collections.singletonList(topicName));
        int counter = 0;
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kConsumer.poll(Duration.ofMillis(pollingTime));
                if (consumerRecords.count() == 0) {
                    counter++;
                    if (counter > pollingAttempts) {
                        break;// If no message found count is reached to threshold exit loop.
                    }
                }
                if (filterConsumerRecords(consumerRecords, filter)) {
                    kafkaConsumerRecords.add(consumerRecords);
                }
            }
        } finally {
            if (!kafkaConsumerRecords.isEmpty()) {
                testContext.setProperty("kafkaEvents", kafkaConsumerRecords);
            }
            kConsumer.close();
        }
    }

    /**
     * This method is used to retrieve Kafka filtered events using consumer client
     *
     * @param topicName - for which topic message needs to consume
     * @param filter    - filter values needs to be apply while consuming message
     */
    public void retrieveKafkaEvents(String topicName, List<Map<String, String>> filter) {
        retrieveKafkaEvents(topicName, filter, 500, 25);
    }

    /**
     * This method is used to retrieve Kafka filtered events using consumer client
     *
     * @param topicName       - for which topic message needs to consume
     * @param recordCount     - how many records you want to consume[Note: Works only for 'one' or 'all' as a value]
     * @param filter          - filter values needs to be apply while consuming message
     * @param pollingTime     - duration of polling in milli seconds
     * @param pollingAttempts - number of polling attempts
     */
    public void retrieveKafkaEvents(String topicName, String recordCount, List<Map<String, String>> filter, long pollingTime, int pollingAttempts) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        kConsumer.subscribe(Collections.singletonList(topicName));
        int counter = 0;
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kConsumer.poll(Duration.ofMillis(pollingTime));
                if (consumerRecords.count() == 0) {
                    counter++;
                    if (counter > pollingAttempts) {
                        break;// If no message found count is reached to threshold exit loop.
                    }
                }
                if (filterConsumerRecords(consumerRecords, filter)) {
                    kafkaConsumerRecords.add(consumerRecords);
                    if (recordCount.equalsIgnoreCase("one")) {
                        break;
                    }
                }
            }
        } finally {
            if (!kafkaConsumerRecords.isEmpty()) {
                testContext.setProperty("kafkaEvents", kafkaConsumerRecords);
            }
            kConsumer.close();
        }
    }

    /**
     * This method is used to retrieve Kafka filtered events using consumer client
     *
     * @param topicName   - for which topic message needs to consume
     * @param recordCount - how many records you want to consume[Note: Works only for 'one' or 'all' as a value]
     * @param filter      - filter values needs to be apply while consuming message
     */
    public void retrieveKafkaEvents(String topicName, String recordCount, List<Map<String, String>> filter) {
        retrieveKafkaEvents(topicName, recordCount, filter, 500, 25);
    }

    /**
     * This method used to filter the consumed messages
     *
     * @param consumerRecords - Messages that are consumed
     * @param filter          - Value that is used to filter in the consumed messages
     * @return boolean          - True, if filter is present in the message else false
     */
    public boolean filterConsumerRecords(ConsumerRecords<String, String> consumerRecords, List<Map<String, String>> filter) {
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            LOG.info("record :: {}", consumerRecord);
            int counter = 0;
            for (Map<String, String> columns : filter) {
                String key = columns.get("filter").trim();
                String[] keys = key.split(":");
                if (keys.length > 1 && keys[1].contains("${")) {
                    String testContextKey = key.substring(key.indexOf("${") + 2, key.indexOf("}"));
                    key = key.replace(testContextKey, testContext.getProperty(testContextKey).toString())
                            .replace("${", "")
                            .replace("}", "");
                    LOG.info("Key after replacing with testContext value and applied for filter is : {}", key);
                }
                if (consumerRecord.toString().contains(key)) {
                    counter++;
                    if (counter == filter.size()) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to validate consumed message
     *
     * @param validationList - The list of values that has to be validated in the consumed message
     * @return boolean        - True, if validationList is present in the message else false
     */
    public boolean validateConsumerRecords(List<Map<String, String>> validationList) {
        for (ConsumerRecords<String, String> consumerRecords : kafkaConsumerRecords) {
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                int counter = 1;
                for (Map<String, String> validation : validationList) {
                    String key = validation.get("validation").trim();
                    String[] keys = key.split(":");
                    if (keys.length > 1 && keys[1].contains("${")) {
                        String testContextKey = key.substring(key.indexOf("${") + 2, key.indexOf("}"));
                        key = key.replace(testContextKey, testContext.getProperty(testContextKey).toString())
                                .replace("${", "")
                                .replace("}", "");
                        LOG.info("Validating with {} key after replacing with testContext value", key);
                    }
                    if (consumerRecord.value().contains(key)) {
                        if (counter == validationList.size()) {
                            LOG.info("Required validation successful on retrieved kafka event records");
                            return true;
                        }
                        counter++;
                    } else {
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method is used to check connectivity to kafka cluster .
     *
     * @param topicsToBeValidated - name of the topic to be searched in kafka cluster
     */

    public void validateTopics(List<String> topicsToBeValidated) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        Map<String, List<PartitionInfo>> topicList = kConsumer.listTopics();
        LOG.debug("Number of Topics to be evaluated: {}", topicList.keySet().size());
        for (String topicName : topicsToBeValidated) {

            if (!topicList.containsKey(topicName)) {
                LOG.error("Topic not created correctly");

                reportAndLogging.addStepToReport(strMessage + topicName + " is not present in Response.","WARN");

            }
        }
    }


    /**
     * This method is used to update properties files for kafka cluster connection .
     *
     * @param props - property file path
     * @return props  - updated properties file
     */

    public Properties fillPropertyDetails(Properties props) {
        String value;
        value = ConsumerConfig.GROUP_ID_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.GROUP_ID_CONFIG");

        value = ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG");

        value = ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG");

        props = kafkaClusterConnect(props, schemaUrl, "schemaUrl");

        value = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.AUTO_OFFSET_RESET_CONFIG");

        value = ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG");

        value = ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG");

        value = ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
        props = kafkaClusterConnect(props, value, "ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG");

        value = CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
        props = kafkaClusterConnect(props, value, "CommonClientConfigs.SECURITY_PROTOCOL_CONFIG");

        value = SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG");

        value = SslConfigs.SSL_KEYSTORE_TYPE_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEYSTORE_TYPE_CONFIG");

        value = SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG");

        value = SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG");

        value = SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG");

        value = SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG");

        value = SslConfigs.SSL_KEY_PASSWORD_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEY_PASSWORD_CONFIG");

        value = SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG");

        value = SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG");

        value = SslConfigs.SSL_KEYSTORE_KEY_CONFIG;
        props = kafkaClusterConnect(props, value, "SslConfigs.SSL_KEYSTORE_KEY_CONFIG");

        return props;
    }


    /**
     * This method is used to update the properties file for kafka cluster connection .
     *
     * @param props - properties file path
     * @param value - value of property from consumer.config class
     * @param str   - property name
     * @return props  - updated properties file as props - string
     */

    public Properties kafkaClusterConnect(Properties props, String value, String str) {

        String varNameID = props.getProperty(str);
        if (null != varNameID && !varNameID.trim().isEmpty())
            props.put(value, varNameID);
        return props;
    }

    /**
     * This method validates a message with a given key-value pair from a Kafka cluster
     *
     * @param message  The message to be validated. It should contain two parts separated by "^". The first part is the JSON key and the second part is the expected value. ex- "after.APPLICATION_ID^kafk"
     * @param keyValue The key value to be checked in the Kafka records.
     * @return Returns true if the key is found in the Kafka records and the associated value matches the expected value. Returns false if the key is not found or the associated value does not match the expected value.
     */
    public boolean validateMessageWithKeyValue(String message, String keyValue) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        String[] strSplit = message.trim().split(Pattern.quote("^"));
        var flagKey = false;
        var flagValue = false;
        var value = "";

        try {
            for (var i = 0; i < 10; i++) {
                Thread.sleep(2000);
                var duration = Duration.ofSeconds(100);
                ConsumerRecords<String, String> records = kConsumer.poll(duration);

                for (ConsumerRecord<String, String> rectempRecordord : records) {

                    String key = rectempRecordord.key();
                    LOG.info("offset = {}, key = {}, value = {} \n", rectempRecordord.offset(), key, rectempRecordord.value());

                    if (rectempRecordord.key().contains(keyValue.trim())) {
                        reportAndLogging.addStepToReport("Key - " + keyValue + " is present in Kafka cluster","INFO");
                        flagKey = true;
                        var js = new JsonPath(rectempRecordord.value());
                        value = js.getString(strSplit[0]);
                        String valueExp = strSplit[1];

                        if (value.trim().equals(valueExp.trim())) {
                            flagValue = true;
                            reportAndLogging.addStepToReport("Expected Value: " + strSplit[1] + ". Actual value  " + value);
                        }
                    }
                }

                if (flagValue) {
                    break;
                }

            }
            if (!flagKey) {
                reportAndLogging.addStepToReport("Key - " + keyValue + " is not present in Kafka cluster","WARN");
                return false;
            }

            if (!flagValue) {
                reportAndLogging.addStepToReport("Expected Value: " + strSplit[1] + " is not matching with Actual Value: " + value,"WARN");
                return false;
            }

        } catch (InterruptedException e) {
            LOG.error("InterruptedException on validateMessageWithKeyValue method: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            kConsumer.close();
        }
        return flagKey;
    }


    /**
     * This method is used to validate message without keyValue We directly receive the message that is to be validated.
     *
     * @param message -message to be validated
     * @throws InterruptedException -when a thread that is sleeping, waiting, or is occupied is interrupted
     */

    public void validateMessageWithoutKeyValue(String message) throws InterruptedException {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        var flagValue = false;
        String recordValString = null;
        try {
            for (var i = 0; i < 10; i++) {
                Thread.sleep(2000);
                var duration = Duration.ofSeconds(100);
                ConsumerRecords<String, String> records = kConsumer.poll(duration);

                for (ConsumerRecord<String, String> tempRecord : records) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("offset = {}, key = {}, value = {} \n", tempRecord.offset(), tempRecord.key(), tempRecord.value());
                        reportAndLogging.addStepToReport("Output Response : ==>" + tempRecord.value());
                    }
                    String recordValue = tempRecord.value();
                    recordValString = recordValue;

                    if (recordValString != null) {
                        break;
                    }
                }
            }

            if (recordValString != null) {
                flagValue = recordValString.contains(message);
            }

            if (flagValue) {
                reportAndLogging.addStepToReport(strMessage + message + " is present in Response.","INFO");

            } else {
                reportAndLogging.addStepToReport(strMessage + message + " is not present in Response.","WARN");
            }
        } finally {
            kConsumer.close();
        }

    }


    /**
     * This method is used to kafka topics count .
     *
     * @param topicName - name of the topic to be searched in kafka cluster
     * @return totalNumOfMsgs  - total number of messages
     */

    public Long validateTopicRowCount(String topicName) {
        Consumer<String, String> kConsumer = (Consumer<String, String>) testContext.getProperty(consumerClient);
        List<TopicPartition> tpList = new ArrayList<>();

        List<PartitionInfo> listPI = kConsumer.partitionsFor(topicName);
        for (PartitionInfo pi : listPI) {
            var tp = new TopicPartition(topicName, pi.partition());
            tpList.add(tp);
        }

        var totalNumOfMsgs = 0L;

        Map<Integer, Long> offsetDifferences = new HashMap<>();

        Map<TopicPartition, Long> beginOffsetMap = kConsumer.beginningOffsets(tpList);

        Map<TopicPartition, Long> endOffsetMap = kConsumer.endOffsets(tpList);

        for (Entry<TopicPartition, Long> endOffsetEntry : endOffsetMap.entrySet()) {
            TopicPartition endTP = endOffsetEntry.getKey();
            Integer endPartitionId = endTP.partition();
            Long endOffset = endOffsetEntry.getValue();
            for (Entry<TopicPartition, Long> beginOffsetEntry : beginOffsetMap.entrySet()) {
                TopicPartition beginTP = beginOffsetEntry.getKey();
                Integer beginPartitionId = beginTP.partition();
                Long beginOffset = beginOffsetEntry.getValue();

                if (beginPartitionId.equals(endPartitionId)) {
                    long diffOffset = endOffset - beginOffset;
                    offsetDifferences.put(endPartitionId, diffOffset);
                    totalNumOfMsgs += diffOffset;
                    LOG.debug("Partition ID = {} | begin offset= {} | end offset= {}", endOffsetEntry.getKey().partition(), beginOffsetEntry.getValue(), endOffsetEntry.getValue());

                    break;
                }
            }
        }
        LOG.info("totalNumOfMsgs = {}", totalNumOfMsgs);
        return totalNumOfMsgs;
    }


    /**
     * Method to fetch the kafka topic partitions
     *
     * @param topicName - name of the topic to be searched in kafka cluster
     * @return tpList      - returns list if topics
     */

    private List<TopicPartition> getTopicPartitions(Consumer<String, GenericRecord> consumer, String topicName) {
        List<TopicPartition> tpList = new ArrayList<>();

        List<PartitionInfo> listPI = consumer.partitionsFor(topicName);
        for (PartitionInfo pi : listPI) {
            var tp = new TopicPartition(topicName, pi.partition());
            tpList.add(tp);
        }

        return tpList;
    }


    /**
     * Method to fetch the records for target kafka topic.
     *
     * @param topicName       - name of the topic to be searched in kafka cluster
     * @param timeBackFromNow - time back from now
     * @param targetConsumer  - to fetch the records for target kafka topic
     * @return records        - returns records from kafka topic between two timestamps
     */
    public List<ConsumerRecord<String, GenericRecord>> getRecordsForTargetTopic(Consumer<String, GenericRecord> targetConsumer, String topicName, Long timeBackFromNow) {

        Long currentTime = System.currentTimeMillis();
        long startTime = currentTime - timeBackFromNow;

        return readRecordsBetweenTime(targetConsumer, topicName, startTime, currentTime);

    }


    /**
     * Method to fetch the records for kafka source topic
     *
     * @param topicName       - name of the topic to be searched in kafka cluster
     * @param timeBackFromNow - time back from now
     * @param sourceConsumer  - source consumer
     * @return records        - returns records from kafka topic between two timestamps
     */
    public List<ConsumerRecord<String, GenericRecord>> getRecordsForSourceTopic(Consumer<String, GenericRecord> sourceConsumer, String topicName, Long timeBackFromNow) {

        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - timeBackFromNow;

        return readRecordsBetweenTime(sourceConsumer, topicName, startTime, currentTime);
    }


    /**
     * Method to fetch the records from kafka topic between two timestamps
     *
     * @param topicName - name of the topic to be searched in kafka cluster
     * @param startTime - start time
     * @param endTime   - end time
     * @param consumer  - to fetch the records from kafka topic
     * @return consumerRecords  - returns list of consumer Records
     */

    public List<ConsumerRecord<String, GenericRecord>> readRecordsBetweenTime(Consumer<String, GenericRecord> consumer, String topicName, long startTime, long endTime) {

        LOG.trace("Start readRecordsBetweenTime ------------------->");
        List<ConsumerRecord<String, GenericRecord>> consumerRecords = new ArrayList<>();
        List<TopicPartition> tpList = getTopicPartitions(consumer, topicName);

        LOG.info("No. of Partitions for topic {} is {} ", topicName, tpList.size());

        consumer.assign(tpList);
        LOG.info("Partitions assigned to Consumer");
        //Map to hold end offsets of each partitions
        Map<TopicPartition, Long> endOffsetMap = consumer.endOffsets(tpList);

        LOG.info("EndOffset Map size is {}", endOffsetMap.size());

        Map<TopicPartition, Long> partitionAndTimeStamp = new HashMap<>();
        for (TopicPartition tp : tpList) {
            partitionAndTimeStamp.put(tp, startTime);
        }
        LOG.info("This new map with having starttime against each partition has size as {}", partitionAndTimeStamp.size());

        LOG.info("Fetching offset for each partitions at time {}", startTime);

        //Map to hold offset and timestamp details of each partition at a particular timestamp
        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestamp =
                consumer.offsetsForTimes(partitionAndTimeStamp);


        LOG.info("Looping through each entry in map of partitions and offsets at time {}", startTime);
        var someRecordsFound = false;
        for (Entry<TopicPartition, OffsetAndTimestamp> offsetAndTimestampEntry : offsetAndTimestamp.entrySet()) {

            TopicPartition tp = offsetAndTimestampEntry.getKey();


            var offsetAndTimestampForPartition = offsetAndTimestampEntry.getValue();

            var offsetAtTime = 0L;
            if (null != offsetAndTimestampForPartition) {
                offsetAtTime = offsetAndTimestampForPartition.offset();
            }

            LOG.info("Offset for partition {} at time {} is {} ", tp.partition(), startTime, offsetAtTime);
            Long endOffset = endOffsetMap.get(tp);
            LOG.info("Offset for partition {} at time {} is {} (End Offset)", tp.partition(), endTime, endOffset);


            if (offsetAtTime < endOffset) {
                consumer.seek(tp, offsetAtTime);
                someRecordsFound = true;
            }

            LOG.info("updated consumer to start consuming from offset {} for partition {} ", offsetAtTime, tp.partition());

        }

        try (consumer) {
            while (someRecordsFound) {
                LOG.info("Inside while loop, Some records to be yet to be consumed");
                someRecordsFound = false;

                var duration = Duration.ofSeconds(20);
                ConsumerRecords<String, GenericRecord> recordsForPartitions = consumer.poll(duration);
                LOG.info("consumer poll for 20 seconds");

                var i = 0;
                i++;
                LOG.info("Iterating over endOffset map for comparison");
                for (Entry<TopicPartition, Long> endOffsets : endOffsetMap.entrySet()) {

                    TopicPartition tp = endOffsets.getKey();
                    List<ConsumerRecord<String, GenericRecord>> consRecords = recordsForPartitions.records(tp);

                    LOG.info("Number of records fetched in poll {} for partition {} is {} ", i, tp.partition(), consRecords.size());
                    consumerRecords.addAll(consRecords);

                    Long endOffset = endOffsets.getValue();
                    long nextOffset = consumer.position(tp);
                    LOG.info("Partition {}: endOffset = {}, nextOffset(position) = {}", tp.partition(), endOffset, nextOffset);

                    if (nextOffset < endOffset) {
                        someRecordsFound = true;
                        LOG.info("There are some more records to poll");
                    }
                }

            }
        }
        LOG.trace("------------------> End readRecordsBetweenTime");
        return consumerRecords;

    }


    /**
     * Method to fetch the records from kafka topic
     *
     * @param topicName       - name of the topic to be searched in kafka cluster
     * @param timeBackFromNow - time back from now
     * @param topicConsumer   - topic consumer
     * @return records        - returns records from kafka topic between two timestamps
     */

    public List<ConsumerRecord<String, String>> getRecordsForTopic(Consumer<String, String> topicConsumer, String topicName, Long timeBackFromNow) {

        Long currentTime = System.currentTimeMillis();
        Long startTime = currentTime - timeBackFromNow;
        Long endTime = currentTime;

        return readRecordsBetweenTimeForTopic(topicConsumer, topicName, startTime, endTime);

    }


    /**
     * Method to fetch the records from kafka topic between two timestamps
     *
     * @param topicName     - name of the topic to be searched in kafka cluster
     * @param startTime     - start time
     * @param topicConsumer - topic consumer
     * @param endTime       - end time
     * @return consumerRecords - returns list of consumer records
     */

    public List<ConsumerRecord<String, String>> readRecordsBetweenTimeForTopic(Consumer<String, String> topicConsumer, String topicName, long startTime, long endTime) {

        LOG.info("Start readRecordsBetweenTime ------------------->");

        List<ConsumerRecord<String, String>> consumerRecords = new ArrayList<>();
        List<TopicPartition> tpList = getTopicPartitionsForConsumers(topicConsumer, topicName);
        LOG.info("No. of Partitions for topic {} is {}", topicName, tpList.size());

        topicConsumer.assign(tpList);

        Map<TopicPartition, Long> endOffsetMap = topicConsumer.endOffsets(tpList);
        LOG.info("EndOffset Map size is {}", endOffsetMap.size());

        LOG.info("created a map with topic partition and startTime as {}", startTime);
        Map<TopicPartition, Long> partitionAndTimeStamp = new HashMap<>();
        for (TopicPartition tp : tpList) {
            partitionAndTimeStamp.put(tp, startTime);
        }
        LOG.info("This new map with having starttime against each partition has size as {}", partitionAndTimeStamp.size());

        LOG.info("Fetching offset for each partitions at time {}", startTime);
        //Map to hold offset and timestamp details of each partition at a particular timestamp
        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestamp =
                topicConsumer.offsetsForTimes(partitionAndTimeStamp);

        //Map to hold offset details of each partition at a particular timestamp

        var someRecordsFound = false;
        for (Entry<TopicPartition, OffsetAndTimestamp> offsetAndTimestampEntry : offsetAndTimestamp.entrySet()) {

            TopicPartition tp = offsetAndTimestampEntry.getKey();
            LOG.info("Verifying results for partition {}", tp.partition());

            var offsetAndTimestampForPartition = offsetAndTimestampEntry.getValue();

            var offsetAtTime = 0L;
            if (null != offsetAndTimestampForPartition) {
                offsetAtTime = offsetAndTimestampForPartition.offset();
            }
            LOG.info("Offset for partition {} at time {} is {}", tp.partition(), startTime, offsetAtTime);
            Long endOffset = endOffsetMap.get(tp);
            LOG.info("Offset for partition {} at time {} is {} (End Offset)", tp.partition(), endTime, endOffset);

            if (offsetAtTime < endOffset) {
                topicConsumer.seek(tp, offsetAtTime);
                someRecordsFound = true;
            }
            LOG.info("updated consumer to start consuming from offset {} for partition {}", offsetAtTime, tp.partition());
        }

        try (topicConsumer) {
            while (someRecordsFound) {
                LOG.info("Inside while loop, Some records to be yet to be consumed");
                someRecordsFound = false;
                var duration = Duration.ofSeconds(20);
                ConsumerRecords<String, String> recordsForPartitions = topicConsumer.poll(duration);
                LOG.info("consumer poll for 20 seconds");

                var i = 0;
                i++;
                LOG.info("Iterating over endOffset map for comparison");
                for (Entry<TopicPartition, Long> endOffsets : endOffsetMap.entrySet()) {

                    TopicPartition tp = endOffsets.getKey();
                    List<ConsumerRecord<String, String>> consRecords = recordsForPartitions.records(tp);
                    LOG.info("Number of records fetched in poll {} for partition {} is {}", i, tp.partition(), consRecords.size());
                    consumerRecords.addAll(consRecords);

                    Long endOffset = endOffsets.getValue();
                    Long nextOffset = topicConsumer.position(tp);
                    LOG.info("Partition {}+: endOffset = {}, nextOffset(position)={}", tp.partition(), endOffset, nextOffset);
                    if (nextOffset < endOffset) {
                        someRecordsFound = true;
                        LOG.info("There are some more records to poll");
                    }
                }

            }
        }
        LOG.info("------------------> End readRecordsBetweenTime");
        return consumerRecords;

    }


    /**
     * This method is used for kafka cluster connection .
     *
     * @param topicName - name of the topic to be searched in kafka cluster
     * @return topiclist         - returns list of topic
     */

    private ArrayList<TopicPartition> getTopicPartitionsForConsumers(Consumer<String, String> consumer, String topicName) {
        ArrayList<TopicPartition> tpList = new ArrayList<>();

        List<PartitionInfo> listPI = consumer.partitionsFor(topicName);
        for (PartitionInfo pi : listPI) {
            var tp = new TopicPartition(topicName, pi.partition());
            tpList.add(tp);
        }

        return tpList;
    }

}
