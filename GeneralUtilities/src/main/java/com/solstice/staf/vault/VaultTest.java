package com.solstice.staf.vault;


import com.bettercloud.vault.VaultException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class consist of vault test related methods.
 * This class contain methods such as test apps list,test secret list,fetch env details from Vault etc.
 */
public class VaultTest {
    private static String devPath = "kv-staf/dev";
    private static String qaPath = "kv-staf/QA";
    private static List<String> devAppList = new ArrayList();

    private static Map<String, String> allSecrets = new HashMap<>();
    private static String infoMsg;
    private static final Logger log = LoggerFactory.getLogger(VaultTest.class);
    public static void main(String[] args) throws Exception {
        fetchEnvDetailsFromVault("kv-staf/E2E");
    }

    /**
     * This method has the implementation of test app list.
     *
     * @param kvSecretEngine - key value of the secret engine to test app list
     * @throws VaultException     - an exception is an event which occurs during the execution of a program, that disrupts the normal flow of the program's instructions
     */
    public static void testAppList(KVSecretEngine kvSecretEngine) throws VaultException {
        devAppList = kvSecretEngine.getAppList(devPath);
        List<String> qaAppList = kvSecretEngine.getAppList(qaPath);
        Assert.assertTrue(devAppList.contains("BRITEBILL_EBPA"));
        Assert.assertTrue(devAppList.contains("app1"));
        Assert.assertTrue(devAppList.contains("app2"));
        Assert.assertTrue(devAppList.contains("app3"));
        Assert.assertTrue(qaAppList.contains("BRITEBILL_EBPA_VISION"));
        infoMsg="Dev App List " + devAppList;
        log.info(infoMsg);
        infoMsg="QA App list " + qaAppList;
        log.info(infoMsg);
    }

    /**
     * This method has the implementation of test secret list.
     *
     * @param kvSecretEngine - key value of the secret engine to test secret list
     * @throws VaultException     - an exception is an event which occurs when user unable to fetch secret from vault
     */
    public static void testSecretList(KVSecretEngine kvSecretEngine) throws VaultException {
        for (String app : devAppList) {
            infoMsg="DevAppPath " + devPath + "/" + app;
            log.info(infoMsg);
            allSecrets.putAll(kvSecretEngine.getAllSecretsAsMap(devPath + "/" + app));
        }
        infoMsg="All secrets " +allSecrets.toString();
        log.info(infoMsg);
    }

    /**
     * This method has the implementation of test secret details.
     */
    public static void testSecretDetails() {
        for (Map.Entry<String,String> entry : allSecrets.entrySet()) {
            String key = entry.getKey();
            infoMsg=key + " " + allSecrets.get(key);
            log.info(infoMsg);
        }
    }

    /**
     * This method has the implementation to fetch environmental details from vault.
     *
     * @param path          - path to the secret to fetch environmental details from vault
     * @throws Exception    - an exception is an event which occurs when user unable to fetch environmental details from vault
     */
    public static void fetchEnvDetailsFromVault(String path) throws Exception {
        SecretEngine secretEngine = new VaultClient().authWithAppRole().getSecretsEngine(SecretEngineType.KEYVALUE);
        KVSecretEngine kvSecretEngine = (KVSecretEngine) secretEngine;
        var infoMsg = "****Traversed path******" + path;
        log.info(infoMsg);
        List<String> rootPathList = kvSecretEngine.getAppList(path);
        infoMsg="Root Path List " + rootPathList;
        log.info(infoMsg);
        for (String app : rootPathList) {
            if (app.endsWith("/")) {
                fetchEnvDetailsFromVault(path + "/" + StringUtils.removeEnd(app, "/"));
            } else {
                Map<String, String> secretsMap = kvSecretEngine.getAllSecretsAsMap(path + "/" + app);
                allSecrets.clear();
                allSecrets.putAll(getSecretsMapWithAppNameAppendedInKey(secretsMap, path + "/" + app));
                infoMsg="**********Secret for App********  " + app;
                log.info(infoMsg);
                log.info("***************************");
                log.info("All Secrets From Vault: {}", allSecrets);
            }
        }
    }

    /**
     * This method has the implementation to get secrets map with apps name and appended in key.
     *
     * @param secretsMap - map containing secret pairs to get secrets map with apps name and appended in key
     * @param app        - name of the apps
     * @return           - modifiedSecretsMap
     */
    private static Map<String, String> getSecretsMapWithAppNameAppendedInKey(Map<String, String> secretsMap, String app) {
        var delimeter = "/";
        Map<String, String> modifiedSecretsMap = new HashMap<>();
        for (Map.Entry<String,String> entry : secretsMap.entrySet()) {
            String key = entry.getKey();
            String keyPath = app + delimeter + key;
            modifiedSecretsMap.put(keyPath.substring(12), secretsMap.get(key));
        }
        return modifiedSecretsMap;
    }
}
