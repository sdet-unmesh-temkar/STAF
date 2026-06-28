package testRunner;

import generalutilities.ReportAndLogging;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;


@CucumberOptions(
        features = {"src/test/java/featureFiles"},
        tags = "@MT_Test_1 or @MT_Test_2",
        glue = {"stepDefinitions"},
        plugin = {"pretty", "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:", "json:target/cucumber.json", "formatter.XraySyncUtil","formatter.ExecutionReport"},
        monochrome = true
)

public class TestNGRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        System.out.println("************ Test Execution Started **************");
        return super.scenarios();
    }

    @BeforeTest
    public static void beforeclass() {
        if (System.getProperty("env") == null) {
            System.setProperty("env", "int-man-03");
        }
        System.out.println("multi thread testing with testNG");
        System.out.println("env:     from runner before class " + System.getProperty("env"));
    }

    @AfterTest
    public static void writeExtentReport(){
        System.out.println("env:     from runner after class " + System.getProperty("env"));
        ReportAndLogging reportAndLogging = new ReportAndLogging();
        reportAndLogging.copyReport();
    }
}