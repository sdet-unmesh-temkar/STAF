package databaseutilities;


import com.zaxxer.hikari.HikariDataSource;
import generalutilities.EnvironmentDataLoader;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import generalutilities.FileSpecificUtilities;
import generalutilities.ReportAndLogging;
import generalutilities.ThreadLocalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static databaseutilities.ConnectionPool.*;


/**
 * This class perform operation for the connection to the database.
 * This class contains methods to perform different operations on database using SQL query.
 */
public class CommonDatabaseMethods {

    private static final ThreadLocal<Properties> prop= new ThreadLocal<>();

    public CommonDatabaseMethods (){
        ThreadLocalRegistry.register(prop);
    }
    FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
    private static final Logger LOG = LoggerFactory.getLogger(CommonDatabaseMethods.class);
    Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private static final String DBQUERIESPATH = File.separator + "DBQueries" + File.separator + "DB-Queries.properties";
    public static final Map<String, HikariDataSource> connectionMap = new HashMap<>();
    private static final String QUERY = "query : {}";
    private static final String UNSUCCESSFUL_DML_QUERY = "DML Query is not executed successfully";
    private static final String QUERY_FAILED = "Failed while executing query: {}";

    /**
     * This method is used to remove ThreadLocal variables.
     */
    public void unload() {
        prop.remove();
    }

    /**
     * This method is used to check PoolExist functionality.
     *
     * @param dbName  - database name to check PoolExist functionality
     * @return        - Boolean true or false for connectionMap size (depends on connectionMap's size and if it has required database)
     */
    public Boolean isPoolExist(String dbName){
        return connectionMap.size()>0 && connectionMap.containsKey(dbName);
    }

    /**
     * This method is used to connect to database calling data from environments.
     *
     * @param database      - to connect to database calling data from environments
     * @return              - connection to the database
     * @throws SQLException - an exception that provides information on a database access error or other errors
     */
    public synchronized Connection connectToDB(String database) throws SQLException {
        LOG.trace("Database:{}", database);
        if (Boolean.FALSE.equals(isPoolExist(database))) {
            try {
                if (environment.get(database) != null) {
                    String[] details = environment.get(database).split(Pattern.quote("~"));
                    LOG.debug("details length: {}", details.length);
                    LOG.debug("DB name {}", details[0]);
                    LOG.debug("DB id {}", details[1]);
                    LOG.debug("DB pw {}", details[2]);
                    byte[] decry = Base64.getDecoder().decode(details[2]);
                    var decrypted = new String(decry);
                    try(Connection connection = createConnection(details[0], details[1], decrypted).getConnection()){
                        connectionMap.put(database, ConnectionPool.getDs());
                        LOG.info("Connection is established with {} ", database);
                        LOG.debug("Active Connections: {}", ConnectionPool.getDs().getHikariPoolMXBean().getActiveConnections());
                        LOG.debug("Idle Connections: {}", ConnectionPool.getDs().getHikariPoolMXBean().getIdleConnections());
                        LOG.debug("Total Connections:{} ", ConnectionPool.getDs().getHikariPoolMXBean().getTotalConnections());
                    }
                } else {
                    LOG.error("Connection details not found for : {} ", database);
                    reportAndLogging.addStepToReport("FAIL : Connection details for " + database + " could not be found","WARN");
                }
            } catch (SQLException ex) {
                LOG.error("FAIL : DB connection failed");
                LOG.error(ex.getMessage());
                ex.getMessage();
                reportAndLogging.addStepToReport("FAIL : DB connection failed","WARN");
            }
        }else {
            Connection existingConnection=connectionMap.get(database).getConnection();
            LOG.info("Connection is established with {} " ,database);
            LOG.debug("Active Connections: {} " , ConnectionPool.getDs().getHikariPoolMXBean().getActiveConnections());
            LOG.debug("Idle Connections: {} " , ConnectionPool.getDs().getHikariPoolMXBean().getIdleConnections());
            LOG.debug("Total Connections: {} " , ConnectionPool.getDs().getHikariPoolMXBean().getTotalConnections());
            return existingConnection;
        }
        return connectionMap.get(database).getConnection();
    }

    /**
     * This method is used to execute query on the database and return the result set received.
     * This method internally calls ConnectToKIASDB(String database) method to establish connection.
     *
     * @param database                - database name to establish connection
     * @param query                   - query in database management is a request for data.if you need to access, manipulate, delete, or retrieve data from your relational database
     * @return list                   - query result
     * @throws SQLException           - an exception that provides information on a database access error or other errors
     */
    public List<String> executeDBQuery(String database, String query) throws SQLException {

        Connection con = connectToDB(database);
        ArrayList<String> result = new ArrayList<>();
        prop.set(fileSpecificUtilities.readPropertyFile(DBQUERIESPATH));
        String queryfromprop = prop.get().getProperty(query);
        LOG.debug("query from property file: {}", queryfromprop);
        reportAndLogging.addStepToReport("Executable query is: " + queryfromprop);
        try (var stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(queryfromprop);
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (var i = 1; i <= columnCount; i++) {
                    result.add(rs.getString(i));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error occurred while retrieving data from result set : {}", e.getMessage());
            e.getMessage();
        }
        finally {
            con.close();
        }
        reportAndLogging.addStepToReport("Query result : " + result);
        return result;
    }

    /**
     * This method is used to execute query on the database and return the result as string.
     *
     * @param database      - database name to establish connection
     * @param query         - query in database management is a request for data. if you need to access, manipulate, delete, or retrieve data from your relational database
     * @param parameters    - to execute query on the database and return the result as string
     * @return String       - final database value
     * @throws SQLException - an exception that provides information on a database access error or other errors
     */
    public String executeDBQuery(String database, String query, String parameters) throws SQLException {
        Connection con = connectToDB(database);
        var finaldbval = "";
        var querypath = "";
        if (query.contains("~")) {
            querypath = query.split("~")[0].trim();
            query = query.split("~")[1].trim();
        }
        prop.set(fileSpecificUtilities.readPropertyFile(querypath + DBQUERIESPATH));
        LOG.debug("property file : {}" , prop.get());
        String[] queryParams = null;
        if (parameters.contains("~")) {
            queryParams = parameters.split(Pattern.quote("~"));
        } else {
            queryParams = parameters.split(" ");
        }
        var queryfromprop = prop.get().getProperty(query);
        LOG.trace(QUERY , queryfromprop);
        for (var j = 0; j < queryParams.length; j++) {
            int k = j + 1;
            queryfromprop = queryfromprop.replace("Key" + k, queryParams[j].trim());
        }
        LOG.info("query after replace: {}", queryfromprop);
        reportAndLogging.addStepToReport("Query need to be executed: " + queryfromprop);
        try (PreparedStatement prepStmt = con.prepareStatement(queryfromprop)) {
            var chkquery = queryfromprop;
            if (chkquery.toLowerCase().contains("insert") || chkquery.toLowerCase().contains("update") || chkquery.toLowerCase().contains("delete")) {
                var queryOutPutCount = String.valueOf(prepStmt.executeUpdate(queryfromprop));
                finaldbval = "DML stmt:" + queryOutPutCount;
                if (Integer.parseInt(queryOutPutCount) == 0) {
                    LOG.error(UNSUCCESSFUL_DML_QUERY);
                    reportAndLogging.addStepToReport(UNSUCCESSFUL_DML_QUERY,"WARN");
                }
            } else {
                ResultSet rs = prepStmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    for (var col = 1; col <= colCount; col++) {
                        var colname = meta.getColumnName(col);
                        var value = rs.getObject(col);
                        if (value == null) {
                            value = "null";
                        }
                        var dbval = value.toString();
                        dbval = colname + ": " + dbval.trim() + '\n';
                        finaldbval = finaldbval.concat(dbval);
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error(QUERY_FAILED, e.getMessage());
            e.getMessage();
        }
        finally {
            con.close();
        }
        return finaldbval;
    }

    /**
     * This method is used to execute user defined query on the database and return the result set received (No property file involvement).
     * This method internally calls ConnectToKIASDB(String database) method to establish connection.
     *
     * @param database                - database name to establish connection
     * @param query                   - query in database management is a request for data. if you need to access, manipulate, delete, or retrieve data from your relational database
     * @return list                   - query result
     * @throws SQLException           - an exception that provides information on a database access error or other errors
     */
    public List<String> executeUserDefinedDBQuery(String database, String query) throws SQLException {

        Connection connection = connectToDB(database);
        ArrayList<String> dBResult = new ArrayList<>();
        LOG.debug("query provided by the user: {}", query);
        reportAndLogging.addStepToReport("Executable query is: " + query);
        try (var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            int clmnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (var i = 1; i <= clmnCount; i++) {
                    dBResult.add(resultSet.getString(i));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error occurred while retrieving data from result set : {}", e.getMessage());
            e.getMessage();
        }
        finally {
            connection.close();
        }
        reportAndLogging.addStepToReport("Query result : " + dBResult);
        return dBResult;
    }

    /**
     * This method is used to execute user defined query on the database and return the result as string (No property file involvement).
     *
     * @param database      - database name to establish connection
     * @param query         - query in database management is a request for data.if you need to access, manipulate, delete, or retrieve data from your relational database
     * @param parameters    - to execute query on the database and return the result as string
     * @return String       - final database value
     * @throws SQLException - an exception that provides information on a database access error or other errors
     */
    public String executeUserDefinedDBQuery(String database, String query, String parameters) throws SQLException {
        Connection connection = connectToDB(database);
        var finaldbvalue = "";
        String[] queryParameters = null;
        if (parameters.contains("~")) {
            queryParameters = parameters.split(Pattern.quote("~"));
        } else {
            queryParameters = parameters.split(" ");
        }
        var queryVar = query;
        LOG.trace(QUERY , queryVar);
        for (var j = 0; j < queryParameters.length; j++) {
            int k = j + 1;
            queryVar = queryVar.replace("Key" + k, queryParameters[j].trim());
        }
        LOG.info("query after replace: {}", queryVar);
        reportAndLogging.addStepToReport("Query need to be executed: " + queryVar);

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryVar)) {
            if (isDMLQuery(queryVar)) {
                finaldbvalue = executeDML(preparedStatement);
            } else {
                finaldbvalue = executeSelect(preparedStatement);
            }
        } catch (SQLException e) {
            LOG.error(QUERY_FAILED, e.getMessage());
            e.getMessage(); // Optional: consider removing if unused
        } finally {
            connection.close();
        }

        return finaldbvalue;
    }

    /**
     * Determines whether a given SQL query is a Data Manipulation Language (DML) operation.
     * DML queries include INSERT, UPDATE, and DELETE.
     *
     * @param query the SQL query to evaluate
     * @return true if the query is a DML operation; false otherwise
     */
    private boolean isDMLQuery(String query) {
        String lower = query.toLowerCase();
        return lower.contains("insert") || lower.contains("update") || lower.contains("delete");
    }

    /**
     * Executes a DML statement (INSERT, UPDATE, DELETE) using the provided PreparedStatement.
     *
     * @param preparedStatement the prepared SQL statement to execute
     * @return a string indicating the number of rows affected by the DML operation
     * @throws SQLException if execution fails or the statement is invalid
     */
    private String executeDML(PreparedStatement preparedStatement) throws SQLException {
        int updateCount = preparedStatement.executeUpdate();
        if (updateCount == 0) {
            LOG.error(UNSUCCESSFUL_DML_QUERY);
            reportAndLogging.addStepToReport(UNSUCCESSFUL_DML_QUERY, "WARN");
        }
        return "DML stmt:" + updateCount;
    }

    /**
     * Executes a SELECT SQL query and formats the ResultSet as a multi-line string.
     *
     * @param preparedStatement the prepared SELECT statement to execute
     * @return the formatted result of the query, with each column name and value on a new line
     * @throws SQLException if an error occurs while retrieving result set metadata or values
     */
    private String executeSelect(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder result = new StringBuilder();

            while (resultSet.next()) {
                for (int col = 1; col <= columnCount; col++) {
                    String columnName = metaData.getColumnName(col);
                    Object val = resultSet.getObject(col);
                    String value = (val == null ? "null" : val.toString().trim());
                    result.append(columnName)
                            .append(": ")
                            .append(value)
                            .append('\n');
                }
            }
            return result.toString();
        }
    }

    /**
     * This method is used to execute query on the database and return the result set received. This method internally calls ConnectToKIASDB(String database) method to establish connection.
     *
     * @param database      - database name to establish connection
     * @param query         - query in database management is a request for data. if you need to access, manipulate, delete, or retrieve data from your relational database
     * @return              - result on list format
     * @throws SQLException - an exception that provides information on a database access error or other errors
     */
    public List<String> executeDBQueryWithQueryPath(String database, String query) throws SQLException {
        Connection con = connectToDB(database);
        ArrayList<String> result = new ArrayList<>();
        var querypath = "";
        if (query.contains("~")) {
            querypath = query.split("~")[0].trim();
            query = query.split("~")[1].trim();
        }
        LOG.trace("query = {}" , query);
        LOG.trace("queryfilepath {} {}",querypath,DBQUERIESPATH);
        prop.set(fileSpecificUtilities.readPropertyFile(querypath + DBQUERIESPATH));
        var queryfromprop = prop.get().getProperty(query);
        LOG.debug(QUERY, queryfromprop);
        reportAndLogging.addStepToReport("Query to be executed: " + queryfromprop);
        try (var stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(queryfromprop);
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (var i = 1; i <= columnCount; i++) {
                    result.add(rs.getString(i));
                }
            }
            reportAndLogging.addStepToReport("executed Query result : " + result);
            LOG.info("Executed Query result : {}", result);
        } catch (SQLException e) {
            LOG.error(QUERY_FAILED, e.getMessage());
            e.getMessage();
        } finally {
            con.close();
        }
        return result;
    }
}