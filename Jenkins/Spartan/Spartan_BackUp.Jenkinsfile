import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.Scanner;
import java.nio.file.*;
import java.time.*

def finalExecutionTime = 0
String startDate = ""
String endDate = ""
def Jira_Id

properties([
  parameters([
    choice(name: 'EnvironmentName', choices: ['int-man', 'int-man-01', 'int-man-02', 'int-man-03', 'X-Test-01', 'X-Test-02', 'X-Test-03', 'Dev-RE1', 'ARIC-Local', 'AWSMig01', 'Int-Auto', 'local', 'MVPE2', 'SOAPTest'], description: 'What´s the destination environemnt'),
    string(name: 'TestType', description: 'Test case type'),
    choice(name: 'moduleName', choices: ['AgentDesktop', 'Apigee', 'APRM', 'Aric', 'AUA', 'BatchMigration', 'BriteBill_BP', 'BriteBill_EBPA', 'Camunda', 'CouchBase', 'DigitalOne', 'IntegrationTest', 'K2View', 'Kafka', 'MOD', 'OGG', 'RTB', 'SOM', 'SOMGui', 'Tibco', 'ToscaUtilities', 'CatalogOne'], description: 'moduleName'),
    string(name: 'projectKey', description: 'projectKey'),
    string(name: 'testPlan', description: 'testPlan'),
    choice(name: 'jenkinsAgentLabelName', choices: ['solstice_art-int01_slave', 'solstice_art-int02_slave', 'solstice-art-int02-windows-slave', 'solstice_art-int03_slave', 'cimcdhdev', 'cimcdhogg2', 'solstice_xtest01_slave', 'solstice_xtest02_windows_slave', 'solstice_xtest02_slave', 'solstice_xtest03_slave', 'solstice_mig01_slave'], description: 'Jenkins Agent label name'),
    string(name: 'labelForXray', description: 'label for xray'),
    string(name: 'EmailID', description: 'EmailID for notification')
  ])
])

pipeline {
  agent {
    label "${jenkinsAgentLabelName}"
  }

  /* GIT Checkout STARTS*/
  stage('GIT Checkout') {
    steps {
      script {
        def branchName = "${GIT_BRANCH}"
        echo "Git commit = ${GIT_COMMIT}"
        branchName = branchName.split("/", 2)[1]
        git branch: "${branchName}", credentialsId: 'bitbucket_deliveryzone', url: 'https://bitbucket.pdepl.aws.solstice.vodafone.com/scm/solta/solsticetestautomationframework.git'
      }
    }
  }
  /* GIT Checkout ENDS*/

  /* BUILD STAGE TO EXECUTE THE TEST CASES STARTS (Mandatory stage)*/
  /* This stage will build STAF and execute the test cases based on the tag name and module name passed from the jenkins job.
     Also execution time is being calculated in this stage. */
  stage('Run Test') {
    steps {
      script {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse("${new Date().format('HH:mm:ss')}");
        Date dateStart = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ");
        startDate = formatter.format(dateStart);
        println startDate
        if (isUnix()) {
          if (forkCount != "NA") {
            sh "mvn clean install -Dmaven.exec.skip=true -Dmaven.wagon.http.ssl.insecure=true -DskipTests -pl ${params.moduleName} -am -T 10 && cd ${params.moduleName} && mvn clean test -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags='${params.TestType}' -Denv='${params.EnvironmentName}' -DforkCount='${params.forkCount}'"

          } else {
            sh "mvn clean install -Dmaven.exec.skip=true -Dmaven.wagon.http.ssl.insecure=true -DskipTests -pl ${params.moduleName} -am -T 10 && cd ${params.moduleName} && mvn clean test -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags='${params.TestType}' -Denv='${params.EnvironmentName}'"
          }
        } else {
          if (forkCount != "NA") {
            bat label: '', script: 'mvn clean install -Dmaven.exec.skip=true -Dmaven.wagon.http.ssl.insecure=true -DskipTests -pl %moduleName% -am -T 10 && cd %moduleName% && mvn clean test -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags="%TestType%" -Denv="%EnvironmentName%" -DforkCount="%forkCount%"'
          } else {
            bat label: '', script: 'mvn clean install -Dmaven.exec.skip=true -Dmaven.wagon.http.ssl.insecure=true -DskipTests -pl %moduleName% -am -T 10 && cd %moduleName% && mvn clean test -Dmaven.wagon.http.ssl.insecure=true -Dcucumber.filter.tags="%TestType%" -Denv="%EnvironmentName%"'
          }
        }

        Date date2 = format.parse("${new Date().format('HH:mm:ss')}");
        def tookTime = date2.getTime() - date1.getTime()
        Date dateEnd = new Date();
        endDate = formatter.format(dateEnd);
        println endDate

        long seconds = (tookTime / 1000);
        if (seconds > 60) {
          long minutes = seconds / 60;
          seconds = seconds % 60;
          if (minutes > 60) {
            long hours = minutes / 60;
            minutes = minutes % 60;
            if (hours > 24) {
              long days = hours / 24;
              finalExecutionTime = "${days}d"
            } else
              finalExecutionTime = "${hours}h${minutes}m"
          } else
            finalExecutionTime = "${minutes}m${seconds}s"
        } else
          finalExecutionTime = "${seconds}s"
      }
    }
  }
  /* BUILD STAGE TO EXECUTE THE TEST CASES ENDS*/

  /* STAGE TO EXPORT RESULT TO XRAY STARTS*/
  stage('Import results to Xray') {
    steps {
      /* This step will create a test execution and will import the results to xray by reading cucumber.json file. */
      step([$class: 'XrayImportBuilder', endpointName: '/cucumber/multipart', importFilePath: '\\${moduleName}\\target\\cucumber.json', importInfo: ''
          '{
          "fields": {
            "project": {
              "key": "${projectKey}"
            },
            "summary": "Result by Jenkins Job - ${JOB_NAME} and build #${BUILD_ID}",
            "description": "",
            "assignee": {
              "name": "9StefanHackenberg.SJIRAAWSJenkinsVG0976YR@vodafone.com"
            },
            "issuetype": {
              "id": "10415"
            },
            "customfield_10465": ["${Environment}"],
            "customfield_10467": ["${testPlan}"],
            "labels": ["${labelForXray}"]
          }
        }
        ''
        ',
        inputInfoSwitcher: 'fileContent', serverInstance: 'SERVER-a4e7b41d-2208-4538-a1e3-faa7d53fada0'])
    /* Step ends for creating test execution and importing result. */
    script {
      /* Reading jenkins console for fetching test execution ID  */
      String logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      //def jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS")+17).trim();
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS: S") + 17, logContent.indexOf("XRAY_TEST_EXECS: S") + 40).split('\n')[0].trim();

      /* Console reading ends */

      env.JIRA_SITE = 'ALM_JIRA'

      /* Making test execution status to done */
      def transitionInput = [transition: [id: '41']]
      def response = jiraTransitionIssue idOrKey: "${Jira_Id}", input: transitionInput, site: 'ALM_JIRA'
      if (response.successful.toString()) {
        println "Jira Transition has been updated successfully."
      }
      /*Making test execution status to done ends*/

      /* Editing multiple labels, start date and end date of execution in test execution */
      labelArray = labelForXray.split(",")
      def testIssue = [
        fields: [
          summary: "Result by Jenkins Job - ${JOB_NAME} and build #${BUILD_ID}",
          description: "Execution results imported from build URL - ${BUILD_URL}\n Test Execution Time - ${finalExecutionTime}",
          assignee: [name: "9StefanHackenberg.SJIRAAWSJenkinsVG0976YR@vodafone.com"],
          issuetype: [id: '10415'],
          labels: labelArray,
          customfield_10465: ["${EnvironmentName}"],
          customfield_10467: ["${testPlan}"],
          customfield_10457: startDate,
          customfield_10458: endDate
        ]
      ]

      response = jiraEditIssue idOrKey: jira_Id, issue: testIssue, site: 'ALM_JIRA'
      echo response.successful.toString()
      echo response.data.toString()
      /*Editing multiple labels, start date and end date of execution in test execution ends */
    }
  }
}
/* STAGE TO EXPORT RESULT TO XRAY ENDS*/

/* STAGE TO SYNC TEST CASES TO XRAY (Optional) */
stage('Sync updated tests to X-ray') {
  steps {
    script {
      if (isUnix()) {
        sh "mkdir ${moduleName}/target/temp"
        if (TestType.contains(",")) {
          String[] testtypearr = TestType.split(",");
          for (int i = 0; i < testtypearr.length; i++) {
            sh "cp `grep -r -l ${testtypearr[i]} ${moduleName}/src/test/java/featureFiles` ${moduleName}/target/temp/"
          }
        } else {
          sh "cp `grep -r -l ${TestType} ${moduleName}/src/test/java/featureFiles` ${moduleName}/target/temp/"
        }
      } else {
        bat "powershell.exe mkdir ${moduleName}/target/temp"
        if (TestType.contains(",")) {
          String[] testtypearr = TestType.split(",");
          for (int i = 0; i < testtypearr.length; i++) {
            bat "powershell.exe cp (findstr -M -S ${testtypearr[i]} ${moduleName}/src/test/java/featureFiles/*) ${moduleName}/target/temp/"
          }
        } else {
          bat "powershell.exe cp (findstr -M -S ${TestType} ${moduleName}/src/test/java/featureFiles/*) ${moduleName}/target/temp/"
        }
      }
    }
    step([$class: 'XrayImportFeatureBuilder', folderPath: "${moduleName}/target/temp", lastModified: '', projectKey: "${projectKey}", serverInstance: 'SERVER-a4e7b41d-2208-4538-a1e3-faa7d53fada0'])
  }
}
/* STAGE TO SYNC TEST CASES TO XRAY ENDS */

/* STAGE TO UPDATE TESTCASE LABEL STARTS (Optional)*/
/* This stage will add all the tags which are above the test case in feature file as a label to jira test */
stage('TestCases Label Update') {
  steps {
    script {
      def list = [] as String[]
      def testCaseItem
      def projects = readJSON file: "\\${moduleName}\\target\\cucumber.json", returnPojo: true
      echo "projects: ${projects}"
      def keyList = projects['elements']['tags']
      echo "keyList: ${keyList}"
      def pattern = "@[SOL][A-Z]{2,}-\\d+";
      echo "Tags JSON: ${keyList}"
      keyList.each {
        it.each {
          list = []
          testCaseItem = null;
          it.each {
            it.each {
              k,
              v ->
              if (v = ~pattern)
                testCaseItem = v.replaceAll("@", "")
              if (!(v = ~pattern))
                list << (v.replaceAll("@", "").replaceAll(" ", ""))
            }
          }
          echo "Arraylist:${list} Testcase: ${testCaseItem}"
          list.findAll {
            it.trim() != ''
          }
          echo "Arraylist trim:${list} Testcase: ${testCaseItem}"
          if (testCaseItem != null) {
            def testIssue1 = [fields: [
              project: [key: "${projectKey}"],
              labels: list,
              issuetype: [id: '10413']
            ]]
            response = jiraEditIssue idOrKey: testCaseItem, issue: testIssue1, site: 'ALM_JIRA'
            echo response.successful.toString()
            echo response.data.toString()
          }
        }
      }
    }
  }
}
/* STAGE TO UPDATE TESTCASE LABEL ENDS*/

/* STAGE TO UPLOAD THE FILE STARTS (Optional)*/
stage('File_Upload') {
  steps {
    /* Below script will attach extent report to test execution in Jira */
    script {
      def logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS: S") + 17, logContent.indexOf("XRAY_TEST_EXECS: S") + 40).split('\n')[0].trim();
      String workspace = "${WORKSPACE}"
      String finalWorkspace = workspace.replace("\\", "/")
      def files = findFiles(glob: "${params.moduleName}/target/ExtentReport/*/*.html")
      String destinationPath = ""
      for (def file: files) {
        destinationPath = (String)
        "${file.path}"
        destinationPath = destinationPath.replace("\\", "/")
        String folderName = destinationPath.split("/")[3]
        def attachment = jiraUploadAttachment idOrKey: Jira_Id, file: "${finalWorkspace}/${params.moduleName}/target/ExtentReport/${folderName}/${file.name}"
      }
    }
    /* Script for attaching extent report ends */

    /* Below script will attach any file which is present in framework to Test Execution.
    (The file path needs to be passed from JenkinsJob) */
    script {
      def logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS") + 17).trim();
      String workspace = "${WORKSPACE}"
      String finalWorkspace = workspace.replace("\\", "/")
      def attachment = jiraUploadAttachment idOrKey: Jira_Id, file: "${finalWorkspace}/${params.moduleName}/${params.filePathToUpload}"
      echo attachment.data.toString()
    }
    /* Script for attaching file ends. */
  }
}
/* STAGE TO UPLOAD THE FILE ENDS*/

/* STAGE TO FETCH JIRA DETAILS (Required when triggering jenkins from Jira)*/
/* This stage will fetch all the details of a Jira issue by passing Jira Issue ID. Eg. SOLTESAU-2210 */
stage('Fetch_Jira_Fields') {
  steps {
    script {
      def Jira_Id = "${params.Issuekey}"
      env.JIRA_SITE = 'ALM_JIRA_PRE_PROD'
      println "${Jira_Id}"
      def response = jiraGetIssue idOrKey: "${Jira_Id}", site: 'ALM_JIRA_PRE_PROD'
      String responsetext = response.toString()
      println responsetext
      env.AGENT = responsetext.substring(responsetext.indexOf("customfield_14001") + 18, responsetext.indexOf("customfield_14001") + 50).split(',')[0].trim();
      env.MODULE = responsetext.substring(responsetext.indexOf("customfield_14100") + 18, responsetext.indexOf("customfield_14100") + 50).split(',')[0].trim();
      env.TESTTAG = responsetext.substring(responsetext.indexOf("customfield_14002") + 18, responsetext.indexOf("customfield_14002") + 50).split(',')[0].trim();
      env.LABELS = responsetext.substring(responsetext.indexOf("labels") + 7, responsetext.indexOf("labels") + 50).split(',')[0].trim().replace("[", "").replace("]", "");;
      env.TYPE = responsetext.substring(responsetext.indexOf("Represents") + 13, responsetext.indexOf("Represents") + 50).split(',')[0].trim();
      if (TYPE == "Test Plan") {
        env.TESTENV = responsetext.substring(responsetext.indexOf("customfield_13503") + 36, responsetext.indexOf("customfield_13503") + 60).split(',')[0].trim();
        env.TESTPLAN = "${Jira_Id}"
      } else if (TYPE == "Test") {
        env.TESTENV = responsetext.substring(responsetext.indexOf("customfield_10465") + 18, responsetext.indexOf("customfield_10465") + 50).split(',')[0].trim().replace("[", "").replace("]", "");;
        env.TESTPLAN = responsetext.substring(responsetext.indexOf("customfield_10449") + 18, responsetext.indexOf("customfield_10449") + 50).split(',')[0].trim().replace("[", "").replace("]", "");;
      }
      println env.TESTPLAN
      println env.TYPE
      println env.LABELS
      println env.TESTENV
      println env.AGENT
      println env.MODULE
      println env.TESTTAG
    }
  }
}

stage('Sync updated tests to X-ray') {
  steps {
    script {
      if (isUnix()) {
        sh "mkdir ${moduleName}/target && mkdir ${moduleName}/target/temp"
        String[] testtypearr = TestType.split(",");
        for (int i = 0; i < testtypearr.length; i++) {
          sh "cp `grep -r -l ${testtypearr[i]} ${moduleName}/src/test/java/featureFiles` ${moduleName}/target/temp/"
        }
      } else {
        bat ""
        "
        powershell.exe mkdir $ {
          moduleName
        }
        /target
        powershell.exe mkdir $ {
          moduleName
        }
        /target/temp
        ""
        "
        String[] testtypearr = TestType.split(",");
        for (int i = 0; i < testtypearr.length; i++) {
          bat "powershell.exe cp (findstr -M -S ${testtypearr[i]} ${moduleName}/src/test/java/featureFiles/*) ${moduleName}/target/temp/"
        }
      }
    }
    step([$class: 'XrayImportFeatureBuilder', folderPath: "${moduleName}/target/temp", lastModified: '', projectKey: "${projectKey}", serverInstance: 'SERVER-a4e7b41d-2208-4538-a1e3-faa7d53fada0'])
  }
}

stage('Update_Jira') {
  steps {
    script {
      def labelArray
      if (labelForXray.contains(",")) {
        labelArray = labelForXray.split(",")
      } else {
        labelArray = labelForXray
      }
      echo "Arraylist:${list} Testcase: ${testCaseItem}"
      println "abc${labelForXray}abc"

      def logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS") + 17).trim();
      env.JIRA_SITE = 'ALM_JIRA'
      def transitionInput = [transition: [id: '41']]
      def response = jiraTransitionIssue idOrKey: "${Jira_Id}", input: transitionInput, site: 'ALM_JIRA'
      println response

      String responseStr = response
      String[] str;
      str = responseStr.split(',')[0].split('=')
      if (str[1] == "true") {
        println "Jira Transition has been updated successfully."
      }

      def testIssue = [fields: [
        project: [key: "${projectKey}"],
        description: "Execution results imported from build URL - ${BUILD_URL} \n Last Commit -  ${GIT_COMMIT} \n Test Execution time - ${finalExecutionTime}",
        issuetype: [id: '10415'],
        labels: labelArray
        //customfield_10457:now

      ]]
      response = jiraEditIssue idOrKey: Jira_Id, issue: testIssue, site: 'ALM_JIRA'

    }

  }

}
stage('File_Upload') {
  steps {
    script {
      def logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS") + 17).trim();
      String workspace = "${WORKSPACE}"
      String finalWorkspace = workspace.replace("\\", "/")
      def attachment = jiraUploadAttachment idOrKey: Jira_Id, file: "${finalWorkspace}/${params.moduleName}/${params.filePathToUpload}"
      echo attachment.data.toString()
    }
    script {
      def logContent = Jenkins.getInstance().getItemByFullName("${JOB_NAME}").getBuildByNumber(Integer.parseInt("${BUILD_NUMBER}")).logFile.text
      def Jira_Id = logContent.substring(logContent.indexOf("XRAY_TEST_EXECS") + 17).trim();
      String workspace = "${WORKSPACE}"
      String finalWorkspace = workspace.replace("\\", "/")
      def files = findFiles(glob: "${params.moduleName}/target/ExtentReport *//* /* *//*.html")
      String destinationPath = ""
      for (def file: files) {
        destinationPath = (String)
        "${file.path}"
        destinationPath = destinationPath.replace("\\", "/")
        String folderName = destinationPath.split("/")[3]
        def attachment = jiraUploadAttachment idOrKey: Jira_Id, file: "${finalWorkspace}/${params.moduleName}/target/ExtentReport/${folderName}/${file.name}"
      }
    }
  }

  post {

    /* EMAIL NOTIFICATION STARTS*/
    success {
      echo 'This will run only if successful'
      mail bcc: '', body: "<br> <b>Project: </b>${env.JOB_NAME}  <br>  <b>Build Number:  </b> ${env.BUILD_NUMBER} <br> <b>URL of build:</b> ${env.BUILD_URL} on success build", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Jenkins job Notification for success build-> ${env.JOB_NAME}", to: "${params.EmailID}";
    }
    failure {
      mail bcc: '', body: "<br> <b>Project: </b>${env.JOB_NAME}  <br>  <b>Build Number:  </b> ${env.BUILD_NUMBER} <br> <b>URL of build:</b>  ${env.BUILD_URL} on failure build", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Jenkins job Notification for Failed build -> ${env.JOB_NAME}", to: "${params.EmailID}";
    }
    changed {
      echo 'This will run only if the state of the Pipeline has changed'
      echo 'For example, if the Pipeline was previously failing but is now successful'
      mail bcc: '', body: "<br> <b>Project: </b>${env.JOB_NAME}  <br>  <b>Build Number:  </b> ${env.BUILD_NUMBER} <br> <b>URL of build:</b>  ${env.BUILD_URL} on changed build", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Jenkins job Notification for changed build -> ${env.JOB_NAME}", to: "${params.EmailID}";
    }
    /* EMAIL NOTIFICATION ENDS*/
    always {
      /* SCRIPT FOR SENDING MODIFIED MAIL WITH REPORT PIE CHART AND GRAPH*/
      script {
        if (params.EmailID != '') {
          try {
            emailext attachmentsPattern: "${moduleName}/target/reportFiles/pieChart.jpeg,${moduleName}/target/ExtentReport/**/ExtentReport.html",
              body: "<br><b>Project:</b> ${env.JOB_NAME}<br>" +
              "<b>Build Number:</b> ${env.BUILD_NUMBER}<br>" +
              "<b>Build URL:</b> ${env.BUILD_URL}<br>" +
              "<b>Xray Test Execution:</b> https://de.jira.agile.vodafone.com/browse/" + jiraID + "<br>" +
              "<html>" +
              "<body>" +
              "<table>" +
              "<td style=\"border: none;\">" + readFile("${moduleName}/target/reportFiles/summary.html") + "</td>" +
              "<td style=\"border: none;\"><img src='cid:pieChart.jpeg' align=\"left\"></td>" +
              "</table>" +
              "</body>" +
              "</html>" +
              readFile("${moduleName}/target/reportFiles/testCaseDetails.html"),
              mimeType: 'text/html',
              subject: "Jenkins job Notification for success build-> ${env.JOB_NAME}",
              to: "${params.EmailID}";
          } catch (all) {
            echo "Email not sent"
          }

        }
      }
      /* SCRIPT FOR SENDING MODIFIED MAIL WITH REPORT PIE CHART AND GRAPH*/
    }
  }
}
}
