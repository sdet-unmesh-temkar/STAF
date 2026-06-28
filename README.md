## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--description)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--getting-started)
 * [`Information about tools/frameworks used` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--information-about-toolsframeworks-used)
 * [`Overview of the STAF Utilities` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--overview-of-the-staf-utilities)
 * [`Cucumber hooks` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--cucumber-hooks)
 * [`Parallel execution` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--parallel-execution)
* [`Logging` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--logging)
* [`Xray logging toggle` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--xray-logging-toggle)
* [`Reporting` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--reporting)
* [`Documentation` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--documentation)
* [`Troubleshoot` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--troubleshoot)
* [`Contribution to the STAF` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--contribution-to-the-staf)
* [`Contributors` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/README.md#--contributors)


 

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**

**Solstice Test Automation Framework (STAF)** is a test automation framework that is used by numerous application testing teams within the Vodafone Solstice project. STAF is designed to empower delivery teams with the ability to automate their tests, thereby streamlining the testing process. **It provides comprehensive framework for reporting/logging/testing**. As well as STAF also **perform some generic actions on APIs, web browsers, Databases, Kafka, TDM, Wiremock, and more**.
 

**Release notes**: This confluence page describes changes in recent versions of STAF. Its primary objective is to document the changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting started`**



STAF require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. 
To import STAF into a Maven project, add the dependency below to your POM.xml file.

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

        <!--  **** STAF UTILITIES DEPENDENCIES **** -->
         <dependency>
           <groupId>STAF</groupId>
           <artifactId>GeneralUtilities</artifactId>
           <version>[Enter latest version]</version>
        </dependency>

        <dependency>
           <groupId>STAF</groupId>
           <artifactId>GeneralUtilities</artifactId>
           <classifier>tests</classifier>
           <version>[Enter latest version]</version>
         </dependency> 
	

       <dependency>
          <groupId>STAF</groupId>
          <artifactId>[Enter the name of the specific Utilities]</artifactId>
          <version>${project.parent.version}</version>
       </dependency>

       <dependency>
          <groupId>STAF</groupId>
          <artifactId>[Enter the name of the specific Utilities]</artifactId>
          <classifier>tests</classifier>
          <version>${project.parent.version}</version>
       </dependency>
    </dependencies>
    </project>
   
 ## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Information about tools/frameworks used`**

**Used tools and frameworks:** 
  * **Used languages**: Java, Groovy
  * **Test framework**: Junit, Cucumber.
  * **Build Management**: Maven
  * **Binary repository**: Nexus
  * **CI/CD**: GitHub actions, Jenkins.
  * **Version control system**: git
  * **Source Code management**: GitHub
  * **Source Code Analysis**: SonarQube
  * **Test Management/Reporting**: Xray
  * **Secret management tool**: HashiCorp Vault
  
  

 ## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Overview of the STAF Utilities`**


#### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`APIUtilities`**  
 **STAF's APIUtilities** is a versatile set of tools and utilities designed to enhance the functionality and capabilities of the APIs. It provides a range of features such as **request and response logging**. APIUtilities should make requests to your **API endpoints** and **assert** the expected response. APIUtilities can be used to trigger RestAssured methods like **get(), post(), put(), and patch()**. It is capable to handle different Authentication of Rest Assured such as **Basic Auth, Bearer, OAuth (2.0)** to check the necessary permissions to perform the requested action. Overall, APIUtilities is a valuable tool for developers looking to trigger an API request and **validate** responses.

Link for the APIUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md    
 ___________________________________________________________________________________________________________________________________________________________________
#### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`DatabaseUtilities`**  
  __DatabaseUtilities__ within STAF are developed with the intention of streamlining tasks such as **manage** and **manipulate databases**. It provides a wide range of features and functionalities to simplify database operations. With DatabaseUtilities, users can easily **create, update**, and **delete** database records, as well as perform **complex queries**. Overall, DatabaseUtilities is a reliable and efficient solution for database management tasks.

Link for the DatabaseUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/DatabaseUtilities/README.md    
 ___________________________________________________________________________________________________________________________________________________________________
 
 
 #### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`GeneralUtilities`**  
  **GeneralUtilities** within STAF are developed to provide a wide range of utility functions and tools that can be used in various programming tasks. It offers a collection of helpful functions for tasks such as **vault connectivity and configuration, generation of the final execution report, Envirnoment configuration, as well as it also performs common operation on file management, application context, and TestContext related operation**. Overall, GeneralUtilities aims to enhance the efficiency and productivity of programming tasks by providing a collection of useful functions.

To simplify the automation process while using the Solstice Test Automation Framework, it is essential to import the GeneralUtilities. This Utilities is an integral part of the Solstice Test Automation Framework and provides a range of utility functions. By importing the GeneralUtilities, User can use these functions in their test scripts and make automation process more efficient. **So it is always necessary to import GeneralUtilities while using STAF.**


Link for the GeneralUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/GeneralUtilities/README.md
 ___________________________________________________________________________________________________________________________________________________________________

 #### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`InterFrameworkUtilities`**  
**STAF's InterFrameworkUtilities** are designed to facilitate integration testing between STAF and other test automation frameworks by exchange inter-framework information over Orchestration webservice developed by OSF test automation. These utilities enable STAF to :
* Initiate integration test cases in between alternate Test Automation frameworks, and
* Obtain prompts to run/continue orchestrated inter-framework integration testing scenarios in STAF, sourced from different TA frameworks.

Link for the InterFrameworkUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md
 ___________________________________________________________________________________________________________________________________________________________________

 #### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`KafkaUtilities`**  
**KafkaUtilities** within STAF are developed to connect to the Kafka server and perform the operations there. Thus, Producer and Consumer can be created, Kafka events can be produced and Kafka event records can be retrieved for Topics.
The user can perform tasks such as creating and managing topics, monitoring the health and performance of your Kafka brokers, producers, and consumers, and analyzing the data flowing through Kafka streams with Kafka Utilities. It also offers various administrative capabilities, including managing consumer groups, configuring security settings, and handling data replication.

Link for the KafkaUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/KafkaUtilities/README.md
 ___________________________________________________________________________________________________________________________________________________________________

 #### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`ProcessBuilderUtilities`**  
**ProcessBuilderUtilities** within STAF are developed to perform various operations on the command line. It offers various features to simplify the usage of ProcessBuilder. It provides methods for easily setting the command and arguments for the process, as well as configuring the working directory, environment variables, and input/output streams.

Link for the ProcessBuilderUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md

____________________________________________________________________________________________________________________________________________________________________

#### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`TDMutilities`** 
**TDMutilities** is designed for the efficient integration of Test Data Creation via the TDM Tool in STAF. With the use of TDM APIs, it provides an alternative solution for data creating and using it E2E test execution flow. 

Link for the TDMutilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/TDMUtilities/README.md

____________________________________________________________________________________________________________________________________________________________________

#### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`WebUtilities`**
__WebUtilities__ within STAF are developed with the intention of streamlining tasks such as **web driver management** and **executing common browser actions**, as well as handling **typical actions for web-based applications** by providing a convenient framework.

Link for the WebUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WebUtilities/README.md
____________________________________________________________________________________________________________________________________________________________________

#### ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`WiremockUtilities`**
__WiremockUtilities__ within STAF are developed with the intention of streamlining a tool used for simulating HTTP-based services. Through STAF, it offers functionalities like initiating and halting the WireMockServer, and launching and resetting the ValidationListener. Overall, WiremockUtilities is a valuable tool for anyone working with WireMock, as it enhances the capabilities and simplifies the usage of this powerful mocking framework.

Link for the WiremockUtilities readme: 

https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md
___________________________________________________________________________________________________________________________________________________________________


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Cucumber hooks`**

**Following are the hooks present in staf-utilities:**

|  **Name of the hooks**          | **Type**  | **Usage Utilities**  | **Trigger condition**|  **Description**|                                   
   | -------------------------------|----------------------| -----------------------|----------------------|----------------|
   | **setVariables(Scenario scenario)** | @Before   | WebUtilities                 | Always triggered if WebUtilities is used                   | The purpose of the setVariables hooks is to prepare for screenshot capture by associating the current test scenario with a screenshot object before each test execution. |
   | **setTestSuiteName(Scenario scenario)** | @Before | InterFrameworkUtilities                 |  Always triggered if InterFrameworkUtilities is used              | This hook is used to sets the test suite name for each scenario. It extracts the name from the scenario's tags and assigns it to the SchemaBuilder instance.     |
   | **setCustomDataForSuite()**  | @Before | InterFrameworkUtilities                            | triggers only for scenarios with tag "@interFrameworkTest"               | This hook is triggered for scenarios tagged with interFrameworkTest. It sets custom data for a given test suite in a TestContext object, based on the SuiteID and passedTestResultIndex system properties.                                            |
   | **terminateWebDriver(Scenario scenario)** |  @After | WebUtilities             | Always triggered if WebUtilities is used               | This hook cleans up post-scenario by taking a screenshot if needed, terminating the WebDriver, and logging the process.               |
   | **killConsumerInstance()**  | @After     | KafkaUtilities                         | Always triggered if KafkaUtilities is used              | The killConsumerInstance hook used to terminate a Kafka consumer instance after a test scenario, logging the process and handling errors.                                             |
   | **threadCleanUp()** | @After  | GeneralUtilities                                   | Always triggered if GeneralUtilities is used                 | This hook is used for cleaning up threads after each test scenario. It ensures that no residual data is left in the threads.                                                      |
   | **closeConnectionPool()** | @AfterAll | DatabaseUtilities                               |  Always triggered if DatabaseUtilities is used | This hook is used to close all active and idle connections in the connection pool after all tests have been executed. This ensures that no residual connections are left open, which could potentially lead to memory leaks or other issues.     |
   




## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Parallel execution`**

**Parallel execution** is a computing concept where multiple tasks are executed simultaneously to speed up processing. This can be achieved through various methods, such as multithreading or multiforking.

* **Profiles and Properties in Maven pom.xml:**
   * Three profiles are defined in STAF parent pom.xml for executing test cases in parallel ( Multi Forking(features only) and Multi Threading (features&scenarios)). 
   * featuresParallel profile is defined as a default profile with threadCount 1. Teams can execute their test cases with below maven command line as usual. (By using the below command test cases will run sequentially)
        
        `mvn clean test  -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags="@BVT" -Denv=int-man-03`  


* **Parallel execution with Multi Threading can be achieved in two different levels:**
    
    **Multi-threading is a recommended option only if the code and all its dependencies are thread-safe.** If the code or its dependencies are not thread-safe, then multi-threading is not a viable option.
    * **Parallel execution with Multi Threading at feature level - with the help of Junit runner:** 
      Default threadCount is 1 in pom.xml and if user want to increase or decrease threadCount in maven command: -DthreadCount=[threadNumber]      
     
        `mvn clean test  -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags="@BVT" -Denv=int-man-03 -DthreadCount=5`
 

   * **Parallel execution with Multi Threading at scenario level - with the help of TestNG runner:**
     Default threadCount is 1 in pom.xml and if user want to increase or decrease threadCount in maven command : -DthreadCount=[threadNumber]
        
       `mvn clean test  -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags="@BVT"-Denv=int-man-03  -PscenariosParallel -DthreadCount=5`
       
       
* **Parallel execution with Multi Forking is done at Feature Files level:** 
    
    **Multiforking is an option for those who do not yet have thread-safe code.**
    In order to execute test cases increase or decrease forkCount in maven command with adding: -DforkCount = [forkNumber] 
       
    For Example: `mvn clean test -Dcucumber.filter.tags="@BVT" -Denv=int-man-02 -DforkCount=2`


 **The following confluence page provides a brief explanation of parallel execution in STAF:**

Parallel Execution Steps : https://de.confluence.agile.vodafone.com/x/Rx27Bg


 
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Logging`**

**Logger Configuration:**

STAF uses Logback for logging and includes following master config file:
* File logging (default): [STAF-logback-fileLog.xml](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/GeneralUtilities/src/test/resources/STAF-logback-fileLog.xml)

By default, logs are generated in: target/Logs/scenarioName.log per scenario and additionally error logs are logged on console

**Logger Usage:**
* **Create logger object:**

  ```
  private static final Logger log = LoggerFactory.getLogger(class-name-here.class);
  ```
  
* **Use logger object**
     ```
       log.info("logging info");
       log.warn("logging warning");
       log.error("logging error");
       log.trace("Tracing logs");
       log.debug("Debugging logs");
    ```
  
**Customizing logback configuration**

The default file logger configuration is set in the STAF parent pom.xml:
```
<properties>
	<logback.configurationFile>STAF-logback-fileLog.xml</logback.configurationFile>
</properties>
```
To use your custom logback config file, update the property in your pom.xml:
```
<properties> 
	<logback.configurationFile>path/to/logback-config-file.xml</logback.configurationFile> 
</properties>
```
Or set the system property from the Maven command line:
```
-Dlogback.configurationFile=path/to/logback-config-file.xml
```
**Default Logger Level**
By default, the root logger is set to INFO in the Logback configuration file. This default ensures a balanced level of output for most development and production environments.

**Overriding the Logger Level**
When you need a different logging level, pass the log.level system property to your Maven command:

```
-Dlog.level=TRACE
```

**Available Log Levels and Their Behaviors**

Each log level controls which messages are emitted by the root logger:

| **Level** | **Behavior** |
|-------|----------|
| TRACE | Finest-grained events. Enables tracing of the full application flow with very high verbosity. |
| DEBUG | Detailed diagnostic information on internal state and variable values. |
| INFO  | General operational messages such as startup, shutdown, and configuration details. |
| WARN  | Potentially harmful situations that do not prevent the application from running. |
| ERROR | Error events that may still allow the application to continue running. |
| OFF   | Disables all logging output. |


**Log Level Hierarchy**
TRACE > DEBUG > INFO > WARN > ERROR > OFF

**Note:** If you are not using the default [STAF-logback-fileLog.xml](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/GeneralUtilities/src/test/resources/STAF-logback-fileLog.xml), please ensure to add below configuration in custom logback.xml:

```xml
<property name="LOG_LEVEL" value="${log.level:-INFO}" />
<root level="${LOG_LEVEL}">
  <appender-ref ref="YOUR_APPENDER"/>
</root>
```

 **For brief explanation of Logback Logging in STAF please refer the below confluence page** 
 
 https://de.confluence.agile.vodafone.com/x/nG99Cw

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Xray logging toggle`**

**Purpose**

Provide a utility-level toggle that lets you control whether to log step details to Xray (Jira). From a user perspective this feature let user to choose between three behaviours at runtime: keep Xray attachments enabled, turn off all Xray attachments globally, or suppress attachments only for specific utilities by supplying a short, comma‑separated list.

**Features**
* User-controlled Xray logging : allow users to enable or disable step attachments to Xray at runtime via configuration.
* Global disable : one-step switch to turn off Xray logging for all utilities (e.g., SKIPLOGSTEPINJIRA=true).
* Per-utility disable :disable Xray logging for specific utilities by listing comma-separated utility tokens (e.g., SKIPLOGSTEPINJIRA=apiutilities,webutilities).
* Safe default : Xray logging remains enabled when no configuration is provided.
  
**Configuration and usage**

* System property name (pass to Maven): -DskipLogStepInJira=(value)

* Accepted values:

	true — disable Xray logging globally.

	false — explicitly enable logging.

	comma-separated utility tokens — disable logging only for listed utilities.

	no value supplied — default behaviour (logging enabled).

**Examples**

* Skip only API utilities:

```
-DskipLogStepInJira=apiutilities
```
* Skip multiple utilities:

```
-DskipLogStepInJira=apiutilities,webutilities
```
* Disable all Xray logging:

```
-DskipLogStepInJira=true
```
* Keep logging explicitly enabled:

```
-DskipLogStepInJira=false
```


**Available utilities (use these tokens)**

Set the skip value to one or more of the following tokens (comma separated):

| **Token Values** |
|-------|
| apiutilities |
| databaseutilities |
| generalutilities  |
| interframeworkutilities  |
| kafkautilities |
| tdmutilities   |
| webutilities   |
| wiremockutilities   |


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Reporting`**


**Xray** is a test management tool for Jira that provides a structure to organize, plan, and report on the progress of your testing. **Xray is used for official test documentation / reporting purpose**, It offers a feature called Test Execution, which acts as an assignable task for executing a group of tests (either manual or automated) and collects the corresponding test results for a specific project version and environment.
 
The **Test Executions Report** in Xray enables you to evaluate relevant metrics of Test Executions and compare them between different cycles. This report can help you analyze both the progress of the Test Execution and the success rate (i.e., the % of Tests contributing to the requirement's OK status), see the number of manual Tests vs. others in the Test Execution, see the overall execution status (i.e., the current status of the Test Runs), and see the number of opened/closed linked defects, in the context of the Test Execution. **In summary, Xray's Test Execution feature and its reporting capabilities provide a comprehensive overview of your testing progress, helping you ensure readiness for deployment.**

**Extent Reports** is an open-source reporting library used in test automation. **This report helps in understanding the overall health of the system under test and in identifying any problematic areas that need attention.** Extent report is only intended **for debugging purposes, but not the official test documentation.**

The path in STAF where the Extent report is generated: **target/ExtentReport/ 20-12-2023 12-59-47.620/ExtentReport.html**




## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

Confluence pages for STAF link : https://de.confluence.agile.vodafone.com/x/C4SoAw

STAF GitHub pages link: https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**


STAF FAQs pages link: https://de.confluence.agile.vodafone.com/x/pZkIBQ



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Contribution to the STAF`**

**Pull Request Process:**
1. **Create a new feature branch**: This is usually done from the staging branch.
2. **Implement the feature**: Write the code for your feature in this new branch.
3. **Test the feature branch**: Ensure that your feature works as expected and doesn't break any existing functionality.
4. **Create a pull request onto the testing branch**: Create a pull request from feature branch to testing branch. it will automatically trigger Continuous Integration (CI) jobs. it will also do compilation checks & sonarqube analysis. 
5. **Peer Review and Merge the pull request**: After triggering the CI jobs, a peer review must be conducted to complete the PR approval process. Once approved, the merge operation can be performed.
6. **Create a pull request onto the staging branch**: If everything works fine on testing branch then create a pull request from testing to staging branch. it will automatically trigger Continuous Integration (CI) jobs and the code will be automatically merge into the staging branch.
7. **Create a pull request onto the main branch**: The final is to create a pull request from staging branch to main branch. This will automatically trigger Continuous Integration (CI) jobs and the code will be automatically merge into the main branch (i.e. production branch).
8. **Artifact creation**: The main branch typically represents the most stable, up-to-date version of the codebase. When changes are merged into the main branch, we initiate a manual build process to create an artifact. This artifact can then be tested, deployed, or distributed as needed. This process ensures that the artifact is always in sync with the latest, stable version of the code.



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Contributors`**


The contributors are the heart of this project. They are the ones who help this project grow by contributing their skills, time, and resources. They can contribute in various ways:
 
- **Code Contributions**: Submitting fixes, features, and improvements to the code.
- **Documentation**: Writing, updating, and improving the documentation.
- **Testing & Reporting Issues**: Testing the application, reporting bugs, and issues.
- **Ideas & Feedback**: Providing ideas for new features and giving feedback on the project.

The following confluence page provides the contributors brief overview of team SPARTAN:

Spartan's confluence page Link: https://de.confluence.agile.vodafone.com/x/6eO8AQ
