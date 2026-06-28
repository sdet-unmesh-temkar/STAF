package staf.kafkautilities.kafkaMethods;

import generalutilities.ReportAndLogging;
import generalutilities.TestContext;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * This class contains methods related to Kafka Admin client.
 * This class contains methods for validating kafka Admin client messages
 */
public class KafkaAdminUtils {

    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminUtils.class);
    String strMessage = "Message: ";
    private String kAdminClient = "kafkaAdminClient";
    TestContext<Object> testContext = TestContext.getInstance();


    /**
     * Method is used to validate topics Acls.
     *
     * @param topicName             - name of the topic to be searched in kafka cluster
     * @param principal             - principal name
     * @param operations            - to validate topics Acls
     * @return boolean              - records from kafka topic between two timestamps
     * @throws InterruptedException - an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     * @throws ExecutionException   - an exception is thrown when attempting to retrieve the result of a task that aborted by throwing an exception
     */
    public boolean validateTopicAcls(String topicName, String principal, List<String> operations) throws InterruptedException, ExecutionException {
        Admin adminClient = (Admin) testContext.getProperty(kAdminClient);
        Set<String> topicList = adminClient.listTopics().names().get();


        if (topicList.contains(topicName)) {

            var rf = new ResourcePatternFilter(ResourceType.TOPIC, topicName, PatternType.ANY);
            var abf = new AclBindingFilter(rf, AccessControlEntryFilter.ANY);
            DescribeAclsResult dar = adminClient.describeAcls(abf);

            List<String> operationsGiven = new ArrayList<>();
            for (AclBinding acls : dar.values().get()) {

                AccessControlEntry acl = acls.entry();
                if (acl.principal().contains(principal))
                    operationsGiven.add(acl.operation().name());

            }

            var operationGranted = true;
            for (String op : operations) {
                if (!operationsGiven.contains(op)) {
                    operationGranted = false;
                    break;
                }
            }

            if (operationGranted) {
                reportAndLogging.addStepToReport(strMessage + topicName + " has correct Permissions for princial " + principal,"INFO");
                return true;
            } else {
                reportAndLogging.addStepToReport(strMessage + topicName + " doesn't have correct Permissions for princial " + principal,"WARN");
                return false;
            }

        } else {
            reportAndLogging.addStepToReport(strMessage + topicName + " is not present.","WARN");
            return false;
        }
    }


    /**
     * Method is used to validate kafka topics
     *
     * @param topicNames - List of topic names to be searched in kafka cluster
     */

    public void validateTopics(List<String> topicNames) {
        Admin adminClient = (Admin) testContext.getProperty(kAdminClient);
        Set<String> topicNamesFromServer = null;
        try {
            topicNamesFromServer = adminClient.listTopics().names().get();
            if (topicNames != null && topicNamesFromServer.containsAll(topicNames)) {
                reportAndLogging.addStepToReport(strMessage + " All topics are present in Response.","INFO");
            } else {
                reportAndLogging.addStepToReport(strMessage + " Atleast one of the topic is not present in Response.","WARN");
            }

        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            Thread.currentThread().interrupt();
            reportAndLogging.logStepInJira("FAIL: Exception while checking " + topicNamesFromServer + " details ");
        }

    }

    /**
     * Method is used to validate group Acls.
     *
     * @param groupName            - group name
     * @param patternType          - pattern Type
     * @param principal            - principal name
     * @param operations           - to validate groups Acls
     * @return boolean             - value of flag
     * @throws ExecutionException  - an exception throws user unable to validate group Acls
     */
    public boolean validateGroupAcls(String groupName, String patternType, String principal, List<String> operations) throws InterruptedException, ExecutionException {
        Admin adminClient = (Admin) testContext.getProperty(kAdminClient);
        ListConsumerGroupsResult consumerGroupsResults = adminClient.listConsumerGroups();
        Collection<ConsumerGroupListing> listConsumerGroupListing = consumerGroupsResults.valid().get();
        LOG.info("listConsumerGroupListing : {}", listConsumerGroupListing);
        boolean flag;
        var rf = new ResourcePatternFilter(ResourceType.GROUP, groupName, PatternType.ANY);
        var abf = new AclBindingFilter(rf, AccessControlEntryFilter.ANY);
        DescribeAclsResult dar = adminClient.describeAcls(abf);

        List<String> operationsGiven = new ArrayList<>();
        for (AclBinding acls : dar.values().get()) {
            ResourcePattern rp = acls.pattern();
            AccessControlEntry acl = acls.entry();

            if (acl.principal().contains(principal) &&
                    rp.patternType().name().equals(patternType)) {
                operationsGiven.add(acl.operation().name());
            }
        }

        var operationGranted = true;
        for (String op : operations) {
            LOG.info("op={}", op);
            if (!operationsGiven.contains(op)) {
                LOG.info("operationGranted=false");
                operationGranted = false;
                break;
            }
        }

        if (operationGranted) {
            reportAndLogging.addStepToReport(strMessage + groupName + " has correct Permissions for princial " + principal,"INFO");
            flag = true;
        } else {
            reportAndLogging.addStepToReport(strMessage + groupName + " doesn't have correct Permissions for princial " + principal,"WARN");
            flag = false;
        }
        return flag;
    }

}
