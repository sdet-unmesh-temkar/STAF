package stepDefinitions;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webutilities.CommonDriverMethods;
import webutilities.ScreenShot;


/**
 * This class perform operations on WebHooks.
 */
public class WebHooks {

    private static final Logger log = LoggerFactory.getLogger(WebHooks.class);
    private final ScreenShot screenShot = new ScreenShot();

    /**
     * This before hook will set variables
     *
     * @param scenario: current scenario object
     */
    @Before
    public void setVariables(Scenario scenario) {
        log.info("*** @Before hook to set variables initiated ***");
        screenShot.setScenario(scenario);
        log.info("*** Scenario set in ScreenShot class  ***");
    }


    /**
     * This method is used to terminate web driver instance after scenario is executed
     *
     * @param scenario: current scenario object
     */
    @After
    public void terminateWebDriver(Scenario scenario) {
        log.info("*** @After hook to kill driver instance initiated ***");
        WebDriver driver = CommonDriverMethods.getDriver();

        /* When scenario is failed and driver is not null */
        if (scenario.isFailed() && driver != null) {
            log.info("*** Scenario is failed and driver instance is not null  ***");
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            screenShot.takeSnapShot(driver, screenshotName);
            driver.quit();
            log.info("*** Killed driver instance  ***");
        }
        /* When scenario is not failed and driver is not null */
        if (!scenario.isFailed() && driver != null) {
            log.info("*** Scenario is not failed and driver instance is not null  ***");
            driver.quit();
            log.info("*** Killed driver instance  ***");
        }
        /* When driver is null */
        else if (driver == null) {
            log.info("*** Driver instance is null  ***");
        }
    }
}
