## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/GeneralUtilities#--description)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/GeneralUtilities#--getting-started)
 * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/GeneralUtilities#--main-features-with-sample-code-snippets)
 * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/GeneralUtilities#--documentation)
 * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/GeneralUtilities#--troubleshoot)


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**

**GeneralUtilities** within STAF are developed to provide a wide range of utility functions and tools that can be used in various programming tasks. **It offers a collection of helpful functions such as vault connectivity and configuration, generation of the final execution report, environment configuration, as well as it also perform common operations on file management, application context, and testContext related operations.** Overall, GeneralUtilities aims to enhance the efficiency and productivity of programming tasks by providing a collection of useful functions.

To simplify the automation process while using the Solstice Test Automation Framework, it is essential to import the GeneralUtilities. This Utilities is an integral part of the Solstice Test Automation Framework and provides a range of utility functions. By importing the GeneralUtilities, User can use these functions in their test scripts and make automation process more efficient. **So it is always necessary to import General Utilities while using STAF.**

**Release notes** : This confluence page describes changes in recent versions of STAF. Its primary objective is to document the changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

 GeneralUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed.
To import GeneralUtilities into a Maven project, **add the dependency below to your POM.xml file**.

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
        <artifactId>GeneralUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>GeneralUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`
* **Application Context**: Predefined functions are available **to share data across different scenarios at the application level.** These functions provide methods to perform various operations on the database such as setting and getting data, and checking if certain data exists. If a user wants to use this feature to share data across scenarios at the application level, they can use the following code snippet:



      //To set/get automation database data
      private static final String key = "System_Dummy_Key";
      private static final String value = "3ef8cc42c5c5ed0f7a1";
      ApplicationContext applicationContext = new ApplicationContext();
      applicationContext.setAutomationDBData(key, value);
      String retrievedValue = applicationContext.getAutomationDBData(key);


* **Test Context**: Predefined functions are available **to share data within a test case. Then this feature can be used.** These functions offer methods to execute various operations on the database, such as setting and retrieving data, and verifying the existence of certain data. If a user wants to utilize this feature to share data within a test case, they can use the following code snippet:

    ```java
     //To set/get test steps database data
     private static final String key = "TestS_Dummy_Key";
     private static final String value = "4gf9dd52d6d6fe1g8b2";
     TestContext<Object> testContext = TestContext.getInstance();
     testContext.setProperty(key, value);
     String fetchedValue = testContext.getProperty(key);
    ```


* **`Secrets Management via Hashicorp Vault:`**  Predefined functions are available for secrets management via HashiCorp vault. Hashicorp vault is a tool used for centralized secrets management. The user can store their static & dynamic secrets in hashicorp vault. **Storing has to be done via the Hashicorp Vault Frontend, and only reading should be done from the code.** If a user wants to retrieve secrets from the HashiCorp vault, they can use the below code snippet.

    ```java
    private static final Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    public static final String SYS_PASSWORD = environment.get("STAF_UTILITIES/SYS_PASSWORD");
    //STAF_UTILITIES/SYS_PASSWORD - Path of the secrets in HashiCorp vault
    ```


 * **`PDF Comparison:`**  Predefined functions are available for pdf comparison.

     To extract text from a PDF file, the example below describes how to do.

      ```java
     private static final String fileName = "C:\\Users\\Individual\\FolderName\\file.pdf";
     try {
        String extractedPdfText = PDFComparison.readPDF(fileName);
     }
     catch () {
        LOG.warn("Could not read PDF file at location {}", fileName);
     }
      ```

     To extract an image from a PDF file and save it in a folder, the example below describes how to do.
      ```java
     private static final String fileName = "C:\\Users\\Individual\\FolderName\\file.pdf";
     private static final String destination = "C:\\Users\\Individual\\FolderName";
     try {
        PDFComparison.imageCapture(fileName, destination);
     }
     catch () {
        LOG.warn("Could not read PDF file at location {}", fileName);
     }
      ```

     To compare two PDFs via visual representation (pixel-by-pixel image comparison), the example below describes how to do.
      ```java
     private static final String file1 = "C:\\Users\\Individual\\FolderName\\file1.pdf";
     private static final String file2 = "C:\\Users\\Individual\\FolderName\\file2.pdf";
     private static final String resultPath = "C:\\Users\\Individual\\FolderName\\folder_result";
     try {
        PDFComparison.comparePDF(file1, file2, resultPath);
     }
     catch () {
        LOG.warn("Could not read PDF file at location {} or {}", file1, file2);
     }
      ```

    > [!NOTE]
    > The implementation behind PDF comparison is [pdf-util](https://github.com/vinsguru/pdf-util) with option VISUAL_MODE, which transforms both PDFs into images and does a pixel-to-pixel comparison (no text comparison!). Therefore, it is not suitable for accessibility tests (like requirements in ["BFSG"/EN 301 549](https://bfsg-gesetz.de/)).

     To extract an image from PDF and compare it with the given image, the example below describes how to do.
     ```java
     private static final String file = "C:\\Users\\Individual\\FolderName\\file1.pdf";
     private static final String originalImage = "C:\\Users\\Individual\\FolderName\\logo.png";
     try {
        PDFComparison.checkPDFLogoImage(file, originalImage);
     }
     catch () {
        LOG.warn("Could not read PDF file at location {} or image at location {}", file, originalImage);
     }
      ```

 * **`ZipUnzip file:`**  Predefined functions are available for zip and unzip the file. If the user wants to use this feature, they can call the **zipFile(File file, String outputPath, String zipFileName)**/**unzip(String zipFilePath, String destFilePath, String password)** function, as shown in the following example.

    ```java
     private static final String zipFilePath = "C:\\Users\\username\\folder1";
     private static final String destFilePath = "C:\\Users\\username\\folder2";
     private static final String outputPath = "C:\\Users\\username\\folder3";
     private static final String password = "nbt44tagamm@";
     File file = new File("path_to_your_file");
     String zipFileName = "name_of_your_zipped_file";
     ZipUnzip zipUnzip = new ZipUnzip();
     //To zip file
     zipUnzip.zipFile(file, outputPath, zipFileName);
     //To unzip file
     zipUnzip.unzip(zipFilePath, destFilePath, password);
    ```

 * **`To read CSV file:`**  Predefined functions are available to read CSV file. The purpose of this function is to read a CSV file and save the data to a StringBuilder object, A StringBuilder is used to efficiently manipulate strings. If the user wants to use this feature, they can call the **readCSVFile(String filePath)** function, as shown in the following example.

    ```java
     private static final String filePath = "C:/Users/username/folder1";
     CSVUtilities csvUtilities = new CSVUtilities();
     csvUtilities.readCSVFile(filePath);
    ```


  * **`To read specific column data from excel file:`**  Predefined functions are available to read specific column data from excel file. If the user wants to read specific column data from excel file, they can call the **getColumnData(String excelFile, String sheetName, int columnIndex, int rowNum)** function, as shown in the following example.

    ```java
     private static final String curwd = System.getProperty("user.dir");
     private static final String fileName = "your_file_name"; // replace with your actual file name
     private static final String filePath = curwd + "/src/test/resources/XMLFiles/environments/" + fileName + ".xml";
     File excelFile = new File(filePath);
     private static final String sheetName = "dummy_ExcelSheet_Name";
     private int columnIndex = 20;
     private int rowNum = 30;
     ExcelUtils excelUtils = new ExcelUtils();
     excelUtils.getColumnData(excelFile, sheetName, columnIndex, rowNum);
    ```
* **`String Interpolation for data present in testContext or applicationContext or vault with syntax ${{}}`**  Predefined functions are available to string interpolate and fetch the values from testContext or applicationContext or vault **stringInterpolation(String value)** function, as shown in the following example.

   ```java
     StringInterpolation stringInterpolation= new StringInterpolation();
     TestContext.getInstance().setProperty("keyExample","keyValue");
     String value=stringInterpolation.stringInterpolation(${{testContext.keyExample}});
     System.out.println("Value fetched after string interpolation:  " +value); //Value fetched after string interpolation: keyValue

   ```
   If the user wants to use data present in testContext or applicationContext or vault in feature steps then following is example gherkin step and step definition. 
   
   ```gherkin
   Given I set data in customerName ${{vault.SAP/Username}} using testContext
   Then I print customerName prefix_${{testContext.name}}_suffix
   Then I print customerRole prefix_${{applicationContext.role}}_suffix
  
  ```
  ```java
    @Given("I set data in customerName {interpolatedString} using testContext")
    public void iSetDataInCustomerName(String name) {
        System.out.println("Setting CustomerName as "+name);
        testContext.setProperty("name", name);
    }
  
    @Then("I print customerName {interpolatedString}")
    public void iPrintCustomerName$TestContextCustomerName(String name) {
        System.out.println("CustomerName: "+name);
    }
  
    @Then("I print customerRole {interpolatedString}")
    public void iPrintCustomerRole$CustomerRole(String role) {
        System.out.println("CustomerRole: "+role);
    }
  
  ```

  * **`Read property file from the path:`**  Predefined functions are available to read property file. After reading the properties file, user can use the returned Properties object to retrieve property values by their keys. For example, user might have properties for database connection details, application settings, or other configuration data. If the user wants to read property file, they can call the **readPropertyFile(String fileName)** function, as shown in the following example.

```java
  private static final String fileName = "yourFileName.properties"; // replace with your actual file name
  FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
  Properties properties = fileSpecificUtilities.readPropertyFile(fileName);
  //to retrieve any property, use below syntax:
  String value = properties.getProperty("Property name/key as string");
 ```

 * **`EC2 Connectivity using AWS SSM:`**  Predefined functions are available to connect to EC2 machines by using AWS SSM. If the user wants to use these functions, they need to set up the IAM Role permissions of their account as described on the following confluence page by team Ungoliant https://de.confluence.agile.vodafone.com/x/WaQ8H. Then, they can call the step definition as shown below. After the connection is established, the user can execute command on the instance.

   * Pre-Requisites:
     * EC2 machine must be configured according to reference document at https://de.confluence.agile.vodafone.com/x/WaQ8H

   * Parameters:
     * <assume_role>    - The role to be assumed on the AWS account. e.g. "ghe-role-assume-dev"
     * <aws_account_id> - The AWS account ID on which the role is to be assumed. e.g. "123456789012"
     * <user_command>   - The user command to be executed on the instance. e.g. "ls -la /home/ec2-user"
     * <instance_id>    - The ID of the EC2 instance where the command will be executed e.g. "i-02agd7g9876ggb124"
     * <auto_scaling_group_name> - The name of the Auto Scaling Group where the EC2 instance is deployed. e.g. "cicd-forge-abc"

  ```
   # Predefined Step Definitions:

    - To connect EC2 machine and execute command on the instance via using <instance_id>:

         When AWS SSM - We assume the role '<assume_role>' on the AWS Account '<aws_account_id>'
         When AWS SSM - We execute the command '<user_command>' on the instance '<instance_id>'

    - To connect EC2 machine and execute command on one instance of the Auto Scaling Group via using <auto_scaling_group_name>:

         When AWS SSM - We assume the role '<assume_role>' on the AWS Account '<aws_account_id>'
         When AWS SSM - We execute the command '<user_command>' on one instance of the auto scale group '<auto_scaling_group_name>'

  ```

* **`Adding logs to extent report:`**  
  This utility provides method to log test steps to the extent report based on configurable report detail levels.

    *  Log Level Hierarchy:
    
        DEBUG > INFO > WARN

        * DEBUG: Captures the most detailed information, and also is set as default value.

        * INFO: Selectively logs informative messages and warnings, filtering out excessive debug-level verbosity while retaining essential events.

        * WARN: Displays only warning or potential issue-related logs, focusing on critical insights and minimizing unnecessary details. 
        
    *  How to Use: 
       
       * Add a Step with a Specific Log Level
       * You can add a step to the test report with a custom log level (`DEBUG`, `INFO`, or `WARN`). The message will be logged only if its level meets or exceeds the configured report detail level.
        ```java
        addStepToReport("This is an informational log", "INFO");
        ```
       * Add a Step with Default Log Level (DEBUG):
       If no log level is specified, the step will be logged at the default DEBUG level.
       ```java
       addStepToReport("This is a debug log");
       ```
       * Configuration: The logging behavior is controlled by the system property reportDetailLevel. You can set it in your test execution environment:
       ```bash
        -DreportDetailLevel=INFO
       ```
       Example Scenario:
       
       Assume reportDetailLevel is set to INFO.
       Calling:
       ```java
       addStepToReport("This is a debug message", "DEBUG"); // Will NOT be logged
       addStepToReport("This is an info message", "INFO");  // Will be logged
       addStepToReport("This is a warning message", "WARN"); // Will be logged
       ```
       


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for GeneralUtilities link : https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/generalutilities/package-summary.html

Secrets management using hashicorp vault : https://de.confluence.agile.vodafone.com/x/ceNHDQ

How to set system variables (Vault_ROLEID & Vault_SECRETID) on Windows, DevVM, AWS workspace and on Jcontroller :

https://de.confluence.agile.vodafone.com/x/p7v2Eg

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : https://de.confluence.agile.vodafone.com/x/pZkIBQ

