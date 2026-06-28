package reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Provides helper methods for checking existence and reading contents of auxiliary report files such as additionalInfo.txt and
 * interframeworkTestSuiteRunID.txt under the target directory.
 */
public class ReportUtils {
    private static final String TARGET = "target";
    private static final Logger LOG = LoggerFactory.getLogger(ReportUtils.class);

    // Private constructor to prevent instantiation
    private ReportUtils() {
    }

    /**
     * Determines whether the file {@code target/additionalInfo.txt} exists.
     *
     * @return {@code true} if {@code additionalInfo.txt} is present in the target directory;
     *         {@code false} otherwise
     */
    public static boolean fileExists() {
        return Files.exists(Paths.get("target/additionalInfo.txt"));
    }

    /**
     * Reads key–value pairs from {@code target/additionalInfo.txt}, where each line is expected in the format {@code key,value}. If a key appears multiple times,
     * its values are concatenated with an HTML line break ({@code <br/>}).
     *
     * @return a map of keys to their aggregated values
     * @throws UncheckedIOException if an I/O error occurs while reading the file
     */
    public static Map<String, String> readAdditionalInfoMap() {
        String fileName = TARGET + File.separator + "additionalInfo.txt";
        Map<String, String> additionalInfoMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2); // Split into 2 parts: key and the rest
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String values = parts[1].trim();
                    if (additionalInfoMap.containsKey(key)) {
                        String existingValue = additionalInfoMap.get(key);
                        additionalInfoMap.put(key, existingValue + "<br/>" + values);
                    } else {
                        additionalInfoMap.put(key, values);
                    }

                }
            }
            return additionalInfoMap;

        } catch (IOException e) {
            throw new UncheckedIOException("Error reading…" + e.getMessage(), e);
        }
    }

    /**
     * Reads the contents of {@code target/interframeworkTestSuiteRunID.txt} and parses it into a map. The file is expected to contain a Java-style map literal
     * (for example, {@code {key1=value1, key2=value2}}). Whitespace and braces are stripped before splitting on commas and equals signs.
     *
     * @return a map of interframework test keys to their run IDs,
     *         or an empty map if the file does not exist or an error occurs
     */
    public static Map<String, String> readInterframeworkFileToMap() {
        String filePath = System.getProperty("user.dir") + File.separator + TARGET + File.separator + "interframeworkTestSuiteRunID.txt";
        File file = new File(filePath);
        if (file.exists()) {
            try {
                String fileContent = new String(Files.readAllBytes(file.toPath()));
                fileContent = fileContent.replaceAll("[{}\\s]", "");
                String[] keyValuePairs = fileContent.split(",");
                Map<String, String> runResults = new HashMap<>();

                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    runResults.put(entry[0], entry[1]);
                }
                LOG.info("Contents in interframeworkTestSuiteRunID.txt {} ", runResults);
                return runResults;

            } catch (IOException e) {
                LOG.info("Error reading the file: {} ", e.getMessage());
            }
        }
        LOG.debug("InterframeworkTestSuiteRunID.txt file does not exists");
        return Collections.emptyMap();
    }
}
