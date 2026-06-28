package generalutilities;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a singleton class for sharing data across scenarios on a application Level. If you want to share data across different scenarios then this class can be used.
 * This class perform different operation on database such as setAutomationDBData/getAutomationDBData, isContainsAutomationDBData, set/get data etc.
 */
    public class ApplicationContext {
    private Map<String, Object> appDataSet = new HashMap<>();
    private Map<String, String> appAutomationDBDataSet = new HashMap<>();

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private ApplicationContext() {
    }

    /**
     * This method is used to set apps data.
     *
     * @param key   - key of the secret to set data
     * @param value - actual value of the key to set data
     */
    public void setData(String key, Object value) {
        appDataSet.put(key, value);
    }

    /**
     * This method is used to get apps data.
     *
     * @param key - key of the secret to get data
     * @return    - Object
     */
    public Object getData(String key) {
        return appDataSet.get(key);
    }

    /**
     * This method is used to check whether application data set contains data.
     *
     * @param key - key of the secret to check whether application data set contains data
     * @return    - true/false (boolean)
     */
    public Boolean isContainsData(String key) {
        return appDataSet.containsKey(key);
    }

    /**
     * This method is used to set automation database data by using key and value.
     *
     * @param key   - key of the secret to set data
     * @param value - actual value of the key to set data
     */
    public void setAutomationDBData(String key, String value) {
        appAutomationDBDataSet.put(key, value);
    }

    /**
     * This method is used to get automation database data.
     *
     * @param key   - key of the secret to get data
     * @return      - appAutomationDBDataSet
     */
    public String getAutomationDBData(String key) {
        return appAutomationDBDataSet.get(key);
    }

    /**
     * This method is used to check whether automation database contains data.
     *
     * @param key   - key of the secret to check whether automation database contains data
     * @return      - true/false (boolean)
     */
    public Boolean isContainsAutomationDBData(String key) {
        return appAutomationDBDataSet.containsKey(key);
    }

    /**
     * This method is used to set automation database keys.
     *
     * @return keySet - appAutomationDBDataSet
     */
    public Set<String> getKeysAutomationDBData() {
        return appAutomationDBDataSet.keySet();
    }


    /**
     * This method is used to get application dataset keys.
     *
     * @return keySet - ApplicationDataSet
     */
    public Set<String> getKeysAppDataSet() {
        return appDataSet.keySet();
    }

    /**
     * Private Static inner class which is loaded when getInstance() is called for the first time.
     */
    private static class AppContextInitializer {
        private static final ApplicationContext instance = new ApplicationContext();
    }

    /**
     * This method will return the singleton instance of ApplicationContext class.
     *
     * @return - AppContextInitializer
     */
    public static ApplicationContext getInstance() {
        return AppContextInitializer.instance;
    }
    }
