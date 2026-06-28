package stepDefinitions;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import stepUtils.StepDefinitionBase;
import testdatatool.TestDataToolManagement;
import testDataTool.utils.TestContextTestDataToolUtilities;
import testdatatool.TestDataToolSearchQuery;

import java.io.IOException;

/**
 * Step definitions for Test Data Tool customer search scenarios.
 * <p>
 * Provides Cucumber step bindings for searching customers using dynamic queries and saving results in context.
 * Also defines a custom parameter type for mapping feature file query strings to enum values.
 * </p>
 */
public class TestDataToolsSteps extends StepDefinitionBase {
    /**
     * Manager for Test Data Tool API interactions.
     */
    TestDataToolManagement testDataToolManagement = new TestDataToolManagement();
    /**
     * Utility for storing and retrieving test context data related to Test Data Tool.
     */
    private final TestContextTestDataToolUtilities testContextTestDataToolUtilities = new TestContextTestDataToolUtilities(loggingUtility);

    /**
     * Step definition for searching a customer by query using the Test Data Tool API.
     * <p>
     * Example usage in feature file:
     * <pre>
     *     When Test Data Tool - Get dynamic test data Customer Id search by query: ACTIVE_CUSTOMER
     * </pre>
     * </p>
     * @param testDataToolSearchQuery The search query enum value parsed from the feature file.
     */
    @When("Test Data Tool - Get dynamic test data Customer Id search by query: {testDataToolSearchQuery}")
    public void getTestDataToolCustomer(TestDataToolSearchQuery testDataToolSearchQuery) throws IOException {
        Response customerSearchResponse = testDataToolManagement.getTestDataToolCustomer(testDataToolSearchQuery);
        assertionUtility.assertHttpResponseCode(customerSearchResponse, HttpStatus.SC_OK,"Get customer");
        testContextTestDataToolUtilities.saveDataToolCustomerResponse(customerSearchResponse);
        loggingUtility.logDebugDetails("Searched Customer ID: {}", testContextTestDataToolUtilities.getCustomerDetails().getId());
        var customerDetails = testContextTestDataToolUtilities.getCustomerDetails();
        if (customerDetails.getIndividuals() != null && customerDetails.getIndividuals().length > 0 && customerDetails.getIndividuals()[0] != null) {
            loggingUtility.logDebugDetails("Searched Individual ID: {}", customerDetails.getIndividuals()[0].getId());
        }
        else {
            loggingUtility.logDebugDetails("No individual details found for customer ID: {}", customerDetails.getId());
        }
    }

    /**
     * Custom Cucumber parameter type for mapping feature file query strings to TestDataToolSearchQuery enum values.
     * <p>
     * Allows step definitions to use readable query names in feature files.
     * </p>
     * @param testDataToolSearchQuery The query string from the feature file.
     * @return Corresponding TestDataToolSearchQuery enum value.
     */
    @ParameterType("ACTIVE_CUSTOMER|ACTIVE_CUSTOMER_WITH_DELPHI_ID|CUSTOMER_WITH_GZ250|CUSTOMER_WITH_GZ500|" +
            "CUSTOMER_WITH_GZ1000|CUSTOMER_WITH_VF_STATION|CUSTOMER_WITH_GZ100|CUSTOMER_WITH_FRITZ6670|ACTIVE_CUSTOMER_WITH_OSF_ID|" +
            "CUSTOMER_WITH_FRITZ6690|CUSTOMER_WITH_MAC_ADDRESS")
    public TestDataToolSearchQuery testDataToolSearchQuery(String testDataToolSearchQuery) {
        return TestDataToolSearchQuery.valueOf(testDataToolSearchQuery);
    }
}