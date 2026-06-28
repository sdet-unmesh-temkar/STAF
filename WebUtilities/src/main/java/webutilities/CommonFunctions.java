package webutilities;

import com.google.common.base.Function;
import generalutilities.ReportAndLogging;
import lombok.NonNull;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;


/**
 * This class perform CommonFunctions or CommonOperations on GUI(Graphical User Interface) elements.
 * This class contains methods to perform different operations on web elements such as click,Double Click,getLocator,Handle Alert and Popup
 */
public class CommonFunctions {

    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private static final Logger LOG = LoggerFactory.getLogger(CommonFunctions.class);
    private final Duration waitForElements = Duration.ofSeconds(5);
    private final Duration waitForMultipleElements = Duration.ofSeconds(5);
    private final Duration waitForMultipleChildElements = Duration.ofSeconds(5);
    private final Duration waitForPopupElements = Duration.ofSeconds(3);
    private final Duration waitForPage = Duration.ofSeconds(60);
    private final Duration implicitWait = Duration.ofSeconds(30);
    private final Duration waitForElementPollingTime = Duration.ofSeconds(100);

    private static final String REPLACE_XPATH = "xpath:=";
    private static final String ALERT_DISMISS = "dismiss";
    private static final String DELAY_MESSAGE = "--- return false - has delayed with --> ";
    private static final String TIME_UNIT = "ms";
    private static final String TRUE_DELAY_MSG = " -- return true -  has delayed with --> ";
    private static final String CLOSE_EXECUTION = "window.close";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String DELAY_WITH_MSG = " - has delayed with --> ";
    private static final String WAIT_FOR_OBJ_MSG = " -- waitForWebElementIsEnabled - return false - for object '";
    WebCommonMethods webCommonMethods = new WebCommonMethods();

    private static final String TEST_DATA_TO_HIDE = "HIDE_TEST_DATA";
    private static final String SET_ATTRIBUTE_EXECUTE_SCRIPT = "arguments[0].setAttribute('style', arguments[1]);";
    private static final String BACKGROUND_COLOR = "color: white; background-color: white; border: 4px solid red;";
    private static final String NULL_DELAY_MSG = "-- return null - has delayed with --> --> ";
    private static final String FOCUS_EXECUTE = "focus()";
    private static final String INVALIDFINDBY_DELAY_MSG = "   - return null --invalid findBy value-- was delayed with :  ";
    private static final String SUCCESS_DELAY_MSG = " -- return success --  was delayed with :  ";
    private static final String ELEMENT_NOT_FOUND_MSG = "  - return null --element not found -- has delayed with --> ";

    private String errorMsg;
    private String infoMsg;
    private WebDriverWait wait;

    private final WebDriver driver;
    private final String driverType;
    private Map<String, String> environment;
    private boolean firstTime = true;

    /**
     * This parameterized constructor is used for Object initialization purpose
     *
     * @param webDriver -  instance of browser to initialize object of class
     * @param dt        -  to initialize object of class
     * @param env       -  to initialize object of class
     */
    public CommonFunctions(WebDriver webDriver, String dt, Map<String, String> env) {
        driver = webDriver;
        driverType = dt;
        environment = env;
        initializeTimeouts();
    }

    /**
     * This method is used to wait for page to load.
     *
     * @return boolean - true/false
     */
    public boolean fWaitForPageLoad() {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(250));
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("(//*[@fill='none'])[1]"))));
            reportAndLogging.logStepInJira("It took more than 250 seconds to load next page. Hence failed.");
            return false;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    /**
     * This method is used to check if WebElement is Present.
     *
     * @param xpath - locator to identify the web element presence on web page
     * @return boolean  - true/false
     */
    public boolean isWebElementPresent(String xpath) {
        xpath = xpath.replace(REPLACE_XPATH, "");
        int waitFrequency = 10;
        int waitTime = 100;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int count = waitFrequency;
        while (count <= waitTime) {
            try {
                Thread.sleep(waitFrequency);
                webCommonMethods.highLighterMethod(driver, driver.findElement(By.xpath(xpath)));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                return true;
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                reportAndLogging.logStepInJira(xpath + " - WebElement is not present on page or not loaded within timelines.");
                return false;
            }
        }
        return false;
    }

    /**
     * This method is used to click on WebElement.
     *
     * @param webElementPath - locators to click on web element
     * @return boolean       - true/false
     */
    public boolean clickWebElement(String webElementPath) {
        webElementPath = webElementPath.replace(REPLACE_XPATH, "");
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            webCommonMethods.highLighterMethod(driver, driver.findElement(By.xpath(webElementPath)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElementPath))).click();
            return true;
        } catch (Exception e) {
            reportAndLogging.logStepInJira(webElementPath + " is not clicked successfully.");
            return false;
        }
    }

    /**
     * This method is used to fill value in WebElement.
     *
     * @param webElementPath        - locators to pass value on the web element
     * @param val                   - value which needs to be passed in WebElement
     * @return boolean              - true/false
     * @throws InterruptedException - an exception throws if unable to fill Value In Web Element
     */
    public boolean fillValueInWebElement(String webElementPath, String val) throws InterruptedException {
        webElementPath = webElementPath.replace(REPLACE_XPATH, "");
        int waitFrequency = 10;
        int waitTime = 100;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        int count = waitFrequency;
        while (count <= waitTime) {
            try {
                Thread.sleep(waitFrequency);
                webCommonMethods.highLighterMethod(driver, driver.findElement(By.xpath(webElementPath)));
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(webElementPath))).clear();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElementPath))).sendKeys(val);
                return true;
            } catch (TimeoutException e) {
                count += waitFrequency;
                if (count == waitTime) {
                    LOG.error(e.getMessage());
                    reportAndLogging.logStepInJira(val + " is not entered successfully.");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to initialize Timeouts.
     */
    private void initializeTimeouts() {
        String debugMessage;
        String debugMessageForMultipleElement;
        String debugMessageForMultipleChildElement;
        String debugMessageForPopupElement;
        String debugMessageforPageWait;
        String debugMsgForImplicitWait;

        try {

            if (firstTime) {
                firstTime = false;
                debugMessage = fGetThreadClassNameAndMethod() + " -- WAIT_FOR_ELEMENT picked from Environments.xlsx: " + waitForElements;
                debugMessageForMultipleElement = fGetThreadClassNameAndMethod() + " -- WAIT_FOR_MULTIPLE_ELEMENTS picked from Environments.xlsx: " + waitForMultipleElements;
                debugMessageForMultipleChildElement = fGetThreadClassNameAndMethod() + " -- WAIT_FOR_MULTIPLE_CHILD_ELEMENTS picked from Environments.xlsx: " + waitForMultipleChildElements;
                debugMessageForPopupElement = fGetThreadClassNameAndMethod() + " -- WAIT_FOR_POPUP_ELEMENTS picked from Environments.xlsx: " + waitForPopupElements;
                debugMessageforPageWait = fGetThreadClassNameAndMethod() + " -- WAIT_FOR_PAGE picked from Environments.xlsx: " + waitForPage;
                debugMsgForImplicitWait = fGetThreadClassNameAndMethod() + " -- IMPLICIT_WAIT picked from Environments.xlsx: " + implicitWait;

                LOG.debug(debugMessage);
                LOG.debug(debugMessageForMultipleElement);
                LOG.debug(debugMessageForMultipleChildElement);
                LOG.debug(debugMessageForPopupElement);
                LOG.debug(debugMessageforPageWait);
                LOG.debug(debugMsgForImplicitWait);
            }

        } catch (Exception e) {
            LOG.error(" Exception while getting the values for explicit waits ");
            LOG.error(e.getMessage());
        }
    }

    /**
     * This method is used to click on the webelement.
     *
     * @param webElmtProp - locator to perform click on the web element
     * @param strObjName  - name of web element to perform click on the web element
     * @return boolean    - true/false
     */
    public boolean fCommonClick(String webElmtProp, String strObjName) {
        long startTime = System.currentTimeMillis();
        WebElement webElement;
        try {
            webElement = fcommonRetrieveVisibleElementOnPage(webElmtProp, strObjName);
            return fCommonClick(webElement, strObjName);
        } catch (NoSuchElementException e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
    }

    /**
     * This method is used to click on the webelement using Action Class.
     *
     * @param webElmtProp - locator to perform click on the web element
     * @param strObjName  - name of web element to perform click on the web element
     * @return boolean    - true/false
     */
    public boolean fCommonActionClick(String webElmtProp, String strObjName) {
        long startTime = System.currentTimeMillis();
        WebElement webElement;
        try {
            webElement = driver.findElement(By.xpath(webElmtProp));
            webCommonMethods.highLighterMethod(driver, webElement);
            return fCommonActionClick(webElement, strObjName);
        } catch (NoSuchElementException e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
    }

    /**
     * This method is used to click on the webelement using Action Class.
     *
     * @param webElement - locator to perform click on the web element
     * @param strObjName - name of web element to perform click on the web element
     * @return boolean   - true/false
     */
    private boolean fCommonActionClick(@NonNull WebElement webElement, String strObjName) {
        long startTime = System.currentTimeMillis();
        Actions action = new Actions(driver);
        try {
            ///////////---Authentication Popup-------////////////////////
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript(CLOSE_EXECUTION);
            ///////////---Authentication Popup-------////////////////////

            //Check WebElement

        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
        }

        //Check if the WebElement is clicked
        boolean bIsClicked = false;
        try {
            waitForWebElementIsEnabled(webElement, strObjName);
            webElement.getLocation();
            action.moveToElement(webElement).click(webElement).perform();
            bIsClicked = true;
        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
            //check if are pop-ups that is making the click failing
            int i = handlePopUps(1);
            if (i <= 0) {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    webElement.getLocation();
                    action.moveToElement(webElement).click(webElement).perform();
                    bIsClicked = true;
                } catch (Exception e1) {
                    isExceptionCaught(startTime, e1);
                    bIsClicked = false;
                }
            } else {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    webElement.getLocation();
                    action.moveToElement(webElement).click(webElement).perform();
                    bIsClicked = true;
                } catch (Exception e2) {
                    fCommonHandleAlert(ALERT_DISMISS);
                    var errorMessage = fGetThreadClassNameAndMethod() + " -- " + "Exception occured : " + e2.toString();
                    LOG.error(errorMessage);
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    bIsClicked = false;

                }

            }

        }
        return bIsClicked;
    }

    /**
     * This method is used to double click on the webelement using Action Class.
     *
     * @param webElmtProp - locator to perform click on the web element
     * @param strObjName  - name of web element to perform double click
     * @return boolean    - true/false
     */
    public boolean fCommonDoubleClick(String webElmtProp, String strObjName) {
        long startTime = System.currentTimeMillis();
        WebElement webElement;
        try {
            webElement = driver.findElement(By.xpath(webElmtProp));
            webCommonMethods.highLighterMethod(driver, webElement);
            return fCommonDoubleClick(webElement, strObjName);
        } catch (NoSuchElementException e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
    }

    /**
     * This method is used to double click on the webelement using Action Class.
     *
     * @param webElement - locator to perform double click on the web element
     * @param strObjName - name of web element to perform double click on the web element
     * @return boolean   - true/false
     */
    private boolean fCommonDoubleClick(@NonNull WebElement webElement, String strObjName) {
        long startTime = System.currentTimeMillis();

        try {
            ///////////---Authentication Popup-------////////////////////
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript(CLOSE_EXECUTION);
            ///////////---Authentication Popup-------////////////////////


        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
        }

        boolean bIsDoubleClicked = false;
        try {
            // just waiting for now until timeout, if still not clickable it will fail downstream
            waitForWebElementIsEnabled(webElement, strObjName);
            Actions action = new Actions(driver);
            webElement.getLocation();
            action.moveToElement(webElement).click(webElement).build().perform();
            action.moveToElement(webElement).click(webElement).build().perform();
            bIsDoubleClicked = true;
        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
            int i = handlePopUps(1);
            if (i <= 0) {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    Actions action = new Actions(driver);
                    webElement.getLocation();
                    action.moveToElement(webElement).doubleClick(webElement).perform();
                    bIsDoubleClicked = true;
                } catch (Exception e1) {
                    fCommonHandleAlert(ALERT_DISMISS);
                    LOG.error(e.getMessage());
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    bIsDoubleClicked = false;
                }
            } else {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    Actions action = new Actions(driver);
                    webElement.getLocation();
                    action.moveToElement(webElement).doubleClick(webElement).perform();
                    bIsDoubleClicked = true;
                } catch (Exception e2) {
                    fCommonHandleAlert(ALERT_DISMISS);
                    errorMsg = fGetThreadClassNameAndMethod() + " -- " + "Exceptions occured : " + e2.toString();
                    LOG.error(errorMsg);
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    bIsDoubleClicked = false;
                }

            }
        }
        return bIsDoubleClicked;
    }

    /**
     * This method is used to click on the webelement.
     *
     * @param webElement - locator to perform click on the web element
     * @param strObjName - name of web element to perform click on the web element
     * @return boolean   - true/false
     */
    public boolean fCommonClick(@NonNull WebElement webElement, String strObjName) {

        long startTime = System.currentTimeMillis();

        try {
            ///////////---Authentication Popup-------////////////////////
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript(CLOSE_EXECUTION);
            ///////////---Authentication Popup-------////////////////////


        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
        }

        //Check if the WebElement is clicked
        boolean bIsClicked = false;
        try {
            // just waiting for now until timeout, if still not clickable it will fail downstream
            waitForWebElementIsEnabled(webElement, strObjName);

            webElement.getLocation();
            webElement.click();
            bIsClicked = true;
        } catch (Exception e) {
            fCommonHandleAlert(ALERT_DISMISS);
            //check if are pop-ups that is making the click failing
            int i = handlePopUps(1);
            //i will become <=0 only if at least one pop-up was found and handled; if not then the click failed from other reasons
            if (i <= 0) {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    webElement.getLocation();
                    webElement.click();
                    bIsClicked = true;
                } catch (Exception e1) {
                    isExceptionCaught(startTime, e1);
                    bIsClicked = false;
                }
            } else {
                try {
                    fCommonHandleAlert(ALERT_DISMISS);
                    webElement.getLocation();
                    webElement.click();
                    bIsClicked = true;
                } catch (Exception e2) {
                    fCommonHandleAlert(ALERT_DISMISS);
                    errorMsg = fGetThreadClassNameAndMethod() + " -- " + "Exceptions  is occured : " + e2.toString();
                    LOG.error(errorMsg);
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    bIsClicked = false;
                }

            }
        }
        return bIsClicked;
    }

    /**
     * This method is used to handle the exception
     *
     * @param startTime - to calculate system current Time in milli seconds.
     * @param e         - an exception triggered if unable to caught errorMsg
     */
    private void isExceptionCaught(long startTime, Exception e) {
        fCommonHandleAlert(ALERT_DISMISS);
        LOG.error(e.getMessage());
        errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.error(errorMsg);
    }

    /**
     * This method is used to handle the PopUps
     *
     * @param intcount - get a count until element get enabled to perform click
     * @return Count   - int
     */
    public int handlePopUps(int intcount) {

        long startTime = System.currentTimeMillis();
        try {
            return handlePopUps(intcount, waitForPopupElements);
        } catch (Exception e) {
            infoMsg = fGetThreadClassNameAndMethod() + "--" + "handlePopUps - return intcount -  has delayed with --> " + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return intcount;
        }

    }

    /**
     * This method is used to handle popups with timeout parameter
     *
     * @param intcount - get a count until element get enabled to perform click
     * @param timeout  - handle popups with timeout parameter
     * @return Count   - int
     */
    private int handlePopUps(int intcount, Duration timeout) {
        WebDriverWait waits = new WebDriverWait(driver, timeout);
        WebElement element = null;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        try {
            infoMsg = fGetThreadClassNameAndMethod() + "--" + "handlePopUps - Survey or Chat Pop Up Exist - Closing it...";
            if (driverType.contains("ANDROID")) {
                element = waits.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class='fsrDeclineButton']")));
                if (element.isEnabled()) {
                    LOG.info(infoMsg);
                    intcount--;
                    element.click();
                    return intcount;
                }

            }
            // use else block
            element = waits.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class='fsrCloseBtn' or @id='tcChat_btnCloseChat_img' or @id='tcXF18000443_xf-10·1']")));
            if (element.isEnabled()) {
                LOG.info(infoMsg);
                intcount--;
                element.click();
                return intcount;
            }

        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        infoMsg = fGetThreadClassNameAndMethod() + "--" + "handlePopUps - No Pop Up displayed...";
        LOG.info(infoMsg);

        return intcount;
    }

    /**
     * This method is used to check if webElement is displayed
     *
     * @param webElmtProp - locator to perform click on the web element
     * @param objName     - name of web element to check visibility of element
     * @return boolean    - true/false
     */
    public boolean fCommonGuiIsDisplayed(String webElmtProp, String objName) {

        long startTime = System.currentTimeMillis();
        //Get WebElement
        try {
            WebElement webElement = fCommonGetObject(webElmtProp, objName);
            webCommonMethods.highLighterMethod(driver, webElement);
            boolean ret = fCommonGuiIsDisplayed(webElement, objName);
            LOG.debug(webElement.getText());
            infoMsg = fGetThreadClassNameAndMethod() + "--" + "fCommonGuiIsDisplayed-Method  is return " + ret + DELAY_WITH_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return ret;
        } catch (NoSuchElementException e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }

    }

    /**
     * This method is used to get the Locator
     *
     * @param locator    - get the Locator
     * @param expression - get the Locator with formal expression
     * @return - By class or null
     */
    public By getLocator(String locator, String expression) {
        switch (locator) {
            case "xpath":
                return By.xpath(expression);
            case "cssselector":
                return By.cssSelector(expression);
            case "linktext":
                return By.linkText(expression);
            case "classname":
                return By.className(expression);
            case "id":
                return By.id(expression);
            case "name":
                return By.name(expression);
            case "partiallinktext":
                return By.partialLinkText(expression);
            default:
                return null;
        }
    }

    /**
     * This method is used to get WebElement
     *
     * @param objDesc - Description of the WebElement to find unique element by using locator
     * @param objName - Name of the WebElement to find unique element by using locator
     * @return webElement
     * Example:
     * if this method is being used to find 'Login' button WebElement by using xpath locator
     * fCommonGetObject("xpath:=expression", 'Login')
     */
    public WebElement fCommonGetObject(String objDesc, String objName) {

        long startTime = System.currentTimeMillis();

        String[] arrFindByValues = objDesc.split(":=");
        String findBy;
        String expression;
        if (arrFindByValues.length == 2) {
            findBy = arrFindByValues[0];
            expression = arrFindByValues[1];
            LOG.info("findby {} val {} ", findBy, expression);
        } else {
            errorMsg = fGetThreadClassNameAndMethod() + objDesc + NULL_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return null;
        }
        WebDriverWait waits = new WebDriverWait(driver, waitForElements);
        boolean invalidFindBy = false;
        boolean elementNotFound = false;
        int intcount = 1;
        while (intcount <= 2) {
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                //Handle all FindBy cases
                By locator = getLocator(findBy, expression);
                if (locator != null) {
                    return waits.until(ExpectedConditions.presenceOfElementLocated(locator));
                } else {
                    invalidFindBy = true;
                    return null;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
                if (driverType.contains("ANDROID") || driverType.contains("IOS")) {
                    fGuiHandleNoThanksModalPgSrc();
                }
                fCommonHandleAlert(ALERT_DISMISS);
                errorMsg = "Exception while getting object " + objDesc + "  ... Checking for PopUps... ";
                LOG.error(errorMsg);
                intcount = handlePopUps(intcount);
                if (intcount == 1) {
                    elementNotFound = true;
                    return null;
                }
                intcount = intcount + 1;
                //Select browser in focus
                try {
                    ((JavascriptExecutor) driver).executeScript(FOCUS_EXECUTE);
                } catch (Exception e1) {
                    fCommonHandleAlert(ALERT_DISMISS);
                }

            } finally {
                if (invalidFindBy) {
                    errorMsg = fGetThreadClassNameAndMethod() + "fCommonGetObject  object " + objDesc + INVALIDFINDBY_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                }
                if (elementNotFound) {
                    errorMsg = fGetThreadClassNameAndMethod() + "fCommonGetObject for object is " + objDesc + ELEMENT_NOT_FOUND_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    errorMsg = fGetThreadClassNameAndMethod() + "Object for fCommonGetObject  " + objDesc
                            + " return null --element not found --  has delayed with --> " + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                } else {
                    infoMsg = fGetThreadClassNameAndMethod() + findBy + " for object " + objDesc + SUCCESS_DELAY_MSG + (System.currentTimeMillis() - startTime)
                            + TIME_UNIT;
                    LOG.info(infoMsg);
                    LOG.info(objName);
                }

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            }
        }
        errorMsg = fGetThreadClassNameAndMethod() + objDesc + NULL_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.error(errorMsg);
        return null;
    }

    /**
     * This method is used to check if webElement not displayed
     *
     * @param webElmtProp - locator to check visibility of the webelement on webpage
     * @param objName     - name of web element to check visibility of element
     * @return boolean    - true/false
     */
    public boolean fCommonGuiIsNotDisplayed(String webElmtProp, String objName) {

        long startTime = System.currentTimeMillis();

        //Get WebElement
        try {

            WebElement webElement = driver.findElement(By.xpath(webElmtProp));
            boolean ret = fCommonGuiIsNotDisplayed(webElement, objName);
            LOG.debug(webElement.getText());
            infoMsg = fGetThreadClassNameAndMethod() + "--" + "return fCommonGuiIsDisplayed" + ret + DELAY_WITH_MSG +
                    (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return ret;
        } catch (NoSuchElementException e) {
            infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return true;
        }

    }

    /**
     * This method is used to check if webElement not displayed
     *
     * @param webElement - locator to check visibility of the webelement on webpage
     * @param objName    - name of web element to check visibility of element
     * @return boolean   - true/false
     */
    public boolean fCommonGuiIsNotDisplayed(WebElement webElement, String objName) {

        long startTime = System.currentTimeMillis();

        //Check if the Webelement is displayed
        boolean bIsDisplayed = false;

        try {
            bIsDisplayed = webElement.isDisplayed();

        } catch (Exception e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }

        //Validate if the element is enabled
        if (!(bIsDisplayed)) {
            infoMsg = fGetThreadClassNameAndMethod() + "fCommonGuiIsNotDisplayed " + objName + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return true;
        }

        errorMsg = fGetThreadClassNameAndMethod() + "--" + "fCommonGuiIsNotDisplayed " + objName + DELAY_WITH_MSG
                + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.error(errorMsg);
        return false;

    }

    /**
     * This method is used to check if webElement is displayed
     *
     * @param webElement - locator to check visibility of the webelement on webpage
     * @param objName    - name of web element to check visibility of element
     * @return boolean   - true/false
     */
    public boolean fCommonGuiIsDisplayed(WebElement webElement, String objName) {

        long startTime = System.currentTimeMillis();

        //Check if the Webelement is displayed
        boolean bIsDisplayed = false;
        try {
            bIsDisplayed = webElement.isDisplayed();

        } catch (Exception e) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }

        //Validate if the element is enabled
        if (!(bIsDisplayed)) {
            errorMsg = fGetThreadClassNameAndMethod() + objName + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
        infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.info(infoMsg);
        return true;

    }

    /**
     * This method is used to handle Alert popup
     *
     * @param intent - to accept or dismiss Alert popup as per the intent of the flow
     * @return boolean - true/false
     */
    public boolean fCommonHandleAlert(String intent) {
        if (driverType.contains("IE")) {
            long startTime = System.currentTimeMillis();
            int i = 0;
            while (i < 2) {
                try {
                    if (intent.equalsIgnoreCase("accept")) {
                        driver.switchTo().alert().getText();
                        driver.switchTo().alert().accept();
                        infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                        LOG.info(infoMsg);
                        return true;
                    } else if (intent.equalsIgnoreCase(ALERT_DISMISS)) {
                        driver.switchTo().alert().getText();
                        driver.switchTo().alert().dismiss();
                        infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                        LOG.info(infoMsg);
                        return true;
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage());

                }

                i++;
            }
        }
        return true;
    }

    /**
     * This method is used to get the class name and method name
     *
     * @return String -  class name and method name
     */
    public String fGetThreadClassNameAndMethod() {
        return getThreadId() + " -- " + driverType + " -- " +
                Thread.currentThread().getStackTrace()[3].getClassName().replaceAll("^.*\\.", "") + "." +
                Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
     * This method is used to get the current Thread Id
     *
     * @return String - current Thread Id
     */
    public String getThreadId() {
        return "THREAD:" + Thread.currentThread().getId();
    }

    /**
     * This method is used to wait for WAIT_FOR_ELEMENT seconds until an element has the expected text
     *
     * @param webElmtProp  - String type webElmtProp is passed as argument which is used to wait for WAIT_FOR_ELEMENT seconds until an element has the expected text
     * @param expectedText - String type expectedText is passed as argument which is used to wait for WAIT_FOR_ELEMENT seconds until an element has the expected text
     * @return boolean     - true/false
     */
    public boolean waitForElementToContainsText(String webElmtProp, final String expectedText) {
        long startTime = System.currentTimeMillis();
        //Get WebElement
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(15));
            WebElement webElement = driver.findElement(By.xpath(webElmtProp));
            WebDriverWait waits = new WebDriverWait(driver, waitForElements, waitForElementPollingTime);
            waits.until((Function<WebDriver, WebElement>) drivers -> {
                if (webElement.getText().trim().contains(expectedText)) {
                    return webElement;
                } else {
                    return null;
                }

            });
        } catch (Exception e) {
            LOG.error(e.toString());
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
        infoMsg = fGetThreadClassNameAndMethod() + " -- waitForElementToContainText - return true - for object '" +
                webElmtProp + " delay with --> " + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.info(infoMsg);
        return true;
    }

    /**
     * This method is used to check if web element text contains expected value
     *
     * @param objectLocator - locator to check if web element text contains expected value
     * @param expectedText  - passed as argument to match Actual text and Expected text
     * @return boolean      - true/false
     */
    public boolean checkWebElementContainsText(String objectLocator, String expectedText) {

        long startTime = System.currentTimeMillis();
        //Check if the WebElement contains expectedText
        try {
            WebElement webElement = driver.findElement(By.xpath("//*[@class='activityStatus activityStatusValue']"));
            if (!webElement.getText().contains(expectedText)) {

                if (webElement.getDomAttribute(VALUE_ATTRIBUTE) != null) {
                    if (!webElement.getDomAttribute(VALUE_ATTRIBUTE).trim().contains(expectedText.trim())) {
                        errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                        LOG.error(errorMsg);
                        return false;
                    }
                } else {
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    return false;
                }
            }
        } catch (Exception e1) {
            LOG.error(e1.toString());
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        }
        infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.info(infoMsg);

        return true;

    }

    /**
     * This method is used to wait for WAIT_FOR_ELEMENT seconds until the web element is enabled
     *
     * @param element     - locator WAIT_FOR_ELEMENT seconds until the web element is enabled
     * @param webElmtName - name of web element to wait for WAIT_FOR_ELEMENT seconds until the web element is enabled
     * @return boolean    - true/false
     */
    public boolean waitForWebElementIsEnabled(final WebElement element, String webElmtName) {

        long startTime = System.currentTimeMillis();

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2));
            fCommonHandleAlert(ALERT_DISMISS);
            WebDriverWait waits = new WebDriverWait(driver, waitForElements, waitForElementPollingTime);
            waits.until((Function<WebDriver, WebElement>) drivers -> {
                if (element == null) {
                    return null;
                } else {
                    if (element.isEnabled()) {
                        return element;
                    } else {
                        return null;
                    }
                }
            });

        } catch (NoSuchElementException noSuchElementException) {
            LOG.error(noSuchElementException.getMessage());
            errorMsg = fGetThreadClassNameAndMethod() + WAIT_FOR_OBJ_MSG +
                    webElmtName + DELAY_WITH_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        } catch (TimeoutException timeoutException) {
            LOG.error(timeoutException.getMessage());
            errorMsg = fGetThreadClassNameAndMethod() + WAIT_FOR_OBJ_MSG +
                    webElmtName + DELAY_WITH_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
            errorMsg = fGetThreadClassNameAndMethod() + WAIT_FOR_OBJ_MSG +
                    webElmtName + DELAY_WITH_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(15));
        }
        infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + webElmtName + DELAY_WITH_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.info(infoMsg);
        return true;
    }

    /**
     * This method used to wait for specified time
     *
     * @param millis - to wait for specified time
     */
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method used to check and handle the Survey Popup
     *
     * @return boolean - true/false
     */
    public boolean fGuiHandleNoThanksModalPgSrc() {
        String str = "class=\"fsrC\"";
        try {
            if (driver.getPageSource().contains(str)) {
                driver.findElement(By.linkText("No, thanks")).click();
                return true;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }

    /**
     * This method is used to Set value in Edit box.
     *
     * @param webElmtProp   - locator to set value in Edit box
     * @param strObjName    - set value in Edit box
     * @param strValue      - set value in Edit box
     * @param strClear      - clear value in Edit box
     * @param strSkipVerify - skip and verify value in Edit box
     * @return boolean      - true/false
     */
    public boolean fCommonSetValueEditBox(String webElmtProp, String strObjName, String strValue, String
            strClear, String strSkipVerify) {

        long startTime = System.currentTimeMillis();
        //Get WebElement
        WebElement objWebEdit = fcommonRetrieveVisibleElementOnPage(webElmtProp, strObjName);
        if (objWebEdit == null) {
            errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return false;
        } else {
            JavascriptExecutor js = ((JavascriptExecutor) driver);
            //Checks if input parameter is Null
            if (strValue == null)
                strValue = "";
            //Set value to the Edit box
            try {
                if (strClear.equalsIgnoreCase("Y")) {
                    objWebEdit.clear();
                    objWebEdit.sendKeys(Keys.CONTROL + "a");
                    objWebEdit.sendKeys(Keys.DELETE);
                }
                if (environment.get(TEST_DATA_TO_HIDE) != null
                        && (environment.get(TEST_DATA_TO_HIDE).equalsIgnoreCase("y") || environment.get(TEST_DATA_TO_HIDE).equalsIgnoreCase("yes"))) {
                    js.executeScript(SET_ATTRIBUTE_EXECUTE_SCRIPT, objWebEdit, BACKGROUND_COLOR);
                }
                objWebEdit.sendKeys(strValue);
            } catch (Exception e) {
                LOG.error(e.getMessage());
                try {
                    if (environment.get(TEST_DATA_TO_HIDE) != null
                            && (environment.get(TEST_DATA_TO_HIDE).equalsIgnoreCase("y") || environment.get(TEST_DATA_TO_HIDE).equalsIgnoreCase("yes"))) {
                        js.executeScript(SET_ATTRIBUTE_EXECUTE_SCRIPT, objWebEdit, BACKGROUND_COLOR);
                    }
                    js.executeScript("arguments[0].setAttribute('value', '" + strValue + "');", objWebEdit);
                } catch (Exception e1) {
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    return false;
                }
            }

            //Validate if the value is selected successfully
            if (strSkipVerify.equalsIgnoreCase("N")) {
                if (objWebEdit.getDomAttribute(VALUE_ATTRIBUTE).equals(strValue)) {
                    infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.info(infoMsg);
                    return true;
                } else {
                    errorMsg = fGetThreadClassNameAndMethod() + DELAY_MESSAGE + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                    return false;
                }
            }
            infoMsg = fGetThreadClassNameAndMethod() + TRUE_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.info(infoMsg);
            return true;
        }
    }

    /**
     * This method used to retrieve visible element on page
     *
     * @param strWebElement  - locator to retrieve visible element on page
     * @param strElementName - name of web element to retrieve visible element on page
     * @return WebElement    - to retrieve visible element on Web page
     */
    public WebElement fcommonRetrieveVisibleElementOnPage(String strWebElement, String strElementName) {

        List<WebElement> arrVisbleElement = fCommonGetMultipleObjects(strWebElement, strElementName);
        WebElement webElmtVisible = null;
        if (arrVisbleElement != null) {
            for (int i = 0; i < arrVisbleElement.size(); i++) {
                if (arrVisbleElement.get(i).getSize().height != 0) {
                    webElmtVisible = arrVisbleElement.get(i);
                    break;
                }
                if (i == arrVisbleElement.size() - 1) {
                    return null;
                }
            }
        } else {
            return null;
        }
        return webElmtVisible;
    }

    /**
     * This method is used to get multiple objects having same property
     *
     * @param objDesc - locator to get multiple objects having same property
     * @param objName - locator to get multiple objects having same property
     * @return WebElement List  - get multiple objects having same property
     */
    public List<WebElement> fCommonGetMultipleObjects(String objDesc, String objName) {

        long startTime = System.currentTimeMillis();
        List<WebElement> lstElements = null;
        //Delimiters
        String[] arrFindByValues = objDesc.split(":=");
        //Get Findby and Value
        String findBy;
        String expression;
        if (arrFindByValues.length == 2) {
            findBy = arrFindByValues[0].toLowerCase();
            expression = arrFindByValues[1];
        } else {
            errorMsg = fGetThreadClassNameAndMethod() + NULL_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
            LOG.error(errorMsg);
            return lstElements;
        }
        WebDriverWait waits = new WebDriverWait(driver, waitForMultipleElements);
        int intcount = 1;
        boolean invalidFindBy = false;
        boolean elementNotFound = false;
        LOG.info(objName);
        while (intcount <= 2) {
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2)); //nullify implicitlyWait()
                //Handle all FindBy cases
                By locator = getLocator(findBy, expression);
                if (locator != null) {
                    return waits.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
                } else {
                    invalidFindBy = true;
                    return lstElements;
                }
            } catch (Exception e) {
                intcount = handlePopUps(intcount);
                if (intcount == 1) {
                    elementNotFound = true;
                    LOG.error(e.getMessage());
                    return lstElements;
                }
                intcount = intcount + 1;
                //Select browser in focus
                ((JavascriptExecutor) driver).executeScript(FOCUS_EXECUTE);
            } finally {
                if (invalidFindBy) {
                    errorMsg = fGetThreadClassNameAndMethod() + objDesc + INVALIDFINDBY_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.error(errorMsg);
                }
                if (elementNotFound) {
                    errorMsg = fGetThreadClassNameAndMethod() + objDesc + ELEMENT_NOT_FOUND_MSG + (System.currentTimeMillis() - startTime) + "ms. Exception occurred ";
                    LOG.error(errorMsg);
                } else {
                    infoMsg = fGetThreadClassNameAndMethod() + findBy + " for objects is " + objDesc + SUCCESS_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
                    LOG.info(infoMsg);
                }
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(15));
            }
        }
        errorMsg = fGetThreadClassNameAndMethod() + "fCommonGetMultipleObjects for object " + objDesc + NULL_DELAY_MSG + (System.currentTimeMillis() - startTime) + TIME_UNIT;
        LOG.error(errorMsg);
        return lstElements;
    }

    /**
     * This method will check for the element to be present or not
     *
     * @param xpath - locator check for the element to be present or not
     * @return boolean  - true/false
     */
    public boolean waitForElementPresent(String xpath) {
        xpath = xpath.replace(REPLACE_XPATH, "");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            return true;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            reportAndLogging.logStepInJira(xpath + " - WebElement is not present on page or not loaded within timelines.");
            return false;
        }
    }

    /**
     * This method will click on WebElement using Actions class
     *
     * @param webElementPath - locator to click on WebElement using Actions class
     * @param objName        - name of WebElement to perform click
     * @return boolean       - true/false
     */
    public boolean actionClick(String webElementPath, String objName) {
        WebElement webElement = fcommonRetrieveVisibleElementOnPage(webElementPath, objName);
        LOG.info(objName);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            Actions action = new Actions(driver);
            webElement.getLocation();
            action.moveToElement(webElement).click(webElement).perform();
            return true;
        } catch (Exception e) {
            reportAndLogging.logStepInJira(webElementPath + " is not clicked successfully.");
            return false;


        }
    }


}