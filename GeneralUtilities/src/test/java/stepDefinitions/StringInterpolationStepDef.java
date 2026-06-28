package stepDefinitions;

import generalutilities.ApplicationContext;
import generalutilities.TestContext;
import io.cucumber.java.en.*;

public class StringInterpolationStepDef {

    TestContext<Object> testContext = TestContext.getInstance();
    ApplicationContext applicationContext = ApplicationContext.getInstance();

    @Given("I set data in customerName {interpolatedString} using testContext")
    public void iSetDataInCustomerName(String name) {
        System.out.println("Setting CustomerName as "+name);
        testContext.setProperty("name", name);

    }

    @Then("I print customerName {interpolatedString}")
    public void iPrintCustomerName$TestContextCustomerName(String name) {
        System.out.println("CustomerName: "+name);
        System.out.println(testContext.getProperty("name"));

    }

    @Given("I set data in customerRole {string} using applicationContext")
    public void iSetDataInCustomerRoleUsingApplicationContext(String role) {
        System.out.println("Setting CustomerRole as "+role);
        applicationContext.setData("role",role);
    }

    @Then("I print customerRole {interpolatedString}")
    public void iPrintCustomerRole$CustomerRole(String role) {
        System.out.println("CustomerRole: "+role);
    }
}
