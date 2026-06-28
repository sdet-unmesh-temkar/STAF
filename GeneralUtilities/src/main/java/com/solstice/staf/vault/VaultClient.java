package com.solstice.staf.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a vault client class containing methods related to vault connectivity and configuration.
 * This class contain a method such as getSecretsEngine, authWithAppRole, getVaultConfig etc.
 */
public class VaultClient {
    private static final Logger log = LoggerFactory.getLogger(VaultClient.class);
    private static final String VAULT_ROLE_ID = "Vault_ROLEID";
    private static final String VAULT_SECRET_ID = "Vault_SECRETID";

    static{
        try {
            if (System.getProperty(VAULT_ROLE_ID) == null)
                System.setProperty(VAULT_ROLE_ID, System.getenv(VAULT_ROLE_ID));
            if (System.getProperty(VAULT_SECRET_ID) == null)
                System.setProperty(VAULT_SECRET_ID, System.getenv(VAULT_SECRET_ID));
        }
        catch (NullPointerException e){
            log.warn("** Please set Vault_ROLEID and Vault_SECRETID in either system variable or system property to connect vault**");
        }
    }

    private static final String VAULTURL = "https://vault.svc.prod.cicdhub.sol-vf.de/";
    private static final String ROLEID = System.getProperty(VAULT_ROLE_ID);
    private static final String SECRETID = System.getProperty(VAULT_SECRET_ID);
    private final Vault vault;
    private VaultConfig config;


    /**
     * public constructor used to instantiate VaultConfig and Vault.
     *
     * @throws VaultException - an exception thrown if unable to instantiate VaultConfig and Vault.
     */
    public VaultClient() throws VaultException {
        this.config = getVaultConfig();
        this.vault = new Vault(config);
    }

    /**
     * This method is used to configure vault config class.
     *
     * @return           - instance of VaultConfig
     * @throws VaultException - an exception thrown if unable to configure vault config class.
     * */
    private VaultConfig getVaultConfig() throws VaultException {
        try {
            config = new VaultConfig()
                    .engineVersion(1)
                    .address(VAULTURL)
                    .build();
        } catch (VaultException e) {
            throw new VaultException("Something went wrong while configuring vault***");
        }
        log.info("***VaultConfig object initialized***");
        return config;
    }
    /**
     * This method is used to authenticate using app role mechanism.
     *
     * @return - VaultClient object
     */
    public VaultClient authWithAppRole() {
        try {
            String clientToken = vault.auth().loginByAppRole(ROLEID, SECRETID).getAuthClientToken();
            config.token(clientToken).build();
        } catch (VaultException e) {
            log.error("Exception {}", e.getMessage());
            log.error("**Something went wrong while authenticating with app role. Please chek the stack trace.***");
        }
        log.info("***Authentication with app role is done.***");
        return this;
    }

    /**
     * This method will return the instance of specified secret engine.
     *
     * @param secretEngineType - the type of secrete engine e.g. KEY VALUE, PKI, etc
     * @return                 - the instance of specified secret engine
     */
    public SecretEngine getSecretsEngine(SecretEngineType secretEngineType) {
        log.info("***Configuring selected secret engine***");
        if (secretEngineType.equals(SecretEngineType.KEYVALUE)) {
            log.info("***KV secret engine configured***");
            return new KVSecretEngine(vault);
        } else {
            log.info("No matching secret engine found***");
            return null;
        }
    }
}
