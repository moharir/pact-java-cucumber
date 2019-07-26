@pact @provider
Feature: PACT: Cigniti Contracts - Provider (ReqRes API) verifying the contract published by Cigniti
  #  NOTE : YOU CAN RIGHT CLICK and RUN THE SCENARIOS DIRECTLY (if your IDE supports)

  #  This feature file demonstrates how ReqRes API as a provider for Cigniti client verified the contracts published by GBG
  #  Remember that contract tests are written at two levels, one is Consumer side and the other is Provider side
  #  This feature file is purely a provider side code

  #  This is a pact verification scenario for the pact published by its consumer (@pact_demo_consumer_1)
  #    Below scenario demonstrates the simple BDD style use of PACT
  #    The PACT Java library we used is the short and simplest by using JUnit rules
  #    If you read any Pact JVM Consumer example, you will understand that the Test Suite mentioned below
  #    can be directly executed by JUnit runner to generate a contract file.
  #    But we wanted to write it in BDD style along with running it by Serenity instead of JUnit
  #    To meet the requirements, we needed to add additional logic in PactSteps class under iVerifyPacts method
  #    This method will invoke the test suite file mentioned below which contains all the logic to generate a contract

  @pact_demo_provider_1
  Scenario: PACT DEMO 1.2 - As ReqRes API & as a provider, I verify the contracts published by my consumer (Cigniti)
    Given I have the pact verification defined in "/cigniti/runner/CignitiContractVerifierTestSuite"
    Then I will verify the pacts


  #  This is a pact verification scenario for the pact published by its consumer (@pact_demo_consumer_2)
  #    For the same reasons mentioned, we want to customise the PACT Provider to accept what Pact States to be executed.
  #    With the current TestSuite used above, we need to hard code everything.
  #    We didn't find a easy method to parameterize everything to PACt provider from the feature files
  #    Hence we have written our own libraries to read the JSON contract file, read interactions from it, read requests
  #    and hit them against te actual service, capture actual response from service and the expected response from contract
  #    Compare them with JSON utils. We can also mention path, uri, provider name etc.
  #    This approach is good if you have bandwidth to extend my example. Or you are welcome to use the first approach given above
  #    Again, this is just a sample with our own PACT like library, you need to extend this if you want more.
  #    But just keep in mind that if you choose to use this custom pact provide methods then you end up re-iventing the wheel
  #    by writing many custom methods to match regex, patterns, etc.

  @pact_demo_provider_2
  Scenario: PACT DEMO 2.2 - As ReqRes API & as a provider, I verify the contracts published by my consumer (Cigniti)
    Given I check the pact with the below parameters
      | state        | POST_user_details1              |
      | pactLocation | target/pact/Cigniti1-ReqRes1.json  |
      | service      | ReqRes                         |
    Then I compare the response body matches in the pact