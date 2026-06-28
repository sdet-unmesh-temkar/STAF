## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

* [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md#--description)
* [`Getting Started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md#--getting-started)
* [`Main features with sample code snippet`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md#--main-features-with-sample-code-snippets)
* [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md#--documentation)
* [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/ProcessBuilderUtilities/README.md#--troubleshoot)


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**


**ProcessBuilderUtilities** within STAF are developed to perform various operations on the command line. It offers various features to simplify the usage of ProcessBuilder. It provides methods for easily setting the command and arguments for the process, as well as configuring the working directory, environment variables, and input/output streams.

**Release notes** : This confluence page describes changes in recent versions of STAF framework. Its primary objective is to document the changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

ProcessBuilderUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. To import ProcessBuilderUtilities into a Maven project, **add the dependency below to your POM.xml file**. 


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
        <artifactId>ProcessBuilderUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>ProcessBuilderUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>

    



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`
* **To fire commands:** Predefined functions are available to fire commands, If user wants to fire commands depends on mode, they can call the **fireCommand(String cmd, String mode)** function as shown in the example below.
    ```
    //To execute command on windows machine
    String cmd = "cmd.exe","/c","cd " + dir," & javac " + mapClassName + ".java -cp " + pathToProjectClasses
    String mode = "cmd.exe~/c";
    CommonProcessBuilderMethods commonProcessBuilderMethods = new CommonProcessBuilderMethods();
    commonProcessBuilderMethods.execCommand(cmd,mode);
    
    //To execute command on linux machine
    String cmd = "cmd.exe","/c","cd " + dir," & javac " + mapClassName + ".java -cp " + pathToProjectClasses
    String mode = "sh~-c";
    CommonProcessBuilderMethods commonProcessBuilderMethods = new CommonProcessBuilderMethods();
    commonProcessBuilderMethods.execCommand(cmd,mode);
    ```
    
    

* **To fire command depends on mode:** To fire command depends on the mode (the operating system (i.e. Windows/Linux) on which the command will be executed), a user can call the **fireCommand(String command, String mode)** predefined step definitions.
   * command - To perform a certain task or operation. It represents a specific action or instruction that needs to be executed. 
   * mode    - the "mode" is determined by the operating system where the command is running. If it's a Windows system, we use `cmd.exe` to execute the command; if it's a Unix-like system (like Linux or Mac), we use `/bin/bash`.
   
   ```
    # To execute command:
      When ProcessBuilder Fire 'command' with 'mode' 
    ```

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for ProcessBuilderUtilities link : 

https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/processbuilderutilities/package-summary.html


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : https://de.confluence.agile.vodafone.com/x/pZkIBQ 


