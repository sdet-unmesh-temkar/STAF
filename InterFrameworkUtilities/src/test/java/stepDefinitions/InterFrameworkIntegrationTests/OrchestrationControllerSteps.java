package stepDefinitions.InterFrameworkIntegrationTests;


import com.solstice.staf.interframework.integrationtests.TestSuiteOperations;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;


/**
 * This class contains steps which decide if Orchestration flow will initiate, continue or terminate in STAF
 */
public class OrchestrationControllerSteps {

    /**
     * Sets the test suite run flag based on the given input.
     *
     * @param initiateOrContinueOrchestratedTestCase A string that determines the state of the test suite.
     *        If it's "initiate", the integration test will be initiated in STAF.
     *        If it's "continue", the integration test will be continued in STAF.
     *        If it's "terminate", the integration test will be terminated in STAF.
     * @param phaseUpdateDetails A DataTable containing the details of the phase update.
     */
    @Given("^We prepare to (initiate|continue|terminate) the inter-framework test case with following values$")
    public void setTestSuiteRunFlag(String initiateOrContinueOrchestratedTestCase, DataTable phaseUpdateDetails) {
        TestSuiteOperations.getInstance().setTestSuiteRunFlag(initiateOrContinueOrchestratedTestCase, phaseUpdateDetails);
    }

    @Given("We prepare to terminate the inter-framework test case")
    public void setTestSuiteRunFlagTerminate() {
        TestSuiteOperations.getInstance().setTestSuiteRunFlagTermination("terminate");
    }
}