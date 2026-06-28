package com.solstice.staf.vault;

import com.bettercloud.vault.VaultException;

import java.util.Map;

/**
 * This is the vault secret engine interface containing methods that are common across all the secret engines.
 * This class contain a method such as writeSecret and readSecret etc.
 */
public interface SecretEngine {

    /**
     * This method is used to write secrets to the vault.
     *
     * @param path       - path to the secret engine to write secrets to the vault
     * @param secrets    - map of secrets
     * @throws VaultException - an exception thrown if unable to write secrets to the vault
     */
    public void writeSecret(String path, Map secrets) throws VaultException;

    /**
     * This method is used to read the secret at a specified path.
     *
     * @param path       - path to the secret to read the secret at a specified path
     * @param key        - key of the secret to read the secret at a specified path
     * @return           - value as the secret in the form of string
     * @throws VaultException - an exception thrown if unable to write secrets to the vault
     */
    public String readSecret(String path, String key) throws VaultException;
}
