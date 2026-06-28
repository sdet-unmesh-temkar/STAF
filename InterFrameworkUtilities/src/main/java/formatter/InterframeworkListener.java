package formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.InvalidJsonException;
import com.solstice.staf.interframework.integrationtests.*;
import de.kabeldeutschland.wss.testsuitedatareport.TestStep;
import de.kabeldeutschland.wss.testsuitedatareport.*;
import de.kabeldeutschland.wss.testsuitedatareport.client.TestSuiteDataReportClient;
import de.kabeldeutschland.wss.testsuitedatareport.client.TestSuiteDataReportClientImpl;
import de.kabeldeutschland.wss.testsuitedatareport.enums.TestStepKeyword;
import de.kabeldeutschland.wss.testsuitedatareport.enums.TestStepStatus;
import generalutilities.TestContext;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.*;
import io.cucumber.shaded.gherkin.messages.internal.gherkin.GherkinDocumentBuilder;
import io.cucumber.shaded.gherkin.messages.internal.gherkin.Parser;
import io.cucumber.shaded.messages.IdGenerator;
import io.cucumber.shaded.messages.types.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.collections.Maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;


public class InterframeworkListener implements ConcurrentEventListener {

    Scenario scenario;
    OrchestrationController orchestrationController = OrchestrationController.getInstance();
    private static final String SCENARIONAME = "ScenarioName";
    Map<String, Object> testStep = new HashMap<>();
    List<TestResult<Object>> scenarioResultsList = new ArrayList<>();
    List<TestStep> scenarioStepResultsList = new ArrayList<>();
    int examplesSize = 0;
    private static final String ENVIRONMENT = "environment";
    private static final String NEXT_PHASE_TO_BE_EXECUTED_IN = "nextPhaseToBeExecutedIn";
    private static final String REQUIRED_TEST_AUTOMATION_FLOW = "requiredTestAutomationFlow";
    private static final String DESCRIPTION = "Description";
    private static final Logger log = LoggerFactory.getLogger(InterframeworkListener.class);
    private static final String JIRAID = "JiraID";
    List<Status> testCaseStatus = new ArrayList<>();
    boolean flag = false;
    Map<String, Object> testSuiteRunID = new HashMap<>();

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
    }

    /**
     * This method fetches custom data from the service for continue and terminate flow when suiteID and passedTestResultIndex is passed in mvn arguments
     * It also fetches test step related information
     * @param event : It is test step started event
     */
    private void handleTestStepStarted(TestStepStarted event) {

        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep testSteps = (PickleStepTestStep) event.getTestStep();
            Step currentStep = testSteps.getStep();
            String testStepName = currentStep.getText();
            testStep.put("TestStepName", testStepName);
            testStep.put("Keyword", currentStep.getKeyword());
        }
        log.info("Test Step Info: {}", testStep);
        if (!testStep.isEmpty() && (System.getProperty("suiteID") != null && System.getProperty("passedTestResultIndex") != null) && !flag) {
            TestContext<Object> testContext = TestContext.getInstance();
            Long suiteID = Long.parseLong(System.getProperty("suiteID"));
            int index = Integer.parseInt(System.getProperty("passedTestResultIndex"));
            Map<String, String> customDataForIndex = TestSuiteOperations.getInstance().getCustomDataBasedOnIndex(suiteID, index);
            log.info("*** CustomData for index: {} of suite:{} :{}   ", index, suiteID, customDataForIndex);
            testContext.setProperty("customDataForIndex", customDataForIndex);
            flag = true;

        }
    }


    /**This method creates list of testStepSchema as per Orchestration services after execution of each test step
     * @param event : This is Test step finished event
     */
    private void handleTestStepFinished(TestStepFinished event) {
        if (!testStep.isEmpty()) {
            TestStepStatus status = getStatus(event.getResult().getStatus());
            TestStep testStepSchema = TestStep.builder()
                    .name((String) testStep.get("TestStepName"))
                    .errorMessage(getErrorMessage(event.getResult().getError()))
                    .duration(event.getResult().getDuration().toMillis())
                    .status(status)
                    .keyword(retrieveTestStepKeyword((String) testStep.get("Keyword")))
                    .infoLog("")
                    .build();
            scenarioStepResultsList.add(testStepSchema);

        }
        testStep.clear();
        log.info("Test step schema: {}", scenarioStepResultsList);

    }

    /**
     * This method returns error message if its present, else it returns ""
     *
     * @param error: Error that is thrown if the test case fails
     * @return String
     */
    private String getErrorMessage(Throwable error) {
        return (error == null) ? "" : error.toString();
    }

    /**
     * This method triggers initiation flow after execution of each scenario/scenario outline if isInitiateIntegrationTest() is true
     * It also call updateJSONFile method to create and update json file for the continue and terminate flow.
     * @param event : This is test case finish event
     */
    @SneakyThrows
    private void handleTestCaseFinished(TestCaseFinished event) {
        Map<String, Object> testCaseInfo;

        if (orchestrationController.isInitiateIntegrationTest() || orchestrationController.isContinueIntegrationTest() || orchestrationController.isTerminateIntegrationTest()) {
            testCaseInfo = getGherkinDocInfo(event);
            testCaseStatus.add(event.getResult().getStatus());
            //Create Test Case schema
            createTestCaseSchema(testCaseInfo, event);
            //This loop is triggered only after execution of all the examples.
            if (examplesSize == 0 || scenarioResultsList.size() == examplesSize) {
                if (orchestrationController.isInitiateIntegrationTest()) {
                    //Create Test Suite schema
                    TestSuiteRun<Object> schema = createTestSuiteSchema(testCaseInfo);
                    //Trigger Orchestration Service
                    triggerInitiateFlow(schema, (String) testCaseInfo.get(JIRAID));
                    testCaseStatus.clear();

                }
                if (orchestrationController.isContinueIntegrationTest() || orchestrationController.isTerminateIntegrationTest()) {
                    ContinueTerminateFlow continueTerminateFlow = new ContinueTerminateFlow();
                    //Update JSON file with testcase and test sept details
                    continueTerminateFlow.updateJSONFile(scenarioResultsList);
                }
                resetValues();
            }
        }
        scenarioStepResultsList.clear();

    }

    /**
     * This method is used to fetch all the required information from Gherkin Document.
     * @param event : This is test case finished event
     * map: This method returns a map with all the required information for creating the test suite schema
     * IOException: Throws exception unable to parse feature file
     */
    private Map<String, Object> getGherkinDocInfo(TestCaseFinished event) throws IOException {
        Map<String, Object> testCaseInfo = new HashMap<>();
        String featurePath = event.getTestCase().getUri().getPath();
        String featureFileContent;
        if (System.getProperty("os.name").contains("Windows")) {
            featureFileContent = Files.readString(Path.of(featurePath.substring(1)));
        } else {
            featureFileContent = Files.readString(Path.of(featurePath));
        }

        IdGenerator idGenerator = new IdGenerator.Incrementing();
        Parser<GherkinDocument> parser = new Parser<>(new GherkinDocumentBuilder(idGenerator));
        GherkinDocument gherkinDocument = parser.parse(featureFileContent);
        scenario = null;
        for (FeatureChild t : gherkinDocument.getFeature().getChildren()) {
            if (t.getScenario() != null && Objects.equals(t.getScenario().getName(), event.getTestCase().getName())) {
                scenario = t.getScenario();
                testCaseInfo.put(SCENARIONAME, scenario.getName());
                testCaseInfo.put(DESCRIPTION, scenario.getDescription());
                for (Examples examples : scenario.getExamples()) {
                    examplesSize = examples.getTableBody().size();
                }
                for (Tag tag : scenario.getTags()) {
                    if (tag.getName().contains("@InterframeworkJiraKey")) {
                        String jiraID;
                        jiraID = tag.getName().replace("@InterframeworkJiraKey:", "");
                        testCaseInfo.put(JIRAID, jiraID);
                    }

                }
            }
        }
        return testCaseInfo;
    }

    /**
     * This method returns the test step status required as per orchestration service
     * @param status: This is the status of the step fetched from the event
     * @return TestStepStatus
     */
    private TestStepStatus getStatus(Status status) {
        TestStepStatus testStepStatus;
        if (status.is(Status.PASSED)) {
            testStepStatus = TestStepStatus.PASS;
        } else if (status.is(Status.FAILED)) {
            testStepStatus = TestStepStatus.FAIL;
        } else if (status.is(Status.SKIPPED)) {
            testStepStatus = TestStepStatus.SKIP;
        } else {
            testStepStatus = TestStepStatus.FAIL;
        }
        return testStepStatus;
    }

    TestStepKeyword stepKeyword;

    /**
     * This method returns the keyword required as per orchestration service
     * @param keyword : This is cucumber keyword fetched for each step
     * @return TestStepKeyword : returns Keyword that is required as per orchestration service
     */
    private TestStepKeyword retrieveTestStepKeyword(String keyword) {

        switch (keyword) {
            case "Given ", "When ", "BUT " -> stepKeyword = TestStepKeyword.PREPARATION;
            case "Then " -> stepKeyword = TestStepKeyword.VALIDATION;
            case "And ", "* " -> {//Do nothing
            }
            default -> {
                //do nothing
            }
        }

        log.info("testStepKeyword for step {}", stepKeyword);
        return stepKeyword;
    }

    /**
     * This method creates Test case schema as per orchestration service
     *
     * @param testCaseInfo : It the map which contains test case information(Test case name, Test case description and JIRA ID)
     * @param event        : This is test case finished event
     */
    private void createTestCaseSchema(Map<String, Object> testCaseInfo, TestCaseFinished event) {
        scenarioResultsList.add(TestResult.builder()
                .testCaseName((String) testCaseInfo.get(SCENARIONAME))
                .status(retrieveTestResultStatus(event.getResult().getStatus()))
                .testCaseDescription((String) testCaseInfo.get(DESCRIPTION))
                .customData(CustomData.getInstance().getCustomData())
                .jiraId((String) testCaseInfo.get(JIRAID))
                .testSteps(Lists.newArrayList(scenarioStepResultsList))
                .build());

    }

    /**
     * This method creates Test Suite schema as per orchestration service
     *
     * @param testCaseInfo: It is map whoch contains test case level information (Test Case, Test case description)
     * @return TestSuiteRun : Returns Test Suite Run object
     */
    private TestSuiteRun<Object> createTestSuiteSchema(Map<String, Object> testCaseInfo) {
        TestSuiteRun<Object> schema = TestSuiteRun.builder()
                .testSuiteName((String) testCaseInfo.get(SCENARIONAME))
                .testSuiteDescription(retrieveDescription((String) testCaseInfo.get(DESCRIPTION)))
                .sourceApplication("Solstice")
                .overallStatus(getTestSuiteStatus())
                .currentPhase(getTestSuiteRunPhase())
                .build();
        log.info("Schema is generated as follows: {}", schema);
        return schema;
    }

    /**
     * This method triggers the Test suite creation to orchestration service and Test suite Run ID is created.
     *
     * @param schema : This is the test suite schema that is required to trigger the service.
     */
    private void triggerInitiateFlow(TestSuiteRun<Object> schema, String jiraID) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestSuiteDataReportClientConfiguration.class)) {
            TestSuiteDataReportClient testSuiteDataReportClient = context.getBean(TestSuiteDataReportClientImpl.class);
            long runId = testSuiteDataReportClient.createTestSuiteRun(schema);
            log.info("*******Orchestration Service Triggered*******");
            log.info("*** Suite ID {} created successfully ***", runId);
            testSuiteRunID.put("@" + jiraID, runId);
            log.info("Jira ID: @ {} ,Test Suite Run ID: {}", jiraID, runId);
            writeToFile(testSuiteRunID);
        } catch (IOException e) {
            log.error("IOException on triggerInitiateFlow method: {}", e.getMessage());
        }
    }

    /**
     * This method write the test suite run ID to a file which is used in github actions.
     *
     * @param runIDs : It contains the run IDs that are generated for a test execution
     * @throws IOException: If the file is not created
     */
    private void writeToFile(Map<String, Object> runIDs) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("target"+File.separator +"interframeworkTestSuiteRunID.txt"))) {
            writer.write(String.valueOf(runIDs));
            writer.newLine();
        } catch (IOException e) {
            log.error("IOException on writeToFile method: {}", e.getMessage());
        }

    }

    private TestSuiteRunPhase<Object> getTestSuiteRunPhase() {
        Map<String, String> phaseDetails = TestSuiteOperations.getPhaseDetails();
        return TestSuiteRunPhase.builder()
                .environment(phaseDetails.get(ENVIRONMENT)) //setEnvironment
                .executedBy("Solstice") //setExecutedBy
                .nextPhaseToBeExecutedIn(getNextPhaseToBeExecutedBy(phaseDetails.get(NEXT_PHASE_TO_BE_EXECUTED_IN)))//setNextStepToBeExecutedIn
                .requiredTestAutomationFlow(phaseDetails.get(REQUIRED_TEST_AUTOMATION_FLOW))//setRequiredTestAutomationFlow
                .executionFinishTime(ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.ofHours(1))) //Time is taken care
                .results(Lists.newArrayList(scenarioResultsList)) //cucumber
                .build();
    }

    /**
     * This method checks if the custom data is a Map or Json, else returns empty String.
     *
     * @return map : Returns custom data map if data is present else returns empty String
     */
    public Object getCustomData() {

        Map<String, Object> customDataMap = CustomData.getInstance().getCustomDataMap();
        Object customDataJson = CustomData.getInstance().getCustomDataJson();
        if (!customDataMap.isEmpty()) {
            log.info("Custom Data Map: {} ", customDataMap);
            return Maps.newHashMap(customDataMap);

        }
        if (customDataJson!=null) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(String.valueOf(customDataJson));// Try to parse as JSON Object or JSON Array
                log.info("Custom Data Json: {}", customDataJson);
                return customDataJson;
            } catch (Exception e) {
                log.error("Invalid Json {}", e.getMessage());
                throw new InvalidJsonException("Invalid JSON format.");
            }

        }
        return "";
    }


    /**
     * This method returns Test case Status as required by orchestration service
     *
     * @param status: This is the status of the test case that is returned by the listener
     * @return TestResultStatus: Returns test case status
     */
    public TestResultStatus retrieveTestResultStatus(Status status) {
        TestResultStatus testResultStatus = TestResultStatus.PASSED;

        if (!status.is(Status.PASSED)) {
            testResultStatus = TestResultStatus.FAILED;
        }

        log.info("Test result status: {}", testResultStatus);
        return testResultStatus;
    }

    public String getNextPhaseToBeExecutedBy(String nextPhase) {
        return nextPhase == null ? "" : nextPhase;
    }

    /**
     * This will retrieve description that is present in the feature file. When test plan and labels for xray passed via maven command, test plan and labels for xray are appended in the test suite description
     *
     * @param description: This is the descriotio that is passed in the feature file under scenario/scenario outline
     * @return string: The description that is to be sent to the service
     */
    public String retrieveDescription(String description) {
        if (description == null || description.equals("")) {
            description = "";
        }

        String testPlan = System.getProperty("interFrameworkTestPlan");
        String labelForXray = System.getProperty("interFrameworkXrayLabels");

        if (testPlan != null) {
            description = description.concat("\n").concat("TEST_PLAN: ").concat(testPlan).concat("\n");
        }

        if (labelForXray != null) {
            description = description.concat("XRAY_LABELS: ").concat(labelForXray);
        }

        return description;
    }

    /**
     * This method determines the test suite status. If all the test cases under the suite are FAILED, then the status of the suite will be DONE. If any test case or all the test cases are PASSED then the suite status will the READY
     *
     * @return TestSuiteRunStatus
     */
    public TestSuiteRunStatus getTestSuiteStatus() {
        log.info("Test Suite result list: {}", testCaseStatus);
        boolean isPass = false;
        for (Status status : testCaseStatus) {
            if (status.equals(Status.PASSED)) {
                isPass = true;
                break;
            }
        }
        TestSuiteRunStatus result = isPass ? TestSuiteRunStatus.READY : TestSuiteRunStatus.DONE;
        log.info("Test Suite Status: {}", result);
        return result;
    }

    /**
     * This method reset the variable after an execution and creation of a test suite
     */
    private void resetValues() {
        scenarioResultsList.clear();
        examplesSize = 0;
        orchestrationController.setInitiateIntegrationTest(false);
        orchestrationController.setContinueIntegrationTest(false);
        orchestrationController.setTerminateIntegrationTest(false);
    }



}




