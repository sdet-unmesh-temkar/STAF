package apiutilities;

import generalutilities.ReportAndLogging;
import generalutilities.TestContext;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalTo;

/**
 * This class is used for validate response.
 * This class contain a method related to verify status/response code,validate JSON tag value,verify response field etc.
 */

public class ResponseValidationMethods {
    TestContext<Object> testContext = TestContext.getInstance();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    APIRequestRelatedMethods apiRequestRelatedMethods = APIRequestRelatedMethods.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(ResponseValidationMethods.class);

    /**
     * This method is used to validates response status code when API is triggered.
     *
     * @param response           - to check status code when API is triggered
     * @param expectedStatusCode - status code value that is expected
     * @return                   - json
     */
    public ValidatableResponse verifyStatusCode(Response response, int expectedStatusCode) {
        ValidatableResponse json;
        json = response.then().statusCode(expectedStatusCode);
        return json;
    }

    /**
     * This method validates the received response status code.
     *
     * @param response     - to check received response status code
     * @param parameters   - parameters consist of field name to be checked and its expected value
     */
    public void verifyResponseCode(String response, String parameters) {
        String[] strSplit = parameters.split(Pattern.quote("^"));
        var js = new JsonPath(response);
        var value = js.getString(strSplit[0]);
        try {
            if (value.equals(strSplit[1])) {
                reportAndLogging.addStepToReport("pass, Expected Response and  Actual Response match ", "INFO");
                reportAndLogging.logStepInJira("Response Body: " + value);
            } else {
                LOG.error("fail Expected Response  Actual Response does not match");
                reportAndLogging.addStepToReport("fail E,Actual and expected Response does not match", "WARN");
                reportAndLogging.logStepInJira("Response Body: " + value);
            }
        } catch (Exception e) {
            reportAndLogging.logStepInJira("Table Name is not not found");
            reportAndLogging.addStepToReport("Table Name is  not found","WARN");
            LOG.error("Table Name is  not found");
        }
    }

    /**
     * This method is used to validate values obtained in API response.
     *
     * @param rootObject - json root object which contains entire json response
     * @param tagName    - tag whose value has to be validated
     * @return           - Object
     */
    public Object validateJSONTagValue(JSONObject rootObject, String tagName) {
        var jSONHandlingMethods = new JSONHandlingMethods();
        return jSONHandlingMethods.getJSONTagValue(rootObject, tagName);
    }

    /**
     * This method validates particular fields and its value in API response.
     *
     * @param json               - response json to be validated
     * @param requestBodyMapData - validate response object and fields to be validated in the form of key-value pairs in a map.
     */
    public void verifyReponseFields(ValidatableResponse json, Map<String, String> requestBodyMapData) {
        for (Map.Entry<String, String> field : requestBodyMapData.entrySet()) {
            if (StringUtils.isNumeric(field.getValue())) {
                json.body(field.getKey(), equalTo(Integer.parseInt(field.getValue())));
            } else {
                json.body(field.getKey(), equalTo(field.getValue()));
            }
        }
    }

    /**
     * This method is used to retrieve response objects.
     *
     * @param responseKey - the response key to retrieve response objects
     * @param envKey      - to retrieve response objects
     */
    public void retriveResponseObjects(String responseKey, String envKey) {
        var js = new JsonPath(apiRequestRelatedMethods.getResponse().getBody().asString());
        var responseValue = js.getString(responseKey);
        testContext.setProperty(envKey, responseValue);
    }
}