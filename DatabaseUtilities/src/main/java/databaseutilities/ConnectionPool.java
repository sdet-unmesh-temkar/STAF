package databaseutilities;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;


/**
 * This class contains methods to establish connection Pool to database.
 * Connection Pool is a cache of database connections maintained so that the connections can be reused when future requests to the database implements using HikariDataSource.
 */
public class ConnectionPool{

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private ConnectionPool(){
    }

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    /**
     * This method is used to return the object of HikariDataSource.
     *
     * @return -  HikariDataSource object or null
     */
    public static HikariDataSource getDs() {
        return ds;
    }

    /**
     * This method is used to return the object of HikariConfig
     *
     * @return HikariConfig object or null
     */
    public static HikariConfig getConfig() {
        return config;
    }

    /**
     * This method is used to create connection to database and return the object HikariDataSource.
     *
     * @param jdbcUrl       - JDBCUrl to establish java database connectivity
     * @param dbUsername    - database username to create connection to database
     * @param dbPassword    - database password to create connection to database
     * @return              - HikariDataSource object
     * @throws SQLException - an exception that provides information on a database access error or other errors
     */
    public static HikariDataSource createConnection(String jdbcUrl, String dbUsername, String dbPassword) {
        config.setJdbcUrl( jdbcUrl );
        config.setUsername( dbUsername );
        config.setPassword( dbPassword );
        config.setMaximumPoolSize(5);
        ds = new HikariDataSource(config);
        return ds;
    }
}