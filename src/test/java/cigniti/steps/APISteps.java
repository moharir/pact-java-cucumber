package cigniti.steps;

import org.json.simple.JSONObject;
import org.testng.Assert;

import cigniti.utils.CignitiProperties;
import cigniti.utils.JSONHelper;
import cigniti.utils.RESTAssuredAPI;
import cigniti.utils.XMLUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.Map;

public class APISteps {

	CignitiProperties properties;
	public JSONObject currentMessageAsJson;
	public Map<String, String> headersAsMap;
	String endpoint;
	String currentMessageAsXML;

	public static RESTAssuredAPI restAssuredAPI = new RESTAssuredAPI();

	@Given("^I have my \"(.*?)\" API ready$")
	public void iGetPathValues(String name) {
		properties = new CignitiProperties(name);
		endpoint = properties.getProperty("URI");
	}

	@Given("^I have my \"(.*?)\" API with path \"(.*?)\" ready$")
	public void iGetPathValues(String name, String path) {
		properties = new CignitiProperties(name);
		endpoint = properties.getProperty(path);
	}

	@Given("^I have my end point as \"(.*?)\"$")
	public void iHaveMyEndPoint(String uri) {
		endpoint = uri;
	}

	@When("^I have the request headers defined as follows$")
	public void iGetHeaderValues(Map<String, String> headers) {
		headersAsMap = headers;
	}

	@When("^I have the URL parameter as \"(.*?)\"$")
	public void iHaveTeURLParam(String param) {
		endpoint = endpoint + "/" + param;
	}

	@When("^I have the (?:soap envelope|request body) defined (?:in|as) \"(.*?)\"$")
	public void iHaveTheBody(String messagePath) {
		if (messagePath.contains(".xml")) {
			currentMessageAsXML = XMLUtil.readXMLFile(messagePath);
		} else if (messagePath.contains(".json")) {
			currentMessageAsJson = JSONHelper.messageAsSimpleJson(messagePath);
			assertThat(currentMessageAsJson, is(notNullValue()));
		} else {
			Assert.fail("Undefined request body");
		}
	}

	@When("^I post the request body as XML$")
	public void iSendXMLToEndpoint() {
		restAssuredAPI.post(currentMessageAsXML, headersAsMap, endpoint);
	}

	@When("^I send a GET request$")
	public void iSendAGetRequest() {
		restAssuredAPI.get(headersAsMap, endpoint);
	}

	@Then("^I verify the response status code as \"(.*?)\"$")
	public void iVerifyTheResponse(int statusCode) {
		assertThat(RESTAssuredAPI.globalResponse.getStatusCode(), is(equalTo(statusCode)));
	}
}
