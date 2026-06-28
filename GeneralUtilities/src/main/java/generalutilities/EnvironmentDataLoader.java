package generalutilities;

import com.solstice.staf.vault.KVSecretEngine;
import com.solstice.staf.vault.SecretEngine;
import com.solstice.staf.vault.SecretEngineType;
import com.solstice.staf.vault.VaultClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a singleton class for environment data loader across scenarios.
 * This class contain a method such as get/set environment, fetchEnvironmentDetails, fetchEnvDetailsFromVault, getSecretsMapWithAppNameAppendedInKey etc.
 */
public class EnvironmentDataLoader {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentDataLoader.class);
    private HashMap<String, String> environment = new HashMap<>();
    private static final String SLASH_STRING = "/";

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private EnvironmentDataLoader() {
    }

    /**
     * This method is used to get environment as HasMap.
     *
     * @return HashMap - environment
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * This method is used to set the data of the environment by using key and value pair.
     *
     * @param key   - to set the data of the environment
     * @param value - its value to set the data
     */
    public void setEnvironment(String key, String value) {
        environment.put(key, value);
    }

    /**
     * Private static inner class which is loaded when getInstance() is called for the first time.
     */
    private static class EnvironmentDataLoaderInitializer {
        private static final EnvironmentDataLoader instance = new EnvironmentDataLoader();
    }

    /**
     * This method will return the singleton instance of EnvironmentDataLoader class.
     *
     * @return - singleton instance of EnvironmentDataLoader
     */
    public static EnvironmentDataLoader getInstance() {
        return EnvironmentDataLoaderInitializer.instance;
    }

    /**
     * This method is used to fetch environment details from the xml file.
     *
     * @param fileName                      - name of the file to fetch environmental details
     * @throws ParserConfigurationException - this is an exception indicates a serious configuration error.
     * @throws SAXException                 - this is an exception class can contain basic error or warning information from either the XML parser or the application
     */
    public void fetchEnvironmentDetails(String fileName) throws ParserConfigurationException, SAXException {
        String environmentsExcel;

        try {
            String curwd = System.getProperty("user.dir");
            String environmentExcelFilePath = File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "XMLFiles" + File.separator + "environments" + File.separator + fileName + ".xml";
            File environmentExcelFile = new File(curwd + environmentExcelFilePath);
            if(!environmentExcelFile.exists()) {
                curwd = curwd.substring(0, curwd.lastIndexOf(File.separator));
            }
            environmentsExcel = curwd + environmentExcelFilePath;

            //Get the Column Index for the ENVIRONMENT Column
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //API to obtain DOM Document instance
            LOG.debug("xml file path is : {}", environmentsExcel);
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document xml = builder.parse(new File(environmentsExcel));
            Node envNode = xml.getFirstChild();
            NodeList appNodeList = envNode.getChildNodes();
            Node appNode;
            Node appSubNode;
            for (var j = 0; j < appNodeList.getLength(); j++) {
                appNode = appNodeList.item(j);
                NodeList nodeChildList = appNode.getChildNodes();
                for (var i = 0; i < nodeChildList.getLength(); i++) {
                    appSubNode = nodeChildList.item(i);
                    String childTextContent = appSubNode.getTextContent();
                    String childNodeName = appSubNode.getNodeName();
                    if (childTextContent.contains("\t")) {
                        childTextContent = childTextContent.replace("\t", " ");
                        childTextContent = childTextContent.replaceAll("\\s+", " ");
                    }
                    childTextContent = childTextContent.trim();
                    setEnvironment(childNodeName, childTextContent);
                }
            }
            LOG.info("***Loaded configuration information from XML file for environment {}", fileName);
        } catch (FileNotFoundException e) {
            LOG.info("***Env XML file for following environment not found {}",fileName );
        } catch (IOException e) {
            LOG.error("Exception {}", e.getMessage());
        }
    }

    /**
     * This method is used to fetch secrets and config information from hashicorp vault.
     *
     * @param path       - root path is passed initially to fetch secrets and config information from hashicorp vaul
     * @throws Exception - an exception thrown if unable to fetch secrets and config information from hashicorp vault.
     */
    public void fetchEnvDetailsFromVault(String path) throws Exception {
        LOG.info("****Started fetching environment details from vault****");
        SecretEngine secretEngine = new VaultClient().authWithAppRole().getSecretsEngine(SecretEngineType.KEYVALUE);
        KVSecretEngine kvSecretEngine = (KVSecretEngine) secretEngine;
        List<String> rootPathList = kvSecretEngine.getAppList(path);
        for (String app : rootPathList) {
            if (app.endsWith(SLASH_STRING)) {
                fetchEnvDetailsFromVault(path + SLASH_STRING + StringUtils.removeEnd(app, SLASH_STRING));
            } else {
                Map<String, String> secretsMap = kvSecretEngine.getAllSecretsAsMap(path + SLASH_STRING + app);
                environment.putAll(getSecretsMapWithAppNameAppendedInKey(secretsMap, path + SLASH_STRING + app));
            }
        }
        LOG.info("***Completed fetching of environment details from vault***");
        String keySetAsString = environment.keySet().toString();
        LOG.info("Environment Key Set : {}", keySetAsString);
    }

    /**
     * This method is used to get secret map with app name append in each key.
     *
     * @param secretsMap - map containing all secrets of an app
     * @param app        - app name for which secrets is configured
     * @return           - modifiedSecretsMap
     */
    private Map<String, String> getSecretsMapWithAppNameAppendedInKey(Map<String, String> secretsMap, String app) {
        LOG.info("***Started appending key name with app name***");
        Map<String, String> modifiedSecretsMap = new HashMap<>();
        for (String key : secretsMap.keySet()) {
            String keyPath = app + SLASH_STRING + key;
            int firstSlashIndex = keyPath.indexOf(SLASH_STRING);
            int secondSlashIndex = keyPath.indexOf(SLASH_STRING, firstSlashIndex + 1);
            modifiedSecretsMap.put(keyPath.substring(secondSlashIndex+1), secretsMap.get(key));
        }
        LOG.info("***Completed appending of key name with app name***");
        return modifiedSecretsMap;
    }
}
