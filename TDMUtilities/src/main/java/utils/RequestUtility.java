package utils;

import io.restassured.response.Response;
import stepUtils.StepDefinitionBase;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static utils.DateTimeUtility.isWithinTimeout;

public class RequestUtility  extends StepDefinitionBase {

    public Response callInLoopWithTimeout(Supplier<Response> supplier, int expectedHttpStatusCode, int timeOutInSeconds) {

        long startTime = System.currentTimeMillis();
        Response response = null;

        loggingUtility.logSubstep("Polling for HTTP {} for up to {} seconds...", expectedHttpStatusCode, timeOutInSeconds);
        while (isWithinTimeout(startTime, timeOutInSeconds)) {
            response = supplier.get();
            int actualHttpStatusCode = response.getStatusCode();

            if (actualHttpStatusCode == expectedHttpStatusCode) {
                loggingUtility.logDebugDetails("Received expected HTTP status: {}", actualHttpStatusCode);
                break;
            }
            loggingUtility.logDebugDetails("Received HTTP {}. Retrying...", actualHttpStatusCode);
        }

        if ((System.currentTimeMillis() - startTime) >= TimeUnit.SECONDS.toMillis(timeOutInSeconds)) {
            softAssertUtility.softAssertTrue(false, String.format("Timeout of %s seconds reached without receiving expected HTTP %s", timeOutInSeconds, expectedHttpStatusCode));
        }

        assertionUtility.assertNotNull(response, "Received Status Response");
        return response;
    }
}
