package utils;

import apiutilities.APIRequestRelatedMethods;
import apiutilities.APIResponseTracking;
import generalutilities.ReportAndLogging;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import utils.reporting.LogLevel;
import utils.reporting.LoggingUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class LogRestRequest {
    protected final LoggingUtility loggingUtility;

    public LogRestRequest(LoggingUtility loggingUtility) {
        this.loggingUtility=loggingUtility;
    }
    private ReportAndLogging reportAndLogging = new ReportAndLogging();
    private APIRequestRelatedMethods apiRequestRelatedMethods = APIRequestRelatedMethods.getInstance();

    private static final String REQUESTURL = "Endpoint URL: ";
    private static final String REQUESTBODY = "Request Body: ";
    private static final String REQUESTHEADER = "Request Header: ";
    private static final String RESPONSEBODY = "Response Body: ";
    private static final String RESPONSEHEADER = "Response Header: ";

    private static final String REQUEST = "Request";
    private static final String REQUESTPREFIX = "<div class=\"container\"><details><summary style=\"color:RebeccaPurple;\"><b>";
    private static final String NONREQUESTPREFIX = "<div class=\"container\"><details><summary style=\"color:darkcyan;\"><b>";
    private static final String SUFFIX = "</div></details></div><hr style=\"margin-bottom: 7px; margin-top: 9px;\">";
    private static final String MIDFIX = "</b></summary>" + "\n" +"<div>";

    /**
     * This Method will add Request's Information to Log.
     *
     * @param request       - request object
     * @param methodType    - The type of request to be fired like GET, POST, PUT etc.
     * @param logLevel      - log level
     */
    public void addRequestInfoToLog(RequestSpecification request, String methodType, LogLevel logLevel) throws IOException {
            addRequestHeaderInfoToLog(request, methodType, logLevel);
            String prefixNoPayloadMsg = "No payload for this ";
            String suffixNoPayloadMsg = " request.";

            FilterableRequestSpecification filterableRequest = (FilterableRequestSpecification) request;
            Object bodyObject = filterableRequest.getBody();

            if (bodyObject!=null)
            {
                reportAndLogging.logBodyInTxtArea(REQUESTBODY, filterableRequest.getBody().toString());
                loggingUtility.logStepInJira(REQUESTBODY + filterableRequest.getBody().toString(), logLevel);
            }
            else
            {
                reportAndLogging.logBodyInTxtArea(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg);
                loggingUtility.logStepInJira(REQUESTBODY, prefixNoPayloadMsg + methodType + suffixNoPayloadMsg, logLevel);
            }
    }

    /**
     * This Method will add Response's Information to Log.
     *
     * @param response   - response object
     * @param methodType - The type of request to be fired like GET, POST, PUT etc.
     * @param totalTime  - total execution time
     * @param logLevel   - log level
     */
    public void addResponseInfoToLog(Response response, String methodType, long totalTime, LogLevel logLevel) throws IOException {
            Map<String, String> responseHeader = convertHeaderToMap(response.getHeaders());
            logHeaderInTable(RESPONSEHEADER, responseHeader, logLevel);
            loggingUtility.logStepInJira(RESPONSEHEADER + responseHeader, logLevel);
            reportAndLogging.logBodyInTxtArea(RESPONSEBODY, response.asPrettyString());
            loggingUtility.logStepInJira(RESPONSEBODY + response.asPrettyString(), logLevel);
            loggingUtility.addStepToReport(SUFFIX, logLevel);
            loggingUtility.addStepToReport("&nbsp; &nbsp; Actual Status Code:<b> " + response.getStatusCode() + "</b>", logLevel);
            loggingUtility.addStepToReport("&nbsp; &nbsp; Method Type:<b> " + methodType + "</b>", logLevel);
            loggingUtility.addStepToReport("&nbsp; &nbsp; Time Taken to Execute:<b> " + APIResponseTracking.convertMilliseconds(totalTime) + "</b>", logLevel);
    }

    /**
     * This Method will add Request's Header Information to Log.
     *
     * @param request       - request object
     * @param methodType - type of request to be fired like GET, POST, PUT etc.
     * @param logLevel   - log level
     */
    public void addRequestHeaderInfoToLog(RequestSpecification request, String methodType, LogLevel logLevel){
        String endpointURL = ((FilterableRequestSpecification) request).getURI();
        loggingUtility.addStepToReport("<div class=\"container\"><details><summary style=\"color:DimGray;\"><b> " + REQUESTURL + endpointURL + " </b></summary>" + "\n" + "<div>", logLevel);
        loggingUtility.logStepInJira(REQUESTURL + endpointURL, logLevel);
        apiRequestRelatedMethods.createCurlCommand(request, methodType);
        Map<String, String> requestHeader = convertHeaderToMap(((FilterableRequestSpecification) request).getHeaders());
        logHeaderInTable(REQUESTHEADER, requestHeader, logLevel);
        loggingUtility.logStepInJira(REQUESTHEADER + requestHeader, logLevel);

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
     * This method is used to log header name and it's value in table.
     *
     * @param headerName - string specifies type of header to log header name and it's value in table
     * @param headers    - to log header
     * @param logLevel   - log level
     */
    public void logHeaderInTable(String headerName, Map<String, String> headers, LogLevel logLevel) {
        var html = new StringBuilder("<table class=\"table-sm m-1 table-bordered\"><tr><th>Name</th><th>Value</th></tr>");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }

        html.append("</table>");
        if(headerName.contains(REQUEST)){
            loggingUtility.addStepToReport(REQUESTPREFIX + headerName + MIDFIX + html + SUFFIX, logLevel);
        }else{
            loggingUtility.addStepToReport(NONREQUESTPREFIX + headerName + MIDFIX + html + SUFFIX, logLevel);
        }
    }
}
