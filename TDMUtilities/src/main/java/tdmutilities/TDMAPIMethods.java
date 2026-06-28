package tdmutilities;

import apiutilities.APIRequestRelatedMethods;
import apiutilities.CommonAPIMethods;
import apiutilities.ResponseValidationMethods;
import generalutilities.EnvironmentDataLoader;
import generalutilities.ReportAndLogging;
import generalutilities.TestContext;
import io.cucumber.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class perform operations related to TDM API Methods.
 * This class contains methods to perform TDM APIs, generation of Token, generate EntityList, trigger TDM task, monitoring Results and checking TDM status.
 */
public class TDMAPIMethods {
  ResponseValidationMethods responseValidationMethods = new ResponseValidationMethods();
  APIRequestRelatedMethods apiRequestRelatedMethods = APIRequestRelatedMethods.getInstance();
  CommonAPIMethods commonAPIMethods = new CommonAPIMethods();
  ReportAndLogging reportAndLogging = new ReportAndLogging();
  TDMCommonMethods tdmCommonMethods = new TDMCommonMethods();
  TestContext<Object> testContext = TestContext.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(TDMAPIMethods.class);
  private static final Map<String,String> environment = EnvironmentDataLoader.getInstance().getEnvironment();


  private String token;
  private String taskExecutionID;
  private String entityList;
  private int entityCount;
  private String entityListOfGeneration;
  private int entityCountOfGeneratedList;
  private String taskStatus;
  private int tdmStatusCode200;
  private int tdmStatusCode201;
  private int tdmStatusCode401;
  private String baseServiceURL;
  private static final String TDM_HEADER_NAME = "tdmHeaderName";
  private static final String TDM_HEADER_NAME_TWO = "tdmHeaderName2";
  private static final String TDM_HEADER_VALUE_TWO = "tdmHeaderValue2";
  private static final String TDM_HEADER_VALUE = "tdmHeaderValue";

  private Map<String, String> listOfConstants;

  /**
   * This method loads parameters from properties file, which are used in TDM implementation.
   *
   * @throws IOException   - an exception thrown if unable to read property file
   */
  public void loadTDMParameters() throws IOException {

    listOfConstants = tdmCommonMethods.tdmReadPropertyFile();
    baseServiceURL= environment.get("STAF_UTILITIES/tdmBaseServiceURL");
    tdmStatusCode200 = Integer.valueOf(listOfConstants.get("tdmStatusCode200"));
    tdmStatusCode201 = Integer.valueOf(listOfConstants.get("tdmStatusCode201"));
    tdmStatusCode401 = Integer.valueOf(listOfConstants.get("tdmStatusCode401"));

  }

  /**
   * This method generates token as authentication for TDM steps, token expires in 15 minutes!
   *
   * @throws IOException     - an exception thrown if file reading operation fails
   */
  public void tdmGenerateToken() throws IOException {
    String tdmApiKey = environment.get("STAF_UTILITIES/tdmApiKey");

    loadTDMParameters();


    LOG.info("serviceURL: {}", baseServiceURL);

    apiRequestRelatedMethods.formRequestUrl(baseServiceURL, listOfConstants.get("tdmResourceName"));
    apiRequestRelatedMethods.initializeRequestObject();

    HashMap<String, String> requestHeader = new HashMap<>();

    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME), listOfConstants.get(TDM_HEADER_VALUE));
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + tdmApiKey);


    apiRequestRelatedMethods.setRequestHeaders(requestHeader);
    apiRequestRelatedMethods.sendRequest("GET");

    reportAndLogging.addStepToReport("Response Body of Generate Token Step: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    responseValidationMethods.verifyStatusCode(apiRequestRelatedMethods.getResponse(), tdmStatusCode200);

    token = apiRequestRelatedMethods.getResponse().getBody().jsonPath().get("Api-Token");
    LOG.info("TOKEN: {}", token);

  }

  /**
   * This method generates EntityList (Customer List) from source environment
   *
   * @param datatable -  contains Customer details with relevant parameters
   * @param sourceEnv -  source environment of customers to generate entity list
   */
  public void generateEntityList(DataTable datatable, String sourceEnv) {

    apiRequestRelatedMethods.formRequestUrl(baseServiceURL, listOfConstants.get("tdmResourceEntityList"));
    apiRequestRelatedMethods.initializeRequestObject();


    HashMap<String, String> requestHeader = new HashMap<>();

    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME), listOfConstants.get(TDM_HEADER_VALUE));
    requestHeader.put(listOfConstants.get("tdmContentType"), listOfConstants.get(TDM_HEADER_VALUE));
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + token);
    LOG.info("Request Header in GenerateEntityList: {}", requestHeader);

    apiRequestRelatedMethods.setRequestHeaders(requestHeader);
    apiRequestRelatedMethods.getRequest().body(tdmCommonMethods.listToJSONObject(datatable, sourceEnv).toString()); //edited this line in first review
    commonAPIMethods.createCurlCommand(apiRequestRelatedMethods.getRequest(), "POST");
    apiRequestRelatedMethods.sendRequest("POST");
    reportAndLogging.addStepToReport("Response Body of Generate Entity List: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    responseValidationMethods.verifyStatusCode(apiRequestRelatedMethods.getResponse(), tdmStatusCode201); //edited this line in first review


    entityListOfGeneration = apiRequestRelatedMethods.getResponse().body().asString().replace("\"", "");
    reportAndLogging.addStepToReport("Entity List: " + entityList);
    LOG.info("Entity List Of Generation : {}", entityListOfGeneration);
    entityCountOfGeneratedList = entityListOfGeneration.split(",").length;
    LOG.info("Entity Count Of GeneratedList: {}", entityCountOfGeneratedList);

  }

  /**
   * This method starts TDM execution and is used to fetch customers from TDM application.
   *
   * @param numberOfCustomer      - provided number of customers from user
   * @param tdmTaskID            - Id of TDM task. Execution results are collected under this ID.
   * @param timeLimit             - time limitation of pending condition.
   * @throws IOException          - an I/O exception thrown during the execution of checkTdmTaskStatus method
   * @throws InterruptedException - an exception thrown if execution interrupted during execution of checkTdmTaskStatus method.
   */

  public void tdmTriggerTask(Integer numberOfCustomer, Integer tdmTaskID, Integer timeLimit) throws IOException, InterruptedException {

    String endPoint = "api/task/" + tdmTaskID + "/forced/true/startTask";
    apiRequestRelatedMethods.formRequestUrl(baseServiceURL, endPoint);
    apiRequestRelatedMethods.initializeRequestObject();

    HashMap<String, String> requestHeader = new HashMap<>();
    HashMap<String, Object> formParam = new HashMap<>();

    StringBuilder stringBuilder = new StringBuilder();
    String[] entityListOfGenerationArray = entityListOfGeneration.split(",");

    if (entityListOfGenerationArray[0].equals("")) {
      throw new AssertionError("There is no Customer Id in Entity List");
    }

    if (numberOfCustomer > entityCountOfGeneratedList) {  //edited this line in first review
      LOG.warn("Warning !!!:  Number of Customers are {} and  greater than Number of CustomerIds {} from Generated Entity List", numberOfCustomer, entityCountOfGeneratedList);
    } else {

      for (int i = 0; i < numberOfCustomer; i++) {
        stringBuilder.append(entityListOfGenerationArray[i]).append(",");

      }
      entityList = stringBuilder.toString().replaceAll(",$", "");
      String[] entityListArray = entityList.split(",");
      entityCount = entityListArray.length;
      LOG.info("Entity List: {} ", entityList);
    }
    LOG.info("entityCount: {} ", entityCount);


    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + token);
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME), listOfConstants.get(TDM_HEADER_VALUE));
    formParam.put("entitieslist", entityList);

    apiRequestRelatedMethods.setRequestHeaders(requestHeader);
    apiRequestRelatedMethods.addFormObjectParameters(formParam);
    apiRequestRelatedMethods.sendRequest("POST");

    reportAndLogging.addStepToReport("Response Body of Trigger Task: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    responseValidationMethods.verifyStatusCode(apiRequestRelatedMethods.getResponse(), tdmStatusCode201);

    taskExecutionID = apiRequestRelatedMethods.getResponse().getBody().jsonPath().get("result.taskExecutionId").toString();
    LOG.info("taskExecutionID: {}", taskExecutionID);

    checkTdmTaskStatus(tdmTaskID, timeLimit);

  }

  /**
   * This method starts TDM execution and is fetching Customers from feature file. You need to provide CustomerID(s) in feature file
   *
   * @param tdmTaskID            - Id of TDM task. Execution results are collected under this ID.
   * @param timeLimit             - time limitation of pending condition.
   * @param datatable             - contains provided Customers from feature file
   * @throws IOException          - an I/O exception thrown during the execution of checkTdmTaskStatus method
   * @throws InterruptedException - an exception thrown if task execution could be interrupted during execution of checkTdmTaskStatus method.
   */

  public void tdmTriggerTaskCustomerID(Integer tdmTaskID, Integer timeLimit, DataTable datatable) throws IOException, InterruptedException {

    String endPoint = "api/task/" + tdmTaskID + "/forced/true/startTask";
    apiRequestRelatedMethods.formRequestUrl(baseServiceURL, endPoint);
    apiRequestRelatedMethods.initializeRequestObject();

    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME), listOfConstants.get(TDM_HEADER_VALUE));
    requestHeader.put(listOfConstants.get("tdmContentType"), listOfConstants.get(TDM_HEADER_VALUE));
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + token);
    apiRequestRelatedMethods.setRequestHeaders(requestHeader);
    apiRequestRelatedMethods.getRequest().body(tdmCommonMethods.datatableToJSONObject(datatable).toString());
    entityCount = datatable.asList().size();
    commonAPIMethods.createCurlCommand(apiRequestRelatedMethods.getRequest(), "POST");
    apiRequestRelatedMethods.sendRequest("POST");
    reportAndLogging.addStepToReport("Response Body Trigger Task with Customer IDs: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    taskExecutionID = apiRequestRelatedMethods.getResponse().getBody().jsonPath().get("result.taskExecutionId").toString();

    LOG.info("taskExecutionID: {}", taskExecutionID);

    checkTdmTaskStatus(tdmTaskID, timeLimit);

  }

  /**
   * This method monitors the status of test execution
   *
   * @param tdmTaskID       - Id of TDM task. Execution results are collected under this ID.
   * @return              - String taskStatus of execution
   * @throws IOException  - an exception thrown during execution of generateToken method
   */
  public String tdmGetMonitoring(Integer tdmTaskID) throws IOException {

    String endPoint = listOfConstants.get("tdmResourceNameMonitor");
    apiRequestRelatedMethods.formRequestUrl(baseServiceURL, endPoint);

    apiRequestRelatedMethods.initializeRequestObject();
    HashMap<String, String> requestHeader = new HashMap<>();
    HashMap<String, String> queryParam = new HashMap<>();


    queryParam.put(listOfConstants.get("tdmTaskIDParam"), String.valueOf(tdmTaskID));
    queryParam.put(listOfConstants.get("tdmExecutionIdParam"), taskExecutionID);
    requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + token);

    apiRequestRelatedMethods.setRequestHeaders(requestHeader);
    apiRequestRelatedMethods.addQueryParameters(queryParam);
    apiRequestRelatedMethods.sendRequest("GET");


    reportAndLogging.addStepToReport("Response Body of Monitoring: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    if (apiRequestRelatedMethods.getResponse().getStatusCode() == tdmStatusCode401) {
      LOG.info("Regenerate Token in Trigger Step");
      tdmGenerateToken();

    } else if (apiRequestRelatedMethods.getResponse().getStatusCode() == tdmStatusCode200) {
      taskStatus = apiRequestRelatedMethods.getResponse().getBody().jsonPath().get("result[\"Task Status\"]").toString();

    }
    return taskStatus;
  }

  /**
   * This method checks the generated Customers status, generated customers and first created customer details are stored in TestContext.
   *
   * @throws IOException - an I/O exception thrown during execution of checkTdmTaskStatus method
   */
  public void tdmExtractResults() throws IOException {

    boolean flag = true;
    do
    {
      String endPoint = listOfConstants.get("tdmResourceNameTestResults");
      apiRequestRelatedMethods.formRequestUrl(baseServiceURL, endPoint);
      apiRequestRelatedMethods.initializeRequestObject();
      HashMap<String, String> requestHeader = new HashMap<>();
      HashMap<String, String> queryParam = new HashMap<>();


      requestHeader.put(listOfConstants.get(TDM_HEADER_NAME_TWO), listOfConstants.get(TDM_HEADER_VALUE_TWO) + " " + token);
      queryParam.put(listOfConstants.get("tdmExecutionIdParam"), taskExecutionID);
      apiRequestRelatedMethods.setRequestHeaders(requestHeader);
      apiRequestRelatedMethods.addQueryParameters(queryParam);
      apiRequestRelatedMethods.sendRequest("GET");
      int statusCode = apiRequestRelatedMethods.getResponse().statusCode();
      if (statusCode == tdmStatusCode401)
        tdmGenerateToken();
      else
        flag = false;

    } while (flag);

    reportAndLogging.addStepToReport("Response Body of Get Results: " + apiRequestRelatedMethods.getResponse().asPrettyString());
    responseValidationMethods.verifyStatusCode(apiRequestRelatedMethods.getResponse(), tdmStatusCode200);

    String responseString = apiRequestRelatedMethods.getResponse().asString();
    String key = "CUSTOMER_ID";
    int successEntities = tdmCommonMethods.getNumberOfCustomersIds(responseString, key);


    if (entityCount != successEntities) {
      throw new AssertionError("Total Entities of Completed Customers  are not equal provided Entity Count ");
    }


    testContext.setProperty("tdmResponse", tdmCommonMethods.createCustomerList(responseString));
    LOG.info("TDM Customers in TestContext {}: ", testContext.getProperty("tdmResponse"));


    testContext.setProperty("tdmFirstCustomer", tdmCommonMethods.getFirstCustomer(responseString));
    LOG.info("TDM First customer in TestContext {}: ", testContext.getProperty("tdmFirstCustomer"));
  }

  /**
   * This method checks the actual status(completed,pending,failed) of execution.
   *
   * @param tdmTaskID               - Id of TDM task. Execution results are collected under this ID.
   * @param timeLimit               - time limitation of pending condition.
   * @throws IOException            - an I/O exception thrown during execution of tdmGetMonitoring method
   * @throws InterruptedException   -  an exception thrown if the task execution is interrupted during sleeping
   */
  private void checkTdmTaskStatus(Integer tdmTaskID, Integer timeLimit) throws IOException, InterruptedException {
    tdmGetMonitoring(tdmTaskID);
    String failedMessage = "failed";
    String completedMessage = "completed";
    String pendingMessage = "Pending";
    int count = 0;
    int loopTime = 10;
    int loopLimit = (timeLimit * 60) / loopTime;
    while (tdmGetMonitoring(tdmTaskID).equals(pendingMessage) && count <= loopLimit) {
      TimeUnit.SECONDS.sleep(loopTime);
      count++;
      LOG.info("TDM API is still Processing !");
//
    }
    if (tdmGetMonitoring(tdmTaskID).equals(completedMessage)) {
      LOG.info("Activity is {} ! ", completedMessage);
      tdmExtractResults();
    } else if (tdmGetMonitoring(tdmTaskID).equals(failedMessage)) {
      LOG.info("Activity is  {} ! ", failedMessage);
      throw new AssertionError(failedMessage);
    } else if (tdmGetMonitoring(tdmTaskID).equals(pendingMessage) && count > loopLimit) {
      throw new AssertionError("Task Status is still Pending !" + failedMessage);
    }

  }


}
