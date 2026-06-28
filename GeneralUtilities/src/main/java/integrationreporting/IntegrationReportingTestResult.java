package integrationreporting;


import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.cucumber.java.Status;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "testCaseName",
        "status",
        "testCaseDescription",
        "orderId",
        "customerId"
})
/**
 * This class perform operation on integration reporting test result.
 * This class contains a method such as get/set test case name,get/set status,get/set order id etc.
 */
@Generated("jsonschema2pojo")
public class IntegrationReportingTestResult {
    @JsonProperty("testCaseName")
    private String testCaseName;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("testCaseDescription")
    private String testCaseDescription;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("customerId")
    private String customerId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * This method is used to get testCaseName.
     *
     * @return - testCaseName
     */
    @JsonProperty("testCaseName")
    public String getTestCaseName() {
        return testCaseName;
    }

    /**
     * This method is used to set testCaseName.
     *
     * @param testCaseName - to set test case name
     */
    @JsonProperty("testCaseName")
    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    /**
     * This method is used to get status.
     *
     * @return - status
     */
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    /**
     * This method is used to set status.
     *
     * @param status - to set status
     */
    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * This method is used to get testCaseDescription.
     *
     * @return - testCaseDescription
     */
    @JsonProperty("testCaseDescription")
    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    /**
     * This method is used to set testCaseDescription.
     *
     * @param testCaseDescription - to set test case description
     */
    @JsonProperty("testCaseDescription")
    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }

    /**
     * This method is used to get orderId.
     *
     * @return - orderId
     */
    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }

    /**
     * This method is used to set orderId.
     *
     * @param orderId - to set order id
     */
    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * This method is used to customerId.
     *
     * @return - customerId
     */
    @JsonProperty("customerId")
    public String getCustomerId() {
        return customerId;
    }

    /**
     * This method is used to set customerId.
     *
     * @param customerId - to set customer id
     */
    @JsonProperty("customerId")
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * This method is used to get additional properties.
     *
     * @return - additionalProperties
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * This method is used to set additionalProperties.
     *
     * @param name  - to set additional property
     * @param value - its value to set additional property
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}