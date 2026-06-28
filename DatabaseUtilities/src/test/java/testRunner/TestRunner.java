package testRunner;

import generalutilities.ReportAndLogging;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features={"src/test/java/featureFiles"},
        tags="@Test",
        glue = {"stepDefinitions"},
        plugin = {"pretty","com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:","json:target/cucumber.json","html:target/reportFiles/summary.html", "formatter.XraySyncUtil","formatter.ExecutionReport"},
        monochrome = true,
        stepNotifications=true)

public class TestRunner {

    @BeforeClass
    public static void beforeclass(){
        if(System.getProperty("env")==null)
            System.setProperty("env","DEV");
        System.out.println("env:     from runner before class " + System.getProperty("env"));

    }

    @AfterClass
    public static void writeExtentReport() {
        System.out.println("env:     from runner after class " + System.getProperty("env"));
        ReportAndLogging reportAndLogging = new ReportAndLogging();
        reportAndLogging.copyReport();
    }

}
