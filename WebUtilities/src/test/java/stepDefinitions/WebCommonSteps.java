package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import webutilities.CommonDriverMethods;
import webutilities.WebCommonMethods;

import java.util.List;


/**
 * This class contains methods related to Web browser
 * This class contains methods to perform different operations on web browser such as launch and close the browser.
 */
public class WebCommonSteps {

	CommonDriverMethods commonDriverMethods = new CommonDriverMethods();
	WebCommonMethods webCommonMethods= new WebCommonMethods();
	private static final Logger LOG = LoggerFactory.getLogger(WebCommonSteps.class);

	/**
	 * This method is used to initialize the Driver and launch the chrome browser
	 *
	 */
	@Given("We launch the Browser Chrome")
	public void initializeDriver(){
		commonDriverMethods.initializeDriver("Chrome");
		LOG.info("Chrome browser is Initialized");
	}

	/**
	 * This method is used to initialize the Driver and launch the specified browser
	 *
	 * @param browserType  specify the browser that has to be launch. Browser options : Chrome and IE
	 *
	 */
	@Given("We launch the Browser {string}")
	public void initializeDriver(String browserType){
		commonDriverMethods.initializeDriver(browserType);
		LOG.info("Browser is Initialized :{}", browserType);
	}

	/**
	 * This method is used to close the Browser
	 */
	@Then("We close the Browser")
	public void closeBrowser() {
		commonDriverMethods.closeBrowser();
		LOG.info("Browser is Closed");
	}


	/**
	 * This method is to validate the cell value in a column for a row value
	 *
	 * @param columnValue - header value of the table
	 * @param value       - the cell valuw which has to be asserted
	 * @param locator     - table locator
	 * @param dataTable   - the conditions for the rows which has to be validated
	 */
	@Given("We assert the column {string} has the value {string} in the row with the following values for the table locator {string}")
	public void tableValidations(String columnValue, String value, String locator, DataTable dataTable) {
		WebDriver driver = CommonDriverMethods.getDriver();
		List<List<String>> dt = dataTable.asLists();
		String cellvalue;
		for (List<String> data : dt) {
			for (int i = 0; i < dt.size(); i++) {
				data.set(i, data.get(i) == null ? "" : data.get(i));
			}
		}
		List<List<String>> tableValues = webCommonMethods.getTableRowValues(driver, dt, locator);
		cellvalue = webCommonMethods.getCellValue(columnValue, tableValues);

		Assert.assertEquals(cellvalue, value);
	}


	/**
	 * Assert the page title
	 *
	 * @param expectedPageTitle - the page title that has to be asserted
	 */
	@Given("We assert the page title {string}")
	public void assertPageTitle(String expectedPageTitle) {
		WebDriver driver = CommonDriverMethods.getDriver();
		boolean actualTitle = webCommonMethods.assertPageTitle(driver, expectedPageTitle);

		Assert.assertTrue(actualTitle);
	}
}



