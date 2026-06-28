## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md#--description
)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md#--getting-started
)                         
 * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md#--main-features-with-sample-code-snippets)      
 * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md#--documentation
)                    
 * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md#--troubleshoot)                       
 

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**


__WebUtilities__ within STAF are developed with the intention of streamlining tasks such as **web driver management** and **executing common browser actions**, as well as handling **typical actions for web-based applications** by providing a convenient framework.

**Release notes** : This confluence page describes changes in recent versions of STAF. Its primary objective is to document changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

WebUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. 
To import WebUtilities into a Maven project, **add the dependency below to your POM.xml file**.

### `Maven`
   
    <!-- Add following parent block in your POM.xml inside <project> block -->
    <project>
      <parent>
        <groupId>STAF</groupId>
        <artifactId>STAF</artifactId>
        <version>[Enter latest version]</version>
      </parent>
    
    <!-- Add following dependencies in your POM.xml inside <dependencies> block -->
    <dependencies>
      <dependency>
        <groupId>STAF</groupId>
        <artifactId>WebUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>WebUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`
* **`WebDriver Management:`**  Predefined functions and step definitions are available for initializing the driver, adding capabilities, and so on.
  To initialize the driver for browsers (IE/Chrome), a user can call the following predefined step definitions.

    ```
    # To initialize driver for chrome:
       Given We launch the Browser Chrome
    
    # To initialize driver for IE:
       Given We launch the Browser IE
    ```
* **`Browser actions:`**  Predefined functions are available to switch between browser windows, handle alerts, perform actions on webelements, fill values in webelements, get locator, close browsers, entering text into webelements, clicking on webelements, waiting for page loads, taking screenshots, etc.
  If the user needs to interact with a button on a browser window other than the currently focused window,  they can call the **switchtoWindow(WebDriver driver, String clickBtn)** function, as shown in the following example. 
    ```
     private WebDriver driver;
     private String clickYesButton = "//*[text()='Yes']";
     driver = commonDriverMethods.initializeDriver("Chrome");
     WebCommonMethods webCommonMethods = new WebCommonMethods();
     webCommonMethods.switchtoWindow(driver, yesBtn);
    ```


* **`To wait until the DOM is loaded:`**
  To wait for the DOM to load before returning control to the driver, the user can invoke the **fWaitForPageLoad** function, as demonstrated in the example below.
     ```
    CommonFunctions cf = new CommonFunctions(driver,driverType,environment);
    cf.fWaitForPageLoad();
    ```
    
 * **`Perform clicking operations on elements:`**
   To perform clicking on webelements such as buttons and links, the user can invoke the **clickWebElement (String webElementPath)** function as depicted in the example below.
    ```
    private String webElementPath = "xpath:=(//div[@class = 'cbui-tablerow-title ellipsis ng-binding'])[1]";
    CommonFunctions cf = new CommonFunctions();
    boolean b = cf.clickWebElement(webElementPath);
    Assert.assertTrue(b);
    ```
    

 * **`To get a screenshot of GUI page:`**   If the user wants to get a screenshot of the GUI page during test case execution, they can call the **takeSnapShot (@Nonnull WebDriver driver, String elementName)** function as shown in the example below.
    ```
     private WebDriver driver;
     private final ScreenShot screenShot = new ScreenShot();
     driver = commonDriverMethods.initializeDriver("Chrome");
     driver.get("www.google.com");
     screenShot.takeSnapShot(driver, "Application_LogIn_Page");
     //Screenshot can be found at (user directory)/target/ScreenShot/(Scenario name)/(Image no.)Application_LogIn_Page.png
    ```
  

* **`For closing driver instance:`**
  To close the driver instance, the user can call the following predefined step definitions. We have a **terminateWebDriver(Scenario scenario)** hooks available to close a driver instance after each scenario.
    ```
    # To close browser:
       Then Close browser
    ```


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for WebUtilities link : https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/webutilities/package-summary.html

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : https://de.confluence.agile.vodafone.com/x/pZkIBQ 

`Most frequent issues :`

If the below error is encountered while executing the test cases, please follow steps mentioned below:

Use the link to download the latest ChromeDriver version: https://googlechromelabs.github.io/chrome-for-testing/

Example: https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/119.0.6045.105/win64/chromedriver-win64.zip

Place the downloaded chromedriver version in [C drive → Users → User name → .cache → Selenium] location & Re-run the test case.. 

<img width="593" alt="image" src="https://github.vodafone.com/storage/user/26896/files/fbcb04e2-660e-48e2-aeed-91af0eb30176">




