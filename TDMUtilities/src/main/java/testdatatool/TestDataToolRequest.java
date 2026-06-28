package testdatatool;

import utils.RestRequest;
import io.restassured.specification.RequestSpecification;
import utils.EnvironmentKeys;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Represents a request to the Test Data Tool API, supporting custom headers, cookies, and request body.
 * Extends RestRequest to leverage common REST functionality.
 * Provides a manual builder for flexible request construction.
 */
public class TestDataToolRequest extends RestRequest {
    /**
     * Cookies to be sent with the request.
     */
    protected Map<String, String> cookies;
    /**
     * XSRF token for request header.
     */
    protected String xsrfToken;

    /**
     * Default constructor for TestDataToolRequest.
     */
    public TestDataToolRequest() {
        super();
    }

    /**
     * Returns the base URI for the Test Data Tool API from environment configuration.
     *
     * @return Base URI as a String.
     */
    protected String getBaseUri() {
        return environment.get(EnvironmentKeys.D1.TEST_DATA_TOOL_HOST);
    }

    /**
     * Sets standard headers and parameters for the request, including XSRF token and cookies if present.
     *
     * @return The updated RequestSpecification.
     */
    protected RequestSpecification setStandardHeadersAndParams() {
        if (xsrfToken!=null) {
            requestSpecification
                    .header("X-XSRF-TOKEN", xsrfToken);
        }
        if (cookies!=null) {
            requestSpecification
                    .cookies(cookies);
        }
        return requestSpecification
                .header("Content-Type","application/json");
    }

    /**
     * Sets authentication for the request. No authentication is used in this implementation.
     *
     * @return The updated RequestSpecification.
     */
    protected RequestSpecification setAuthentication() {
        return requestSpecification.auth().none();
    }

    /**
     * Returns a list of adapter types for the request. Empty by default.
     *
     * @return List of adapter types.
     */
    protected List<Type> getAdapterTypes() {
        return List.of();
    }

    /**
     * Processes the response after the request is executed. No-op in this implementation.
     */
    protected void processResponse() {
        // No processing implemented.
    }

    /**
     * Returns a new Builder instance for constructing TestDataToolRequest objects.
     *
     * @return Builder for TestDataToolRequest.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Manual builder for TestDataToolRequest, allowing flexible configuration of endpoint, request body, cookies, and XSRF token.
     */
    public static class Builder {
        private String endpoint;
        private String objectId = "";
        private Object requestBodyObject;
        private RequestSpecification requestSpecification;
        private java.util.Map<String, String> cookies;
        private String xsrfToken;

        /**
         * Sets the endpoint for the request.
         * @param endpoint API endpoint.
         * @return Builder instance.
         */
        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Sets the object ID for the request.
         * @param objectId Object ID.
         * @return Builder instance.
         */
        public Builder withObjectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        /**
         * Sets the request body object.
         * @param requestBodyObject Request body.
         * @return Builder instance.
         */
        public Builder withRequestBodyObject(Object requestBodyObject) {
            this.requestBodyObject = requestBodyObject;
            return this;
        }

        /**
         * Sets the request specification.
         * @param requestSpecification RequestSpecification instance.
         * @return Builder instance.
         */
        public Builder withRequestSpecification(RequestSpecification requestSpecification) {
            this.requestSpecification = requestSpecification;
            return this;
        }

        /**
         * Sets cookies for the request.
         * @param cookies Map of cookies.
         * @return Builder instance.
         */
        public Builder withCookies(Map<String, String> cookies) {
            this.cookies = cookies;
            return this;
        }

        /**
         * Sets the XSRF token for the request.
         * @param xsrfToken XSRF token.
         * @return Builder instance.
         */
        public Builder withXsrfToken(String xsrfToken) {
            this.xsrfToken = xsrfToken;
            return this;
        }

        /**
         * Builds and returns a TestDataToolRequest instance with the configured parameters.
         * @return TestDataToolRequest instance.
         */
        public TestDataToolRequest build() {
            TestDataToolRequest req = new TestDataToolRequest();
            req.endpoint = this.endpoint;
            req.objectId = this.objectId;
            req.requestBodyObject = this.requestBodyObject;
            req.requestSpecification = this.requestSpecification;
            req.cookies = this.cookies;
            req.xsrfToken = this.xsrfToken;
            return req;
        }
    }
}