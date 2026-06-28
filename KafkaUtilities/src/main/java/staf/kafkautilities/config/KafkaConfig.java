package staf.kafkautilities.config;

import com.google.common.base.Enums;
import com.solstice.staf.vault.*;
import generalutilities.FileSpecificUtilities;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods of Kafka Producer/Consumer configurations
 * This class contains methods to load Kafka Producer/Consumer configuration.
 */
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    /**
     * This method is used to read the secrets from vault and stored in the provided path
     *
     * @param path        - vault path where Kafka secretes are stored
     * @return secrets    - map for all Kafka secrets
     * @throws Exception  - an exception thrown if unable to instantiate VaultConfig and Vault.
     */
    public Map<String, String> getKafkaSecrets(String path) throws Exception {
        String env = System.getProperty("env");
        Map<String, String> secrets;
        if(Enums.getIfPresent(VaultEnv.class, env.toUpperCase()).isPresent()) {
            String vaultPath = path;
            SecretEngine secretEngine = new VaultClient().authWithAppRole().getSecretsEngine(SecretEngineType.KEYVALUE);
            KVSecretEngine kvSecretEngine = (KVSecretEngine) secretEngine;
            secrets = kvSecretEngine.getAllSecretsAsMap("kv-staf/" + env + "/" + vaultPath);
            secrets.putAll(getKafkaStore(secrets)); // add keystore, truststore file location to Kafka secrets
        } else {
            String propFileName = path;
            FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
            Map conversionProptoMap = fileSpecificUtilities.readPropertyFile("KAFKA/" + env + "/" + propFileName + ".properties");
            secrets = conversionProptoMap;
        }
        return secrets;
    }


    /**
     * This method is used to get Kafka Producer configurations from vault
     *
     * @param secretsFolderPath   - vault path where Kafka secretes are stored
     * @return producerSecrets    - returns kafka producer configuration as map
     * @throws Exception          - an exception thrown if unable to instantiate VaultConfig and Vault.
     */
    public Map<String, String> getProducerConfigs(String secretsFolderPath) throws Exception {
        Map<String, String> producerSecrets = getKafkaSecrets(secretsFolderPath);
        producerSecrets.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerSecrets.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        log.info("Kafka producer configuration properties are loaded");
        return producerSecrets;
    }

    /**
     * This method is used to get Kafka Consumer configuration from vault
     *
     * @param secretsFolderPath   - vault path where Kafka secretes are stored
     * @return consumerSecrets    - returns kafka consumer configuration in a map
     * @throws Exception          - throws an execution when unable to instantiate VaultConfig and Vault.
     */
    public Map<String, String> getConsumerConfigs(String secretsFolderPath) throws Exception {
        Map<String, String> consumerSecrets = getKafkaSecrets(secretsFolderPath);
        consumerSecrets.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerSecrets.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        log.info("Kafka consumer configuration properties are loaded");
        return consumerSecrets;
    }

    /**
     * This method is used to get Kafka keystore/truststore files from vault.
     *
     * @param secrets          - map with Kafka secrets
     * @return kafkaSecrets    - returns config map of keystore/truststore file location
     * @throws IOException     - throws exception when there are issue with creating a directory
     */
    public Map<String, String> getKafkaStore(Map<String, String> secrets) throws IOException {
        String certificatePath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "KafkaCertificates" + File.separator;
        HashMap<String, String> kafkaSecrets = new HashMap<>();

        for (Map.Entry<String, String> map : secrets.entrySet()) {
            if (map.getKey().endsWith(".p12") || map.getKey().endsWith(".jks") || map.getKey().endsWith(".keystore") || map.getKey().endsWith(".truststore")) {
                log.info("Creating kafka store for key :: {}", map.getKey());
                Base64.Decoder decoder = Base64.getMimeDecoder();
                // Decoding MIME encoded message
                File file = new File(certificatePath + map.getKey());
                file.getParentFile().mkdirs();
                if(file.createNewFile()) {
                    log.info("Truststore or Keystore file is created");
                }
                try (FileOutputStream fos = new FileOutputStream(file, true)) {
                    fos.write(decoder.decode(map.getValue()));           //writes bytes into file
                }                                                       //close the file

                if (map.getKey().contains("keystore")) {
                    kafkaSecrets.put("ssl.keystore.location", certificatePath + map.getKey());
                } else if (map.getKey().contains("truststore")) {
                    kafkaSecrets.put("ssl.truststore.location", certificatePath + map.getKey());
                }
            }
        }
        return kafkaSecrets;
    }
}
