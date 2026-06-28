package stepDefinitions;

import apiutilities.APIRequestRelatedMethods;
import apiutilities.CommonAPIMethods;
import apiutilities.ResponseValidationMethods;
import generalutilities.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;

/**
 * This class contain rest API related method.
 * This class contain a method such as set parameters for the specified REST service,send/post/get request etc.
 */

public class RestAPISteps {
    Map<String, String> Environment = EnvironmentDataLoader.getInstance().getEnvironment();
    CommonAPIMethods commonAPIMethods = new CommonAPIMethods();
    ResponseValidationMethods responseValidationMethods = new ResponseValidationMethods();
    APIRequestRelatedMethods apiRequestRelatedMethods = APIRequestRelatedMethods.getInstance();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private static final Logger LOG = LoggerFactory.getLogger(RestAPISteps.class);

    /**
     * This method is used to configure the REST request with URL and the resource name.
     *
     * @param serviceURL   The base URL to connect to the REST services API.
     * @param resourceName The specific resource name to be used in the API request.
     */
    @Given("We configure the REST request with URL {string} and the resource name as {string}")
    public void connectToRestService(String serviceURL, String resourceName) {
        LOG.debug("Service URL from feature file: {} ", serviceURL);
        LOG.debug("Resource name from feature file: {}", resourceName);
        commonAPIMethods.connectToRestService(serviceURL, resourceName);
    }

    /**
     * This method is used to configure the REST request with URL, the resource name and the customer ID.
     *
     * @param serviceURL   The base URL for connecting to the REST services API.
     * @param resourceName The specific resource name to be used in the API request.
     * @param customerID   The unique identifier for the customer.
     */
    @Given("We configure the REST request with URL {string}, the resource name {string}, and the customer ID {string}")
    public void connectToRestService(String serviceURL, String resourceName, String customerID) {
        commonAPIMethods.connectToRestServiceWithCustId(serviceURL, resourceName, customerID);
    }

    /**
     * This method is used to set a request header using the header name with the header values.
     *
     * @param headerName  The name of the header to be set.
     * @param headerValue The value of the header.
     */
    @Given("We set a request header using the header name {string} and the header value {string}")
    public void setHeader(String headerName, String headerValue) {
        commonAPIMethods.setHeader(headerName, headerValue);
    }

    /**
     * This method is used to send a POST request with request body.
     *
     * @param requestBodyFileName File name that contains request body for the POST request inside folder /src/test/resources/JSONFiles/.
     * @throws IOException    If there is an I/O error while sending the POST request.
     * @throws ParseException If there is an error while parsing the service request body.
     */
    @When("We send a POST request with the request body from file {string}")
    public void sendPostRequest(String requestBodyFileName) throws IOException, ParseException {
        commonAPIMethods.sendPostRequest(requestBodyFileName);
    }

    /**
     * This method is used to fetch the string value from the response header and the fetched value from the response header is stored in a TestContext structure for later use.
     * This method is useful for extracting specific data from the response header and storing it and use in the test execution.
     *
     * @param headerKey key used to fetch the string value from the response header
     */
    @Then("We fetch the value from the response using the header key {string} and store it")
    public void fetchValuefromResponseHeader(String headerKey) {
        commonAPIMethods.retriveResponseHeaderObjects(headerKey, headerKey);
    }

    /**
     * This method is used to send a PUT request with request body.
     *
     * @param requestBodyFileName File name that contains request body for the PUT request inside folder /src/test/resources/JSONFiles/.
     * @throws IOException    This exception is thrown when there is an I/O error while sending the PUT request.
     * @throws ParseException This exception is thrown when there is an error while parsing the service request body.
     */
    @When("We send a PUT request with the request body from file {string}")
    public void sendPutMethod(String requestBodyFileName) throws IOException, ParseException {
        commonAPIMethods.sendPutMethod(requestBodyFileName);
    }

    /**
     * This method is used to send a POST request with request body and query parameter.
     *
     * @param requestBodyFileName File name that contains request body for the POST request inside folder /src/test/resources/JSONFiles/.
     * @param queryKey          The key of the parameter to be included in the request body.
     * @param queryValue         The value of the parameter to be included in the request body.
     * @throws IOException    This exception is thrown when there is an I/O error while sending the POST request.
     * @throws ParseException This exception is thrown when there is an error while parsing the service request body.
     */
    @When("We send a POST request with the request body from file {string}, the query key {string}, and the query value {string}")
    public void sendPostRequestWithRequestBody(String requestBodyFileName, String queryKey, String queryValue) throws IOException, ParseException {
        commonAPIMethods.sendPostRequestWithRequestBody(requestBodyFileName, queryKey, queryValue);
    }

    /**
     * This methods is used to send a POST request with provided queryKey, queryValue, formKey and formValue.
     *
     * @param queryKey       The key used in the authentication request.
     * @param queryValue     The corresponding value for the key in the authentication request.
     * @param formKey   The form key used in the authentication request.
     * @param formValue The corresponding value for the form key in the authentication request.
     */
    @When("We send a POST request with the query key {string}, query value {string}, a form Key {string} and a form Value {string}")
    public void sendPostAuthRequest(String queryKey, String queryValue, String formKey, String formValue) {
        commonAPIMethods.sendPostAuthRequest(queryKey, queryValue, formKey, formValue);
    }

    /**
     * This method is used to send a GET request.
     */
    @When("We send a GET request")
    public void sendGetRequest() {
        commonAPIMethods.sendGetRequest();
    }

    /**
     * This method is used to send a POST request without a request body.
     */
    @When("We send a POST request without a request body")
    public void sendPOSTMethodwithoutrequest() {
        commonAPIMethods.sendPOSTMethodwithoutrequest();
    }

    /**
     * This method is used to send a PATCH request with the request body and the key.
     *
     * @param requestBodyFileName File name that contains request body for the PATCH request inside folder /src/test/resources/JSONFiles/.
     * @param key       The key for the PATCH request.
     * @throws IOException    An exception is throws when there is an I/O error while sending the PATCH request.
     * @throws ParseException An exception is throws when there is an error while parsing the request body file.
     */
    @When("We send a PATCH request with the request body from file {string} and the key {string}")
    public void preparePatchRequest(String requestBodyFileName, String key) throws IOException, ParseException {
        commonAPIMethods.preparePatchRequest(requestBodyFileName, key);
    }

    /**
     * This method is used to send a PATCH request with the request body.
     *
     * @param requestBodyFileName File name that contains request body for the PATCH request inside folder /src/test/resources/JSONFiles/.
     * @throws IOException    An exception is throws when there is an error in input/output operations.
     * @throws ParseException An exception is throws when there is an error while parsing.
     */
    @When("We send a PATCH request with the request body from file {string}")
    public void sendPatchRequestF3(String requestBodyFileName) throws IOException, ParseException {
        commonAPIMethods.sendPatchRequestF3(requestBodyFileName);
    }

    /**
     * This method is used send a GET request with the key and value.
     *
     * @param key   The key to be set for the request.
     * @param value The corresponding value to be set for the request.
     * @throws IOException An exception is thrown when there is an error in input/output operations.
     */
    @When("We send a GET request with the query key {string}, and the query value {string}")
    public void sendRequestToService(String key, String value) throws IOException {
        commonAPIMethods.sendRequestToService(key, value);
    }


    /**
     * This method is used to assert the response body contains the key value pair.
     *
     * @param parameters to validate responses (format response body key ^ expected value)
     * @throws IOException an exception occur during a batch update operation or attempting to access a file that does not exist at the specified location
     */
    @Then("We assert the response body contains the key value pair {string}")
    public void ValidateResp(String parameters) throws IOException {
        if (!parameters.equals("^")) {
            responseValidationMethods.verifyResponseCode(apiRequestRelatedMethods.getResponse().getBody().asString(), parameters);
            reportAndLogging.logBodyInTxtArea("Response body", apiRequestRelatedMethods.getResponse().asPrettyString());
        }
    }

    /**
     * This method is used to assert the response body contains the text.
     *
     * @param Parameters The parameter to be validated in the response.
     * @throws IOException An exception is thrown when there is an error in input/output operations.
     */
    @Then("We assert the response body contains the text {string}")
    public void ValidateResp1(String Parameters) throws IOException {
        commonAPIMethods.validateResponse(Parameters);
    }


    /**
     * This method is used to assert the actual response code matches with the expected response code.
     *
     * @param expectedResponseCode to validate response code
     * @throws IOException An exception is thrown when there is an error in input/output operations.
     */
    @Then("We assert the actual response code matches with the expected response code {string}")
    public void validateResponseStatus(String expectedResponseCode) throws IOException {
        commonAPIMethods.validateResponseStatus(expectedResponseCode);
    }

    /**
     * This method compare the actual response of an API request with an expected response, while ignoring the following keys.
     *
     * @param expected  The filename of the expected JSON file located in the resources directory.
     * @param actual    The filename of the actual JSON file.
     * @param datatable A DataTable containing the tags of the values to be ignored during comparison.
     * @throws IOException    An exception is thrown when there is an issue with accessing the JSON files.
     * @throws ParseException An exception is thrown when there is an issue with parsing the JSON data.
     */
    @Given("We compare the expected JSON file {string} with the actual JSON file {string}, ignoring the following keys")
    public void compareJsonFileswithIgnoredValue(String expected, String actual, DataTable datatable) throws IOException, ParseException {
        commonAPIMethods.compareJsonFileswithIgnoredValue(expected, actual, datatable);
    }

    /**
     * This method is used for compare two json file. It retrieves the expected JSON file from the resources directory and compares it with the actual JSON file.
     *
     * @param expected The filename of the expected JSON file located in the resources directory.
     * @param actual   The filename of the actual JSON file or the key to retrieve the actual JSON data from TestContext.
     * @throws IOException    An exception is thrown when there is an issue with accessing the JSON files.
     * @throws ParseException An exception is thrown when there is an issue with parsing the JSON data.
     */
    @Given("We compare the expected JSON file {string} with the actual JSON file {string}")
    public void compareJsonFiles(String expected, String actual) throws IOException, ParseException {
        commonAPIMethods.compareJsonFiles(expected, actual);
    }


    /**
     * This method is used configure query parameter with key and value.
     *
     * @param key   key to be set for sending request to service with query param
     * @param value its value
     */
    @When("We configure the query parameters with the key {string} and the value {string}")
    public void sendRequestToServiceWithQueryParam(String key, String value) {
        String logInfoKey = "QKey is : " + key + ":" + Environment.get(key);
        String logInfoValue = "QValue is : " + value + ":" + Environment.get(value);
        LOG.info(logInfoKey);
        LOG.info(logInfoValue);
        apiRequestRelatedMethods.addQueryParameters(commonAPIMethods.convertStringToMap(Environment.get(key), Environment.get(value), "~"));
    }

    /**
     * This method is used to configure the connection timeout for REST requests.
     *
     * @param time to set connection timeout in milliseconds
     */
    @Then("We configure the connection timeout of {int} milliseconds for REST requests")
    public void setConnectionTimeout(int time) {
        apiRequestRelatedMethods.setConfig(RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", time)
                        .setParam("http.connection.timeout", time)));
    }

}
