Feature: Get data from TDM

  @TDM
  Scenario: Get Test Data from TDM with Parameters
    Given TDM - we generate 1 customers in the environment 'TESTDONE' based on the TDMTaskID 404 and a maximum timeout of 15 minutes with following filters

      | parameter   | operator | value  |
      | ACRM.STATUS | =        | Active |


  @TDMCustomerID
  Scenario: Get Test Data from TDM with Provided Customers
    Given TDM - we generate customers based on the TDMTaskID 404 and a maximum timeout of 15 minutes based on the following CustomerIDs
      | 100000157068 |

