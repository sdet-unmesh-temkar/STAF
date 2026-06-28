package stepDefinitions;

import generalutilities.ReportAndLogging;
import java.sql.SQLException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;
import databaseutilities.CommonDatabaseMethods;

/**
 * This class perform connection to the Database.
 * This class contains methods to perform different operations on database using SQL query as well as test DB Connectivity.
 */
public class DatabaseSteps {
	ReportAndLogging reportAndLogging = new ReportAndLogging();
	CommonDatabaseMethods db = new CommonDatabaseMethods();

	/**
	 * This method is used to execute query on the database and return the final database value.
	 *
	 * @param query         - SQL query to be executed on the database. If the user needs to access, manipulate, delete, or retrieve data from yourrelational database.
	                          e.g. ("String query = SELECT Key1,Key2 from customer where id = ****")
	 * @param database      - database name to establish connection and on which the query is to be executed.
	                          e.g. (String dummyDatabaseUrl = "jdbc:postgresql://localhost:443")
	 * @param parameters    - parameters for the SQL query
                              e.g. (String parameters = "param1~param2")
	 * @return String       - result of the executed query
	 * @throws SQLException - an exception thrown if an error occurs while executing the query
	 */
	@When("We execute a query {string} on database {string} with parameters {string}")
	public String ExecutedatabaseQuery(String query,String database,String parameters) throws SQLException {
		String finaldbval = "";
		finaldbval=  db.executeDBQuery(database,query,parameters);
		return finaldbval;
	}

	/**
	 * This method is used to execute user defined query on the database and return the final database value (No property file involvement).
	 *
	 * @param query         - SQL query to be executed on the database. If the user needs to access, manipulate, delete, or retrieve data from your relational database.
	e.g. ("String query = SELECT Key1,Key2 from customer where id = ****")
	 * @param database      - database name to establish connection and on which the query is to be executed.
	e.g. (String dummyDatabaseUrl = "jdbc:postgresql://localhost:443")
	 * @param parameters    - parameters for the SQL query
	e.g. (String parameters = "param1~param2")
	 * @return String       - result of the executed query
	 * @throws SQLException - an exception thrown if an error occurs while executing the query
	 */
	@When("We execute a user defined query {string} on database {string} with parameters {string}")
	public String ExecuteDatabaseUDQuery(String query,String database,String parameters) throws SQLException {
		String finaldbval = "";
		finaldbval=  db.executeUserDefinedDBQuery(database, query, parameters);
		return finaldbval;
	}

	/**
	 * This method is used to establish a connection to a specified database.
	 *
	 * @param databaseName  - A String representing the name of the database to connect to.
	                          e.g. (String dummyDatabaseName = "jdbc:postgresql://localhost:443")
	 * @throws SQLException - an exception thrown if a database access error occurs
	 * If the connection to the database is unsuccessful, an assertion error is thrown and a failure message is logged.
	 * If the connection is successful, a success message is logged.
	 */
	@Given("We establish connection to the database {string}")
	public void connectToDatabase(String databaseName) throws SQLException {

		if(db.connectToDB(databaseName)==null){
			reportAndLogging.addStepToReport("Connect to Database failed","WARN");
			//If the connection to the database is unsuccessful, an assertion error is thrown and a failure message is logged.
			Assert.assertTrue(false);
		}
		else{
			// If the connection is successful, a success message is logged.
			reportAndLogging.addStepToReport("Connect to Database successful","INFO");
		}
	}
}