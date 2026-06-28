package staf.kafkautilities.config;

import java.util.Map;

/**
 * This class contains methods of Admin configurations
 * This class use to read secrets of kafka admin client
 */
public class AdminConfig {


    /**
     * This method is used to load kafka admin configuration from vault or property file.
     * @param path        - path of vault secret path or property file name
     * @return secrets    - returns admin kafka credentials from the provided path
     * @throws Exception  - an exception thrown if unable to instantiate VaultConfig and Vault
     */
    public Map<String, String> getAdminConfigs(String path) throws Exception {
        return new KafkaConfig().getKafkaSecrets(path);
    }
}
