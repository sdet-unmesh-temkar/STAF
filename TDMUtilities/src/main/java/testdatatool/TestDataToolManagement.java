package testdatatool;

import io.restassured.response.Response;
import testDataTool.data.CustomerRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Manages interactions with the Test Data Tool API for customer data retrieval.
 * Provides methods to build request bodies and execute API calls using search queries.
 */
public class TestDataToolManagement {
    /**
     * Endpoint for Test Data Tool customer API.
     */
    public static final String TEST_DATA_TOOL_ENDPOINT = "/api/customer/e2e";

    /**
     * Retrieves customer data from the Test Data Tool API using the provided search query.
     *
     * @param query The search query containing customer and order criteria.
     * @return Response from the Test Data Tool API.
     */
    public Response getTestDataToolCustomer(TestDataToolSearchQuery query) throws IOException {
        Response tokenResponse =  TestDataToolRequest.builder()
                .withEndpoint(TEST_DATA_TOOL_ENDPOINT)
                .withRequestBodyObject(buildGetCustomerRequestBody(query))
                .build()
                .sendPostRequest();

        Map<String, String> cookies = tokenResponse.getCookies();
        String xsrfToken = tokenResponse.getCookie("XSRF-TOKEN");

        return TestDataToolRequest.builder()
                .withEndpoint(TEST_DATA_TOOL_ENDPOINT)
                .withRequestBodyObject(buildGetCustomerRequestBody(query))
                .withCookies(cookies)
                .withXsrfToken(xsrfToken)
                .build()
                .sendPostRequest();
    }

    /**
     * Builds the request body for the Test Data Tool customer API call based on the search query.
     *
     * @param query The search query containing customer and order criteria.
     * @return CustomerRequest object representing the request body.
     */
    public CustomerRequest buildGetCustomerRequestBody(TestDataToolSearchQuery query) {
        return CustomerRequest.builder()
                .customer(CustomerRequest.Customer.builder()
                        .status(query.getTestToolData().getCustomerStatus())
                        .hasValidEmail(query.getTestToolData().getCustomerHasValidEmail())
                        .hasMigrationProcess(query.getTestToolData().getCustomerHasMigrationProcess())
                        .hasMyVodafoneAccount(query.getTestToolData().getCustomerHasMyVodafoneAccount())
                        .build())
                .products(new CustomerRequest.Product[]{
                        CustomerRequest.Product.builder()
                                .name(query.getTestToolData().getOrderItemName())
                                .build()
                })
                .orders(new CustomerRequest.Order[]{
                        CustomerRequest.Order.builder()
                                .hasOsfId(query.getTestToolData().getOrderHasOsfId())
                                .hasDelphiId(query.getTestToolData().getOrderHasDelphiId())
                                .orderItem(CustomerRequest.OrderItem.builder()
                                                .name(query.getTestToolData().getOrderItemName())
                                                .hasMacAddress(query.getTestToolData().getOrderItemHasMacAddress())
                                                .build())
                                .build()
                })
                .pageSize(1)
                .sortOrder("RANDOM")
                .build();
    }
}