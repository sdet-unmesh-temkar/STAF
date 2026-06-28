## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

* [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md#--description)
* [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md#--getting-started)                         
* [`Main features with sample code snippet`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md#-main-features-with-sample-code-snippets)      
* [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md#--documentation)                    
* [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md#--troubleshoot)                       


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**

__DatabaseUtilities__ within STAF are developed with the intention of streamlining tasks such as **manage** and **manipulate databases**. It provides a wide range of features and functionalities to simplify database operations. With DatabaseUtilities, users can easily **create, update**, and **delete** database records, as well as perform **complex queries**. Overall, DatabaseUtilities is a reliable and efficient solution for database management tasks.


**Release notes** : This confluence page describes changes in recent versions of STAF. Its primary objective is to document the changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

DatabaseUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. To import DatabaseUtilities into a Maven project, add the dependency below to your POM.xml file.

### `Maven`

    <!-- Add following parent block in your POM.xml inside <project> block -->
    <project>
       <parent>
         <groupId>STAF</groupId>
         <artifactId>STAF</artifactId>
         <version>[Enter latest version]</version>
       </parent>
    
    <!-- Add following dependencies in your POM.xml inside <dependencies> block -->
    <dependencies>
       <dependency>
         <groupId>STAF</groupId>
         <artifactId>DatabaseUtilities</artifactId>
         <version>[Enter latest version]</version>
       </dependency>

       <dependency>
         <groupId>STAF</groupId>
         <artifactId>DatabaseUtilities</artifactId>
         <classifier>tests</classifier>
         <version>[Enter latest version]</version>
       </dependency>
     </dependencies>
    </project>




## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png) **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`

* **`To connect to the database:`**
  If the user wants to **connect to the database**, they can call the **connectToDB(String database)** function as shown in the example below.
     
    ```
    private static final String dummyDBUrl = "jdbc:postgresql://localhost:443";
    CommonDatabaseMethods cdm = new CommonDatabaseMethods();
    Connection connection = cdm.connectToDB(dummyDBUrl);
    Statement stmt = connection.createStatement();  
    ResultSet rs = stmt.executeQuery("select * from tableName");  
    while(rs.next())
    { System.out.println(rs.getString());} 
     connection.close();
    ```
    
 * **`Trigger query to perform operations on database:`**
   If the user wants to perform operations on databases such as CRUD (create, read, update, delete), they can call the **executeDBQuery(database, query, parameters)** function as shown in the example below.
   
     ```
     private static final String dummyDBUrl = "jdbc:postgresql://localhost:443";
     private static final String query = "UPDATE table_name SET column1 = value1, column2 = value2, WHERE condition";
     private static final String parameter = "select testname,testvalue from table where id = ****'; 
     CommonDatabaseMethods cdm = new CommonDatabaseMethods();
     String result = cdm.executeDBQuery(dummyDBUrl, query, parameter)
    ```
 
* **`To execute database query:`**
  When a user wants to execute a query on the database to get some valid processed results, they can call the predefined step definitions mentioned below.
  
     * dbName  - needs to store in the HashiCorpVault
     * query   - will be passed from DB-Queries.properties as reference 
     * params  - will be passed from a feature file as input to generic query and form final executable query

     ```
    #To execute query on database 
    private static final String dbName = "jdbc:postgresql://localhost:443"; 
    private static final String query = "SELECT Key1,Key2 from customer where id = ****"; 
    private static final String[] params = "param1~param2"
    Given Execute database 'dbName' with Query 'query',parameters 'params'
     ```

 
   
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for DatabaseUtilities link :

https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/databaseutilities/package-summary.html


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**
STAF frequently asked questions pages link : 

https://de.confluence.agile.vodafone.com/x/pZkIBQ



