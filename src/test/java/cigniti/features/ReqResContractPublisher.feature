@pact @consumer
Feature: PACT: Cigniti Contracts - Consumer Publishing the Contracts for ReqRes API
  #  NOTE : YOU CAN RIGHT CLICK and RUN THE SCENARIOS DIRECTLY (if your IDE supports)

  #  This section assumes that you have good theoretical knowledge on how contract testing is done and what PACT recommends
  #  You are here to implement your theory knowledge into something practical
  #  This feature file demonstrates how Cigniti as a consumer of ReqRes API publishes its contracts to ReqRes API Development Team
  #  Remember that contract tests are written at two levels, one is Consumer side and the other is Provider side
  #  This feature file is purely a consumer side code

  #  Below scenario demonstrates the simple BDD style use of PACT
  #    The PACT Java library we used is the short and simplest by using JUnit rules
  #    If you read any Pact JVM Consumer example, you will understand that the Test Suite mentioned below
  #    can be directly executed by JUnit runner to generate a contract file (in target/pacts).
  #    But we wanted to write it in BDD style along with running it by Serenity instead of JUnit
  #    To meet the requirements, we needed to add additional logic in PactSteps class under iVerifyPacts method
  #    This method will invoke the test suite file mentioned below which contains all the logic to generate a contract
  #    To verify the pact after generating this contract, execute te provider code (@pact_demo_provider_1)

  @pact_demo_consumer_1
  Scenario: PACT DEMO 1.1 - As a consumer of Service ReqRes, I publish my contracts
    Given I have the pact publisher defined in "/cigniti/runner/CignitiContractPublisherTestSuite"
    Then I will publish the pacts


  #  If you look at the above scenario, we see that a lot of test data is hard coded (ex: headers, body)
  #    in the TestSuite file, if we want to use PACT DSL to write regex patters in request/response comparision then the
  #    current approach doesnt suit us. You want to pass all of your pact dsl string matchers, regex patterns from the feature file itself.
  #    This is because the current approach relies on JUnit runner shortcuts given by PACT
  #    We want to get rid of JUnit runner and delegate Serenity to run the PACT test suites
  #    Hence we want to write plain java code for PACT so that you can parameterize many things from
  #    this feature file such as headers, body, pact state name, pact interaction name, request and responses etc.
  #    below scenario is an example demonstrating that flexibility by doing the same operation we did above
  #    but by getting rid of this hard coded TestSuite class

  @pact_demo_consumer_2
  Scenario: PACT DEMO 2.1 - As a consumer of Service ReqRes, I publish my contracts
    Given I have the path as "/api/users"
    And I have the request headers defined as follows
      | Content-Type | application/json |
    And I have the request body defined in "src/test/resources/user_details.json"
    And I have the below pact parameters
      | providerName    | ReqRes1           |
      | consumerName    | Cigniti1          |
      | method          | POST              |
      | interactionName | POST_user_details1 |
    And I have the response status as "201"
    And I have the response body as follows
      | name      | morpheus            |
      | job       | leader              |
      | id        | Regex<number>       |
      | createdAt | Regex<datetime>     |
      #    We know the response body automatically generates a number for id and a timestamp for createdAt
      #    Hence we want to build a contract with regex matchers rather than comparing against static/fixed values
    When I build the contract
    Then I will publish the pacts and verify them