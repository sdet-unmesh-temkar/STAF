package kafkautilities;


import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;

import generalutilities.FileSpecificUtilities;

/**
 * his class contains methods related configurations.
 * This class contains methods to load configuration values for kafka execution
 *  */
public class ConfigUtils {
    FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
    Properties props;
    String sslKeystoreConfig = "SslConfigs.SSL_KEYSTORE_TYPE_CONFIG";
    String sslTruststoreConfig = "SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG";
    String sslTruststorePasswordConfig = "SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG";
    String sslKeystorePasswordConfig = "SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG";
    String sslKeyPasswordConfig = "SslConfigs.SSL_KEY_PASSWORD_CONFIG";
    String schemaRegistryUrl = "schema.registry.url";
    String fileName = "KAFKA/";
    String fileExtension = ".properties";

    /**
     * This method is used to get configuration for Kafka Producer
     *
     * @param propFileName - path where configuration file is available
     * @return props       - returns a properties objects with producer configuration values
     */
    public Properties getConfigurationProducer(String propFileName) {
        String env = System.getProperty("env");
        props = fileSpecificUtilities.readPropertyFile(fileName + env + "/" + propFileName + fileExtension);

        String strKeySrl = props.getProperty("ConsumerConfig.KEY_SERIALIZER_CLASS_CONFIG");
        String strValSrl = props.getProperty("ConsumerConfig.VALUE_SERIALIZER_CLASS_CONFIG");
        String strTrustStoreProducerLocation = props.getProperty("SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG_PRODUCER");
        String strKeyStoreProducerLocation = props.getProperty("SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG_PRODUCER");
        String strBootStrapServer = props.getProperty("ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG");
        String strKeyConfig = props.getProperty(sslKeystoreConfig);
        String strTrustConfig = props.getProperty(sslTruststoreConfig);
        String strTrustPass = props.getProperty(sslTruststorePasswordConfig);
        String strKeyPass = props.getProperty(sslKeystorePasswordConfig);
        String strKeyPassCon = props.getProperty(sslKeyPasswordConfig);
        String strSchema = props.getProperty(schemaRegistryUrl);
        String strSecPro = props.getProperty("CommonClientConfigs.SECURITY_PROTOCOL_CONFIG");

        props.put(schemaRegistryUrl, strSchema);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, strKeySrl);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, strValSrl);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, strSecPro);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, strTrustStoreProducerLocation);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, strKeyStoreProducerLocation);
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, strKeyConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, strTrustConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, strTrustPass);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, strKeyPass);
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, strKeyPassCon);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootStrapServer);
        return props;
    }


    /**
     * This method is used to get configuration for Kafka Consumer
     *
     * @param propFileName -  path where configuration file is available
     * @return props       - returns a properties objects with consumer configuration values
     */
    public Properties getConfigurationConsumer(String propFileName) {
        String env = System.getProperty("env");

        props = fileSpecificUtilities.readPropertyFile(fileName + env + "/" + propFileName + fileExtension);

        String strKeyDeSrl = props.getProperty("ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG");
        String strValDeSrl = props.getProperty("ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG");
        String strTrustStoreConsumerLocation = props.getProperty("SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG_CONSUMER");
        String strKeyStoreConsumerLocation = props.getProperty("SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG_CONSUMER");
        String strGroupID = props.getProperty("ConsumerConfig.GROUP_ID_CONFIG");
        String strSchema = props.getProperty(schemaRegistryUrl);
        String strResetConfig = props.getProperty("ConsumerConfig.AUTO_OFFSET_RESET_CONFIG");
        String strCommitConfig = props.getProperty("ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG");
        String strCommitInterval = props.getProperty("ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG");
        String strBootStrapServer = props.getProperty("ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG");
        String strSecPro = props.getProperty("CommonClientConfigs.SECURITY_PROTOCOL_CONFIG");
        String strKeyConfig = props.getProperty(sslKeystoreConfig);
        String strTrustConfig = props.getProperty(sslTruststoreConfig);
        String strTrustPass = props.getProperty(sslTruststorePasswordConfig);
        String strKeyPass = props.getProperty(sslKeystorePasswordConfig);
        String strKeyPassCon = props.getProperty(sslKeyPasswordConfig);

        props.put(schemaRegistryUrl, strSchema);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, strGroupID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, strKeyDeSrl);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, strValDeSrl);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, strResetConfig);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, strCommitConfig);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, strCommitInterval);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootStrapServer);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, strSecPro);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, strTrustStoreConsumerLocation);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, strKeyStoreConsumerLocation);
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, strKeyConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, strTrustConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, strTrustPass);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, strKeyPass);
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, strKeyPassCon);
        return props;
    }

    /**
     * This method is used to get configuration for Kafka Admin Client
     *
     * @param propFileName-  path where configuration file is available
     * @return props      - returns a properties objects with Admin Client configuration values
     */
    public Properties getConfigurationAdminClient(String propFileName) {
        String env = System.getProperty("env");

        props = fileSpecificUtilities.readPropertyFile(fileName + env + "/" + propFileName + fileExtension);

        String strBootstrapServers = props.getProperty("AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG");
        String strSecPro = props.getProperty("AdminClientConfig.SECURITY_PROTOCOL_CONFIG");
        String strTrusStrLoca = props.getProperty("SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG");
        String strKeyConfig = props.getProperty(sslKeystoreConfig);
        String strTrustConfig = props.getProperty(sslTruststoreConfig);
        String strTrustPass = props.getProperty(sslTruststorePasswordConfig);
        String strKeyLocConfig = props.getProperty("SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG");
        String strKeyPass = props.getProperty(sslKeystorePasswordConfig);
        String strKeyPassCon = props.getProperty(sslKeyPasswordConfig);

        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, strBootstrapServers);
        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, strSecPro);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, strTrusStrLoca);

        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, strKeyConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, strTrustConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, strTrustPass);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, strKeyLocConfig);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, strKeyPass);
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, strKeyPassCon);

        return props;
    }

}
