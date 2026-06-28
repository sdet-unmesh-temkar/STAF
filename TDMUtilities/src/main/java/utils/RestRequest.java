package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import digitalone.utils.TestContextD1DataUtility;
import generalutilities.EnvironmentDataLoader;
import generalutilities.TestContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import utils.reporting.LogLevel;
import utils.reporting.LoggingUtility;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public abstract class RestRequest {
    protected static final Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    protected final TestContext<Object> testContext = TestContext.getInstance();
    protected final LoggingUtility loggingUtility = new LoggingUtility(log);
    protected final LogRestRequest logRestRequest = new LogRestRequest(loggingUtility);
    protected final TestContextD1DataUtility testContextD1DataUtility = new TestContextD1DataUtility(loggingUtility);
    private static final String REQUEST_BODY = "Request Body";

    protected String endpoint;
    @Builder.Default
    protected String objectId = "";
    protected Object requestBodyObject;
    protected RequestSpecification requestSpecification;
    protected Response response;
    @Builder.Default
    protected LogLevel logLevelRequestSubstep = LogLevel.INFO;

    protected long startTime;
    protected long endTime;

    protected abstract String getBaseUri();

    protected abstract RequestSpecification setStandardHeadersAndParams();

    protected abstract RequestSpecification setAuthentication();

    protected abstract List<Type> getAdapterTypes();

    protected abstract void processResponse();

    public String getUri() {
        return getBaseUri() + endpoint;
    }

    public String getEndpoint() {
        if (objectId != null && !objectId.isEmpty()) {
            return endpoint.replace("{{OBJECT_ID}}", objectId);
        }
        return endpoint;
    }

    public Response sendPostRequest() throws IOException {
        return sendHttpRequest(HttpMethod.POST);
    }

    public Response sendPostAuthRequest() throws IOException {
        return sendHttpRequest(HttpMethod.POSTAUTH);
    }

    public Response sendGetRequest() throws IOException {
        return sendHttpRequest(HttpMethod.GET);
    }

    public Response sendPutRequest() throws IOException {
        return sendHttpRequest(HttpMethod.PUT);
    }

    public Response sendDeleteRequest() throws IOException {
        return sendHttpRequest(HttpMethod.DELETE);
    }

    public Response sendPatchRequest() throws IOException {
        return sendHttpRequest(HttpMethod.PATCH);
    }

    private void buildRequest() {
        requestSpecification = RestAssured.given()
                .baseUri(getBaseUri())
                .basePath(getEndpoint())
                .relaxedHTTPSValidation();

        setStandardHeadersAndParams();
        setAuthentication();

        if (requestBodyObject != null) {
            setRequestBodyFromObject(requestBodyObject);
        }

    }

    private void setRequestBodyFromObject(Object serializableObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        getAdapterTypes().forEach(
                type -> gsonBuilder.registerTypeAdapter(type, new EnumToStringAdapter<>((Class) type)));
        Gson gson = gsonBuilder.create();
        String requestBody = gson.toJson(serializableObject);

        requestSpecification.body(requestBody);
    }

    private Response sendHttpRequest(HttpMethod methodType) throws IOException {
        buildRequest();

        //log HTTP Request
        if (logLevelRequestSubstep.isActiveLevel())
        {
            loggingUtility.logSubstep("Sending HTTP {} request: {}", methodType.name(), getEndpoint());
        }

        //Send Request
        sendRequest(methodType);

        //save StatusCode and Timestamp
        String httpResponseStatusCode = String.valueOf(response.getStatusCode());
        testContext.setProperty("StatusCode", httpResponseStatusCode);
        String timestamp = String.valueOf(response.getHeaders().get("date"));
        testContext.setProperty("Timestamp", timestamp.substring(timestamp.indexOf("=") + 1).replaceAll("\\s", ""));

        processResponse();

        return response;
    }

    public Response sendRequest(HttpMethod methodType) throws IOException {
        startTime = System.currentTimeMillis();
        response = switch (methodType) {
            case GET -> requestSpecification.get();
            case POST, POSTAUTH -> requestSpecification.post();
            case PUT -> requestSpecification.put();
            case DELETE -> requestSpecification.delete();
            case PATCH -> requestSpecification.patch();
        };
        endTime = System.currentTimeMillis();

        long totalTime = endTime - startTime;

        logRequestResponseInfo(methodType, totalTime);

        return response;
    }

    protected LogLevel getLogLevelByResponseStatus(Response response)
    {
        return response.statusCode() <= 299 || logLevelRequestSubstep == LogLevel.DEBUG? LogLevel.DEBUG:LogLevel.WARN;
    }

    private void logRequestResponseInfo(HttpMethod methodType, long totalTime) throws IOException {
        LogLevel logLevel = getLogLevelByResponseStatus(response);

        logRestRequest.addRequestInfoToLog(requestSpecification, methodType.toString(), logLevel);
        logRestRequest.addResponseInfoToLog(response, methodType.toString(), totalTime, logLevel);
    }
}
