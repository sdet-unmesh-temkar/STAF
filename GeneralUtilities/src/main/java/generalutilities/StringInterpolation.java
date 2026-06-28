package generalutilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringInterpolation {
    private static final Logger LOG = LoggerFactory.getLogger(StringInterpolation.class);

    /**
     * This method fetches and replaces the value for the key from testcontext or application or vault for the syntax ${{testContext.key}} ${{applicationContext.key}} ${{vault.key}}
     *
     * @param key - This is the key for which value has to be picked from testContext or applicationContext or vault
     * @return - It returns the replaced value instead of key, fetched from testContext or applicationContext Eg: 10${{testContext.key}}abc returns 10valueforTestContextabc
     */

    public Object stringInterpolation(String key) {
        String patternString = "\\$\\{\\{(.*?)}}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(key);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            // Append text before the match
            result.append(key, lastEnd, matcher.start());
            // Fetch the key and the corresponding replacement
            String keys = matcher.group(1);
            Object interpolatedValue = getValue(keys);
            if (!(interpolatedValue instanceof String)) {
                return getValue(keys);
            }
            String replacement = (String) interpolatedValue;
            // Append the replacement value
            result.append(replacement);
            // Update the end position of the last match
            lastEnd = matcher.end();
        }
        // Append the remaining part of the input string
        result.append(key.substring(lastEnd));
        LOG.info("Value after string interpolation is: {}", result);
        return result.toString();
    }

    /**
     * This private method fetches value from textContext or applicationContext or vault
     *
     * @param key :This is the key for which value has to be picked from testContext or applicationContext or vault
     * @return: It returns string value that is fetched from textContext or applicationContext or vault
     */
    private Object getValue(String key) {
        Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
        if (key.toLowerCase().contains("testcontext")) {

            String[] splitString = key.split("\\.");
            Object data;
            data = TestContext.getInstance().getProperty(splitString[1].trim());
            var stringValue = String.valueOf(TestContext.getInstance().getProperty(splitString[1].trim()));
            if (stringValue == null) {
                LOG.debug(splitString[1], " value is NULL in ", splitString[0]);
                throw new NullPointerException(splitString[1] + " value is NULL in " + splitString[0]);
            }
            LOG.info(splitString[1], " value from  ", splitString[0], "is", data);
            return data;

        }
        if (key.toLowerCase().contains("applicationcontext.")) {
            String[] splitString = key.split("\\.");
            Object data = ApplicationContext.getInstance().getData(splitString[1].trim());
            var stringValue = ApplicationContext.getInstance().getData(splitString[1].trim());
            if (stringValue == null) {
                LOG.debug(splitString[1], " value is NULL in  ", splitString[0]);
                throw new NullPointerException(splitString[1] + " is Null");
            }

            LOG.info(splitString[1], " value from ", splitString[0], "is", data);
            return data;

        }
        if (key.toLowerCase().contains("vault.")) {
            String[] splitString = key.split("\\.");
            String data = environment.get(splitString[1].trim());
            if (data.equals("")) {
                LOG.debug(splitString[1], "  value is NULL in ", splitString[0]);
                throw new NullPointerException(splitString[1] + " value is Null in " + splitString[0]);
            }
            LOG.info(splitString[1], " value from ", splitString[0], "is", data);
            return data;

        } else
            return key;
    }

}
