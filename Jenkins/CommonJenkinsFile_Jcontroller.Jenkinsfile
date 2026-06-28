/**  This Jenkins file is updated using the shared-jenkins-library. To use this file please configure
  *  the jenkins library using the "Import SharedJenkinsLibrary" stage which is mentioned at the start of pipeline.
  *
  *  Library URL     :- https://github.vodafone.com/VFDE-Solstice-TestAutomation/shared-jenkins-library.git
  *  Confluence Page :- https://de.confluence.agile.vodafone.com/pages/viewpage.action?pageId=197175278
  */

//mandatory import statement for JController
//@Library('shared-jenkins-library@main')

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.Scanner;
import java.nio.file.*;
import java.time.*;

/* PARAMETERS DECLARATION STARTS*/
/* Declare all the parameters here which needs to be passed from Jenkins Job */


int mavenActualPassedPercentage;
int mavenActualFailedPercentage;

properties([
  parameters([
    choice(name: 'EnvironmentName', choices: ['E2E','Dev-Done','int-man','int-man-01','int-man-02','int-man-03','X-Test-01','X-Test-02','X-Test-03','Dev-RE1','Dev-RE2','ARIC-Local','AWSMig01','Int-Auto','local','MVPE2','SOAPTest','INTMAN1','Test-Done','DEV'], description: 'Environment on which test case needs to be executed'),
    string(name: 'TestType', description: "Your test tag name. To pass multiple tag please use 'or' between them. For eg. @tag1 or @tag2"),
    string(name: 'projectKey', description: 'Project key using which the test execution will get generated.'),
    string(name: 'testPlan', description: "Your test plan ID. Eg. SOLTESAU-1234"),
    string(name: 'labelForXray', description: "Labels for your test execution. To pass multiple labels, please separate them by ','. Eg. Test1,Test2"),
    choice(name: 'isParallelExecute', choices: ['NO','YES'], description: ' Do you want to parallel execution? NO is byDefault and will execute test cases sequentially. For parallel execution please select YES '),
    choice(name: 'typeOfParallelExecution', choices: ['Features Parallel','Scenarios Parallel', 'Multi Forking']),
    choice(name: 'threadOrForkCount', choices: ['1','2','3','4','5','6','7','8','9'], description: '"1" is byDefault and will execute test cases sequentially. For parallel execution please select other numbers based on the number of feature files you want to execute parallel.'),
    choice(name: 'mavenExpectedPassedPercentage', choices: [100, 90, 80], description: 'Maven Expected Passe Percentage should be 100%. User may customize it as per choice.'),
    string(name: 'EmailID', description: "EmailId's to get execution report. To pass multiple id's, please separate them by ','. Eg. abc@test.com,xyz@test.com")
  ])
])
/* PARAMETERS DECLARATION ENDS */

/* PIPELINE STARTS */
pipeline {

  /* DEFINING JENKINS AGENT (Mandatory step)*/
  agent any

  /* STAGES STARTS */
  stages {

    /* IMPORT SHARED JENKINS LIBRARY AND SET CREDENTIALS STAGES START */
    stage("Import SharedJenkinsLibrary and Set credentials") {
      steps {
        script{
          /** This method will load the file which has the code to import the library (Mandatory for jenkins master)
           */
      	   load "src/test/resources/Jenkins/importLibraryFile.groovy"
      	}

        script {
          /**
           * This method has implementation to set Jira Site Credentials as per the prod or preprod environment
           *
           * @byDefault prod environment is selected when no parameter is passed
           * @param pass string "preprod" as parameter to set the preprod parameters
           * @return none
           */
          setCommonCredentials()
        }
      }
    }
    /* IMPORT SHARED JENKINS LIBRARY AND SET CREDENTIALS STAGES ENDS */

    /* BUILD STAGE TO EXECUTE THE TEST CASES STARTS (Mandatory stage)*/
    stage('Run Test'){
      steps{
        /**
         * This method has implementation to build STAF and execute the test cases based on parameters passed
         *
         * @param TestType - its the TestTag that needs to be executed
         * @param EnvironmentName - environment name on which tests needs to be executed
         * @param isParallelExecute (optional)- to define whether tests will be parallel execution or not
         * @param typeOfParallelExecution (optional) - to define which type of parallel execution
         * @param threadOrForkCount (optional) - to define the thread's or fork's count depends on type of parallel execution
         *                                       Exclude the optional parameters if not required
         */
          runTests(TestType, EnvironmentName, isParallelExecute, typeOfParallelExecution, threadOrForkCount)
      }
    }
    /* BUILD STAGE TO EXECUTE THE TEST CASES ENDS*/

    /* STAGE TO SYNC UPDATED TEST TO XRAY STARTS */

    stage('Sync updated tests to X-ray') {
      steps {
        /**
         * This method will sync a existing test case into xray and create new test cases in xray if flag is true.
         *
         * @param projectKey - It is the projectKey
         * @param flag (optional) - This flag is used to decide whether new test cases need to create in xray or not
         */
          syncTestCaseToXray(projectKey)
          //syncTestCaseToXray(projectKey, true)

      }
    }
    /* STAGE TO SYNC UPDATED TEST TO XRAY  ENDS */

    /* STAGE TO IMPORT RESULTS TO XRAY STARTS */
    stage('Import results to Xray') {
       steps {
         /**
          * This method has implementation to create a test execution and will import the results to xray by reading cucumber.json file.
          *
          * @param projectKey - It is the projectKey
          * @param EnvironmentName - It is the environment name on which tests needs to be executed
          * @param testPlan (Optional) - It is the testPlan ID to which Test Executions needs to be linked
          * @param labelForXray (Optional) - These are Labels which are updated to Jira
          *                                  Exclude the optional parameter if not required
          */
          importResultsToXray(projectKey, EnvironmentName, testPlan, labelForXray)

       }
    }
    /* STAGE TO IMPORT RESULTS TO XRAY ENDS */


     /* STAGE TO UPDATE TESTCASE LABEL STARTS*/
    stage('TestCases Label Update'){
      steps {
         /**
          * This method has implementation to add all the tags which are above the test case in feature file as a label to jira test
          *
          * @param projectKey - It is the projectKey
          */
          testcaseLabelUpdate(projectKey)
      }
    }
    /* STAGE TO UPDATE TESTCASE LABEL ENDS*/


    /*STAGE TO VALIDATE TEST CASE PASS RATE STARTS*/
    stage('Validate Test Case Pass Rate') {
       steps {
          /**
           * This method has implementation to validate Test case pass rate
           */
           script {
              mavenActualPassedPercentage = getMavenPassPercentage()
              if (mavenActualPassedPercentage >= (mavenExpectedPassedPercentage as int)){
                  echo "Trigger deployment pipeline"
              } else {
                  error("Pipeline Failed: Doesn't meet maven passed percentage criteria")
              }
           }
       }
    }
    /* STAGE TO VALIDATE TEST CASE PASS RATE ENDS*/

    /* TRIGGER DEPLOYMENT PIPELINE STARTS */
    stage('Trigger deployment pipeline') {
       steps {
         /**
          * This method has implementation to Trigger deployment pipeline when maven passed percentage criteria.
          */
          script {
             build job: 'TriggerBuild', propagate: false, wait: true
          }
       }
    }
    /* TRIGGER DEPLOYMENT PIPELINE STARTS ENDS */
  }
  /* STAGES ENDS */

  /* SEND EMAIL WITH EXECUTION DETAILS IN JENKINS EMAIL */
  post {
    always {
      script {
        /**
         * This method will trigger the mail with execution details and graphs to the mail address mentioned in Jenkins parameters
         *
         * @param EmailID - It is EmailID to which mail will be triggered with execution details
         * @param moduleName - It is the module name
         * @return None
         */
        triggerJenkinsEmail(EmailID)
      }
    }
  }
  /* SEND EMAIL WITH EXECUTION DETAILS IN JENKINS EMAIL ENDS */
}
