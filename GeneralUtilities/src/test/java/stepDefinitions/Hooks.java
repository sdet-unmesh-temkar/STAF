package stepDefinitions;


import com.solstice.staf.config.EnvConfig;
import generalutilities.ReportAndLogging;
import generalutilities.ThreadLocalRegistry;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.Assert;


/**
 * This class contains hooks to perform different operation such as generate scenario lag, set variables and thread cleanup etc.
 */
public class Hooks extends EnvConfig {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);
    private final ReportAndLogging reportAndLogging = new ReportAndLogging();

    static {
        log.info("*** static block initiated ***");
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        log.info("*** System property for 'webdriver.http.factory' is set ***");
    }

    /**
     * This before hook is used to generate scenario log separately in different files.
     *
     * @param scenario - current scenario object
     */
    @Before(order = 0)
    public void generateScenarioLogsSeparately(Scenario scenario) {
        MDC.put("scenarioName", scenario.getName()); //Setting Mapped Diagnostic context to generate logs in different files
        log.info("*** Scenario name set in Mapped Diagnostic context to generate logs in different files ***");
    }

    /**
     * This before hook is used to set variables.
     *
     * @param scenario - current scenario object
     */
    @Before
    public void setVariables(Scenario scenario) {
        log.info("*** @Before hook to set variables initiated ***");
        ReportAndLogging.setScenario(scenario);
        log.info("*** Scenario set in ReportingAndLogging class ***");
    }

    /**
     * This hook is used to check scenario status and if status is ambiguous, then it handle AmbiguousStepDefinitionsException and add step to report.
     *
     * @param scenario - current scenario object
     */
    @After
    public void handleAmbiguousStepDefinitionsExceptionInReport(Scenario scenario) {
        log.info("*** @After hook to check if scenario status is ambiguous initiated ***");

        String status = scenario.getStatus().toString();
        log.info("*** Scenario status: {} ***", status);

        if (status.equals("AMBIGUOUS")) {
            reportAndLogging.addStepToReport("<div style=\"color:red;\"><b>Matches more than one step definition: AmbiguousStepDefinitionsException</b>","INFO");
            reportAndLogging.logStepInJira("Matches more than one step definition: AmbiguousStepDefinitionsException");
            Assert.assertEquals(status, "PASSED");
            log.info("*** Scenario status is 'AMBIGUOUS. Added steps to report and Jira");
        } else {
            log.info("*** Scenario status is not 'AMBIGUOUS ***");
        }
    }

    /**
     * This after hooks is used for thread cleanup.
     */
    @After(order = 0)
    public void threadCleanUp() {
        log.info("*** @After hook to clean up threads initiated ***");
        ThreadLocalRegistry.clearAll();
        log.info("*** Thread cleaned up! ***");
    }
}