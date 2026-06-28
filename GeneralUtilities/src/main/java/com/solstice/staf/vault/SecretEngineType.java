package com.solstice.staf.vault;

/**
 * This enum contains group of constants for secret engine types in hashicorp vault.
 */

public enum SecretEngineType {

    KEYVALUE, PKI, AWS, DATABASE, CONSUL;
}
