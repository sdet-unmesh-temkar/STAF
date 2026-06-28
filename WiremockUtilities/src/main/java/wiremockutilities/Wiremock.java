package wiremockutilities;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.atlassian.oai.validator.wiremock.OpenApiValidationListener;
import com.atlassian.ta.wiremockpactgenerator.WireMockPactGenerator;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class perform operation on Wiremock.
 * Wiremock is a mocking setup for integration testing.
 * It is mainly used during the development and more significantly during the integration testing while a system or service talks to one or multiple external or internal dependencies/services.
 */
public class Wiremock {
    WireMockServer wireMockServer;
    private static final Logger LOG = LoggerFactory.getLogger(Wiremock.class);
    OpenApiValidationListener validationListener;

    /**
     * This is parametrized constructor used to mocking setup for integration testing.
     *
     * @param folder - to define name of folder which has wiremock server options
     */
    public Wiremock(String folder) {
        String classPath = this.getClass().getResource("/WireMock/" + folder).toString().split(":", 2)[1];
        wireMockServer = new WireMockServer(options()
                .usingFilesUnderClasspath(classPath)
                .extensions(new ResponseTemplateTransformer(true)));
    }


    /**
     * This method is used to start wiremock server.
     */
    public void wiremockStart() {
        wireMockServer.start();
        LOG.info("Wiremock started------->");
    }

    /**
     * This method is used to stop wiremock server.
     */
    public void wiremockStop() {
        wireMockServer.stop();
        LOG.info("Wiremock stoped------->");
    }


    /**
     * This method is used to start validation listener.
     *
     * @param filePath - to define which validation listener will start
     */
    public void startValidationListener(String filePath) {
        String yamlFilePath = this.getClass().getResource("/YamlFiles/" + filePath).toString().replaceFirst("/", "").split(":", 2)[1];
        LOG.info("File path yaml: {}", yamlFilePath);
        validationListener = new OpenApiValidationListener(yamlFilePath);
        wireMockServer.addMockServiceRequestListener(validationListener);
        LOG.info("ValidationListener started------->");
    }


    /**
     * This method is used to reset the current validation listener.
     */
    public void validationListenerReset() {
        validationListener.reset();
        LOG.info("validationListener reset------->");
    }


    /**
     * This method is used to build and start wiremock pact generator.
     *
     * @param consumer - to define consumer of pact generator
     * @param provider - to define provider of pact generator
     */
    public void startPactGenerator(String consumer, String provider) {
        wireMockServer.addMockServiceRequestListener(WireMockPactGenerator.builder(consumer, provider).build());
        LOG.info("startPactGenerator started------->");
    }


    /**
     * This method is used to validate swagger.
     */
    public void validateSwagger() {
        validationListener.assertValidationPassed();
    }
}
