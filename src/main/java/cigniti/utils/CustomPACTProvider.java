package cigniti.utils;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomPACTProvider {

	public String providerURI;
	Response response;
	String headers = "headers";
	String matchingRule = "matchingRules";

	/**
	 * A generic approach to verify the complete PACT Request with matchers Read
	 * all the request attributes from the contract and use them to hit the
	 * actual provider Store the response to compare in another method
	 *
	 * @param path
	 * @param state
	 */
	public void verifyAllOfThePactRequest(String path, String state) {
		JSONObject pactFile = JSONHelper.messageAsActualJson(path);
		JSONArray interactions = pactFile.getJSONArray("interactions");
		Map<String, String> headersMap = null;
		Object body = null;
		String actualMethodFromPact;
		String actualPathFromPact;
		for (int i = 0; i < interactions.length(); i++) {
			String stateName = interactions.getJSONObject(i).getString("providerState");

			// Read the PACT State from contract and compare if that is what we
			// want to verify
			if (stateName.equalsIgnoreCase(state)) {
				JSONObject actualRequestFromPact = interactions.getJSONObject(i).getJSONObject("request");

				// Read the headers from contract
				boolean pactRequestHeaderExists = actualRequestFromPact.has(headers);
				if (pactRequestHeaderExists) {
					headersMap = new HashMap<>();
					Set<String> headerKeys = actualRequestFromPact.getJSONObject(headers).keySet();
					for (String headerKey : headerKeys)
						headersMap.put(headerKey, actualRequestFromPact.getJSONObject(headers).getString(headerKey));
				}

				// Read the request body from contract
				boolean pactRequestBodyExists = actualRequestFromPact.has("body");
				if (pactRequestBodyExists) {
					try {
						body = actualRequestFromPact.getJSONObject("body");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// Read the path from contract
				actualPathFromPact = actualRequestFromPact.getString("path");
				if (!actualPathFromPact.equals("/")) {
					providerURI = providerURI + actualPathFromPact;
				}

				// Read the method/verb from contract
				actualMethodFromPact = actualRequestFromPact.getString("method");
				if (actualMethodFromPact.equalsIgnoreCase("POST")) {
					response = new RESTAssuredAPI().post(body, headersMap, providerURI);
				} else if (actualMethodFromPact.equalsIgnoreCase("GET")) {
					response = new RESTAssuredAPI().get(providerURI, headersMap, null);
				} else if (actualMethodFromPact.equalsIgnoreCase("PUT")) {
					response = new RESTAssuredAPI().put(providerURI, body, headersMap);
				} else if (actualMethodFromPact.equalsIgnoreCase("DELETE")) {
					response = new RESTAssuredAPI().delete(headersMap, providerURI);
				}
				break;
			}
		}
	}

	/**
	 * A generic approach to verify the complete PACT Response with matchers
	 * Read the response from the contract and copare all items such as
	 * responseHeaders, responseStatus, responseBody etc. With the actual
	 * response returned by the Provider
	 *
	 * @param path
	 * @param state
	 */
	public void verifyAllOfThePactResponse(String path, String state) {
		JSONObject pactFile = JSONHelper.messageAsActualJson(path);
		JSONArray interactions = pactFile.getJSONArray("interactions");

		for (int i = 0; i < interactions.length(); i++) {
			String stateName = interactions.getJSONObject(i).getString("providerState");
			if (stateName.equalsIgnoreCase(state)) {
				JSONObject expectedResponseFromPact = interactions.getJSONObject(i).getJSONObject("response");

				boolean pactStatusExists = expectedResponseFromPact.has("status");
				if (pactStatusExists) {
					Assert.assertEquals(expectedResponseFromPact.get("status").toString(),
							String.valueOf(response.getStatusCode()));
				}

				boolean pactHeadersExist = expectedResponseFromPact.has(headers);
				if (pactHeadersExist) {
					Set<String> headersFromPact = expectedResponseFromPact.getJSONObject(headers).keySet();
					for (String header : headersFromPact)
						Assert.assertEquals(
								expectedResponseFromPact.getJSONObject(headers).get(header).toString().toLowerCase(),
								response.getHeader(header).toLowerCase());
				}

				// Read the response body from the contract
				boolean pactBodyExist = expectedResponseFromPact.has("body");
				Object actualResponseBody = null;
				JSONObject expectedResponseBody;
				JSONObject responseBody;
				if (pactBodyExist) {
					try {
						actualResponseBody = new JSONObject(response.getBody().print());
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean pactMatchingRulesExist = expectedResponseFromPact.has(matchingRule);

					// See if you need to compare the Regex and matching rules
					// in the response body
					if (pactMatchingRulesExist) {
						Set<String> keysForBodyMatchingRules = expectedResponseFromPact.getJSONObject(matchingRule)
								.keySet();
						JSONObject matchingRules = expectedResponseFromPact.getJSONObject(matchingRule);
						for (String keyForBodyMatchingRules : keysForBodyMatchingRules) {
							if (keyForBodyMatchingRules.contains("body")) {
								String actualKeyFromMatchingRule = keyForBodyMatchingRules.split("\\.")[2];
								responseBody = (JSONObject) actualResponseBody;
								String actual = responseBody.get(actualKeyFromMatchingRule).toString();
								String expected = null;
								if (matchingRules.getJSONObject(keyForBodyMatchingRules).getString("match")
										.equals("regex")) {
									expected = matchingRules.getJSONObject(keyForBodyMatchingRules).getString("regex");
								} else if (matchingRules.getJSONObject(keyForBodyMatchingRules).getString("match")
										.equals("number")) {
									expected = "[0-9]*";
								}
								if (!actual.matches(expected))
									Assert.fail("Actual : " + actual + " doesn't match with expected : " + expected);
							}
						}
					} else {
						expectedResponseBody = expectedResponseFromPact.getJSONObject("body");
						Set<String> expectedResponseBodyKeys = expectedResponseBody.keySet();
						for (String key : expectedResponseBodyKeys) {
							Assert.assertEquals(expectedResponseBody.get(key),
									((JSONObject) actualResponseBody).get(key));
						}
					}
				}
				break;
			}
		}
	}
}