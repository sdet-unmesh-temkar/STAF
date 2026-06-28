package webutilities;


import generalutilities.EnvironmentDataLoader;
import generalutilities.ThreadLocalRegistry;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


/**
 * This class perform operations related to Common Driver Methods.
 * This class contains methods to perform different operations on WebDriver which is going to invoke(IE,Chrome,Firefox)
 */
public class CommonDriverMethods {

    private static final Logger LOG = LoggerFactory.getLogger(CommonDriverMethods.class);
    private static final Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    private static final ThreadLocal<WebDriver> instance = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> chromeOptions = ThreadLocal.withInitial(ArrayList::new);

    /**
     * This method is used to return webdriver instance
     *
     * @return -  WebDriver instance of type static
     */
    public static WebDriver getDriver() {
        ThreadLocalRegistry.register(instance);
        ThreadLocalRegistry.register(chromeOptions);
        return instance.get();
    }

    /**
     * This method is used to set webdriver instance
     *
     * @param driver - set the webdriver instance
     */
    public static void setDriver(WebDriver driver) {

        instance.set(driver);
    }

    /**
     * This method add options to Chrome Driver
     *
     * @param options - add options to the driver
     */
    public static void addDriverOptions(String... options) {

        chromeOptions.get().addAll(Arrays.asList(options));

    }

    /**
     * This method add options to Chrome Driver
     *
     * @param options - to add options to driver
     */
    public static void addDriverOptions(List<String> options) {

        chromeOptions.get().addAll(options);

    }

    /**
     * This method is used to cleanup the threadlocals attributes.
     */
    public void unload() {
        instance.remove();
        chromeOptions.remove();
    }

    /**
     * This method initializes driver instance based on the browser selected
     *
     * @param driverType - which is going to invoke(IE,Chrome,Firefox) instance of browser
     * @return WebDriver - driver instance based on the browser selected
     */
    public WebDriver initializeDriver(String driverType) {

        String proxyDetails = "Proxy_details";
        if (driverType.equalsIgnoreCase("CHROME")) {

            ChromeOptions options = new ChromeOptions();

            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);

            if (System.getProperty("os.name").contains("Linux")) {
                options.addArguments("--no-sandbox");
                if (Objects.equals(System.getProperty("headlessBrowser"), "true")) {
                    options.addArguments("--headless=new");
                }
            }
            options.addArguments("--remote-allow-origins=*");
            options.addArguments(chromeOptions.get());
            LOG.debug("Chrome Options are set: {}", chromeOptions);
            String envProxyDetails = environment.get(proxyDetails);
            LOG.debug("Proxy_Details : {}", envProxyDetails);
            setDriver(new ChromeDriver(options));
            LOG.info("Chrome driver is launched");
            getDriver().manage().window().setSize(new Dimension(1920, 1200));
            LOG.debug("Browser Window is maximized");

        } else if (driverType.equalsIgnoreCase("IE")) {
            InternetExplorerOptions capabilities = new InternetExplorerOptions().setPageLoadStrategy(PageLoadStrategy.NORMAL);
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            String envProxyDetails = environment.get(proxyDetails);
            LOG.debug("Proxy_Details : {}", envProxyDetails);
            setDriver(new InternetExplorerDriver(capabilities));
            LOG.debug("IE driver is launched");
            getDriver().manage().window().maximize();
            LOG.debug("Browser Window is maximized");
            try {

                Alert alert = getDriver().switchTo().alert();
                alert.dismiss();
                //if alert present, accept and move on.
            } catch (NoAlertPresentException e) {
                LOG.error(e.getMessage());
            }
        }
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        LOG.debug("ImplictWait time is set to 30 sec");
        return getDriver();
    }

    /**
     * This method is used to close the browser
     */
    public void closeBrowser() {
        getDriver().close();
    }

}