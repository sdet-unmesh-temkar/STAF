## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Index`**

  * [`Description` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--description)
  * [`Getting started` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--getting-started)   
  * [`Terminology (specific to Interframework utilities)` ](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--terminology-specific-to-interframework-utilities)                       
  * [`Main features and their usuage`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--main-features-and-their-usuage)      
  * [`Documentation`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--documentation)                    
  * [`Troubleshoot`](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/blob/main/InterFrameworkUtilities/README.md#--troubleshoot)                            


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Description`**

**STAF's InterFrameworkUtilities** are designed to facilitate integration testing between STAF and other test automation frameworks by exchange inter-framework information over orchestration webservice developed by the ITQA department. These utilities enable STAF to :
* Initiate integration test cases in between alternate Test Automation frameworks, and
* Obtain prompts to continue next phases of orchestrated inter-framework integration tests scenarios in STAF, sourced from different TA frameworks.
* Store and retrieve test results for integration test cases


**Release notes** : https://de.confluence.agile.vodafone.com/x/27OuBw (This confluence page describes changes in recent versions of STAF. Its primary objective is to document the changes that are of interest to users.)





## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Getting Started`**

InterFrameworkUtilities require **Java** (JDK-17) and **Maven** (3.6.3 and above) to be installed. To import InterFrameworkUtilities into a Maven project, **add the dependency below to your POM.xml file**. 


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
        <artifactId>InterFrameworkUtilities</artifactId>
        <version>[Enter latest version]</version>
      </dependency>

      <dependency>
        <groupId>STAF</groupId>
        <artifactId>InterFrameworkUtilities</artifactId>
        <classifier>tests</classifier>
        <version>[Enter latest version]</version>
      </dependency>
    </dependencies>
    </project>


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Terminology (specific to Interframework utilities)`**

* **Inter-framework test:** An integration test which starts in a framework as initial phase and continues in other test automation frameworks as next phases.
* **Orchestration webservice:** This is webservice developed by the ITQA department and it facilitates inter-framework integration testing between two or more test automation frameworks. You can find more details in the Swagger documentation, which is available at the following URL: https://z3-test4b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/. Reachable on ubuntu dev-vm
* **Interframework test phase(s):** When an inter-framework integration test in a test automation framework completes its execution, it is considered to have concluded a phase of execution.
* **Test Suite:** It contains set of test cases for a particular flow to be exchanged in between two or more test automation frameworks participating in inter-framework test.
* **Custom data:** This is simply test data from the current phase, destined for use in the corresponding test case of the next phase.


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Main features and their usuage`**
**Primary use cases for Interframework testing in STAF:**

There are three use cases for Interframework testing in STAF:

* **Initiating inter-framework test suites from STAF**

* **Identifying & continuing inter-framework suites waiting for execution in staf**

* **Terminating inter-framework test suites**

Lets understand each of the above use case with an example. Consider an integration test where:

[1] The first phase of execution will be in STAF (we initiate the test suite)

[2] The second phase of execution will continue in 'otherTestAutomationName'

[3] Again, the third phase of execution will continue in STAF (@flowNameOneForSTAF1)

[4] The fourth phase of execution will continue in 'otherTestAutomationName'

[5] The final phase of execution (Termination) will be triggered in STAF (@flowNameTwoForSTAF1)

* **Initiating inter-framework test suites from STAF**
 
      #Feature file template in STAF for initiating inter-framework test suites for other TAs:

      Feature: Sample Inter-framework test suite
      
      @testCaseInitiationTemplate @InterframeworkJiraKey:SOLTESRP-25536
      Scenario Outline: Sample Inter-framework test case
      Given print on console 'Actual test case Step 1 in STAF'
      Given print on console 'Actual test case Step 2 in STAF'
       Given We prepare to initiate the inter-framework test case with following values
         | environment                | envForOtherTestAutomation         |
         | nextPhaseToBeExecutedIn    | otherTestAutomationName           |
         | requiredTestAutomationFlow | flowNameOneForOtherTestAutomation |
       And We update the custom data for the inter-framework test case with the following values
         | stafDataKeyOne | <stafDataValueOne> |
         | stafDataKeyTwo | <stafDataValueTwo> |
       Examples:
         | stafDataValueOne | stafDataValueTwo |
         | a1               | b1               |
         | a2               | b2               |
    
             
**Scenario Description:**
Above act as a template for initiating interframework test suites from staf. 
Use this github workflow for initiating suite: 
InterFrameworkUtilities/src/test/resources/interframework_workflows/InitiateInterFrameworkTestsFromSTAF.yml

After running above **@testCaseInitiationTemplate** cucumber test from the given workflow, a test suite will be generated as below, example 5614, & can be viewed using swagger. User can pass tesplan and xray label details as maven parameters like this 

	-DinterFrameworkTestPlan="SOLTESAU-20422" -DinterFrameworkXrayLabels="L1, L2, L3"


**Swagger URLs accessible on Ubuntu Dev-VM:**

https://z3-test1b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/

https://z3-test2b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/

https://z3-test3b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/

https://z3-test4b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/

https://z3-test5b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/


**Note: Credentials are required for all APIs (including Swagger access). Stored in Vault: kv-staf/DEV/TestSuiteDataReportClient_Service_Credentials**

![image](https://github.vodafone.com/storage/user/26896/files/ce30b6cb-279a-4a93-ab5b-56615ba50d58)

<details>
<summary><b>Suite ID 5614 current phase as in Swagger after initiating in STAF (Phase 1):</b></summary>

	{
	  "testSuiteName": "Sample Inter-framework test case",
	  "testSuiteDescription": "\nTEST_PLAN: SOLTESAU-20422\nXRAY_LABELS: L1, L2, L3",
	  "testSuiteRunId": 5614,
	  "sourceApplication": "Solstice",
	  "overallStatus": "READY",
	  "currentPhase": {
	    "environment": "envForOtherTestAutomation",
	    "executedBy": "Solstice",
	    "nextPhaseToBeExecutedIn": "otherTestAutomationName",
	    "requiredTestAutomationFlow": "flowNameOneForOtherTestAutomation",
	    "executionFinishTime": "2025-09-10T07:45:48+02:00",
	    "results": [
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "stafDataKeyOne": "a1",
	          "stafDataKeyTwo": "b1"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "print on console 'Actual test case Step 1 in STAF'",
	            "errorMessage": "",
	            "duration": 283,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 2 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We prepare to initiate the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 3,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We update the custom data for the inter-framework test case with the following values",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      },
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "stafDataKeyOne": "a2",
	          "stafDataKeyTwo": "b2"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "print on console 'Actual test case Step 1 in STAF'",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 2 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We prepare to initiate the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We update the custom data for the inter-framework test case with the following values",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      }
	    ]
	  },
	  "_links": {
	    "self": {
	      "href": "http://z3-test4b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/api/runs/5614"
	    }
	  }
	}
	

</details>


This concludes process of initiating inter-framework test suites in STAF
 
 -------------------------------------------------------------------------------------------------------------------------------------------------------------------

* **Identifying & continuing inter-framework suites waiting for execution in staf**

Extending above example, we initiated test suite ID 5614 for **"otherTestAutomationName"** as specified in parameter **"nextPhaseToBeExecutedIn":** **"otherTestAutomationName"**. now next phase will be executed in mentioned TA. After finishing its phase, otherTestAutomationName will update suites details back in service to continue the orchestrated flow(s) in other test automation as below, for example, in this case the next phase of this suite will continue in STAF.



<details>
<summary><b>Suite ID 5164 current phase as in Swagger after updated by otherTestAutomation: (Phase 2)</b></summary>


	{
	  "testSuiteName": "Sample Inter-framework test case",
	  "testSuiteDescription": "\nTEST_PLAN: SOLTESAU-20422\nXRAY_LABELS: L1, L2, L3",
	  "testSuiteRunId": 5614,
	  "sourceApplication": "Solstice",
	  "overallStatus": "READY",
	  "currentPhase": {
	    "environment": "DEV",
	    "executedBy": "otherTestAutomationName",
	    "nextPhaseToBeExecutedIn": "Solstice",
	    "requiredTestAutomationFlow": "flowNameOneForSTAF1",
	    "executionFinishTime": "2025-09-10T07:45:48+02:00",
	    "results": [
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "A1": "a1",
	          "B1": "b1"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "otherTestAutomation- Step 1",
	            "errorMessage": "",
	            "duration": 283,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "otherTestAutomation- Step 2",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          }
	        ]
	      },
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "A1": "a1",
	          "B1": "b1"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "otherTestAutomation- Step 1",
	            "errorMessage": "",
	            "duration": 283,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "otherTestAutomation- Step 2",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          }
	        ]
	      }
	    ]
	  }
	}
	


</details>



**Identifying suites waiting to continue next phase in STAF** 

STAF has setup github actions workflow:
(InterFrameworkUtilities/src/test/resources/interframework_workflows/Interframework_Ready.yml)
which queries and collects all test suites (using webservice client) which are to be executed in STAF (status READY) and if there are any READY suites, it triggers and executes respective tests.

In current example, for the suite 5614, based on the current phase detail parameters "nextPhaseToBeExecutedIn": "Solstice" and "requiredTestAutomationFlow": "flowNameOneForSTAF1", this cucumber scenario (as shown below) '@flowNameOneForSTAF1' will be triggered in intergration-test repo


      @testCaseContinuationTemplate @flowNameOneForSTAF1
      Scenario: Dummy scenario to demonstrate continuation of an orchestrated test case in STAF
      Given We prepare to continue the inter-framework test case with following values
        | environment                | envForOtherTestAutomation         |
        | nextPhaseToBeExecutedIn    | otherTestAutomationName           |
        | requiredTestAutomationFlow | flowNameTwoForOtherTestAutomation |
     Then We update the custom data for the inter-framework test case with the following values
        | stafDataKeyThree | stafDataValueThree |
        | stafDataKeyFour  | stafDataValueFour  |
        | stafDataKeyFive  | stafDataValueFive  |
     Given print on console 'Actual test case Step 1 in STAF'
     Given print on console 'Actual test case Step 2 in STAF'


Note: The above scenario serves as a template for interframework test case continuation from STAF. Avoid using Scenario Outline for continuation scenarios, as the continuation/termination steps are fixed and can be covered by a single scenario. Scenario Outline isn't applicable in this context.

Now next phase will be executed in STAF and after finishing its phase, STAF will update suites details back in service as below



<details>
<summary><b>Suite ID 5614 current phase as in Swagger after STAF execution @flowNameOneForSTAF1: (Phase 3)</b></summary>

	{
	  "testSuiteName": "Sample Inter-framework test case",
	  "testSuiteDescription": "\nTEST_PLAN: SOLTESAU-20422\nXRAY_LABELS: L1, L2, L3",
	  "testSuiteRunId": 5614,
	  "sourceApplication": "Solstice",
	  "overallStatus": "READY",
	  "currentPhase": {
	    "environment": "envForOtherTestAutomation",
	    "executedBy": "Solstice",
	    "nextPhaseToBeExecutedIn": "otherTestAutomationName",
	    "requiredTestAutomationFlow": "flowNameTwoForOtherTestAutomation",
	    "executionFinishTime": "2025-09-10T12:14:06+02:00",
	    "results": [
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "stafDataKeyThree": "stafDataValueThree",
	          "stafDataKeyFour": "stafDataValueFour",
	          "stafDataKeyFive": "stafDataValueFive"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "We prepare to continue the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 1003,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We update the custom data for the inter-framework test case with the following values",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 1 in STAF'",
	            "errorMessage": "",
	            "duration": 258,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 2 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      },
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "stafDataKeyThree": "stafDataValueThree",
	          "stafDataKeyFour": "stafDataValueFour",
	          "stafDataKeyFive": "stafDataValueFive"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "We prepare to continue the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 1057,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "We update the custom data for the inter-framework test case with the following values",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 1 in STAF'",
	            "errorMessage": "",
	            "duration": 258,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 2 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      }
	    ]
	  }
	}

</details>


      

Similarly, the next phase of this flow will continue in 'otherTestAutomationName', as specified in parameter "nextPhaseToBeExecutedIn": "otherTestAutomationName" above. After executing its phase in 'otherTestAutomationName', details will be updated back to service as below:


<details>
<summary><b>Suite ID 5614 current phase as in Swagger after otherTestAutomationName execution: (Phase 4)</b></summary>

	{
	  "testSuiteName": "Sample Inter-framework test case",
	  "testSuiteDescription": "\nTEST_PLAN: SOLTESAU-20422\nXRAY_LABELS: L1, L2, L3",
	  "testSuiteRunId": 5614,
	  "sourceApplication": "Solstice",
	  "overallStatus": "READY",
	  "currentPhase": {
	    "environment": "DEV",
	    "executedBy": "otherTestAutomationName",
	    "nextPhaseToBeExecutedIn": "Solstice",
	    "requiredTestAutomationFlow": "flowNameTwoForSTAF1",
	    "executionFinishTime": "2025-09-10T12:14:06+02:00",
	    "results": [
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "a1": "a2",
	          "b1": "b2",
	          "c1": "c2"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "otherTestAutomation- Step 3",
	            "errorMessage": "",
	            "duration": 1003,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "otherTestAutomation- Step 4",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          }
	        ]
	      },
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": {
	          "x1": "x2",
	          "y1": "y2",
	          "z1": "z2"
	        },
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "otherTestAutomation- Step 3",
	            "errorMessage": "",
	            "duration": 1003,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "otherTestAutomation- Step 4",
	            "errorMessage": "",
	            "duration": 1,
	            "status": "PASS",
	            "keyword": "VALIDATION",
	            "infoLog": ""
	          }
	        ]
	      }
	    ]
	  }
	}

</details>

      

 -------------------------------------------------------------------------------------------------------------------------------------------------------------------

* **Terminating inter-framework test suites**

Scenario template in STAF for terminating inter-framework test suites in STAF:

      @testCaseTerminationTemplate @flowNameTwoForSTAF1
      Scenario: Dummy scenario to demonstrate final phase of an orchestrated test case in STAF
      Given We prepare to terminate the inter-framework test case
      Given print on console 'Actual test case Step 3 in STAF'
      Given print on console 'Actual test case Step 4 in STAF'    


Note: The above scenario serves as a template for interframework test case termination from STAF. Avoid using Scenario Outline for termination scenarios, as the continuation/termination steps are fixed and can be covered by a single scenario. Scenario Outline isn't applicable in this context.

After @flowNameTwoForSTAF1 is executed in STAF, final phase execution details are updated back to service and suite will be marked as done as below


<details>
<summary><b>Suite ID 5614 current phase as in Swagger after STAF execution @flowNameTwoForSTAF1: (Phase 5)</b></summary>

	{
	  "testSuiteName": "Sample Inter-framework test case",
	  "testSuiteDescription": "\nTEST_PLAN: SOLTESAU-20422\nXRAY_LABELS: L1, L2, L3",
	  "testSuiteRunId": 5614,
	  "sourceApplication": "Solstice",
	  "overallStatus": "DONE",
	  "currentPhase": {
	    "environment": "DEV",
	    "executedBy": "Solstice",
	    "nextPhaseToBeExecutedIn": "",
	    "requiredTestAutomationFlow": "",
	    "executionFinishTime": "2025-09-10T12:35:15+02:00",
	    "results": [
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": "",
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "We prepare to terminate the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 2423,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 3 in STAF'",
	            "errorMessage": "",
	            "duration": 273,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 4 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      },
	      {
	        "testCaseName": "Sample Inter-framework test case",
	        "status": "PASSED",
	        "testCaseDescription": "",
	        "customData": "",
	        "jiraId": "SOLTESAU-34965",
	        "testSteps": [
	          {
	            "name": "We prepare to terminate the inter-framework test case with following values",
	            "errorMessage": "",
	            "duration": 1613,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 3 in STAF'",
	            "errorMessage": "",
	            "duration": 283,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          },
	          {
	            "name": "print on console 'Actual test case Step 4 in STAF'",
	            "errorMessage": "",
	            "duration": 0,
	            "status": "PASS",
	            "keyword": "PREPARATION",
	            "infoLog": ""
	          }
	        ]
	      }
	    ]
	  }
	}

</details>

            

Once the suite is marked as DONE it will picked up by github actions workflow (InterFrameworkUtilities/src/test/resources/interframework_workflows/Interframework_Done.yml)

***Each test case of the suite will be uploaded to JIRA, and once all tests are uploaded, the suite's status will be changed to DONE_AND_UPLOADED.***

--------------------------------------------------------------------------------------------------------------------------------------------------------------------

## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Documentation`**

GitHub pages for InterFrameworkUtilities link : https://github.vodafone.com/pages/VFDE-Solstice-TestAutomation/javadocs/com/solstice/staf/interframework/integrationtests/package-summary.html

All Interframework GitHub actions workflows : [InterFrameworkUtilities/src/test/resources/interframework_workflows/](https://github.vodafone.com/VFDE-Solstice-TestAutomation/staf-utilities/tree/main/InterFrameworkUtilities/src/test/resources/interframework_workflows)

Inter-framework integration testing using STAF : https://de.confluence.agile.vodafone.com/x/ccMvDg

Orchestration webservice swagger documentation : https://z3-test4b-app-wss-dev-lb.kabeldeutschland.de/TestSuiteDataReport/swagger-ui/index.html?url=/TestSuiteDataReport/v3/api-docs&validatorUrl=#/

Orchestration webservice documentation by ITQA department : https://de.confluence.agile.vodafone.com/display/ITQA/TestSuiteDataReport+Service+for+Connecting+Different+Test+Automations


## ![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)  **`Troubleshoot`**

STAF FAQs pages link : https://de.confluence.agile.vodafone.com/x/pZkIBQ
