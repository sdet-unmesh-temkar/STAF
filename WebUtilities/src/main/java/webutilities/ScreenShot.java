package webutilities;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import generalutilities.ThreadLocalRegistry;
import jakarta.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Point;
import generalutilities.ReportAndLogging;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods related to take ScreenShot.
 */
public class ScreenShot {

	private static ThreadLocal<Scenario> scenario = new ThreadLocal<>();

	private int imgNo = 0;
	private String startImgNo = "1";
	private static final String PATH_DELIMETER = "//";
	private static final String STEP_COMPLETE = "stepComplete";
	private static final String IMAGE_TYPE = "image/png";
	private static final Logger LOG = LoggerFactory.getLogger(ScreenShot.class);
	ReportAndLogging reportAndLogging = new ReportAndLogging();

	/**
	 * This method used to get the Scenario name
	 *
	 * @return - scenario name
	 */
	public Scenario getScenario() {
		ThreadLocalRegistry.register(scenario);
		return scenario.get();
	}

	/**
	 * This method is used to set the Scenario name
	 *
	 * @param gscenario - set Scenario name
	 */
	public void setScenario(Scenario gscenario) {
		scenario.set(gscenario);
	}


	/**
	 * This method is used to cleanup the threadlocals
	 *
	 */
	public void unload() {
		scenario.remove();
	}

	/**
	 * This method is used to get Screenshot Folder path
	 *
	 * @return String - get path of Screenshot Folder
 	 */
	private String getScreenshotFolder() {
		String filepath = System.getProperty("user.dir") + "//target//ScreenShot";
		return (filepath + PATH_DELIMETER + getScenario().getName() + PATH_DELIMETER);
	}

	/**
	 * This method has implementation to get screenshot of GUI elements/page during test case execution. Takes the screenshot automatically when a test case fails but have to be explicitly called for passed scenarios
	 *
	 * @param driver                - instance of browser to capture screenshot of GUI elements/page
	 * @param elementName           - for which screenshot has to be taken
	 * @throws NullPointerException - an exception thrown if unable to take screenshot of GUI elements/page
	 */
	public void takeSnapShot(@Nonnull WebDriver driver, String elementName) throws NullPointerException {
		try {
			TakesScreenshot scrShot = ((TakesScreenshot) driver);
			var srcfile = scrShot.getScreenshotAs(OutputType.FILE);

			var destfile = new File(getScreenshotFolder() + imgNo + elementName + ".png");
			FileUtils.copyFile(srcfile, destfile);
			if (startImgNo.equalsIgnoreCase(STEP_COMPLETE))
				startImgNo = imgNo + "";
			imgNo++;
			LOG.info("Scenario: {} ", scenario.get().getName());
			LOG.info("img no: {}", imgNo);
			reportAndLogging.addScreenshotToReport(destfile.toString());

			final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			scenario.get().attach(screenshot, IMAGE_TYPE, "");
			LOG.info("Screenshot captured {}", elementName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * This method has implementation to get partial screenshot of GUI elements/page during test case execution. Takes the screenshot automatically when a test case fails but have to be explicitly called for passed scenarios
	 *
	 * @param driver       - instance of browser to capture screenshot of GUI elements/page
	 * @param elementName  - element whose screenshot has to be taken
	 * @param positionX    - X-axis position of element to capture the partial screenshot
	 * @param positionY    - Y-axis position of element to capture the partial screenshot
	 * @param width        - get partial screenshot of GUI elements/page
	 * @param height       - get partial screenshot of GUI elements/page
	 * @throws IOException - an exception thrown if unable to take screenshot of GUI elements/page
	 */
	public void takePartialScreenShot(WebDriver driver, String elementName, int positionX, int positionY, int width, int height) throws IOException
	{
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		var destfile = new File(getScreenshotFolder() + imgNo + elementName + ".png");
		BufferedImage partialImage = ImageIO.read(src).getSubimage(positionX, positionY, width, height);
		takeScreenshot(partialImage, src, destfile);
		LOG.info("Screenshot captured {}", elementName);

	}

	/**
	 * This method has implementation to get partial screenshot of GUI elements/page during test case execution. Takes the screenshot automatically when a test case fails but have to be explicitly called for passed scenarios
	 *
	 * @param driver       -  instance of browser to capture partial screenshot of GUI elements/page
	 * @param elementName  -  to the element whose screenshot has to be taken
	 * @param elementXpath -  locators to identify the web element to get partial screenshot of GUI elements/page
	 * @throws IOException -  exception thrown if unable to take partial screenshot of GUI elements/page
	 */
	public void takePartialScreenShot(WebDriver driver, String elementName, String elementXpath) throws IOException {

		WebElement element = driver.findElement(By.xpath(elementXpath));
		var destfile = new File(getScreenshotFolder() + imgNo + elementName + ".png");
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		//location of webelement
		Point location = element.getLocation();
		Dimension size = element.getSize();
		// Dimension of element
		int x = location.getX();
		int y = location.getY();
		int w = size.getWidth();
		int h = size.getHeight();
		// Image Crop
		BufferedImage cropImage = ImageIO.read(src).getSubimage(x, y, w, h);
		takeScreenshot(cropImage, src, destfile);
	}

	/**
	 * This method has implementation to take screenshot of GUI elements/page during test case execution. Takes the screenshot automatically when a test case fails but have to be explicitly called for passed scenarios
	 *
	 * @param cImage        -  capture screenshot of GUI elements/page
	 * @param src           -  store screenshot to source folder
	 * @param destfile      -  store screenshot to destination folder
	 * @throws IOException  -  an exception thrown if unable to take screenshot of GUI elements/page
	 */
	public void takeScreenshot(BufferedImage cImage, File src, File destfile) throws IOException {
		ImageIO.write(cImage, "png", src);
		FileUtils.copyFile(src, destfile);

		if (startImgNo.equalsIgnoreCase(STEP_COMPLETE)) {
			startImgNo = imgNo + "";
		}
		imgNo++;
		reportAndLogging.addScreenshotToReport(destfile.toString());

		var baos = new ByteArrayOutputStream();
		ImageIO.write(cImage, "png", baos);
		final byte[] screenshot = baos.toByteArray();
		scenario.get().attach(screenshot, IMAGE_TYPE, "");

	}
}