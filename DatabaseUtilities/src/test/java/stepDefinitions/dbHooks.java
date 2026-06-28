package stepDefinitions;


import com.zaxxer.hikari.HikariDataSource;
import databaseutilities.CommonDatabaseMethods;
import databaseutilities.ConnectionPool;
import io.cucumber.java.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * This class checks status for connection pool on database.
 * This class contains methods to perform different operations on database such as close connection Pool.
 */
public class dbHooks {
    private static final Logger log = LoggerFactory.getLogger(CommonDatabaseMethods.class);

    /**
     * This method is used to close the connection pool.
     * This method will close all active and Idle connections.
     */
    @AfterAll
    public static void closeConnectionPool(){
        log.info("*** @AfterAll hook to CLOSE DATASOURCES AND REMOVE MAP initiated ***");
        if (!CommonDatabaseMethods.connectionMap.isEmpty()) {
            for (Map.Entry mEntry : CommonDatabaseMethods.connectionMap.entrySet()) {
                HikariDataSource dataSource = (HikariDataSource) mEntry.getValue();
                String keyAsString = mEntry.getKey().toString();
                log.debug("Pool info before closing Connection Pool: {} ", keyAsString);
                log.debug("Active Connections: {}" , ConnectionPool.getDs().getHikariPoolMXBean().getActiveConnections());
                log.debug("Idle Connections: {}" , ConnectionPool.getDs().getHikariPoolMXBean().getIdleConnections());
                log.debug("Total Connections: {}" , ConnectionPool.getDs().getHikariPoolMXBean().getTotalConnections());
                dataSource.close();
            }
            CommonDatabaseMethods.connectionMap.clear();
            log.info("*** All active/idle connections cleared! ***");
        }else {
            log.info("*** THERE IS NO DATABASE CONNECTION ***");
        }

    }
}