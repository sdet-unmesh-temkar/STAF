package staf.kafkautilities.clients;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import staf.kafkautilities.config.AdminConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains method of Kafka Admin client
 * This class contains methods for creating Kafka Admin client.
 */
public class AdminClients {
    private static final Logger log = LoggerFactory.getLogger(AdminClients.class);


    /**
     * This method will create and return the Kafka Admin Client.
     * @param path     - path of the file which contains Admin configurations
     * @return admin   - Admin Client
     */
    public Admin createKafkaAdminClient(String path){
        Admin admin = null;
        try {
            Map<String, String> secrets = new AdminConfig().getAdminConfigs(path);
            admin = AdminClient.create(new HashMap<>(secrets));
            log.info("Kafka admin client is created...");
        } catch(Exception e) {
            log.error("Some issues with Kafka admin client creation.. : {}", e.getMessage());
        }
        return admin;
    }

}
