package stepDefinitions;

import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiremockutilities.Wiremock;

/**
 * This hooks is used to stop wiremock and reset the validation listener.
 */
public class wiremockHooks {

    Wiremock wiremock;
    private static final Logger LOG = LoggerFactory.getLogger(wiremockHooks.class);

    /**
     * This hooks is used to stop wiremock and reset listener.
     */
    @After("@wiremock")
    public void after() {
        if (System.getProperty("env").equals("local")) {
            System.out.println("-----> Stop WireMock, reset Listener");
            try {
                wiremock.wiremockStop();
                wiremock.validationListenerReset();
            } catch (NullPointerException e) {
                LOG.error("NullPointerException on wiremockHooks after method: {}", e.getMessage());
            }
        }

    }
}