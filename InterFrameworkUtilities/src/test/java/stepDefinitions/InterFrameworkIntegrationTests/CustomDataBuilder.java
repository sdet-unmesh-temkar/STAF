package stepDefinitions.InterFrameworkIntegrationTests;


import com.solstice.staf.interframework.integrationtests.CustomData;
import generalutilities.StringInterpolation;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;


/**
 * This class contains steps responsible to update test data (also called custom data) for the upcoming phase of the orchestrated test case in another automation
 */
public class CustomDataBuilder {
    StringInterpolation stringInterpolation = new StringInterpolation();


    /**
     * This method updates test data (also called custom data) for the upcoming phase of the orchestrated test case in another automation.
     * In the test data if we pass a value with syntax ${{testContext.key}}, ${{applicationContext.key}}, ${{vault.key}} the test data gets string interpolated and fetches value of the key from testContext , applicationContext, vault respectively.
     *
     * @param testData datatable representing test data for test case in another automation
     */
    @When("We update the custom data for the inter-framework test case with the following values")
    public void prepareCustomData(DataTable testData) {
        Map<String, String> dataTableMap = testData.asMap();
        Map<String, Object> replacedMapValue = new HashMap<>();
        for (Map.Entry<String, String> entry : dataTableMap.entrySet()) {
            replacedMapValue.put(entry.getKey(), stringInterpolation.stringInterpolation(entry.getValue()));
        }
        CustomData.getInstance().setCustomDataMap(replacedMapValue);
    }

    /**
     * This method updates json data (also called custom data) for the upcoming phase of the orchestrated test case in another automation.
     *
     * @param value - It is the json value that has to be updated in the service
     */
    @When("We update the custom data for the inter-framework test case with the json {string}")
    public void prepareCustomDataJson(String value) {
        Object jsonValue = stringInterpolation.stringInterpolation(value);
        CustomData.getInstance().setCustomDataJson(jsonValue);
    }
}