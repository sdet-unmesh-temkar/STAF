package generalutilities;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a method file related to specific functionality such as read property file,read file as a string etc.
 */
public class FileSpecificUtilities{
	private static final Logger LOG = LoggerFactory.getLogger(FileSpecificUtilities.class);
	
	/**
	 * This method will read property file from the specified paths.
	 *
	 * @param fileName  - to read a property file from the specified paths
	 * @return          - property file from the specified paths
	 */
	public Properties readPropertyFile(String fileName) {
		 String filePath = null;
		 InputStreamReader inputStreamReader = null;
		 try {
		 	inputStreamReader = new InputStreamReader(this.getClass().getResourceAsStream("/PropertyFiles/"+fileName), StandardCharsets.UTF_8);
		 	filePath = IOUtils.toString(inputStreamReader);
		 } catch (IOException e1) {
			 LOG.error(e1.getMessage());
		 }
		 var property = new Properties();
		 try {
		 	property.load(new StringReader(filePath));
		 } catch (IOException e) {
			 LOG.error(e.getMessage());
		 }
		 return property;
	}

	/**
	 * This method will read property file as a string from the specified path.
	 *
	 * @param filePath - path of the file to read property file as a string
	 * @return String  - data from the readable file
	 */
	public String readFileAsString(String filePath) {
		String data = "";
		try {
			data = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return data;
	}
}