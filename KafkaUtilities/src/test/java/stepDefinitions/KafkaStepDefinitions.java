package stepDefinitions;


import generalutilities.FileSpecificUtilities;
import generalutilities.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.testng.Assert;
import staf.kafkautilities.clients.AdminClients;
import staf.kafkautilities.clients.KafkaClients;
import staf.kafkautilities.kafkaMethods.KafkaCommonMethods;

import java.util.List;
import java.util.Map;


/**
 * This class contains cucumber step definitions for performing generic kafka actions like produce, consume and validate kafka events
 */
public class KafkaStepDefinitions {

    private AdminClients adminClient = new AdminClients();
    private KafkaClients kafkaClient = new KafkaClients();
    private KafkaCommonMethods kafkaCommonMethods = KafkaCommonMethods.getInstance();
    private final TestContext<Object> testContext = TestContext.getInstance();
    private FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();

    /**
     * This method configures a Kafka producer with the configuration specified in the Hashicorp Vault path
     *
     * @param path path in the Hashicorp Vault where the Kafka producer configuration is saved, ex.KAFKA/Int-Done_withStore (or one can also specify config file name located at src/test/resources/PropertyFiles/KAFKA/{env}/{propFileName}.properties)
     */
    @Given("Kafka - we configure a Producer based on the configuration in the Hashicorp Vault path {string}")
    public void configureProducer(String path) {
        Producer<String, String> producer = kafkaClient.producer(path);
        testContext.setProperty("kafkaProducer", producer);
    }

    /**
     * This method configures a Kafka consumer with the configuration specified in the Hashicorp Vault path
     *
     * @param path path in the Hashicorp Vault where the Kafka consumer configuration is saved, ex.KAFKA/Int-Done_withStore (or one can also specify config file name located at src/test/resources/PropertyFiles/KAFKA/{env}/{propFileName}.properties)
     */
    @Given("Kafka - we configure a Consumer based on the configuration in the Hashicorp Vault path {string}")
    public void configureConsumer(String path) {
        Consumer<String, String> consumer = kafkaClient.consumer(path);
        testContext.setProperty("kafkaConsumer", consumer);
    }

    /**
     * This method configures a Kafka admin client with the configuration specified in the Hashicorp Vault path
     *
     * @param path path in the Hashicorp Vault where the Kafka consumer configuration is saved, ex.KAFKA/Int-Done_withStore (or one can also specify config file name located at src/test/resources/PropertyFiles/KAFKA/{env}/{propFileName}.properties)
     */
    @Given("Kafka - we configure an admin client based on the configuration in the Hashicorp Vault path {string}")
    public void configureAdminClient(String path) {
        Admin aClient = adminClient.createKafkaAdminClient(path);
        testContext.setProperty("kafkaAdminClient", aClient);
    }

    /**
     * This method produces a Kafka event by referring to the given json for the specified topic name
     *
     * @param fileName  file name of the json file which is present under "src/test/resources/KafkaJsonFiles/"
     * @param topicName name of the topic for which the event has to be produced
     */
    @When("Kafka - we produce an event by referring to the json file named {string} for the topic named {string}")
    public void produceKafkaMessageFromJsonFile(String fileName, String topicName) {
        String data = fileSpecificUtilities.readFileAsString("src/test/resources/KafkaJsonFiles/" + fileName + ".json");
        kafkaCommonMethods.producerClient(topicName, data);
    }

    /**
     This method is used to Produce Kafka event from json for a topic on specific partition
     * @param fileName    - file name of the json file which is present under "src/test/resources/KafkaJsonFiles/"
     * @param topicName   - name of the topic for which the event has to be produced
     */

    @When("Kafka - we produce an event by referring to the json file {string} for the topic {string} on partition {int}")
    public void produceKafkaMessageFromFileWithPartition(String fileName, String topicName, int partition) throws InterruptedException {
        String data = fileSpecificUtilities.readFileAsString("src/test/resources/KafkaJsonFiles/" + fileName + ".json");
        kafkaCommonMethods.producerClientWithPartition(topicName,partition, data);
    }


    /**
     * This method fetches Kafka event records for a given topic, polling every 500ms for 25 iterations and stops immediately upon detecting any event
     *
     * @param topicName name of the topic for retrieving the records
     */
    @When("Kafka - we retrieve event records for the topic {string}")
    public void consumeKafkaMessage(String topicName) {
        kafkaCommonMethods.retrieveKafkaEvents(topicName);
    }

    /**
     * This method fetches Kafka event records for a given topic for specified poll time and poll frequency and stops immediately upon the detection of any event
     *
     * @param topicName       name of the topic for retrieving the records
     * @param pollingTime     duration of polling in milli seconds
     * @param pollingAttempts number of polling attempts
     */
    @When("Kafka - we retrieve event records for the topic {string} with a polling interval of {long} milliseconds and poll for {int} times or until we receive an event")
    public void consumeKafkaMessage(String topicName, long pollingTime, int pollingAttempts) {
        kafkaCommonMethods.retrieveKafkaEvents(topicName, pollingTime, pollingAttempts);
    }

    /**
     * This method fetches Kafka event records for a given topic using specified filters, polling every 500ms for 25 iterations and stops immediately upon detecting any event
     *
     * @param topicName    name of the topic for retrieving the records
     * @param eventFilters data table that contains the filters
     */
    @When("Kafka - we retrieve event records for the topic {string} with the following filters")
    public void consumeKafkaMessage(String topicName, DataTable eventFilters) {
        final List<Map<String, String>> filter = eventFilters.asMaps(String.class, String.class);
        kafkaCommonMethods.retrieveKafkaEvents(topicName, filter);
    }

    /**
     * This method fetches Kafka event records for a given topic using specified filters, poll time and poll frequency and stops immidately upon the detection of any event
     *
     * @param topicName       name of the topic for retrieving the records
     * @param eventFilters    data table that contains the filters
     * @param pollingTime     duration of polling in milli seconds
     * @param pollingAttempts number of polling attempt
     */
    @When("Kafka - we retrieve event records for topic {string} with a polling interval of {long} milliseconds and poll for {int} times or until we receive an event with the following filters")
    public void consumeKafkaMessage(String topicName, long pollingTime, int pollingAttempts, DataTable eventFilters) {
        final List<Map<String, String>> filter = eventFilters.asMaps(String.class, String.class);
        kafkaCommonMethods.retrieveKafkaEvents(topicName, filter, pollingTime, pollingAttempts);
    }

    /**
     * This method fetches specified number of Kafka event records (one/all) for a given topic using specified filters, polling every 500ms for 25 iterations and stops immediately upon detecting any event
     *
     * @param recordCount  refers to the number of records you wish to consume. Please note that this parameter only accepts the values "one" or "all"
     * @param topicName    name of the topic for retrieving the records
     * @param eventFilters data table that contains the filters
     */
    @When("Kafka - we retrieve {string} count of event records for the topic {string} with the following filters")
    public void consumeKafkaMessage(String recordCount, String topicName, DataTable eventFilters) {
        final List<Map<String, String>> filter = eventFilters.asMaps(String.class, String.class);
        kafkaCommonMethods.retrieveKafkaEvents(topicName, recordCount, filter);
    }

    /**
     * This method fetches specified number of Kafka event records (one/all) for a given topic using specified filters, poll time and poll frequency and stops immidately upon the detection of any event
     *
     * @param recordCount     refers to the number of records you wish to consume. Please note that this parameter only accepts the values "one" or "all"
     * @param topicName       name of the topic for retrieving the records
     * @param pollingTime     duration of polling in milli seconds
     * @param pollingAttempts number of polling attempt
     * @param eventFilters    data table that contains the filters
     */
    @When("Kafka - we retrieve {string} count of event records for the topic {string} with a polling interval of {long} milliseconds and poll for {int} times or until we receive an event with the following filters")
    public void consumeKafkaMessage(String recordCount, String topicName, long pollingTime, int pollingAttempts, DataTable eventFilters) {
        final List<Map<String, String>> filter = eventFilters.asMaps(String.class, String.class);
        kafkaCommonMethods.retrieveKafkaEvents(topicName, recordCount, filter, pollingTime, pollingAttempts);
    }

    /**
     * This method validates the obtained kafka records against the given validation list
     *
     * @param validationListTable data table that holds a list of values. These values are cross-checked with the Kafka message
     */
    @Then("Kafka - we validate the event records we have obtained with the following list")
    public void validateKafkaMessage(DataTable validationListTable) {
        final List<Map<String, String>> validationList = validationListTable.asMaps(String.class, String.class);
        Assert.assertTrue(kafkaCommonMethods.validateConsumerRecords(validationList), "Required validation on retrieved kafka records :-> ");
    }

    /**
     * This method validates a message with a given key-value pair from a Kafka cluster
     *
     * @param messageToValidate  The message to be validated. It should contain two parts separated by "^". The first part is the JSON key and the second part is the expected value. ex- "after.APPLICATION_ID^kafk"
     * @param keyValue The key value to be checked in the Kafka records.
     */
    @Then("Kafka - we validate the message {string} with the key-value pair {string} in the Kafka records")
    public void validateNewMessage(String messageToValidate, String keyValue) {
        kafkaCommonMethods.validateMessageWithKeyValue(messageToValidate, keyValue);
    }
}