package formatter;

import generalutilities.ReportAndLogging;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher ;
import io.cucumber.plugin.event.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reporting.ReportGenerator;
import reporting.ReportUtils;
import reporting.TestResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A Cucumber event listener that captures test execution results and delegates report generation to the {@link ReportGenerator}. It collects normal and
 * interframework test results and reads auxiliary data files to enrich the report.
 */

public class ExecutionReport implements ConcurrentEventListener {
    private final List<TestResult> normalResults = new ArrayList<>();
    private final List<TestResult> interframeworkResults = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionReport.class);
    private static final Object fileLock = new Object();

    public static ExecutionReport getInstance() {return new ExecutionReport();}

    /**
     * Subscribes to Cucumber’s test lifecycle events.
     *
     * @param publisher the event publisher used to register handlers
     */
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
    }

    /**
     * Invoked when an individual test case finishes. Extracts the test’s name, status, error (if any), and JIRA tag to build a {@link TestResult}, then
     * classifies it as normal or interframework.
     * @param event the event containing the finished test case data
     */
    private void handleTestCaseFinished(TestCaseFinished event) {
        Map<String, String> interframeworkRunResults = ReportUtils.readInterframeworkFileToMap();
        String jiraTag=getJiraID(event);
        String name = event.getTestCase().getName();
        Status status = event.getResult().getStatus();
        String reason = event.getResult().getError() != null ? event.getResult().getError().getMessage() : "Test scenario is passed";
        String error=StringUtils.left(reason, 250);
        boolean isInter = jiraTag.startsWith("@InterframeworkJiraKey:");
        String key = isInter ? jiraTag.replace("@InterframeworkJiraKey:", "@") : jiraTag;
        String testSuitId = isInter ? interframeworkRunResults.getOrDefault(key, "") : "";
        TestResult result = new TestResult(key, name, status, error, testSuitId);
        if (isInter) {
            interframeworkResults.add(result);
        }
        else{
            normalResults.add(result);
        }
    }

    /**
     * Invoked after all test cases have run. Determines if auxiliary info files exist, reads any additional info, and triggers report generation.
     * @param event the event indicating the test run has finished
     */
    private void handleTestRunFinished(TestRunFinished event) {
        boolean infoFileExists = ReportUtils.fileExists();
        Map<String, String> additionalInfo = getAdditionalInfoMap(infoFileExists);
        ReportGenerator.generateFiles(normalResults,interframeworkResults,infoFileExists,additionalInfo);
    }

    /**
     * Reads additional information from file if it exists, otherwise returns an empty map.
     *
     * @param fileExists whether the additional info file is present
     * @return a map of additional info key–value pairs, or empty if none
     */
    private static Map<String, String> getAdditionalInfoMap(boolean fileExists) {
        if (fileExists) {
            return ReportUtils.readAdditionalInfoMap();
        }
        LOG.debug("additionalInfo.txt file does not exists");
        return Collections.emptyMap();
    }

    /**
     * Scans the tags of a finished test case to extract the JIRA identifier.
     *
     * @param event the event whose test case tags are to be scanned
     * @return the matching JIRA tag, or an empty string if none found
     */
    private static String getJiraID(TestCaseFinished event){
        List<String> tags = event.getTestCase().getTags();
        String jiraTag = "";
        if (tags != null) {
            for (String tag : tags) {
                if (tag.startsWith("@SOL")) {
                    jiraTag = tag;
                    break;
                }
                else if (tag.startsWith("@InterframeworkJiraKey")) {
                    jiraTag = tag;
                }
            }
        }
        return jiraTag;
    }

    public void addAdditionalInfo(String info) {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("target" + File.separator + "additionalInfo.txt", true))) {
                String scenarioName = ReportAndLogging.getScenario().getName();
                writer.write(scenarioName + "," + info);
                writer.newLine();
                LOG.info("Additional info added to the file successfully.");

            } catch (IOException e) {
                LOG.debug("Error writing to the file additionalInfo.txt: {}", e.getMessage());
            }
        }


    }

}
