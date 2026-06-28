@TestDataToolCustomerSearch
Feature: Searched customer master data change - First name
#  @SOLTESRP-20517
  Scenario: Searched customer master data change - First name

  Reads the data of a Searched customer from TestData tool.
  Verifies customer in various FusionC and related systems.

    # Get customer from testDataTool
    Given Test Data Tool - Get dynamic test data Customer Id search by query: ACTIVE_CUSTOMER

    #Given D1 - Get customer from dynamic test data Customer Id
    #And D1 - Get individual from customer
    #And D1 - Get billing account from customer
    #And D1 - Get geographic address from individual

    # Agent Desktop
#    Given AgentDesktop - Login
#    When AgentDesktop - Search customer by CUSTOMERNUMBER
#    Then AgentDesktop - Validate customer details
#    And AgentDesktop - Change customers FIRSTNAME via Writing Actions
#    And AgentDesktop - Validate personal data value in Write Orders Details page
#    And AgentDesktop - Validate updated customers personal data on homepage
#    And AgentDesktop - Logout
