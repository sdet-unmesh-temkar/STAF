@triggerCommand
Feature: Validate feature 1

  Scenario: Validate scenario 1
    Given ProcessBuilder - we execute the command 'ssh -i ../key.pem -o \"StrictHostKeyChecking no\" ogg_int@cim-ogg-preprod.aws.solstice.vodafone.com -p 2022 \"whoami\"'

  Scenario: Validate scenario 2
    Given ProcessBuilder - we execute the command 'ls'