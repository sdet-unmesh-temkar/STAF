package com.solstice.staf.interframework.integrationtests;

/**
 * This class decides if Orchestration flow will initiate, continue or terminate in STAF
 */
public class OrchestrationController {

    private boolean initiateIntegrationTest;
    private boolean continueIntegrationTest;
    private boolean terminateIntegrationTest;

    /**
     * Returns the current state of the terminateIntegrationTest flag.
     *
     * @return true if the integration test should be terminated, false otherwise.
     */
    public boolean isTerminateIntegrationTest() {
        return this.terminateIntegrationTest;
    }

    /**
     * Sets the state of the terminateIntegrationTest flag.
     *
     * @param terminateIntegrationTest the new state of the terminateIntegrationTest flag.
     */
    public void setTerminateIntegrationTest(boolean terminateIntegrationTest) {
        this.terminateIntegrationTest = terminateIntegrationTest;
    }

    /**
     * This method is used to check if the integration test should continue.
     *
     * @return boolean This returns the current state of the continueIntegrationTest.
     */
    public boolean isContinueIntegrationTest() {
        return this.continueIntegrationTest;
    }

    /**
     * This method is used to set the state of the integration test.
     *
     * @param continueIntegrationTest This is the new state of the integration test.
     */
    public void setContinueIntegrationTest(boolean continueIntegrationTest) {
        this.continueIntegrationTest = continueIntegrationTest;
    }

    /**
     * Returns the current status of the initiateIntegrationTest flag.
     *
     * @return true if the integration test is to be initiated, false otherwise.
     */
    public boolean isInitiateIntegrationTest() {
        return this.initiateIntegrationTest;
    }

    /**
     * Sets the status of the initiateIntegrationTest flag.
     *
     * @param initiateIntegrationTest the new status of the initiateIntegrationTest flag.
     */
    public void setInitiateIntegrationTest(boolean initiateIntegrationTest) {
        this.initiateIntegrationTest = initiateIntegrationTest;
    }

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private OrchestrationController() {
    }

    /**
     * This is a private static inner class which is loaded when getInstance() is called for the first time.
     */
    private static class OrchestrationControllerInitializer {
        private static final OrchestrationController instance = new OrchestrationController();
    }

    /**
     * This method will return the singleton instance of this class.
     *
     * @return - instance (object) of this class
     */
    public static OrchestrationController getInstance() {
        return OrchestrationController.OrchestrationControllerInitializer.instance;
    }
}