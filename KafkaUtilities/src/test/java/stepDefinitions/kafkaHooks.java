package stepDefinitions;


import generalutilities.TestContext;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processbuilderutilities.CommonProcessBuilderMethods;

import java.io.IOException;
import java.util.Map;


public class kafkaHooks {

    /**
     This class contains methods of Kafka hooks
     */
    private static final Logger log = LoggerFactory.getLogger(kafkaHooks.class);
    private final TestContext<Object> testContext = TestContext.getInstance();

    /**
     This method is used to kill the consumer instance
     * @throws IOException            - which occurs when user inputs improper data into the program.
     * @throws InterruptedException   - when a thread that is sleeping, waiting, or is occupied is interrupted
     */
    @After(order = 0)
    public void killConsumerInstance() throws IOException, InterruptedException {
        log.info("*** @After hook kill kafka consumer instance initiated ***");
        if (testContext.isPropertyPresent("ConsumerStatusTopicBroker")) {
            String[] consumerDetails = testContext.getProperty("ConsumerStatusTopicBroker").toString().split("_");
            int status = Integer.parseInt(consumerDetails[0]);
            log.info("Consumer Status : {}", status);
            String topic = consumerDetails[1];
            String broker = consumerDetails[2];
            String consumerStopcmd = "";
            String curwd = System.getProperty("user.dir");
            String mode = "";
            if (System.getProperty("os.name").contains("Windows")) {
                mode = "C:/Program Files/Git/git-bash.exe~-c";
                curwd = curwd.replace("\\", "/");
                consumerStopcmd = "chmod 777 " + curwd + "/src/test/resources/ShellScript/WinKafkaConsumerStop.sh ~ " + curwd + "/src/test/resources/ShellScript/WinKafkaConsumerStop.sh kafka-console-consumer.bat";
            } else {
                mode = "sh~-c";
                consumerStopcmd = "chmod 777 " + curwd + "/src/test/resources/ShellScript/LinKafkaConsumerStop.sh ~ " + curwd + "/src/test/resources/ShellScript/LinKafkaConsumerStop.sh " + broker + " " + topic;
            }
            Map<String, Object> outputStatus;
            outputStatus = new CommonProcessBuilderMethods().fireCommand(consumerStopcmd, mode);
            if (!outputStatus.get("status").toString().equals("0"))
                log.error("Consumer stop failed");
            else
                log.info("*** Killed kafka consumer instance ***");
        } else {
            log.info("*** No property 'ConsumerStatusTopicBroker' found in TestContext object! ***");
        }
    }
}
