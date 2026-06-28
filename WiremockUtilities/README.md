## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md#--description
)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md#--getting-started)                         
 * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md#--main-features-with-sample-code-snippets)      
 * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md#--documentation
)                    
 * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/WiremockUtilities/README.md#--troubleshoot)                       
 

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**


__WiremockUtilities__ within STAF are developed with the intention of streamlining a tool used for simulating HTTP-based services. Through STAF, it offers functionalities like initiating and halting the WireMockServer, and launching and resetting the ValidationListener. Overall, WiremockUtilities is a valuable tool for anyone working with WireMock, as it enhances the capabilities and simplifies the usage of this powerful mocking framework.

**Release notes** : This confluence page describes changes in recent versions of STAF. Its primary objective is to document changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

WiremockUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. 
To import WiremockUtilities into a Maven project, **add the dependency below to your POM.xml file**.

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
        <artifactId>WiremockUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>WiremockUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`

* **`WireMockServer Start:`**  Predefined functions are available to start the WireMockServer. This class allows you to configure and start a standalone server that can simulate HTTP responses based on predefined mappings.These mappings specify the request URL, HTTP method, and the desired response. This allows you to simulate different scenarios and test your application's behavior against various responses. 
    ```
     // To start WireMockServer using predefined functions:
        Wiremock wireMock = new Wiremock();
        wireMock.wiremockStart(); 
     
      # To start WireMockServer using predefined step definitions:
        Given Wiremock start 'MoD'
    ```

* **`Start ValidationListener:`**  Predefined functions are available to start ValidationListener. The OpenApiValidationListener is a component used in software development to validate API requests and responses against an OpenAPI specification. With OpenApiValidationListener, developers can catch potential issues early on and improve the overall quality and reliability of their API implementation.

   * **YAML file -**  It provides a structured way to define the desired behavior of the server, allowing you to simulate and mock API responses for testing and development purposes.

    ```
     // To start start ValidationListener using predefined functions:
        private static final String yamlFilePath = "D:\User\mod\ApplicationName\src\test\resources\YamlFiles"
        Wiremock wireMock = new Wiremock();
        wireMock.startValidationListener(yamlFilePath); 
     
      # To start ValidationListener using predefined step definitions:
        Wiremock ValidationListener start with 'YamlFile'
    ```

* **`Start WireMockPactGenerator:`**  Predefined functions are available to build and start WireMockPactGenerator. It is a simple java proxy server that writes out all the interactions as pact files. WireMockPactGenerator captures HTTP request/response interactions with your WireMocks and generates Pact files so you can verify, via contract testing, that your mocks are consistent with the real provider. The Pact file is the contract both parties(Consumer & Producer) promise to adhere to. 
    * **Consumer -** Refers to the application or client that sends the API requests to the WireMock server. 
    * **Provider -** It is the WireMock server itself, which returns the simulated API responses for the consumer.
    ```
      // To start start PactGenerator using predefined functions:
         private static final String consumer = "https://dummy.restapiexample.com/api/v1/consumer"
         private static final String provider = "https://dummy.restapiexample.com/api/v1/producer"
         Wiremock wireMock = new Wiremock();
         wireMock.startPactGenerator(consumer,provider);
     
       # To start PactGenerator with Consumer and Provider:
         Wiremock PactGenerator with 'consumer' and 'provider'
    ```

* **`ValidateSwagger:`**  Predefined functions are available to ValidateSwagger.It is a useful feature in WireMockServer that allows you to validate the Swagger specification of your API. It helps ensure that the API responses generated by WireMockServer comply with the defined Swagger schema. By using ValidateSwagger, you can verify that the responses returned by your API match the expected structure and data types specified in the Swagger documentation. This can be particularly helpful in maintaining the consistency and correctness of your API implementation.
    ```
     // To start start PactGenerator using predefined functions:
        Wiremock wireMock = new Wiremock();
        wireMock.validateSwagger()
    
      # To validate WireMock against Swagger using predefined step definitions:
        Wiremock validate against Swagger
    ```
    
* **`WireMockServer Stop:`**  Predefined functions are available to stop the WireMockServer. **@After hook** is used in STAF so that one can not have to put extra effort to stop WireMockServer.
    ```
     # To stop WireMockServer using predefined functions:
       Wiremock wireMock = new Wiremock();
       wireMock.wiremockStop();  
    ```
    
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for WiremockUtilities link : 

https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/wiremockutilities/package-summary.html

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : 

https://de.confluence.agile.vodafone.com/x/pZkIBQ 

