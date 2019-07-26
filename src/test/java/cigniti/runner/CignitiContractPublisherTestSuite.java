package cigniti.runner;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Rule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This will create a PACT contract as a json file in the target location This
 * can be executed all alone by calling its JUnit runner (right click and run as
 * JUnit) But we are currently calling this from a BDD step definition to give a
 * touch of BDD
 */
public class CignitiContractPublisherTestSuite {

	@Rule
	public PactProviderRuleMk2 ruleMk = new PactProviderRuleMk2("ReqRes", this);
	   
	private JSONObject requestBody = null;
	private JSONObject responseBody = null;
	String path = "/api/users";

	// Building the Pact contract
	@Pact(provider = "ReqRes", consumer = "Cigniti")
	public RequestResponsePact createPact(PactDslWithProvider provider) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		String bodyPath = "pactautomation/pact/resources/user_details.json";
		String contractInteractionName = bodyPath.substring(bodyPath.lastIndexOf('/') + 1, bodyPath.indexOf('.'));

		try {
			JSONParser parser = new JSONParser();
			requestBody = (JSONObject) parser.parse(new FileReader(bodyPath));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		responseBody = requestBody;

		return provider.given("POST_" + contractInteractionName)
				.uponReceiving("POST REQUEST for a Message : " + contractInteractionName).path(path).method("POST")
				.headers(headers).body(requestBody.toString()).willRespondWith().status(201)
				.body(responseBody.toString()).toPact();
	}

	// Verifying Actual Request with Pact Mock Service
	@Test
	@PactVerification("ReqRes")
	public void runTest() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		String jsonBody = requestBody.toString();
		ResponseEntity<String> response = new RestTemplate().exchange(ruleMk.getUrl() + path, HttpMethod.POST,
				new HttpEntity<Object>(jsonBody, httpHeaders), String.class);

		assert (response.getStatusCode().toString().equalsIgnoreCase("201"));
	}
}
