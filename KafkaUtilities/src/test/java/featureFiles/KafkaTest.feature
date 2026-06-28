Feature: Kafka API POC Feature

  @KafkaAPI
  Scenario: Simple Kafka test
    Given Kafka - we configure a Producer based on the configuration in the Hashicorp Vault path 'KAFKA/Int-Done_withStore'
    And Kafka - we produce an event by referring to the json file named 'KafkaJson' for the topic named 'compositeOrderFalloutNotification'
    And Kafka - we configure a Consumer based on the configuration in the Hashicorp Vault path 'KAFKA/Int-Done_withStore'
    When Kafka - we retrieve event records for the topic 'compositeOrderFalloutNotification'
    Then Kafka - we validate the event records we have obtained with the following list
      | validation          |
      | "country": "DEU"    |
      | "streetNr": "60000" |