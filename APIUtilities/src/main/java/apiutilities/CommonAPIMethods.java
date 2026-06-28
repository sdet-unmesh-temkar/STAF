package apiutilities;

import constants.GeneralConstants;
import generalutilities.*;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * This class contains common API related method.
 * This class perform operation on json such as get/set RequestBodyFromJSONFile,convertStringToMap, convertHeaderToMap, connectToRestService etc.
 */
public class CommonAPIMethods {
    private String requestBodyFromJSONFile;
    private Properties prop = null;
    private final Map<String, String> requestBodyMap = new HashMap<>();
    private Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    APIRequestRelatedMethods apiRequestRelatedMethods = APIRequestRelatedMethods.getInstance();
    FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
    TestContext<Object> testContext = TestContext.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(CommonAPIMethods.class);
    private static final String JSON_FILES_PATH = File.separator + "JSONFiles" + File.separator;
    private static final String REQUEST_URL_PREFIX = "Request URL : ";
    private static final String REQUEST_BODY_LABEL = "Request Body";
    private static final String RESPONSE_BODY_LABEL = "Response Body";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String POSTAUTH = "POSTAUTH";
    public static final String JSON = ".json";
    public static final String RESPONSE_HEADER = "Response Header";
    public static final String REQUEST_IS = "Request is : ";
    public static final String ACTUAL_JSON = "ActualJson";
    public static final String REQUEST_URL = "Request URL : {}";
    public static final String PATCH = "PATCH";
    public static final String RESPONSE_BODY = "Response body";
    public static final String REST_SERVICE_URL = "\nRestService url: ";
    public static final String RESOURCE_VALUE_IS = "\nresourcevalue is :";


    /**
     * This method is used for get request body from the json file.
     *
     * @return String - requestBodyFromJSONFile
     */
    public String getRequestBodyFromJSONFile() {
        return requestBodyFromJSONFile;
    }

    /**
     * This method is used for set request body for the json file.
     *
     * @param  requestBodyFromJSONFile - to set request body for the json file
     */
    public void setRequestBodyFromJSONFile(String requestBodyFromJSONFile) {
        this.requestBodyFromJSONFile = requestBodyFromJSONFile;
    }

    /**
     * This method is used to get properties from the json file.
     *
     * @return prop - Properties
     */
    public Properties getProp() {
        return prop;
    }

    /**
     * This method is used to set properties from the json file.
     *
     * @param prop - to set properties from the json file
     */
    public void setProp(Properties prop) {
        this.prop = prop;
    }

    /**
     * This method is used to get environment.
     *
     * @return Map - environment
     */
    public Map<String, String> getEnviornment() {
        return this.environment;
    }

    /**
     * This method has implemented to get filename from the string passed and read the property file.
     *
     * @param serviceName  - its the file where details are stored in string format.
     */
    public void readServiceFile(String serviceName) {
        if (serviceName.contains("_")) {
            String[] arrValue = serviceName.split(Pattern.quote("_"));
            serviceName = arrValue[0];
        }
        prop = fileSpecificUtilities.readPropertyFile(serviceName + ".properties");
    }

    /**
     * This method takes key, value and parameters to be set in header and converts it to hashmap.
     *
     * @param key            - key to be set to convert string to map
     * @param value          - its value
     * @param splitDelimeter - separator for splitting values
     * @return               - requestBodyMap
     */
    public Map<String, String> convertStringToMap(String key, String value, String splitDelimeter) {
        requestBodyMap.clear();
        if (key.contains("~")) {
            String[] arrKey = key.split(Pattern.quote(splitDelimeter));
            String[] arrValue = value.split(Pattern.quote(splitDelimeter));
            if (arrKey != null && arrValue != null && arrKey.length == arrValue.length) {
                for (var i = 0; i < arrKey.length; i++) {
                    requestBodyMap.put(arrKey[i], arrValue[i]);
                }
            }
        } else {
            requestBodyMap.put(key, value);
        }
        return requestBodyMap;
    }

    /**
     * This method takes key, value and parameters to be set in header and converts it to hashmap
     *
     * @param key            - key to be set to convert string to map
     * @param value          - its value
     * @param parameters     - set in value of header at run time (value may have certain fields which has to be set at run time.
     * @param splitDelimeter - separator for splitting values
     * @return               - requestBodyMap
     */
    public Map<String, String> convertStringToMap(String key, String value, String parameters, String splitDelimeter) {
        requestBodyMap.clear();
        String[] parametersToSetInValue = parameters.split(splitDelimeter);
        if (value.contains("Key")) {
            String valueText = value;
            int firstindex = valueText.indexOf("Key");
            int lastIndex = valueText.lastIndexOf("Key");
            var count = 0;
            if (firstindex == lastIndex) {
                count = count + 1;
            } else {
                while (firstindex != -1) {
                    count++;
                    valueText = valueText.substring(firstindex + 1);
                    firstindex = valueText.indexOf("Key");
                }
            }
            if (count == parametersToSetInValue.length) {
                for (var i = 0; i < count; i++) {
                    if (parametersToSetInValue[i].equalsIgnoreCase("UniqueNumber")) {
                        parametersToSetInValue[i] = parametersToSetInValue[i].replace("UniqueNumber", testContext.getProperty("CFR_SEQ_NO").toString());
                    }
                    value = value.replace("Key" + (i + 1), parametersToSetInValue[i]);
                }
            }
        }
        if (key.contains("~")) {
            String[] arrKey = key.split(Pattern.quote(splitDelimeter));
            String[] arrValue = value.split(Pattern.quote(splitDelimeter));
            if (arrKey != null && arrValue != null && arrKey.length == arrValue.length) {
                for (var i = 0; i < arrKey.length; i++) {
                    requestBodyMap.put(arrKey[i], arrValue[i]);
                }
            }
        } else {
            requestBodyMap.put(key, value);
        }
        return requestBodyMap;
    }

    /**
     * This method takes key, value and parameters to be set in header and converts it to hashmap.
     *
     * @param headers - keys to be set in request
     * @return        - responseHeaderMap
     */
    public Map<String, String> convertHeaderToMap(Headers headers) {
        Map<String, String> responseHeaderMap = new HashMap<>();
        var headerToString = headers.toString();
        String[] headerArray = headerToString.split("\n");
        for (var i = 0; i < headerArray.length; i++) {
            String[] keyValue = headerArray[i].split("=", 2);
            responseHeaderMap.put(keyValue[0], keyValue[1]);
        }
        return responseHeaderMap;
    }

    /**
     * This method reads the content of json file and returns as json object.
     *
     * @param requestBodyFile - string that will contain file name
     * @return                - JSONObject
     * @throws IOException    - an exception occur during a batch update operation or an exception that is thrown when an I/O error occurs
     * @throws ParseException - this is a checked exception and it occur when you fail to parse a String that is ought to have a special format.
     */
    public Object setDataInJSON(String requestBodyFile) throws IOException, ParseException {
        var ioReader = new InputStreamReader(this.getClass().getResourceAsStream( JSON_FILES_PATH+ requestBodyFile.trim() + JSON ));
        var jsonParser = new JSONParser();
        return jsonParser.parse(ioReader);
    }

    /**
     * This method reads the content of json file and returns as json array.
     *
     * @param requestBodyFile - string that will contain file name
     * @return                - JSONArray
     * @throws IOException    - an exception occur during a batch update operation or an exception that is thrown when an I/O error occurs
     * @throws ParseException - this is a checked exception and it occur when you fail to parse a String that is ought to have a special format.
     */
    public JSONArray setDataInJSONArray(String requestBodyFile) throws IOException, ParseException {
        InputStream inputStream;
        inputStream = FileSpecificUtilities.class.getResourceAsStream(JSON_FILES_PATH + requestBodyFile.trim() + JSON );
        var jsonParser = new JSONParser();
        return (JSONArray) jsonParser.parse(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * This method assembles header parameters and converts into map format in order to set it in request.
     *
     * @param headerName  - header parameter keys to be set in request
     * @param headerValue - header parameter values to be set in request
     */
    public void setHeader(String headerName, String headerValue) {
        Map<String, String> headerMap;
        headerMap = convertStringToMap(environment.get(getValue(headerName)), environment.get(getValue(headerValue)), "~");
        reportAndLogging.logHeaderInTable("Request Header", headerMap);
        apiRequestRelatedMethods.setRequestHeaders(headerMap);
        reportAndLogging.logStepInJira("Header Name : " + environment.get(getValue(headerName)) + "\nHeader value : " + environment.get(getValue(headerValue)));
    }

    /**
     * This method used to get the value of the parameter from property file.
     *
     * @param stringToFetch - string whose value has to be fetched
     * @return              - resource
     */
    public String getValue(String stringToFetch) {
        String resource = null;
        stringToFetch = stringToFetch.trim();
        if (stringToFetch.contains("<") || stringToFetch.contains(">") || stringToFetch.contains("'")) {
            if (stringToFetch.contains("_PropFile")) {
                String[] arr = stringToFetch.split(Pattern.quote("_"));
                stringToFetch = arr[0];
            }
            stringToFetch = stringToFetch.replace("'", "");

        }
        if (prop != null) {
            stringToFetch = stringToFetch.replaceAll("[< |/>]", "");
            if (prop.containsKey(stringToFetch)) {
                resource = prop.getProperty(stringToFetch);
            } else {
                resource = stringToFetch;
            }
        } else {
            resource = stringToFetch;
        }
        return resource;
    }

    /**
     * This method prevents encoding of URL, headers or parameters sent in request.
     *
     * @param url - string to be decoded
     * @return    - decodeURL
     */
    public String decode(String url) {
        try {
            var prevURL = "";
            String decodeURL = url;
            while (!prevURL.equals(decodeURL)) {
                prevURL = decodeURL;
                decodeURL = URLDecoder.decode(decodeURL, "UTF-8");
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while decoding" + e.getMessage();
        }
    }

    /**
     * This method is used to connect to Rest Service.
     *
     * @param restServiceURL - url connect to Rest Service
     * @param resourceName   - resource name connect to Rest Service
     */
    public void connectToRestService(String restServiceURL, String resourceName) {
        String envment = System.getProperty("env");
        String baseurl = environment.get(getValue(restServiceURL));
        String resource = environment.get(getValue(resourceName));
        apiRequestRelatedMethods.formRequestUrl(baseurl, resource);
        apiRequestRelatedMethods.initializeRequestObject();
        reportAndLogging.logStepInJira("environment:" + envment + REST_SERVICE_URL + environment.get(getValue(restServiceURL)) + " " + RESOURCE_VALUE_IS + environment.get(getValue(resourceName)));

    }

    /**
     * This method is used to connect to rest service with customer id.
     *
     * @param restServiceURL - connect to rest service
     * @param resourceName   - name connect to rest service with customer id
     * @param customerID         - connect to rest service with customer id
     */
    public void connectToRestServiceWithCustId(String restServiceURL, String resourceName, String customerID) {
        String envment = System.getProperty("env");
        String baseurl = environment.get(getValue(restServiceURL));
        String resource = environment.get(getValue(resourceName)) + customerID;
        apiRequestRelatedMethods.formRequestUrl(baseurl, resource);
        apiRequestRelatedMethods.initializeRequestObject();
        reportAndLogging.logStepInJira("environment:" + envment + REST_SERVICE_URL + environment.get(getValue(restServiceURL)) + " " + RESOURCE_VALUE_IS + environment.get(getValue(resourceName)));
    }

    /**
     * This method is used to create curl command.
     *
     * @param request    - RequestSpecification to create curl command
     * @param httpMethod - to create curl command.
     */
    public void createCurlCommand(RequestSpecification request, String httpMethod) {
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) request;
        String qurl = httpRequest.getURI();
        String qrequest = httpMethod;
        String jsonRequestData = httpRequest.getBody();
        var headerRequest = httpRequest.getHeaders().toString();
        var formData = httpRequest.getFormParams().toString();
        String formDataS = formData;
        var formDataC = new StringBuilder();
        var newline = "' \\ \n";
        if (formData.isEmpty() || !formData.contains("{}")) {
            String[] formDataArr = formDataS.split(",");
            var formDataFinal = new String[formDataArr.length];
            for (var i = 0; i < formDataArr.length; i++) {
                if (formDataArr[i].contains("{")) {
                    formDataArr[i] = formDataArr[i].replace("{", "");
                }
                if (formDataArr[i].contains("}")) {
                    formDataArr[i] = formDataArr[i].replace("}", "");
                }
                formDataFinal[i] = formDataArr[i].trim();
                formDataC.append("--data-urlencode '" + formDataFinal[i] + newline);
            }
        }
        var formDataCs = formDataC.toString();
        var concatit = new StringBuilder();
        var cURL = "";
        var dataConcat = new StringBuilder();
        if (!headerRequest.contains("") || headerRequest.equals("")) {
            String[] arrKey = headerRequest.split("\n");
            for (var i = 0; i < arrKey.length; i++) {
                arrKey[i] = arrKey[i].replace("=", ":");
                concatit.append(concatit + "-H '" + arrKey[i] + newline);
            }
        }

        if (jsonRequestData != null) {
            dataConcat.append(dataConcat + "  -d '" + jsonRequestData + newline);
        }
        var concatits = concatit.toString();
        var dataConcats = dataConcat.toString();
        cURL = "curl -X " + qrequest + " " + concatits + formDataCs + dataConcats + "" + qurl;
        String html = "<textarea id='textsre' style='background-color:#cce6ff; overflow-y: auto; height: 100px;' onclick='this.focus();this.select(); '>" + cURL + "</textarea><script type='text/javascript'>function copyCode(){var textare=document.getElementById('textsre');console.log(textare.value());textare.select();document.execCommand('Copy');}</script>";
        reportAndLogging.addStepToReport("<br><b>Curl Command:</b>" + "\n" + html, "INFO");
    }

    /**
     * This method is used to retrieve response objects.
     *
     * @param responseKey - key to retrieve response objects
     * @param envKey      - to retrieve response objects
     */
    public void retriveResponseObjects(String responseKey, String envKey) {
        var js = new JsonPath(apiRequestRelatedMethods.getResponse().getBody().asString());
        var responseValue = js.getString(responseKey);
        testContext.setProperty(envKey, responseValue);
    }

    /**
     * This method is used to retrieve response object soap xml.
     *
     * @param responseKey - key to retrieve response object soap xml
     * @param envKey      - to retrieve response object soap xml
     */
    public void retriveResponseObjectsSOAPXML(String responseKey, String envKey) {
        var jsXpath = new XmlPath(apiRequestRelatedMethods.getResponse().asString());//Converting string into xml path to assert
        var responseValue = jsXpath.getString(responseKey);
        testContext.setProperty(envKey, responseValue);
    }

    /**
     * This method is used to retrieve response object json path utility.
     *
     * @param responseKey - to retrieve response object json path utility
     * @param envKey      - key to retrieve response object json path utility
     */
    public void retriveResponseObjectsJsonPathUtility(String responseKey, String envKey) {
        var jsonBody = apiRequestRelatedMethods.getResponse().getBody().asString();
        var jsonHelper = new JsonPathUtility(jsonBody);
        var responseValue = jsonHelper.getStringElement(responseKey);
        testContext.setProperty(envKey, responseValue);
    }


    /**
     * This method is used to retrieve response header objects.
     *
     * @param headerKey - header key to retrieve response header object
     * @param envKey    - to retrieve response header objects
     */
    public void retriveResponseHeaderObjects(String headerKey, String envKey) {
        String headerKeyValue = apiRequestRelatedMethods.getResponse().getHeader(headerKey);
        testContext.setProperty(envKey, headerKeyValue);
    }

    /**
     * This method used for common function to modify the json request file.
     *
     * @param key - to modify json request file
     */
    public void modifyJsonFile(String key) {
        String jsonKey = "{{" + key + "}}";
        String getKey = (String) testContext.getProperty(key);
        setRequestBodyFromJSONFile(getRequestBodyFromJSONFile().replace(jsonKey, getKey));
    }

    /**
     * This method is used to common function to modify the json request file name.
     *
     * @param key   - key to be set to modify the json request file name
     * @param value - its value
     */

    public void modifyJsonFile(String key, String value) {
        String jsonKey = "{{" + key + "}}";
        setRequestBodyFromJSONFile(getRequestBodyFromJSONFile().replace(jsonKey, value));
    }

    ////////*********************** Setup(Generate AccessToken section) ********************************************************
    /**
     * This method is used to API post request function to generate access token.
     *
     * @param serviceURL     - url for API post request function to generate access token
     * @param resourceName   - for API post request function to generate access token
     * @param headerName     - header parameter keys to be set in request
     * @param headerValue    - for API post request function to generate access token
     * @param parameterName  - name of the parameter
     * @param parameterValue - header parameter values to be set in request
     * @param formKey        - key to be set
     * @param formValue      - its value
     */
    public void generateAccessToken(String serviceURL, String resourceName, String headerName, String headerValue, String parameterName, String parameterValue, String formKey, String formValue) {
        connectToRestService(serviceURL, resourceName);
        setHeader(headerName, headerValue);
        String key1 = environment.get(parameterName);
        String value1 = environment.get(parameterValue);
        String key2 = environment.get(formKey);
        String value2 = environment.get(formValue);
        apiRequestRelatedMethods.addQueryParameters(convertStringToMap(getValue(key1), getValue(value1), "~"));
        apiRequestRelatedMethods.addFormParameters(convertStringToMap(getValue(key2), getValue(value2), "~"));
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX + httpRequest.getURI(),"INFO");
        apiRequestRelatedMethods.sendRequest(POSTAUTH);
        retriveResponseObjects(ACCESS_TOKEN, "D1_token");
    }

    /**
     * This method is used to API post request function to generate access token name.
     *
     * @param salesChannel - API post request function to generate access token name
     */
    public void generateAccessToken(String salesChannel) {
        String baseurl = getServiceRoute("keycloak");
        String endPoint = GeneralConstants.GENERATE_TOKEN_URL;
        apiRequestRelatedMethods.formRequestUrl(baseurl, endPoint);
        apiRequestRelatedMethods.initializeRequestObject();
        HashMap<String, String> requestHeader = new HashMap<>();
        HashMap<String, String> queryParam = new HashMap<>();
        HashMap<String, String> formParam = new HashMap<>();
        requestHeader.put(GeneralConstants.HEADER_PARAM_CONTENT_TYPE, GeneralConstants.HEADER_CONTENT_TYPE_VALUE_AUTHORIZATION);
        queryParam.put(GeneralConstants.QUERY_PARAM_SALES_CHANNEL, GeneralConstants.SalesChannel.getBySalesChannel(salesChannel).getSalesChannel());
        formParam.put(GeneralConstants.FORM_PARAM_CLIENT_ID, GeneralConstants.FORM_PARAM_CLIENT_ID_VALUE);
        formParam.put(GeneralConstants.FORM_PARAM_GRANT_TYPE, GeneralConstants.FORM_PARAM_GRANT_TYPE_VALUE);
        formParam.put(GeneralConstants.FORM_PARAM_USERNAME, GeneralConstants.SalesChannel.getBySalesChannel(salesChannel).getUser());
        formParam.put(GeneralConstants.FORM_PARAM_PASSWORD, GeneralConstants.SalesChannel.getBySalesChannel(salesChannel).getPassword());
        apiRequestRelatedMethods.setRequestHeaders(requestHeader);
        apiRequestRelatedMethods.addQueryParameters(queryParam);
        apiRequestRelatedMethods.addFormParameters(formParam);
        testContext.setProperty("salesChannel", GeneralConstants.SalesChannel.getBySalesChannel(salesChannel).getSalesChannel());
        apiRequestRelatedMethods.sendRequest(POSTAUTH);
        reportAndLogging.addStepToReport(RESPONSE_BODY_LABEL + apiRequestRelatedMethods.getResponse().asPrettyString());
        retriveResponseObjectsJsonPathUtility(ACCESS_TOKEN, "D1_token");
    }

    /**
     * This method is used to retrieve the service route.
     *
     * @param serviceName - The name of the service for which the route is to be retrieved.
     * @return            - The route of the specified service.
     */
    private String getServiceRoute(String serviceName) {
        return ((Map<String, String>) testContext.getProperty("routes")).get(serviceName);
    }

    /**
     * This method is used to sends a POST request with the provided service request body..
     *
     * @param serviceRequestBody - The request body for the POST request
     * @throws IOException       - This exception is thrown when there is an I/O error while sending the POST request.
     * @throws ParseException    - This exception is thrown when there is an error while parsing the service request body.
     */
    public void sendPostRequest(String serviceRequestBody) throws IOException, ParseException {
        CommonAPIMethods commonApiMethods = new CommonAPIMethods();
        Object requestBody = commonApiMethods.setDataInJSON(serviceRequestBody);
        LOG.info("requestBody : {} \n", requestBody);
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        reportAndLogging.logBodyInTxtArea(REQUEST_BODY_LABEL, requestBody.toString());
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.logBodyInTxtArea(REQUEST_BODY_LABEL, requestBody.toString());
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX + httpRequest.getURI(),"INFO");
        LOG.info("REQUEST_URL_PREFIX {}", httpRequest.getURI());
        commonApiMethods.createCurlCommand(apiRequestRelatedMethods.getRequest(), "POST");
        apiRequestRelatedMethods.sendRequest("POST");
        Map<String, String> responseHeader = commonApiMethods.convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        reportAndLogging.logStepInJira(REQUEST_IS  + requestBody.toString());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, apiRequestRelatedMethods.getResponse().asPrettyString());
    }

    /**
     * This method is used to send an API PUT request with the provided service request body.
     *
     * @param serviceRequestBody - The request body for the POST request
     * @throws IOException       - This exception is thrown when there is an I/O error while sending the PUT request.
     * @throws ParseException    - This exception is thrown when there is an error while parsing the service request body.
     */
    public void sendPutMethod(String serviceRequestBody) throws IOException, ParseException {
        CommonAPIMethods commonApiMethods = new CommonAPIMethods();
        Object requestBody = commonApiMethods.setDataInJSON(serviceRequestBody);
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, requestBody.toString());
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX + httpRequest.getURI(),"INFO");
        apiRequestRelatedMethods.sendRequest("PUT");
        Map<String, String> responseHeader = commonApiMethods.convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        LOG.info("Response received from API: {}", apiRequestRelatedMethods.getResponse());
        reportAndLogging.logStepInJira(REQUEST_IS  + requestBody.toString());
    }

    /**
     * This method is used to send a POST request with a request body, using a specific key and value.
     *
     * @param serviceRequestBody - The request body to be sent with the POST request
     * @param paramName          - The name of the parameter to be included in the request body
     * @param paramValue         - The value of the parameter to be included in the request body
     * @throws IOException       - This exception is thrown when there is an I/O error while sending the POST request.
     * @throws ParseException    - This exception is thrown when there is an error while parsing the service request body.
     */
    public void sendPostRequestWithRequestBody(String serviceRequestBody, String paramName, String paramValue) throws IOException, ParseException {
        String key = environment.get(paramName);
        String value = environment.get(paramValue);
        LOG.info("Parameter name: {}", key);
        LOG.info("parameter value: {}", value);
        Object requestBody = setDataInJSON(serviceRequestBody);
        LOG.info("requestBody.toString() : {} \n", requestBody);
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        apiRequestRelatedMethods.addQueryParameters(convertStringToMap(getValue(key), getValue(value), "~"));
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, requestBody.toString());
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, requestBody.toString());
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX + httpRequest.getURI(),"INFO");
        LOG.info(REQUEST_URL , httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "POST");
        apiRequestRelatedMethods.sendRequest("POST");
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        reportAndLogging.logStepInJira(REQUEST_IS  + requestBody.toString());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, apiRequestRelatedMethods.getResponse().asPrettyString());
    }

    /**
     * This method is used to send POST authentication request.
     *
     * @param key          - The key used in the authentication request.
     * @param value        - The corresponding value for the key in the authentication request.
     * @param formKey      - The key used in the form data of the authentication request.
     * @param formValue    - The corresponding value for the form key in the authentication request.
     */
    public void sendPostAuthRequest(String key, String value, String formKey, String formValue) {
        String key1 = environment.get(key);
        String value1 = environment.get(value);
        LOG.info("Parameter name: {}", key1);
        LOG.info("Parameter value: {}", value1);
        String key2 = environment.get(formKey);
        String value2 = environment.get(formValue);
        apiRequestRelatedMethods.addQueryParameters(convertStringToMap(getValue(key1), getValue(value1), "~"));
        apiRequestRelatedMethods.addFormParameters(convertStringToMap(getValue(key2), getValue(value2), "~"));
        Map<String, String> formData = convertStringToMap(getValue(key2), getValue(value2), "~");
        LOG.info("FormData : {}", formData);
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "POST");
        apiRequestRelatedMethods.sendRequest(POSTAUTH);
        JsonPath js = new JsonPath(apiRequestRelatedMethods.getResponse().getBody().asString());
        String accessToken = js.getString(ACCESS_TOKEN);
        try (FileWriter fileWriter = new FileWriter("src" + File.separator + "test" + File.separator + "resources" + File.separator + "TextFiles" + File.separator + "MoD" + File.separator + "access_token.txt")) {
            fileWriter.write(accessToken);
        } catch (IOException e) {
            LOG.error("IOException on sendPostAuthRequest method: {}  ", e.getMessage());
        }
    }

    /**
     * This method is used for send Request of the specified type.
     *
     * @param requestType  - The type of request to send
     * @throws IOException - an exception occur during a batch update operation or attempting to access a file that does not exist at the specified location
     */
    public void send_Request(String requestType) throws IOException {
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info("REQUEST_URL_PREFIX {} ", httpRequest.getURI());
        apiRequestRelatedMethods.sendRequest(requestType);
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        reportAndLogging.addStepToReport("Response Body: " + apiRequestRelatedMethods.getResponse().asPrettyString());
        LOG.info("Response Body: {}", apiRequestRelatedMethods.getResponse());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, apiRequestRelatedMethods.getResponse().asPrettyString());
        retriveResponseHeaderObjects("ETag", "ETag");
    }


    /**
     * This method is used to send GET method.
     */
    public void sendGetRequest() {
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info("Request URL : {} ", httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "GET");
        apiRequestRelatedMethods.sendRequest("GET");
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
    }

    /**
     * This method is used to send POST method without request body.
     */
    public void sendPOSTMethodwithoutrequest() {
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info("Request URL : {} ", httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "GET");
        apiRequestRelatedMethods.sendRequest("POST");
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
    }


    /**
     * This method is used to send a PATCH request using the provided request body file and path parameter.
     *
     * @param requestBodyFile   - The file containing the request body for the PATCH request.
     * @param pathParam         - The path parameter for the PATCH request.
     * @throws IOException      - An exception is throws when there is an I/O error while sending the PATCH request.
     * @throws ParseException   - An exception is throws when there is an error while parsing the request body file.
     */
    public void preparePatchRequest(String requestBodyFile, String pathParam) throws IOException, ParseException {
        Object requestBody = setDataInJSON(getValue(requestBodyFile));
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, requestBody.toString());
        LOG.info("Request Body: {}", requestBody);
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info(REQUEST_URL , httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), PATCH);
        apiRequestRelatedMethods.sendRequest(PATCH, getValue(pathParam));
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
    }

    /**
     * This method is used to send a PATCH request.
     *
     * @param requestBodyFile - The file that contains the request body data for the PATCH request.
     * @throws IOException    - An exception is throws when there is an error in input/output operations.
     * @throws ParseException - An exception is throws when there is an error while parsing.
     */
    public void sendPatchRequestF3(String requestBodyFile) throws IOException, ParseException {
        Object requestBody = setDataInJSON(getValue(requestBodyFile));
        apiRequestRelatedMethods.getRequest().body(requestBody.toString());
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, requestBody.toString());
        LOG.info("Request Body: {}", requestBody);
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info(REQUEST_URL, httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), PATCH);
        apiRequestRelatedMethods.sendRequest(PATCH);
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY_LABEL, apiRequestRelatedMethods.getResponse().asPrettyString());
    }

    /**
     * This method is used to send a request to a service with specified key-value parameters.
     *
     * @param key          - The key to be set for the request.
     * @param value        - The corresponding value to be set for the request.
     * @throws IOException - An exception is thrown when there is an error in input/output operations.
     */
    public void sendRequestToService(String key, String value) throws IOException {
        String key1 = environment.get(key);
        String value1 = environment.get(value);
        apiRequestRelatedMethods.addQueryParameters(convertStringToMap(getValue(key1), getValue(value1), "~"));
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info(REQUEST_URL , httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "GET");
        apiRequestRelatedMethods.sendRequest("GET");
        Map<String, String> responseHeader =convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
        reportAndLogging.logBodyInTxtArea(REQUEST_BODY_LABEL , apiRequestRelatedMethods.getResponse().asPrettyString());
    }

    /**
     * This method is used to send a request to a service with specified key-value parameters.
     *
     * @param key                     - The key to be set for the request.
     * @param value                   - The corresponding value to be set for the request.
     * @param parametersToSetinValue  - The parameters to be set in the value for the request.
     */
    public void sendRequestToService(String key, String value, String parametersToSetinValue) {
        apiRequestRelatedMethods.addQueryParameters(convertStringToMap(getValue(key), getValue(value), parametersToSetinValue, "~"));
        FilterableRequestSpecification httpRequest = (FilterableRequestSpecification) apiRequestRelatedMethods.getRequest();
        reportAndLogging.addStepToReport(REQUEST_URL_PREFIX  + httpRequest.getURI(),"INFO");
        LOG.info(REQUEST_URL , httpRequest.getURI());
        createCurlCommand(apiRequestRelatedMethods.getRequest(), "GET");
        apiRequestRelatedMethods.sendRequest("GET");
        Map<String, String> responseHeader = convertHeaderToMap(apiRequestRelatedMethods.getResponse().getHeaders());
        reportAndLogging.logHeaderInTable(RESPONSE_HEADER , responseHeader);
    }

    /**
     * This method is used to validate the response of a service request.
     *
     * @param parameters   - The parameter to be validated in the response.
     * @throws IOException - An exception is thrown when there is an error in input/output operations.
     */
    public void validateResponse(String parameters) throws IOException {
        String strResponse = apiRequestRelatedMethods.getResponse().getBody().asString();
        if (strResponse.contains(parameters)) {
            LOG.info("Response Validated: parameter is present in Response: {} " , parameters);
            reportAndLogging.addStepToReport("Response Validated: " + parameters + " is present in Response");
        } else {
            LOG.error("Fail: Response Validated: parameter is not present in Response: {} " , parameters);
            reportAndLogging.addStepToReport("Fail: Response Validated: " + parameters + " is not present in Response");
        }
        reportAndLogging.logBodyInTxtArea(RESPONSE_BODY, apiRequestRelatedMethods.getResponse().asPrettyString());
    }

    /**
     * This method is used to validate response Status with expected status code.
     *
     * @param expectedStatusCode to validate response Status
     * @throws IOException - An exception is thrown when there is an error in input/output operations.
     */
    public void validateResponseStatus(String expectedStatusCode) throws IOException {
        ResponseValidationMethods responseValidationMethods = new ResponseValidationMethods();
        ValidatableResponse json = responseValidationMethods.verifyStatusCode(apiRequestRelatedMethods.getResponse(), Integer.parseInt(getValue(expectedStatusCode)));
        LOG.info("ValidatableResponse json: {} ", json);
        int actualStatusCode = apiRequestRelatedMethods.getResponse().getStatusCode();
        int expectedStatus = Integer.parseInt(expectedStatusCode);
        if (actualStatusCode == expectedStatus) {
            LOG.info("Status Code Validated- Expected: {} Actual: {}", expectedStatus, actualStatusCode);
            reportAndLogging.addStepToReport("Status Code Validated- Expected: " + expectedStatus + " Actual: " + actualStatusCode);
            reportAndLogging.logStepInJira("Response is " + apiRequestRelatedMethods.getResponse().asPrettyString());
            reportAndLogging.logStepInJira("Response code is " + actualStatusCode);
            reportAndLogging.logBodyInTxtArea(RESPONSE_BODY, apiRequestRelatedMethods.getResponse().asPrettyString());
        } else {
            String responseAsString = apiRequestRelatedMethods.getResponse().asPrettyString();
            LOG.error("Validation failed, Response is {}", responseAsString);
            reportAndLogging.logAssertValues("assertFalse", Integer.toString(expectedStatus), Integer.toString(actualStatusCode), "", "");
            reportAndLogging.logStepInJira("Response is " + responseAsString);
            reportAndLogging.logBodyInTxtArea(RESPONSE_BODY, responseAsString);

        }
    }

    /**
     * This method compares two JSON files (actual and expected) while ignoring certain values.
     *
     * @param expected         - The filename of the expected JSON file located in the resources directory.
     * @param actual           - The filename of the actual JSON file.
     * @param datatable        - A DataTable containing the tags of the values to be ignored during comparison.
     * @throws IOException     - An exception is thrown when there is an issue with accessing the JSON files.
     * @throws ParseException  - An exception is thrown when there is an issue with parsing the JSON data.
     */
    public void compareJsonFileswithIgnoredValue(String expected, String actual, DataTable datatable) throws IOException, ParseException {
        JSONHandlingMethods jsonHandlingMethods = new JSONHandlingMethods();
        List<Map<String, String>> rows = datatable.asMaps(String.class, String.class);
        StringBuilder ignoredValueTemp = new StringBuilder();
        for (Map<String, String> columns : rows) {
            ignoredValueTemp.append(columns.get("Tag")).append("~");
        }
        String ignoredValue = ignoredValueTemp.toString();
        LOG.info("Expected: {}", this.getClass().getResource(JSON_FILES_PATH  + expected.trim() + JSON ));
        expected = this.getClass().getResource(JSON_FILES_PATH  + expected.trim() + JSON ).toString().split(":", 2)[1];
        if (Boolean.TRUE.equals(testContext.isPropertyPresent(ACTUAL_JSON ))) {
            actual = (String) testContext.getProperty(ACTUAL_JSON );
        } else
            actual = actual.trim() + JSON ;
        jsonHandlingMethods.compareJSONFiles(expected, actual, ignoredValue);
    }

    /**
     * This method is used for compare two json file. It retrieves the expected JSON file from the resources directory and compares it with the actual JSON file.
     *
     * @param expected         - The filename of the expected JSON file located in the resources directory.
     * @param actual           - The filename of the actual JSON file or the key to retrieve the actual JSON data from TestContest.
     * @throws IOException     - An exception is thrown when there is an issue with accessing the JSON files.
     * @throws ParseException  - An exception is thrown when there is an issue with parsing the JSON data.
     */
    public void compareJsonFiles(String expected, String actual) throws IOException, ParseException {
        JSONHandlingMethods jsonHandlingMethods = new JSONHandlingMethods();
        expected = this.getClass().getResource(JSON_FILES_PATH + expected.trim() + JSON).toString().split(":", 2)[1];
        if (Boolean.TRUE.equals(testContext.isPropertyPresent(ACTUAL_JSON))) {
            actual = (String) testContext.getProperty(ACTUAL_JSON);
        } else
            actual = actual.trim() + JSON;
        jsonHandlingMethods.compareJSONFiles(expected, actual, "");
    }

    /**
     * This method is used to upload a CSV file to a specified endpoint.
     *
     * @param key          - The header key to be set for the upload request.
     * @param value        - The corresponding value for the header key.
     * @throws IOException - An exception is thrown when there is an I/O error occurs during the file upload.
     */
    public void uploadCSVFile(String key, String value) throws IOException {
        File filePath = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "JSONFiles" + File.separator + "Joker" + File.separator + "customer_list.csv");
        LOG.info("File Path : {}", filePath.getAbsolutePath());
        String baseUrl = "https://api-test-env.art-int03.aws.solstice.vodafone.com";
        String endPoint = "/digitalmass/v1/uploadFile";
        apiRequestRelatedMethods.formRequestUrl(baseUrl, endPoint);
        apiRequestRelatedMethods.initializeRequestObject();
        reportAndLogging.logStepInJira(REST_SERVICE_URL + baseUrl + " " + RESOURCE_VALUE_IS + endPoint);
        setHeader(key, value);
        apiRequestRelatedMethods.getRequest().multiPart(filePath);
        requestBodyFromJSONFile = "[]";
        apiRequestRelatedMethods.getRequest().body(requestBodyFromJSONFile);
        reportAndLogging.logBodyInTxtArea(REQUEST_BODY_LABEL , requestBodyFromJSONFile);
        send_Request("POST");
    }
}