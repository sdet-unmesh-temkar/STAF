package com.solstice.staf.config;

import com.google.common.base.Enums;
import com.solstice.staf.vault.VaultEnv;
import generalutilities.EnvironmentDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * This class contains static block which is used to load configuration information from xml file and vault.
 * As the config information has to be loaded just once,hence keeping it in static block.
 */
public class EnvConfig {
    private static final Logger log = LoggerFactory.getLogger(EnvConfig.class);

    static {
        String env = System.getProperty("env");
        log.info("***Loading env properties for {} environment***",env);
        try {
            log.info("***Loading configuration information from XML file for {} environment.",env);
            EnvironmentDataLoader.getInstance().fetchEnvironmentDetails(env);
        } catch (ParserConfigurationException | SAXException e) {
            log.info("***Unable to load configuration information from XML file for {} environment. This may be due to env mismatch or some exception",env);         log.error(e.getMessage());
        }
        if (Enums.getIfPresent(VaultEnv.class, env.toUpperCase()).isPresent()) {
            log.info("***Loading configuration information from vault for {} environment.",env);
            try {
                EnvironmentDataLoader.getInstance().fetchEnvDetailsFromVault("kv-staf/"+env.toUpperCase());
            } catch (Exception e) {
                log.error("Exception {}", e.getMessage());
            }
            log.info("***Loaded configuration information from vault for {} environment.",env);
        } else {
            log.info(env,"*** {} environment is not available in vault. Hence skipping the configuration from vault***");
        }
    }
}
