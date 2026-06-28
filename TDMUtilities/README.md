
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

  
 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/TDMUtilities#--description)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/TDMUtilities#--getting-started)                         
 * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/TDMUtilities#--main-features-with-sample-code-snippets)      
 * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/TDMUtilities#--documentation)            
 * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/TDMUtilities#--troubleshoot)   
    
    
    
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**


**TDMutilities** is designed for the efficient integration of Test Data Creation via the TDM Tool in STAF. With the use of TDM APIs, it provides an alternative solution for data creating and using it E2E test execution flow.  


**Release notes:** This confluence page describes changes in recent versions of STAF. Its primary objective is to document changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw
   



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

TDMUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. 
To import TDMUtilities into a Maven project, **add the dependency below to your POM.xml file**.

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
        <artifactId>TDMUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>TDMUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>
    

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
 
 **Test Data Creation:** 

There are two ways to generate the Customer Data:  
 
**1. Create Data with TDM Parameters**: TDM application has specific parameters to filter data. Depending on suitable parameters, TDM will generate the Customers. As seen in the below example, the user can optimize the **Number of Generated Customers**, **Source Environment**, the **TDMTaskID** of execution, and the **timeout** in the test step.
            
            @TDM
            Scenario: Get Test Data from TDM with Parameters
             Given TDM - we generate 1 customers in the environment 'TESTDONE' based on the TDMTaskID 404 and a maximum timeout of 15 minutes with following filters
             #Given TDM Generate Customers 1 in  environment 'TESTDONE' with Taskid 9 with timeout of 15 minutes
               | parameter   | operator | value  |
               | ACRM.STATUS | =        | Active |
           
        
           
Available parameters are found in the following documentation:
<a href="https://de.confluence.agile.vodafone.com/x/kHC3Dw" target="_blank" >TDM Parameters List</a>    

Depending on the above feature file, the user can implement creation of customer data in the step definition as shown in the following example:
          
          
          TDMAPIMethods tdmAPIMethods = new TDMAPIMethods();
          DataTable dataTable = DataTable.create(List.of(List.of("parameter","operator","value"),List.of("ACRM.STATUS","=","Active")));
          String sourceEnv = "TESTDONE";
          Integer numberOfCustomer = 1;
          Integer tdmTaskID = 404; // The user can retrieve tdmTaskID from TDM Application. 
          //Integer taskId = 9; 
          Integer timeLimit = 15; 
          tdmAPIMethods.tdmGenerateToken();
          tdmAPIMethods.generateEntityList(dataTable, sourceEnv);
          tdmAPIMethods.tdmTriggerTask(numberOfCustomer, tdmTaskID, timeLimit);
          //tdmAPIMethods.tdmTriggerTask(numberOfCustomer, taskId, timeLimit);
          
 Below methods are used in the above Step definition,
- **tdmGenerateToken()**  - This function is used to generate an authentication token. 
- **generateEntityList(dataTable, sourceEnv)** - This method is used to create the request body for POST request. dataTable parameter, filters the customer information to be created with the "parameter, operator and value" in the feature file. sourceEnviroment specifies the database environment in which these filters will be used. 
- **tdmTriggerTask(numberOfCustomer, tdmTaskID, timeLimit)** - This method runs the application and starts the customer creation. If this step is not completed in the passed time limit (in minutes), the execution is stopped. The default time limit value is 15 minutes in the feature file. Users can change this time limit value according to their own scenarios.
After 15 minutes the token expired. If the flow lasts more than 15 minutes, the token is regenerated. 




 
 **2. Create Data with Provided Customer IDs:** It is possible to provide specific **Customer ID**, which must be found in the source Database of TDM.                  Depending on suitable CustomerIDs, the TDM application will generate Customers. As seen in the below feature file, the user can optimize **TDMTaskID** and **timeout** in test step.  

We do not recommend using this step. The Customer ID used here may belong to a real customer and this may be a data problem because the customer may not be present forever in the same state in its own environment or may not be present at all in other environments.
     
     
         @TDMCustomerID
         Scenario: Get Test Data from TDM with Provided Customers
           Given TDM - we generate customers based on the TDMTaskID 404 and a maximum timeout of 15 minutes with the following CustomerID
           #Given TDM Generate Customers with Taskid 9 with timeout of 15 minutes based on Entities
            | 12******78 |
         
         
Depending on the above feature file, the user can implement the creation of customer data in the step definition as shown in the following example:


        TDMAPIMethods tdmAPIMethods = new TDMAPIMethods();
        DataTable dataTable = DataTable.create(List.of(List.of("12******78"))),
        String sourceEnv = "TESTDONE";
        Integer tdmTaskID = 404; // The user can retrieve tdmTaskID from TDM Application. 
        //Integer taskId = 9;
        Integer timeLimit = 15; 
        tdmAPIMethods.tdmGenerateToken();
        tdmAPIMethods.tdmTriggerTaskCustomerID(taskId, timeLimit, dataTable);
    

 Below methods are used in the above Step definition,
- **tdmGenerateToken()** - This function is used to generate an authentication token. 
- **tdmTriggerTaskCustomerID(tdmTaskID,timeLimit,dataTable)** - This method runs the application and starts the customer creation. If this step is not completed in in the passed time limit (in minutes), execution will fail. The default time limit value is 15 minutes in the feature file. Users can change this time limit value according to their own scenarios.
After 15 minutes the token expired. If the flow lasts more than 15 minutes, the token is regenerated. 
In this method, the dataTable parameter represents customer id(s) and it is used as a reference of new created customers.

The customer data generated in the above two ways, can be used in the test execution flow. The generated data is stored in the TestContext and deleted when the execution is finished. The following shows how to retrieve the stored customer from TestContext to use it in the next step: 


        
        TestContext<Object> testContext = TestContext.getInstance();
        testContext.getProperty("tdmResponse")); // Generated TDM Customers are stored as tdmResponse 
        testContext.getProperty("tdmFirstCustomer"); //  First customer is stored as tdmFirstCustomer
        
**3. Create dummy data:** Dummy data can be created. It can be used as shown below.
      
        Faker faker = new Faker();
        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton
        String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449
      

        
 **Customer Search :** 

The **Customer Search** functionality demonstrates how customer data retrieval and validation can be automated using the **Test Data Tool**.
The primary scenario focuses on searching for a customer using dynamic queries and validating the customer’s **master data** across **FusionC and related systems**.
Customer data is dynamically fetched from the Test Data Tool and stored in the test context for subsequent verification steps.
 
**Step Defination Mapping :**
* The step **Given Test Data Tool - Get dynamic test data Customer Id search by query: {QUERY_NAME}** is implemented in the TestDataToolsSteps class. 
* It Calls the **Test Data Tool API** to fetch customer data using a dynamic query (e.g., ACTIVE_CUSTOMER).
* Asserts the HTTP response code is **200 OK**.
* Saves the customer response in the **test context** for further validation.
* Logs
    * The searched customer ID
    * Individual ID for traceability and debugging purposes
            
            @TestDataToolCustomerSearch
            Scenario: Searched customer master data change - First name
                      Reads the data of a Searched customer from TestData tool.
                      Verifies customer in various FusionC and related systems.
            Given Test Data Tool - Get dynamic test data Customer Id search by query: {QUERY_NAME}
        
**Supported Queries**

The following queries are supported and can be used in the feature file to retrieve different types of customer data:

| **Query** |**Description** |
|-------|-------|
| ACTIVE_CUSTOMER | Search for a customer with status active |
| ACTIVE_CUSTOMER_WITH_DELPHI_ID |  Search for an active customer with Delphi ID |
| ACTIVE_CUSTOMER_WITH_OSF_ID | Search for an active customer with OSF and Delphi IDs |
|CUSTOMER_WITH_GZ100 | Search for an active customer with GigaZuhause 100 Cable product |
| CUSTOMER_WITH_GZ250 | Search for an active customer with GigaZuhause 250 Cable product |
| CUSTOMER_WITH_GZ500 | Search for an active customer with GigaZuhause 500 Cable product |
|CUSTOMER_WITH_GZ1000 | Search for an active customer with GigaZuhause 1000 Cable product |
|CUSTOMER_WITH_VF_STATION | Search for an active customer with Vodafone Station Option product |
|CUSTOMER_WITH_FRITZ6670 | Search for an active customer with HomeBox FRITZ!Box 6670 product |
|CUSTOMER_WITH_FRITZ6690 | Search for an active customer with HomeBox FRITZ!Box 6690 product |
|CUSTOMER_WITH_MAC_ADDRESS | Search for an active customer with MAC address |

**Configuration and usage :** 
* Users can specify any of the supported query names in the feature file
* The Test Data Tool dynamically returns a matching customer based on the selected query
* Retrieved customer data can be reused across multiple validation steps
* This approach enables:
    * Flexible test data selection
    * Reduced dependency on static test data
    * Improved test reliability and coverage



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for TDMUtilities: https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/tdmutilities/package-summary.html


TDM Logic in STAF: https://de.confluence.agile.vodafone.com/x/kerdEQ


TDM API Token Authentication: https://de.confluence.agile.vodafone.com/x/jHC3Dw


TDM Parameters List: https://de.confluence.agile.vodafone.com/x/kHC3Dw


Start TDM Task: https://de.confluence.agile.vodafone.com/x/yGwADg


Task Execution Monitoring: https://de.confluence.agile.vodafone.com/x/XqYvDg


Task Execution Result: https://de.confluence.agile.vodafone.com/x/TiAADg


Java Faker Library: https://github.com/DiUS/java-faker



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**
STAF FAQs pages link :https://de.confluence.agile.vodafone.com/x/pZkIBQ

 
