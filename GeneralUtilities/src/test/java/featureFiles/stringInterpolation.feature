@customParamTest
Feature: Validate feature 1

  Scenario: Validate scenario 1
    Given I set data in customerName ${{vault.SAP/Username}} using testContext
    Given I set data in customerRole "coder" using applicationContext

  Scenario: Validate scenario 2
    Then I print customerName prefix_${{testContext.name}}_suffix
    Then I print customerRole prefix_${{applicationContext.role}}_suffix