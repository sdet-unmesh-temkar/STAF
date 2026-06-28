package com.solstice.staf.vault;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.json.JsonObject;
import com.bettercloud.vault.response.LogicalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
/**
 * This class contains methods related to key and value secret engine. This class implements SecretEngine interface.
 * This class consist of a methods such as writeSecret, readSecret, getAllSecretsAsMap, getAppList etc.
 */
public class KVSecretEngine implements SecretEngine {
    private static final Logger log = LoggerFactory.getLogger(KVSecretEngine.class);
    private final Vault vault;

    /**
     * Public constructor used to instantiate vault.
     *
     * @param vault - to instantiate vault
     */
    public KVSecretEngine(Vault vault) {
        this.vault = vault;
    }

    /**
     * This method is used to write secrets into the KV secret engine.
     *
     * @param path    - path of the secret to write secrets into the KV secret engine
     * @param secrets - map containing secret pairs to write secrets into the KV secret engine
     */
    @Override
    public void writeSecret(String path, Map secrets) throws VaultException {
        log.info("***Started writing secret into vault***");
        try {
            final LogicalResponse writeResponse = vault.logical().write(path, secrets);
            log.info("Response Data : {}", writeResponse.getData());
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while writing secret into the vault.Please check the stack trace.");
        }
        log.info("***Secret {} written into vault***",secrets);
    }

    /**
     * This method is used to read secret from key value secret engine.
     *
     * @param path      - path of the secret to read secret from key value secret engine
     * @param secretKey - the key of the secret which is used to identify the particular secret
     * @return          - value of the secret based on the key
     */
    @Override
    public String readSecret(String path, String secretKey) throws VaultException {
        log.info("***Started reading secret {} from the vault***",secretKey);
        String secretValue = null;
        try {
            secretValue = vault
                    .logical()
                    .read(path)
                    .getData()
                    .get(secretKey);
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while reading secret from the vault. Please check the stack trace.");
        }
        log.info("***Secret {} is read from the vault***",secretKey);
        return secretValue;
    }

    /**
     * This method is used to get the KV secrets and store into Map.
     *
     * @param path       - path of the secret to get the map of KV secrets
     * @return           - map of secrets
     * @throws VaultException - an exception thrown if unable to get the map of KV secrets.
     */
    public Map<String, String> getAllSecretsAsMap(String path) throws VaultException {
        log.info("***Getting all secrets for the app {} as Map***",path);
        Map<String, String> keyValueMap = null;
        try {
            keyValueMap = vault
                    .logical()
                    .read(path)
                    .getData();
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while getting all secrets from the vault. Please check the stack trace.");
        }
        log.info("***All secrets retrieved for the app {} as Map***",path);
        return keyValueMap;
    }

    /**
     * This method is used to get all the KV secrets at a path in the form of JSON object
     *
     * @param path       - path of the secrets to get all the KV secrets
     * @return           - JsonObject containing all secrets of an app in the form of JSON
     * @throws VaultException - an exception thrown if unable get all the KV secrets at a path in the form of JSON object
     */
    public JsonObject getAllSecretsAsJSON(String path) throws VaultException {
        log.info("***Getting all secrets for an app as JSON***");
        JsonObject keyValueJson = null;
        try {
            keyValueJson = vault
                    .logical()
                    .read(path)
                    .getDataObject();
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while getting all secrets from the vault. Please check the stack trace.");
        }
        log.info("***All secrets retrieved for an app as JSON***");
        return keyValueJson;
    }

    /**
     * This method is used to get the list of all the apps for which KV secrets is configured.
     *
     * @param path       - parent path to the apps
     * @return           - JSONObject containing app names
     * @throws VaultException - an exception thrown if unable to get the list of all the apps for which KV secrets is configured.
     */
    public JsonObject getAppJson(String path) throws VaultException {
        log.info("***Getting all apps as JSON for the selected environment***");
        JsonObject appJson = null;
        try {
            appJson = vault.logical().list(path).getDataObject();
        } catch (VaultException e) {
            log.error("Exception {}", e.getMessage());
            throw new VaultException("Something went wrong while getting all apps from the vault. Please check the stack trace.");
        }
        log.info("***All apps retrieved as JSON for the selected environment***");
        return appJson;
    }
    /**
     * This method is used to get the list of all the apps for which KV secrets is configured.
     *
     * @param path       - parent path to the apps to get the list of all the apps
     * @return           - app names in the form of list
     * @throws Exception - an exception thrown if unable to get the list of all the apps for which KV secrets is configured.
     */
    public List<String> getAppList(String path) throws VaultException {
        log.info("***Getting all apps as List for the selected environment***");
        List<String> appList = null;
        try {
            appList = vault.logical().list(path).getListData();
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while getting all apps from the vault. Please check the stack trace.");
        }
        log.info("***All apps retrieved as List for the selected environment***");
        return appList;
    }
}
