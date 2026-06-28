package com.solstice.staf.interframework.integrationtests;


import de.kabeldeutschland.wss.testsuitedatareport.TestSuiteRunPatch;
import de.kabeldeutschland.wss.testsuitedatareport.TestSuiteRunStatus;
import de.kabeldeutschland.wss.testsuitedatareport.client.TestSuiteDataReportClient;
import io.cucumber.datatable.DataTable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;


/**
 * This class perform operation on test suites.
 * This class contains methods to perform different operations on test suites such as, to check test suite status (ready/in progress/done), filter passed tests from suites, etc.
 */
@SuppressWarnings("squid:S6548") // Justification: Singleton required for centralized test suite operations
public class TestSuiteOperations {

    private static ThreadLocal<Map<String, String>> phaseDetails = ThreadLocal.withInitial(HashMap::new);

    private TestSuiteDataReportClient client;

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private TestSuiteOperations() {
    }

    public static Map<String, String> getPhaseDetails() {
        return phaseDetails.get();
    }

    public void setPhaseDetails(Map<String, String> phaseDetail) {
        phaseDetails.set(phaseDetail);
    }

    /**
     * This method will return the singleton instance of this class.
     *
     * @return - instance(object) of this class
     */
    public static TestSuiteOperations getInstance() {
        return TestSuiteOperationsInitializer.instance;
    }

    /**
     * This method is used to cleanup the thread local instance.
     */
    public void unload() {
        phaseDetails.remove();
    }

    /**
     * This method is used to call instance of test suite data report client.
     *
     * @return - instance of test suite data report client
     */
    public TestSuiteDataReportClient getClientInstance() {
        if (this.client == null) {
            createClientInstance();
        }
        return this.client;
    }

    /**
     * This method is used to create instance of test suite data report client.
     */
    public void createClientInstance() {
        TestSuiteDataReportClient cl;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestSuiteDataReportClientConfiguration.class);
        cl = context.getBean(TestSuiteDataReportClient.class);
        this.client = cl;
    }

    /**
     * This method is used to change overall suite status to ready/in progress/done/done_and_uploaded etc.
     *
     * @param suiteId       - test suite ID
     * @param overallStatus - test suite status
     */
    public void changeSuiteStatus(Long suiteId, TestSuiteRunStatus overallStatus) {
        getClientInstance().updateTestSuiteRun(suiteId, TestSuiteRunPatch.builder().overallStatus(overallStatus).build());
    }

    /**
     * This method is used to retrieve the custom data based on index for given suite.
     *
     * @param suiteId - test suite ID
     * @param index   - test case index
     * @return - custom data from test suite for given index
     */
    public Map<String, String> getCustomDataBasedOnIndex(Long suiteId, int index) {
        return (Map<String, String>) getClientInstance().getTestSuiteRun(suiteId).getCurrentPhase().getResults().get(index).getCustomData();
    }

    /**
     * Sets the test suite run flag based on the given input.
     *
     * @param initiateOrContinueOrchestratedTestCase A string that determines the state of the test suite.
     *                                               If it's "initiate", the integration test will be initiated in STAF.
     *                                               If it's "continue", the integration test will be continued in STAF.
     *                                               If it's "terminate", the integration test will be terminated in STAF.
     * @param phaseUpdateDetails                     A DataTable containing the details of the phase update.
     */
    public void setTestSuiteRunFlag(String initiateOrContinueOrchestratedTestCase, DataTable phaseUpdateDetails) {
        Map<String, String> phaseDetail = phaseUpdateDetails.asMap();
        OrchestrationController orchestrationController = OrchestrationController.getInstance();
        setPhaseDetails(phaseDetail);
        if (initiateOrContinueOrchestratedTestCase.equalsIgnoreCase("initiate")) {
            orchestrationController.setInitiateIntegrationTest(true);
        }
        if (initiateOrContinueOrchestratedTestCase.equalsIgnoreCase("continue")) {
            orchestrationController.setContinueIntegrationTest(true);
        }
        if (initiateOrContinueOrchestratedTestCase.equalsIgnoreCase("terminate")) {
            orchestrationController.setTerminateIntegrationTest(true);
        }
    }

    public void setTestSuiteRunFlagTermination(String initiateOrContinueOrchestratedTestCase) {
        OrchestrationController orchestrationController = OrchestrationController.getInstance();
        if (initiateOrContinueOrchestratedTestCase.equalsIgnoreCase("terminate")) {
            orchestrationController.setTerminateIntegrationTest(true);
        }
    }

    /**
     * Private Static inner class which is loaded when getInstance() is called for the first time.
     */
    private static class TestSuiteOperationsInitializer {
        private static final TestSuiteOperations instance = new TestSuiteOperations();
    }


}