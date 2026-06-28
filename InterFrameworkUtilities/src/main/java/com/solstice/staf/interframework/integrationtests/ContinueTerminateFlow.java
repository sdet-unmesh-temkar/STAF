package com.solstice.staf.interframework.integrationtests;

import de.kabeldeutschland.wss.testsuitedatareport.TestResult;
import de.kabeldeutschland.wss.testsuitedatareport.TestStep;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ContinueTerminateFlow {
    private static final Logger log = LoggerFactory.getLogger(ContinueTerminateFlow.class);
    private static final String STATUS="status";
    /**
     * This method creates a JSON object that represents the results of a set of tests.
     * It reads the test results from a Cucumber JSON report, and then constructs a new JSON object
     * that includes details about each test case and its steps. The output of this method is a JSON file
     * named 'resultsObject.json' in the 'target' directory.
     *
     * @throws IOException If an input or output exception occurred
     *                     <p>
     *                     The JSON object in the file will have the following structure:
     *                     {
     *                     "environment": "<name of the environment>",
     *                     "executedBy": "<name of the executedBy>",
     *                     "nextPhaseToBeExecutedIn": "<name of the nextPhaseToBeExecutedIn>",
     *                     "requiredTestAutomationFlow": "<name of the requiredTestAutomationFlow>",
     *                     "results": [
     *                     {
     *                     "testCaseName": "<name of the test case>",
     *                     "status": "<PASSED or FAILED>",
     *                     "testCaseDescription": "<description of the test case>",
     *                     "customData": "<custom data associated with the test result>",
     *                     "testSteps": [
     *                     {
     *                     "name": "<name of the test step>",
     *                     "errorMessage": "<error message if the step failed>",
     *                     "duration": "<duration of the test step>",
     *                     "status": "<status of the test step>",
     *                     "keyword": "<keyword associated with the test step>",
     *                     "infoLog": "<information log of the test step>"
     *                     },
     *                     ...
     *                     ]
     *                     }
     *                     ]
     *                     }
     */
    public void updateJSONFile(List<TestResult<Object>> scenarioResultsList) throws IOException {
        Long suiteID = Long.parseLong(System.getProperty("suiteID"));
        int index = Integer.parseInt(System.getProperty("passedTestResultIndex"));

        TestSuiteOperations testSuiteOperations = TestSuiteOperations.getInstance();
        String testCaseName = testSuiteOperations.getClientInstance().getTestSuiteRun(suiteID).getCurrentPhase().getResults().get(index).getTestCaseName();
        String testCaseDescription = testSuiteOperations.getClientInstance().getTestSuiteRun(suiteID).getCurrentPhase().getResults().get(index).getTestCaseDescription();
        String jiraId = testSuiteOperations.getClientInstance().getTestSuiteRunHistory(suiteID).getAllPhases().get(0).getResults().get(0).getJiraId();
        Map<String, String> phaseDetails = TestSuiteOperations.getPhaseDetails();
        JSONObject jsonObjectPhaseDetails = new JSONObject();
        jsonObjectPhaseDetails.put("environment", getEnvironment(phaseDetails.get("environment")));
        jsonObjectPhaseDetails.put("executedBy", "Solstice");
        jsonObjectPhaseDetails.put("nextPhaseToBeExecutedIn", phaseDetails.get("nextPhaseToBeExecutedIn"));
        jsonObjectPhaseDetails.put("requiredTestAutomationFlow", phaseDetails.get("requiredTestAutomationFlow"));

        JSONArray resultsArray = new JSONArray();
        for (TestResult<Object> testResult : scenarioResultsList) {
            JSONObject resultObject = new JSONObject();
            resultObject.put("testCaseName", testCaseName);
            resultObject.put(STATUS, testResult.getStatus().toString());
            resultObject.put("testCaseDescription", testCaseDescription);
            resultObject.put("customData", CustomData.getInstance().getCustomData());
            resultObject.put("jiraId", jiraId);

            JSONArray testStepsArray = new JSONArray();
            for (TestStep testStep : testResult.getTestSteps()) {
                JSONObject testStepObject = new JSONObject();
                testStepObject.put("name", testStep.getName());
                testStepObject.put("errorMessage", testStep.getErrorMessage());
                testStepObject.put("duration", testStep.getDuration());
                testStepObject.put(STATUS, testStep.getStatus().toString());
                testStepObject.put("keyword", testStep.getKeyword().toString());
                testStepObject.put("infoLog", testStep.getInfoLog());
                testStepsArray.add(testStepObject);
            }
            resultObject.put("testSteps", testStepsArray);
            resultsArray.add(resultObject);
        }
        jsonObjectPhaseDetails.put("results", resultsArray);

        log.info("JsonObject to be read in Github actions: \n {}", jsonObjectPhaseDetails);

        File testResultsJson = new File("target" + File.separator + "resultsObject.json");
        if (!testResultsJson.exists()) {
            boolean isFileCreated = testResultsJson.createNewFile();
            if (!isFileCreated) {
                log.info("File already exists or could not be created");
            }
        }

        try (PrintWriter pw = new PrintWriter(testResultsJson)) {
            pw.println(jsonObjectPhaseDetails);
        }
    }

    private String getEnvironment(String env) {
        if (env == null || env.equals("")) {
            return System.getProperty("env");
        }
        return env;
    }


}
