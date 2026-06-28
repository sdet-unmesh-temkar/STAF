## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

  * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md#--description)
  * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md#--getting-started)                         
  * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md#--main-features-with-sample-code-snippets)      
  * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md#--documentation)                    
  * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/APIUtilities/README.md#--troubleshoot)                            


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**

**STAF's APIUtilities** is a versatile set of tools and utilities designed to enhance the functionality and capabilities of the APIs. It provides a range of features such as **request and response logging**. APIUtilities should make requests to your **API endpoints** and **assert** the expected response. APIUtilities can be used to trigger RestAssured methods like **get(), post(), put(), delete() and patch()**. It also provides different Authentication of Rest Assured such as **Basic Auth, Bearer, OAuth (1.0), OAuth (2.0)** to check the necessary permissions to perform the requested action. Overall, APIUtilities is a valuable tool for developers looking to trigger an **API request** and **validate** responses.

**Release notes** : This confluence page describes changes in recent versions of STAF. Its primary objective is to document the changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

APIUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. To import APIUtilities into a Maven project, **add the dependency below to your POM.xml file**. 


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
        <artifactId>APIUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>APIUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`
         
* **`Trigger rest assured POST request:`** Predefined functions are available to trigger rest assured POST request, if the user wants to trigger a POST request they can call the step definition as shown below. **POST is used to send data to a server to create/update a resource**. The parameter used is the path of the json request body file that is used to trigger POST request. The file should be by default stored under "/src/main/resources/JSONFiles" folder. The path to **'JSONFiles'** has been prefixed by a script, so it can be used as the default.
    
   * To connect to REST services, you need to send an HTTP request to the server in the form of a web URL as **HTTP GET or POST or PUT or DELETE** request. After that, a response comes back from the server in the form of a resource which can be anything like HTML, XML, Image, or JSON. 
   * **Adding headers to your REST requests can help you to communicate more effectively with the API server** and enable you to customize the API’s behaviour.
   * The Hashicorp Vault is an ideal location to store below parameters. Please refer to the following URL to learn how to **use Hashicorp Vault**.
     
       https://de.confluence.agile.vodafone.com/x/ceNHDQ
     
     * BaseServiceURL   - https://jsonplaceholder.typicode.com 
     * ResourceName     - (Resource_Path)
     * HeaderName       - (Authorization, Content-Type)
     * HeaderValue      - (Bearer Token, application/json)
    
   ```
      # To trigger a POST request using predefined step definitions:
         Given API with BaseServiceURL 'VaultPathFor_BaseURL' and ResourceName'VaultPathFor_ResourceName'
         When API 'VaultPathFor_HeaderName' and 'VaultPathFor_HeaderValue' is added to request
         When API POST request for 'folderName/jsonRequestFileName' is sent
   ```
   
   ```
      // To  trigger a POST request using predefined functions:
        import io.restassured.RestAssured;
        import static io.restassured.RestAssured.given;
        import io.restassured.response.Response;
        import io.restassured.http.ContentType;
        import org.junit.Assert;
        
        private static String requestBody = "{\"name\":\"abc\", \"language\":\"test\", \"id\":\"V59OF\", \"bio\":\"Donec hjgsdj\", \"version\":6.1 }";
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/Resource")
                .then()
                .extract().response();
        Assert.assertEquals(201, response.statusCode());
    ```  
     
* **`Trigger rest assured GET request:`** Predefined functions are available to trigger rest assured GET request, if the user wants to trigger a GET request they can call the step definition as shown below. **The GET method is used to 'retrieve' a record or a collection of records from the server**. To trigger a GET request using REST Assured, you can use the get() method from the REST-assured library.
     
   ```
      # To trigger a GET request using predefined step definitions:
        Given API with BaseServiceURL 'VaultPathFor_BaseURL' and ResourceName'VaultPathFor_ResourceName'
        When API 'VaultPathFor_HeaderName' and 'VaultPathFor_HeaderValue' is added to request
        API GET request for is sent
   ```
   
   ```
      // To  trigger a GET request using predefined functions:
      import io.restassured.RestAssured;
      import io.restassured.response.Response;
      
      private String baseUrl = "https://test.restapiexample.com/";
      Response response = RestAssured.get(baseUrl);
      LOG.info("Status Code={}", response.getStatusCode());
   ```   

       
* **`Trigger rest assured PATCH request:`** Predefined functions are available to trigger rest assured PATCH request, if the user wants to trigger a PATCH request they can call the step definition as shown below. **The PATCH method is a request method in HTTP for making partial changes to an existing resource**. The parameter used is the path of the json request body file that is used to trigger PATCH request. The file should be by default stored under "/src/main/resources/JSONFiles" folder. The path to **'JSONFiles'** has been prefixed by a script, so it can be used as the default.
      
     ```
        # To trigger a PATCH request using predefined step definitions:
          Given API with BaseServiceURL 'VaultPathFor_BaseURL' and ResourceName'VaultPathFor_ResourceName'
          When API 'VaultPathFor_HeaderName' and 'VaultPathFor_HeaderValue' is added to request
          And API PATCH request for 'folderName/jsonRequestFileName' is sent 
     ```
     ```
       // To  trigger a PATCH request using predefined functions:
        import io.restassured.RestAssured;
        import io.restassured.response.Response;
        import io.restassured.http.ContentType;
        import static io.restassured.RestAssured.given;
        import org.junit.Assert;
        
        private static String requestBody = "{\"name\":\"abc\", \"language\":\"test\", \"id\":\"V59OF\", \"bio\":\"Donec hjgsdj\", \"version\":6.1 }";
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        Response response = given()
                .header("Content-type", "application/json-patch+json")
                .and()
                .body(requestBody)
                .when()
                .patch("//Resource")
                .then()
                .extract().response();
        Assert.assertEquals(200, response.statusCode());
   ```
   

* **`Trigger SOAP request:`** Predefined functions are available to trigger SOAP request, if the user wants to trigger a SOAP request they can use the below code snippet. The file should be by default stored under "/src/main/resources" folder. Refer below url to use Hashicorp vault. 
     
  https://de.confluence.agile.vodafone.com/x/ceNHDQ
  
  ```
         
         
  ```  
   
  ```
        CommonSoapAPIMethods commonSoapAPIMethods=new CommonSoapAPIMethods();
        private static Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
        String filePath   = "SoapRequest/CreateBusinessCustomer";
        String baseUri    = environment.get("SAP/BaseURI");
        String basePath   = environment.get("SAP/BasePathCreateCustomer");
        String header     = environment.get("SAP/HeaderCreateCustomer");
        String userName   = environment.get("SAP/Username");
        String password   = environment.get("SAP/Password");
        Document document = commonSoapAPIMethods.readSOAPXMLFile(filePath);
        commonSoapAPIMethods.triggerSOAPRequest(document,baseUri,basePath,header,userName,password);  
   ```       
       



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for APIUtilities link : 

https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/apiutilities/package-summary.html


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : https://de.confluence.agile.vodafone.com/x/pZkIBQ

  
