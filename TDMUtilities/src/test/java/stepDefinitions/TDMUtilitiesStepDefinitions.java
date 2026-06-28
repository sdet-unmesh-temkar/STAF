package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import tdmutilities.TDMAPIMethods;
import java.io.IOException;

/**
 * This class performs the operation on the TDM Utilities.
 * This class contains the methods to implement TDM APIs.
 */
public class TDMUtilitiesStepDefinitions {

  TDMAPIMethods tdmAPIMethods = new TDMAPIMethods();


  /**
   *  This method covers all the TDM API executions for Generating Entity List from the TDM Application and Generated Customer Details are saved in the test context keys as "tdmResponse" and "tdmFirstCustomer".
   *
   * @param numberOfCustomer        provided number of customers from the user.
   * @param sourceEnv               source environment of the customers.
   * @param tdmTaskID               Id of TDM task. Execution results are collected under this ID in the TDM application. The user can retrieve TDMTaskID from the TDM application
   * @param timeLimit               time limitation of the pending condition.
   * @param dataTable               contains the Customer details with the relevant parameters.
   * @throws IOException            an IOException is thrown if the file reading operation fails.
   * @throws InterruptedException   an InterruptedException is thrown if the task execution is interrupted during the execution.
   */

  @Given("TDM - we generate {int} customers in the environment {string} based on the TDMTaskID {int} and a maximum timeout of {int} minutes with following filters")
  public void tdmGenerateCustomersWithParameters(Integer numberOfCustomer, String sourceEnv, Integer tdmTaskID, Integer timeLimit, DataTable dataTable) throws  IOException,  InterruptedException {
    tdmAPIMethods.tdmGenerateToken();
    tdmAPIMethods.generateEntityList(dataTable,sourceEnv);
    tdmAPIMethods.tdmTriggerTask(numberOfCustomer,tdmTaskID,timeLimit);
  }

  /**
   * This method covers all TDM API executions for Providing Entity List from Feature file and Generated Customer Details are saved in the test context keys as "tdmResponse" and "tdmFirstCustomer".
   * @param tdmTaskID              Id of TDM task. The execution results are collected under this ID in the TDM application. The user can retrieve TDMTaskID from the TDM application
   * @param timeLimit              time limitation of the pending condition.
   * @param dataTable              contains the Customer ID(s) with the relevant parameters.
   * @throws IOException           an IOException is thrown if the file reading operation fails.
   * @throws InterruptedException  an InterruptedException is thrown if the task execution is interrupted during the execution of the checkTdmTaskStatus method.
   */

  @Given("TDM - we generate customers based on the TDMTaskID {int} and a maximum timeout of {int} minutes based on the following CustomerIDs")
  public void tdmGenerateCustomersWithCustomerIDs(Integer tdmTaskID, Integer timeLimit, DataTable dataTable) throws IOException, InterruptedException {
    tdmAPIMethods.tdmGenerateToken();
    tdmAPIMethods.tdmTriggerTaskCustomerID(tdmTaskID,timeLimit,dataTable);


  }
}
