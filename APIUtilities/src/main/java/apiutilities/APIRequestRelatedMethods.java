package apiutilities;


import generalutilities.ReportAndLogging;
import generalutilities.ThreadLocalRegistry;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * This class contains the API Request related methods.
 * This class perform operation such as get/set request and response,send request,create curl command,convert header to map etc.
 */
public class APIRequestRelatedMethods {
    private static final ThreadLocal<APIRequestRelatedMethods> instance = ThreadLocal.withInitial(APIRequestRelatedMethods::new);

    private Response response;
    private RequestSpecification request;
    private RestAssuredConfig config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    private String baseURI;
    private String basePath;
    private static final Logger LOG = LoggerFactory.getLogger(APIRequestRelatedMethods.class);
    APIResponseTracking apiResData = APIResponseTracking.getInstance();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private Random random = new SecureRandom();

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private static final String PUT = "PUT";
    private static final String PATCH = "PATCH";
    private static final String POSTAUTH = "POSTAUTH";
    private static final String REQUESTBODY = "Request Body : ";
    private static final String REQUESTHEADER = "Request Header : ";
    private static final String RESPONSEBODY = "Response Body : ";
    private static final String RESPONSEHEADER = "Response Header : ";

    private long startTime;
    private long endTime;

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private APIRequestRelatedMethods() {
        ThreadLocalRegistry.register(instance);
    }

    /**
     * This method is used to get instance.
     *
     * @return - instance of APIRequestRelatedMethods
     */
    public static APIRequestRelatedMethods getInstance() {
        return instance.get();
    }


    /**
     * This method is used to remove threads.
     */
    public void unload() {
        instance.remove();
    }

    /**
     * This method is used to get the response.
     *
     * @return - response for the selected method
     */
    public Response getResponse() {
        return response;
    }


    /**
     * This method is used to set the response.
     *
     * @param response - set the response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * This method is used to get the request.
     *
     * @return - request
     */
    public RequestSpecification getRequest() {
        return request;
    }

    /**
     * This method is used to set the request.
     *
     * @param request - set the request.
     */
    public void setRequest(RequestSpecification request) {
        this.request = request;
    }

    /**
     * This method is used to get config.
     *
     * @return - config
     */
    public RestAssuredConfig getConfig() {
        return config;
    }

    /**
     * This method is used to set config.
     *
     * @param config - set the Configuration
     */
    public void setConfig(RestAssuredConfig config) {
        this.config = config;
    }

    /**
     * This method has implementation to form API URL combining the base URL and query endpoint.
     *
     * @param baseUrl       - the url where API resides
     * @param queryEndPoint - the resource on which API functions
     */

    public void formRequestUrl(String baseUrl, String queryEndPoint) {
        this.baseURI = baseUrl;
        this.basePath = queryEndPoint;
    }

    /**
     * This method initializes the request object which will be used for sending API request.
     */
    public void initializeRequestObject() {
        setRequest(RestAssured.given().baseUri(this.baseURI).basePath(this.basePath).relaxedHTTPSValidation());
    }

    /**
     * This method has implementation to set headers in the request object
     *
     * @param headers - headers to be set in the request are passed as hashmap in the form of key-value pairs.
     */
    public void setRequestHeaders(Map<String, String> headers) {
        getRequest().headers(headers);
    }

    /**
     * This method has implementation to set authentication in the request object based on the type of authentication selected.
     *
     * @param dt - datatable to set authentication in the request object
     */
    public void setAuthentication(DataTable dt) {
        List<String> authDetails = dt.asList(String.class);
        switch (authDetails.get(0)) {
            case ("Bearer"):
                getRequest().header(authDetails.get(1), authDetails.get(2));
                break;
            case ("Basic"):
                setRequest(getRequest().auth().basic(authDetails.get(1), authDetails.get(2)));
                break;
            case ("Digest"):
                setRequest(getRequest().auth().digest(authDetails.get(1), authDetails.get(2)));
                break;
            case ("Form"):
                setRequest(getRequest().auth().form(authDetails.get(1), authDetails.get(2)));
                break;
            case ("Oauth1"):
                setRequest(getRequest().auth().oauth(authDetails.get(1), authDetails.get(2), authDetails.get(3), authDetails.get(4)));
                break;
            case ("Oauth2"):
                setRequest(getRequest().auth().oauth2(authDetails.get(1)));
                break;
            default:
                LOG.info("Default block got executed ");
                break;
        }
    }

    /**
     * This method has implementation to add query parameters to the request object based on the parameters provided as hashmap.
     *
     * @param parameterValues - parameters to be set in the request are passed as hashmap in the form of key-value pairs. This hashmap will be set directly to the request object using Rest assured's in built method.
     * @return                - RequestSpecification
     */
    public RequestSpecification addQueryParameters(Map<String, String> parameterValues) {
        getRequest().queryParams(parameterValues);
        return getRequest();
    }

    /**
     * This method has implementation to add form parameters to the request object based on the parameters provided as hashmap.
     *
     * @param parameterValues - values of the parameter
     * @return                - RequestSpecification
     */
    public RequestSpecification addFormParameters(Map<String, String> parameterValues) {
        getRequest().formParams(parameterValues);
        return getRequest();
    }
    public RequestSpecification addFormObjectParameters(Map<String, Object> parameterValues) {
        getRequest().formParams(parameterValues);
        return getRequest();
    }
    public String handlePathParameters(Map<String, String> parameterValues, String path) {
        for (Map.Entry<String, String> entry : parameterValues.entrySet()) {
            path = path.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return path;
    }

    /**
     * This method has implementation to set path parameters and fire API request based on the method type.
     *
     * @param methodType - type of request to be fired like GET, POST, PUT etc.
     * @param pathParam  - its the path parameter to be set in the API
     */
    public void sendRequest(String methodType, String pathParam) {
        String[] pathParameter = pathParam.split(Pattern.quote("~"));
        getRequest().pathParam(pathParameter[0], pathParameter[1]);

        switch (methodType) {
            case (GET):
                startTime = System.currentTimeMillis();
                setResponse(getRequest().get());
                endTime = System.currentTimeMillis();
                break;
            case (POST):
                startTime = System.currentTimeMillis();
                setResponse(getRequest().post());
                endTime = System.currentTimeMillis();
                break;
            case (PUT):
                startTime = System.currentTimeMillis();
                setResponse(getRequest().put());
                endTime = System.currentTimeMillis();
                break;
            case (DELETE):
                startTime = System.currentTimeMillis();
                setResponse(getRequest().delete());
                endTime = System.currentTimeMillis();
                break;
            case (PATCH):
                startTime = System.currentTimeMillis();
                setResponse(getRequest().patch());
                endTime = System.currentTimeMillis();
                break;
            default:
                LOG.info("Default block got executed as none of the case match ");
                break;
        }
      long totalTime = endTime - startTime;
      setRequestVar(methodType, totalTime);
      try{
        addRequestInfoToLog(methodType);
        addResponseInfoToLog(methodType, totalTime);
        }catch(Exception e){
        LOG.error(e.getMessage());
      }
    }

    /**
     * This method has implementation to fire API request based on the method type.
     *
     * @param methodType - type of request to be fired like GET, POST, PUT etc
     * @return           - response based on selected the method type
     */
    public Response sendRequest(String methodType) {
        switch (methodType) {
            case (GET) -> {
                startTime = System.currentTimeMillis();
                setResponse(getRequest().given().config(getConfig()).get());
                endTime = System.currentTimeMillis();
            }
            case (POST), (POSTAUTH) -> {
                startTime = System.currentTimeMillis();
                setResponse(getRequest().given().config(getConfig()).post());
                endTime = System.currentTimeMillis();
            }
            case (PUT) -> {
                startTime = System.currentTimeMillis();
                setResponse(getRequest().given().config(getConfig()).put());
                endTime = System.currentTimeMillis();
            }
            case (PATCH) -> {
                startTime = System.currentTimeMillis();
                setResponse(getRequest().given().config(getConfig()).patch());
                endTime = System.currentTimeMillis();
            }
            case (DELETE) -> {
                startTime = System.currentTimeMillis();
                setResponse(getRequest().given().config(getConfig()).delete());
                endTime = System.currentTimeMillis();
            }
            default -> LOG.info("Default block is executed ");
        }
        long totalTime = endTime - startTime;
        setRequestVar(methodType, totalTime);
        try{
          addRequestInfoToLog(methodType);
          addResponseInfoToLog(methodType, totalTime);
        }catch(Exception e){
          LOG.error(e.getMessage());
        }
        return getResponse();
      }

    /**
     * This method will create the curl command and write it into HTML report.
     *
     * @param request    - to create the curl
     * @param httpMethod - methods to create the curl command and write it into HTML report
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
        if (formData.isEmpty() || !formData.contains("{}")) {
            String[] formDataArr = formDataS.split(",");
            var formDataFinal = new String[20];
            for (var i = 0; i < formDataArr.length; i++) {
                if (formDataArr[i].contains("{")) {
                    formDataArr[i] = formDataArr[i].replace("{", "");
                }
                if (formDataArr[i].contains("}")) {
                    formDataArr[i] = formDataArr[i].replace("}", "");
                }
                formDataFinal[i] = formDataArr[i].trim();
                formDataC.append("--data-urlencode '" + formDataFinal[i] + "' \\ \n");
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
                concatit.append(concatit + "-H '" + arrKey[i] + "' \\\n");
            }
        }
        if (jsonRequestData != null) {
            dataConcat.append(dataConcat + "  -d '" + jsonRequestData + "' \\\n");
        }
        var concatits = concatit.toString();
        var dataConcats = dataConcat.toString();
        cURL = "curl -X " + qrequest + " \\\n'" + qurl + "' \\\n\n" + " " + concatits + formDataCs + dataConcats + "";
        var tagId = random.nextInt(1000);
        String html1 = "<div hidden id='textsre" + tagId + "' style='background-color:#cce6ff; overflow-y: auto; height: 100px;' >" + cURL + "</div>";
        reportAndLogging.addStepToReport(html1);
        String html = "<button onclick=\"myFunction" + tagId + "()\">Copy curl Command</button>\n" +
                "\n" +
                "<script>\n" +
                "function myFunction" + tagId + "() {\n" +
                "var text = document.getElementById(\"textsre" + tagId + "\").innerHTML;\n" +
                "navigator.clipboard.writeText(text);\n" +
                "  alert(document.getElementById(\"textsre" + tagId + "\").innerHTML);\n" +
                "}\n" +
                "</script>";

        reportAndLogging.addStepToReport("<hr style=\"margin-bottom: 7px; margin-top: 9px;\"><div class=\"container\"><details><summary style=\"color:brown;\"><b>Curl Command:</b></summary><div class='row m-0 mt-1 border border-1 border-secondary rounded p-1 text-dark'><div class='col text-left font-weight-light p-0'> For Postman -> cURL Script for Postman (click on the below button & Click 'Ok') -> [Postman : import -> Paste Raw Text]</div></div><br>" + "\n" + "<div>" + html + "</div></details></div><hr style=\"margin-bottom: 7px; margin-top: 9px;\">");
    }

    /**
     * This method is used to convert header into map.
     *
     * @param headers - to convert header into map
     * @return        - headerMap
     */
    public Map<String, String> convertHeaderToMap(Headers headers) {
        Map<String, String> headerMap = new HashMap<>();
        var headerToString = headers.toString();
        String[] headerArray = headerToString.split("\n");
        for (var i = 0; i < headerArray.length; i++) {
            String[] keyValue = headerArray[i].split("=", 2);
            headerMap.put(keyValue[0], keyValue[1]);
        }
        return headerMap;
    }

    /**
     * This method will set the request's variables.
     *
     * @param methodType - type of request to be fired like GET, POST, PUT etc.
     * @param totalTime  - total execution time
     */
    public void setRequestVar(String methodType, long totalTime) {
        var time = Long.toString(totalTime);
        var statusCodeInt = response.getStatusCode();
        var statusCode = Integer.toString(statusCodeInt);
        apiResData.setData("APIRespURL", ((FilterableRequestSpecification) getRequest()).getURI());
        apiResData.setData("APIRespBasePath", basePath);
        apiResData.setData("APIRespMethType", methodType);
        apiResData.setData("APIRespStart", time);
        //to get Total count to calculate matrics
        apiResData.setData("APIRespEnd", time);
        apiResData.setData("APIRespTime", APIResponseTracking.convertMilliseconds(totalTime));
        apiResData.setData("APIRespStatusCode", statusCode);
        apiResData.createCsvDataSpecial();
    }

      /**
       * This Method will add Response's Information to Log.
       *
       * @param methodType - The type of request to be fired like GET, POST, PUT etc.
       * @param totalTime  - total execution time
       */
    public void addResponseInfoToLog(String methodType, long totalTime) {
        try {
          Map<String, String> responseHeader = convertHeaderToMap(getResponse().getHeaders());
          reportAndLogging.logHeaderInTable(RESPONSEHEADER, responseHeader);
          reportAndLogging.logStepInJira(RESPONSEHEADER + responseHeader);
          reportAndLogging.logBodyInTxtArea(RESPONSEBODY, getResponse().asPrettyString());
          reportAndLogging.logStepInJira(RESPONSEBODY + getResponse().asPrettyString());
          reportAndLogging.addStepToReport("</div></details></div><hr style=\"margin-bottom: 7px; margin-top: 9px;\">");
          reportAndLogging.addStepToReport("&nbsp; &nbsp; Actual Status Code:<b> " + getResponse().getStatusCode() + "</b>");
          reportAndLogging.logStepInJira("Actual Status Code: " + getResponse().getStatusCode());
          reportAndLogging.addStepToReport("&nbsp; &nbsp; Method Type:<b> " + methodType + "</b>");
          reportAndLogging.addStepToReport("&nbsp; &nbsp; Time Taken to Execute:<b> " + APIResponseTracking.convertMilliseconds(totalTime) + "</b>");
        }catch (Exception ioe)  {
          LOG.error(ioe.getMessage());
        }
    }

    /**
     * This Method will add Request's Information to Log.
     *
     * @param methodType    - The type of request to be fired like GET, POST, PUT etc.
     * @throws  IOException - an exception occur when attempting to access a file that does not exist at the specified location
     */
    public void addRequestInfoToLog(String methodType) throws IOException {
      var status= Integer.toString(getResponse().getStatusCode());
      var fileAndFlag = reportAndLogging.getTheFlag();
      var fileExistAndFlag = fileAndFlag.split(":");
      if(fileExistAndFlag[0].equals("true") && fileExistAndFlag[1].equals("true") && (status.equals("201") || status.equals("200"))){
        LOG.info("Details step result will not print in html report as Flag set to false");
      } else {
        addRequestHeaderInfoToLog(methodType);
        var prefixNoPayloadMsg = "No payload for this ";
        var suffixNoPayloadMsg = " request.";
        if (methodType.equals(POST) || methodType.equals(POSTAUTH) || methodType.equals(PUT) || methodType.equals(PATCH)) {
          try {
            reportAndLogging.logBodyInTxtArea(REQUESTBODY, ((FilterableRequestSpecification) getRequest()).getBody().toString());
            reportAndLogging.logStepInJira(REQUESTBODY + ((FilterableRequestSpecification) getRequest()).getBody().toString());
          } catch (Exception ignored) {
            reportAndLogging.logBodyInTxtArea(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg);
            reportAndLogging.logStepInJira(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg);
          }
        } else if (methodType.equals(GET)) {
          reportAndLogging.logBodyInTxtArea(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg);
          reportAndLogging.logStepInJira(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg);
        }
      }
    }

    /**
     * This Method will add Request's Header Information to Log.
     *
     * @param methodType - type of request to be fired like GET, POST, PUT etc.
     */
    public void addRequestHeaderInfoToLog(String methodType){
      reportAndLogging.addStepToReport("<div class=\"container\"><details><summary style=\"color:DimGray;\"><b> " + ((FilterableRequestSpecification) getRequest()).getURI() + " </b></summary>" + "\n" + "<div>");
      createCurlCommand(getRequest(), methodType);
      Map<String, String> requestHeader = convertHeaderToMap(((FilterableRequestSpecification) getRequest()).getHeaders());
      reportAndLogging.logHeaderInTable(REQUESTHEADER, requestHeader);
      reportAndLogging.logStepInJira(REQUESTHEADER + requestHeader);

    }
}
