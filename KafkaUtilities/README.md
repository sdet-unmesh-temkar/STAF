## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

  
 * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/KafkaUtilities#--description)
 * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/KafkaUtilities#--getting-started)                         
 * [`Main features with sample code snippets`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/KafkaUtilities#--main-features-with-sample-code-snippets)      
 * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/KafkaUtilities#--documentation)            
 * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/KafkaUtilities#--troubleshoot)   
    
    
    
## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**


**KafkaUtilities** within STAF are developed to connect to the Kafka servers and perform the operations there. Thus, Producer and Consumer can be created, Kafka events can be produced and Kafka event records can be retrieved for Topics.
The user can perform tasks such as creating and managing topics, monitoring the health and performance of your Kafka brokers, producers, and consumers, and analyzing the data flowing through Kafka streams with Kafka Utilities. It also offers various administrative capabilities, including managing consumer groups, configuring security settings, and handling data replication.


**Release notes:** This confluence page describes changes in recent versions of STAF. Its primary objective is to document changes that are of interest to users.

https://de.confluence.agile.vodafone.com/x/27OuBw
   



## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

KafkaUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. 
To import KafkaUtilities into a Maven project, **add the dependency below to your POM.xml file**.

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
        <artifactId>KafkaUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>KafkaUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>
    

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features with sample code snippets`**
#### `Main features, Methods and Step Definitions:`

* **`Pre-Requisites to use the Kafka Utilities in the Hashicorp Vault: `**


Hashicorp vault is a tool used for centralized secrets management. The user can store their static & dynamic secrets in hashicorp vault. In the following link, you can see how values and credentials can be stored in the Hashicorp vault application.

Secrets management using hashicorp vault: https://de.confluence.agile.vodafone.com/x/ceNHDQ

In order to use the KafkaUtilities correctly, configuration data must be specified and used as a prerequisite. Since these data are secrets and credentials, they are stored in the Hashicorp vault application. The data is stored in the environments here, under a specific file name, the data is stored in the form of key and value. 

The path, which we specify in the snippet code sections of the following examples of KafkaUtilities, specifies the location of the configuration data to be used.

In the following snippet code example, you can see an example of the required Kafka configuration data.

         {
            "auto.offset.reset": "earliest",
            "bootstrap.servers": "brokerURL",
            "client.id": "****",
            "enable.auto.commit": "**", [Note: If this parameter is set to true then Kafka Consumer will not consume the messages which are already been consumed.]
            "group.id": "****",
            "keystore.p12": "(content of keystore in encoded format)",
            "max.poll.records": "1",
            "security.protocol": "****",
            "ssl.key.password": "****",
            "ssl.keystore.password": "****",
            "ssl.keystore.type": "****",
            "ssl.truststore.password": "****",
            "truststore.jks": "(content of keystore in encoded format)"
          }
          

* **`How to encode keystore file and truststore file and fetch the contents : `**

Hashicorp vault takes only key-value pairs and does not have the option to keep files. For this reason, the user should encode keystore and truststore file content and put it in the Hashicorp vault. The user should follow following steps:  


Step 1: Open Win64 OpenSSl Command Prompt


![image](https://github.vodafone.com/storage/user/25260/files/3e3cfd6b-cecd-4638-83a3-eb23afebf8c2)


Step 2: Enter below command "**openssl base64 -A -in <path of the .keystore/.trustore file>**" and hit **Enter.**


Step 3: Copy the contents and paste it in vault.
          

KafkaUtilities is used basically with the following activities:


* **`Produce Event or Message: `** The user can produce a record or an event at this step for a specific topic. The test steps used in the feature file to perform this process can be seen in the following:
         
         Scenario Outline:Kafka connectivity
         Given Kafka - we configure a Producer based on the configuration in the Hashicorp Vault path '<path>'
         And Kafka - we produce an event by referring to the json file '<JsonFile>' for the topic '<topic>'
         Examples:
         | topic                             | path                     | JsonFile  |
         | compositeOrderFalloutNotification | MOD/MoDFormValues        | KafkaJson |
       
 
The user can produce an event or message using the **producer(path)** method. The **path** used as a parameter in the method points to the location of the Producer details in the Hashicorp vault application. The user uses the **producerClient(topicName, data)** method to produce Kafka events.  The method uses **topicName** and **data** as parameters. **topicName** parameter is provided from the feature file. **data** parameter is the String form of a property file(JSON file). The user uses this file as a reference for producing event for the topic. 

         // Create Producer
         TestContext<Object> testContext = TestContext.getInstance();
         FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();
         KafkaCommonMethods kafkaCommonMethods = KafkaCommonMethods.getInstance();
         KafkaClients kafkaClient = new KafkaClients();
         public static final String path = "MOD/MoDFormValues"; 
         Producer<String, String> producer = kafkaClient.producer(path);
         testContext.setProperty("kafkaProducer", producer);
         
         // Produce Kafka event
         public static final String fileName = "KafkaJson";
         public static final String filePath = ""src/test/resources/KafkaJsonFiles/" + fileName + ".json""; 
         public static final String data = fileSpecificUtilities.readFileAsString(filePath);
         public static final String topicName = "compositeOrderFalloutNotification";
         kafkaCommonMethods.producerClient(topicName, data);
         

         
      
* **`Consume Kafka Message: `** In this section the consumer details are configured and the message is consumed for the specified topic. After creation of consumer, the user can retrieve event records for the Kafka topic. For this step, the user has two options to implement it.

In the first option, only the topic name is used as a parameter. Topic value is sent from feature file. In the second option, the user can make the pollingTime(duration of polling in milliseconds) and pollingAttempts(number of polling attempts) parameters adjustable. The user can see this test step in the following snippet code on the commented line.


        Given Kafka - we configure a Consumer based on the configuration in the Hashicorp Vault path '<path>'
        When Kafka - we retrieve event records for the topic '<topic>'
       #When("Kafka - we retrieve event records for the topic {string} with a polling interval of {long} milliseconds and poll for {int} times or until we receive an event")
        Examples:
        | path                     | topic                             |
        | MOD/MoDFormValues        | compositeOrderFalloutNotification |
        
        
The user can consume the message using the **consumer(path)** method. The **path** used as a parameter in the method points to the location of the consumer details in the Hashicorp vault application.


During the implementation of retrieving Kafka event records step, the **retrieveKafkaEvents(topicName)** method is used. The method has the parameter of **topicName**. It is the name of the topic for which the event has to be produced. The default values for pollingTime and pollingAttempts parameters are used and default values are defined as pollingTime 500 and as pollingAttempts 25.  


The user can make the pollingTime and pollingAttempts parameters adjustable. The **retrieveKafkaEvents(topicName, pollingTime, pollingAttempts)** method can use the adjustable pollingTime and pollingAttempts parameters along with the method topicName. This implementation checks if the user has received any messages during the pollingTime time interval. If no message is found, this process continues until the maximum number of pollingAttempts is reached. If there will be more than pollingAttempts, the loop is broken. 
If any messages are received within the pollingTime, the loop is ended and the received messages are saved as topicName.
        
        // Create consumer
        TestContext<Object> testContext = TestContext.getInstance();
        KafkaCommonMethods kafkaCommonMethods = KafkaCommonMethods.getInstance();
        public static final String path = "MOD/MoDFormValues"; 
        Consumer<String, String> consumer = kafkaClient.consumer(path);
        testContext.setProperty("kafkaConsumer", consumer);
        
        // Retrieve events for topic with default pollingTime and pollingAttempts values
        public static final String topicName = "compositeOrderFalloutNotification";
        kafkaCommonMethods.retrieveKafkaEvents(topicName);
        
        // Retrieve events for topic with adjustable pollingTime and pollingAttempts values
         public static final Long pollingTime = 500L;
         public static final Integer pollingAttempts = 20; 
         kafkaCommonMethods.retrieveKafkaEvents(topicName, pollingTime, pollingAttempts);
        
  
 

 

        

* **`Kafka Message Validation: `** This section is used to validate the retrieved Kafka event records and is used after consuming messages. The expected validation values are sent from the feature file by the user.  

        Then Kafka - we validate the event records we have obtained with the following list
          | validation          |
          | "country": "DEU"    |
          | "streetNr": "60000" |
          
          
 The user can perform the validation process by following the steps in the snippet code below.        
          
         
        KafkaCommonMethods kafkaCommonMethods = KafkaCommonMethods.getInstance();
        DataTable validationList = DataTable.create(List.of(List.of("validation"), List.of("country: DEU"), List.of("streetNr: 60000")));
        Assert.assertTrue(kafkaCommonMethods.validateConsumerRecords(validationList), "Required validation on retrieved kafka records :-> ");
        
  
* **`Admin Client Creation: `** 
The user can also create  Kafka Admin client. The Admin client supports managing and inspecting topics, brokers, configurations and ACLs(Access Control Lists). To create an Admin client, the user can use the **admin(path)** method. The **path** used as a parameter in the method points to the location of the Admin details in the Hashicorp vault application.
 
 
         TestContext<Object> testContext = TestContext.getInstance();
         AdminClients adminClient = new AdminClients();
         public static final String path = "MOD/MoDFormValues"; 
         Admin aClient = adminClient.admin(path);
         testContext.setProperty("kafkaAdminClient", aClient);      
        
       
      

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for KafkaUtilities: https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/kafkautilities/package-summary.html





## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**
STAF FAQs pages link: https://de.confluence.agile.vodafone.com/x/pZkIBQ

 

