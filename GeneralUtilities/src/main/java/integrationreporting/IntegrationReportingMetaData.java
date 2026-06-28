package integrationreporting;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "environment",
        "testSuiteName",
        "testSuiteDesc",
        "testSuiteRunId",
        "executionfinishTimeStamp",
        "sourceApp",
        "toBeExecutedIn",
        "overallStatus",
        "requiredTestAutomationFlow",
        "testResults"
})

/**
 * This class perform operation on integration reporting data.
 * This class contains a method such as get/set envirnoment,get/set test suite name,get/set test suite description etc.
 */
@Generated("jsonschema2pojo")
public class IntegrationReportingMetaData {
    @JsonProperty("environment")
    private String environment;
    @JsonProperty("testSuiteName")
    private String testSuiteName;
    @JsonProperty("testSuiteDesc")
    private String testSuiteDesc;
    @JsonProperty("testSuiteRunId")
    private Double testSuiteRunId;
    @JsonProperty("executionfinishTimeStamp")
    private String executionfinishTimeStamp;
    @JsonProperty("sourceApp")
    private String sourceApp;
    @JsonProperty("toBeExecutedIn")
    private String toBeExecutedIn;
    @JsonProperty("overallStatus")
    private String overallStatus;
    @JsonProperty("requiredTestAutomationFlow")
    private String requiredTestAutomationFlow;
    @JsonProperty("testResults")
    private List<IntegrationReportingTestResult> testResults = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * This method is used to get the envirnoment.
     *
     * @return String - environment
     */
    @JsonProperty("environment")
    public String getEnvironment() {
        return environment;
    }

    /**
     * This method is used to set the envirnoment.
     *
     * @param environment - envirnoment
     */
    @JsonProperty("environment")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * This method is used to get testSuiteName.
     *
     * @return String - testSuiteName
     */
    @JsonProperty("testSuiteName")
    public String getTestSuiteName() {
        return testSuiteName;
    }

    /**
     * This method is used to set testSuiteName.
     *
     * @param testSuiteName - test suite name
     */
    @JsonProperty("testSuiteName")
    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    /**
     * This method is used to get testSuiteDesc.
     *
     * @return String - testSuiteDesc
     */
    @JsonProperty("testSuiteDesc")
    public String getTestSuiteDesc() {
        return testSuiteDesc;
    }

    /**
     * This method is used to set testSuiteDesc.
     *
     * @param testSuiteDesc - test suite description
     */
    @JsonProperty("testSuiteDesc")
    public void setTestSuiteDesc(String testSuiteDesc) {
        this.testSuiteDesc = testSuiteDesc;
    }

    /**
     * This method is used to get testSuiteRunId.
     *
     * @return - testSuiteRunId
     */
    @JsonProperty("testSuiteRunId")
    public Double getTestSuiteRunId() {
        return testSuiteRunId;
    }

    /**
     * This method is used to set test suite run id.
     *
     * @param testSuiteRunId - test suite run id
     */
    @JsonProperty("testSuiteRunId")
    public void setTestSuiteRunId(Double testSuiteRunId) {
        this.testSuiteRunId = testSuiteRunId;
    }

    /**
     * This method is used to get executionfinishTimeStamp.
     *
     * @return - executionfinishTimeStamp
     */
    @JsonProperty("executionfinishTimeStamp")
    public String getExecutionfinishTimeStamp() {
        return executionfinishTimeStamp;
    }

    /**
     * This method is used to set executionfinishTimeStamp.
     *
     * @param executionfinishTimeStamp - total time required for execution
     */
    @JsonProperty("executionfinishTimeStamp")
    public void setExecutionfinishTimeStamp(String executionfinishTimeStamp) {
        this.executionfinishTimeStamp = executionfinishTimeStamp;
    }

    /**
     * This method is used to get source app.
     *
     * @return - sourceApp
     */
    @JsonProperty("sourceApp")
    public String getSourceApp() {
        return sourceApp;
    }

    /**
     * This method is used to set source app.
     *
     * @param sourceApp - to set source app
     */
    @JsonProperty("sourceApp")
    public void setSourceApp(String sourceApp) {
        this.sourceApp = sourceApp;
    }

    /**
     * This method is used to get to be executed in.
     *
     * @return - toBeExecutedIn
     */
    @JsonProperty("toBeExecutedIn")
    public String getToBeExecutedIn() {
        return toBeExecutedIn;
    }

    /**
     * This method is used to set to be executed in.
     *
     * @param toBeExecutedIn - to set to be executed in
     */
    @JsonProperty("toBeExecutedIn")
    public void setToBeExecutedIn(String toBeExecutedIn) {
        this.toBeExecutedIn = toBeExecutedIn;
    }

    /**
     * This method is used to get overall status.
     *
     * @return - overallStatus
     */
    @JsonProperty("overallStatus")
    public String getOverallStatus() {
        return overallStatus;
    }

    /**
     * This method is used to set overall status.
     *
     * @param overallStatus -  to set overall status (pass/fail/skip)
     */
    @JsonProperty("overallStatus")
    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    /**
     * This method is used to get requiredTestAutomationFlow.
     *
     * @return - requiredTestAutomationFlow
     */
    @JsonProperty("requiredTestAutomationFlow")
    public String getRequiredTestAutomationFlow() {
        return requiredTestAutomationFlow;
    }

    /**
     * This method is used to set requiredTestAutomationFlow.
     *
     * @param requiredTestAutomationFlow - to set required test automation flow name
     */
    @JsonProperty("requiredTestAutomationFlow")
    public void setRequiredTestAutomationFlow(String requiredTestAutomationFlow) {
        this.requiredTestAutomationFlow = requiredTestAutomationFlow;
    }

    /**
     * This method is used to get testResults.
     *
     * @return - testResults
     */
    @JsonProperty("testResults")
    public List<IntegrationReportingTestResult> getTestResults() {
        return testResults;
    }

    /**
     * This method is used to set test results.
     *
     * @param testResults - to set test results
     */
    @JsonProperty("testResults")
    public void setTestResults(List<IntegrationReportingTestResult> testResults) {
        this.testResults = testResults;
    }

    /**
     * This method is used to get the additional properties.
     *
     * @return - additionalProperties
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * This method is used to set the additional property.
     *
     * @param name  - name to set additional properties
     * @param value - its value to set the additional property
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
