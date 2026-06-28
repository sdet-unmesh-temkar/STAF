package webutilities;


import generalutilities.ReportAndLogging;
import generalutilities.StringInterpolation;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * This class perform operations on WebCommonMethods.
 * This class contains methods to perform different operations on web elements such as click,Window Switching, Handle Alert
 */
public class WebCommonMethods {

    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private static final int WAITFREQUENCY = 3;
    private static final int WAITTIME = 20;
    private static final Logger LOG = LoggerFactory.getLogger(WebCommonMethods.class);
    StringInterpolation stringInterpolation= new StringInterpolation();


    /**
     * This method is used to click on specific WebElement.
     *
     * @param driver          - instance of browser to click on specific web element.
     * @param webElementPath  - locators to identify the web element
     */
    public void clickWebElement(WebDriver driver, String webElementPath)  {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(WAITTIME))
                .pollingEvery(Duration.ofSeconds(WAITFREQUENCY));
        try {
            highLighterMethod(driver, driver.findElement(By.xpath(webElementPath)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElementPath))).click();
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("Unable to find Webelement :{}", e);
        }


    }


    /**
     * This method is  used to click on javascript executor elements where normal click method does not work.
     *
     * @param driver          - instance of browser to click on javascript executor element
     * @param webElementPath  - locators to identify the web element
     */
    public void clickWebElementJSE(WebDriver driver, String webElementPath) {
        WebElement element = driver.findElement(By.xpath(webElementPath));
        driver.manage().timeouts().scriptTimeout(Duration.ofMillis(5000));
        try {
            highLighterMethod(driver, element);
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * This method is used to send keys to the web element
     *
     * @param driver          - instance of browser to pass values to the web element
     * @param webElementPath  - locators to identify the web element
     * @param value           - required value which need to be entered.
     */
    public void fillValueInWebElement(WebDriver driver, String webElementPath, String value) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(WAITTIME))
                .pollingEvery(Duration.ofSeconds(WAITFREQUENCY));
        try {
            highLighterMethod(driver, driver.findElement(By.xpath(webElementPath)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElementPath))).clear();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElementPath))).sendKeys(value);
        }  catch (NoSuchElementException e) {
            throw new NoSuchElementException("Unable to find Webelement :{}", e);
        }
        catch (Exception e){
            throw e;
        }
    }


    /**
     * This method is also used to send keys to the web element.This method is used when fillValueInWebElement method doesn’t work then javascript executor method is used to send keys
     *
     * @param driver          - instance of browser to pass values on the web element
     * @param webElementPath  - locators to identify the web element
     * @param val             - provide the path of the element to pass value in WebElement
     */
    public void fillValueInWebElementJSE(WebDriver driver, String webElementPath, String val) {

        int count = WAITFREQUENCY;
        WebElement element = driver.findElement(By.xpath(webElementPath));
        while (count <= WAITTIME) {
            try {
                highLighterMethod(driver, element);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
                JavascriptExecutor myExecutor = ((JavascriptExecutor) driver);
                myExecutor.executeScript("arguments[0].value='" + val + "';", element);
                break;
            } catch (Exception e) {
                count += WAITFREQUENCY;
                if (count == WAITTIME)
                    throw e;
            }
        }

    }

    /**
     * This method is used to check whether the required element is present or not by using its XPath as a property to check its presence
     *
     * @param driver  - instance of browser to check presence of element by using its XPath
     * @param xpath   - of the element whose presence to be checked
     * @return        - true/false
     */
    public boolean isWebElementPresent(WebDriver driver, String xpath) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(WAITTIME))
                .pollingEvery(Duration.ofSeconds(WAITFREQUENCY));
        try {
            highLighterMethod(driver, driver.findElement(By.xpath(xpath)));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))).isDisplayed();
        } catch (Exception e) {
            LOG.error("Element not found :{}", e.getMessage());
        }

        return false;
    }

    /**
     * This method is used to switch between the two windows i.e the current window and the child window.
     *
     * @param driver    - instance of browser to switch between the current window and child window
     * @param clickBtn  - locators to identify the web element
     */
    public void switchtoWindow(WebDriver driver, String clickBtn) {

        String currentwindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String childwindow : allWindows) {
            if (!childwindow.equalsIgnoreCase(currentwindow)) {
                driver.switchTo().window(childwindow);
                clickWebElementJSE(driver, clickBtn);
            }

        }
        driver.switchTo().window(currentwindow);
    }

    /**
     * This method is used to handle alert pop-up.
     *
     * @param driver   - instance of browser to handle alert pop-up
     * @param message  - text displayed on pop-up window
     */
    public void handleAlerts(WebDriver driver, String message) {
        try {
            var alert = driver.switchTo().alert();
            String alertMessage = alert.getText();
            if (message.equals("")) {
                alert.dismiss();
            } else if (alertMessage.contains(message)) {
                alert.accept();
            } else {
                reportAndLogging.addStepToReport("Alert message is:" + message,"INFO");
                reportAndLogging.logStepInJira("Alert message is:" + message);
                LOG.info(alertMessage);
            }
        } catch (Exception ex) {
            reportAndLogging.addStepToReport("Alert is not displayed on the page","WARN");
            reportAndLogging.logStepInJira("Alert is not displaying on page");
            LOG.info("Alert is not displaying  on the page");
        }
    }

    /**
     * This method is used to high light the webelement. This can we enabled by setting system property to "highlight". This should be used only for demostration purpose as it will reduce the performance
     *
     * @param driver  - takes driver as an input
     * @param element - the webelement that has to be highlighted
     */
    public void highLighterMethod(WebDriver driver, WebElement element) {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            if ("enabled".equals(System.getProperty("highlight"))) {
                // Number of times to change the border
                int times = 2;
                // Delay in milliseconds
                int delay = 200;
                for (int i = 0; i < times; i++) {
                    // Save the original border style
                    String originalStyle = element.getCssValue("border");
                    // Change the border to highlight
                    FluentWait<WebDriver> wait = new FluentWait<>(driver)
                            .withTimeout(Duration.ofSeconds(20))
                            .pollingEvery(Duration.ofMillis(500))
                            .ignoring(StaleElementReferenceException.class);
                    WebElement finalElement = element;
                    element = wait.until(d -> finalElement);

                    // Ensure that checkboxes and radio buttons are properly targeted
                    if (element.getTagName().equals("input") &&
                            (element.getDomAttribute("type").equals("checkbox") || element.getDomAttribute("type").equals("radio"))) {
                        js.executeScript("arguments[0].style.outline = '3px solid red';", element);
                    } else {
                        js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red;');", element);
                    }

                    Thread.sleep(delay);
                    // Change the border back to original
                    if (element.getTagName().equals("input") &&
                            (element.getDomAttribute("type").equals("checkbox") || element.getDomAttribute("type").equals("radio"))) {
                        js.executeScript("arguments[0].style.outline = '';", element);
                    } else {
                        js.executeScript("arguments[0].setAttribute('style', 'border: " + originalStyle + ";');", element);
                    }
                    Thread.sleep(delay);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

    }

    /**
     * This method reads the complete table
     *
     * @param driver       - takes driver as an input
     * @param tableLocator - the locator for the table
     * @return - list of list where each list a row
     */
    public List<List<String>> readTable(WebDriver driver, String tableLocator) {
        List<List<String>> tableValues = new ArrayList<>();
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(WAITTIME))
                .pollingEvery(Duration.ofSeconds(WAITFREQUENCY));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(tableLocator)));
        WebElement table = driver.findElement(By.xpath(tableLocator));
        List<WebElement> trValue = table.findElements(By.tagName("tr"));

        LOG.debug("Tr size: {}", trValue.size());

        for (WebElement tr : trValue) {
            List<String> cellValues = new ArrayList<>();
            LOG.info("");
            List<WebElement> thElement = tr.findElements(By.tagName("th"));
            for (WebElement th : thElement) {
                cellValues.add(th.getText());
                highLighterMethod(driver, th);
            }
            List<WebElement> tdValue = tr.findElements(By.tagName("td"));
            for (WebElement td : tdValue) {
                highLighterMethod(driver, td);
                cellValues.add(td.getText());
            }
            tableValues.add(cellValues);
        }
        LOG.info("Table Values: {}", tableValues);
        return tableValues;

    }

    /**
     * this method returns a list which contains, table header and the row which matches the conditions
     *
     * @param driver              - Takes driver as input
     * @param validationRowValues - The conditions on which a specific row has to be fetched
     * @return - a list of table header and the row which matches the conditions
     */
    public List<List<String>> getTableRowValues(WebDriver driver, List<List<String>> validationRowValues, String locator) {


        List<List<String>> tableValues = readTable(driver, locator);
        List<List<String>> resultSublists = new ArrayList<>();
        Map<Integer, String> searchCriteria = new HashMap<>();
        /*Fetch the index of the column provided in conditions using header values */
        for (List<String> values : validationRowValues) {
            searchCriteria.put(tableValues.get(0).indexOf(values.get(0)), (String) stringInterpolation.stringInterpolation(values.get(1)));
        }
        LOG.info("Search Criteria: {}", searchCriteria);
        /*Fetch the row which matches the conditions provided*/
        for (List<String> sublist : tableValues) {
            boolean matches = true;
            for (Map.Entry<Integer, String> entry : searchCriteria.entrySet()) {
                int index = entry.getKey();
                String value = entry.getValue();

                if (sublist.size() <= index || !sublist.get(index).equals(value)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                resultSublists.add(sublist); // Add the sublist containing all search values at the given indices
            }

        }
        /*Adding the header to the result list */
        resultSublists.add(0, tableValues.get(0));

        LOG.info("Result list: {}", resultSublists);


        return resultSublists;

    }

    /**
     * This method returns the cell values from the row which has the matched conditions
     *
     * @param columnValue - takes column value of the data that has to be fetched
     * @param tableValues - takes table value as parameter
     * @return - celll value for the specificed header
     */
    public String getCellValue(String columnValue, List<List<String>> tableValues) {
        String cellvalue;
        try {
            cellvalue = tableValues.get(1).get(tableValues.get(0).indexOf(stringInterpolation.stringInterpolation(columnValue)));
        } catch (IndexOutOfBoundsException e) {
            cellvalue = null;
            LOG.info("Invalid reference value");
        }

        return cellvalue;
    }


    /**
     * This method asserts page title
     *
     * @param driver        - takes driver as an input
     * @param expectedTitle - the expected title
     * @return - true is the expected tile matches else false
     */
    public boolean assertPageTitle(WebDriver driver, String expectedTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleContains(expectedTitle));
        LOG.info("Title of the page: {}", driver.getTitle());
        return driver.getTitle().equals(expectedTitle);

    }

}
