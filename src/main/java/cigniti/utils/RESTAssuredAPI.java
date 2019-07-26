package cigniti.utils;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RESTAssuredAPI {

	public static Response globalResponse;
	final String contentType = "Content-Type";
	final String reqResType = "application/json";

	public Response get(String endPoint, Map<String, ?> queryParam) {
		Map<String, String> headers = new HashMap<>();
		headers.put(contentType, reqResType);
		return get(endPoint, headers, queryParam);
	}

	public Response get(Map<String, String> headers, String endPoint) {
		return get(endPoint, headers, null);
	}

	public Response get(String endPoint, Map<String, String> headers, Map<String, ?> queryParam) {
		Response response = null;
		RequestSpecification httpRequest;
		try {
			RestAssured.baseURI = endPoint;
			if (endPoint.contains("https")) {
				httpRequest = RestAssured.given().relaxedHTTPSValidation();
				httpRequest.port(443);
			} else {
				httpRequest = RestAssured.given();
			}
			if (headers != null) {
				Set<String> headerKeys = headers.keySet();
				for (String headerKey : headerKeys)
					httpRequest.header(headerKey, headers.get(headerKey));
			}
			if (queryParam != null)
				httpRequest.params(queryParam);

			response = httpRequest.get();
			globalResponse = response;

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return response;
		}
	}

	public Response get(String endPoint) {
		return get(endPoint, "null");
	}

	public Response get(String endPoint, String param) {
		Map<String, String> headers = new HashMap<>();
		headers.put(contentType, reqResType);
		endPoint += "/" + param;
		return get(endPoint, headers, null);
	}

	public Response post(String messageFilePath, String endPoint) {
		JSONObject jsonobj = null;
		try {
			JSONParser parser = new JSONParser();
			jsonobj = (JSONObject) parser.parse(new FileReader(messageFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return post(jsonobj, endPoint);
	}

	public Response postMap(Map<String, String> messageAsMap, String endPoint) {
		return post(new JSONObject(messageAsMap), endPoint);
	}

	public Response post(Object jsonobj, String endPoint) {
		Map<String, String> map = new HashMap<>();
		map.put(contentType, reqResType);
		return post(endPoint, false, jsonobj.toString(), null, map);
	}

	public Response post(String requestBody, Map<String, String> headers, String endPoint) {
		return post(endPoint, false, requestBody, null, headers);
	}

	public Response post(String endPoint, boolean urlencodedForm, String requestBody,
			Map<String, String> requestBodyAsMap, Map<String, String> headers) {
		Response response = null;
		RequestSpecification httpRequest;
		try {
			RestAssured.baseURI = endPoint;
			httpRequest = RestAssured.given();
			if (urlencodedForm) {
				httpRequest.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs("x-www-form-urlencoded", ContentType.URLENC)));
			}
			if (endPoint.contains("https")) {
				httpRequest.relaxedHTTPSValidation();
				httpRequest.port(443);
			}
			if (headers != null) {
				Set<String> headerKeys = headers.keySet();
				for (String headerKey : headerKeys)
					httpRequest.header(headerKey, headers.get(headerKey));
			}
			if (urlencodedForm && requestBodyAsMap != null)
				httpRequest.formParams(requestBodyAsMap);
			else
				httpRequest.body(requestBody);
			response = httpRequest.post();

			globalResponse = response;

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Rest Assured Response Exception while performing POST operation: " + e.getMessage());
			return response;
		}
	}

	public Response post(Object requestBodyJSON, Map<String, String> headers, String endPoint) {
		return post(endPoint, false, requestBodyJSON.toString(), null, headers);
	}

	public Response post(String endPoint, boolean urlencodedForm, Map<String, String> body) {
		return post(endPoint, urlencodedForm, null, body, null);
	}

	public Response post(String endPoint, boolean urlencodedForm, Map<String, String> body,
			Map<String, String> headers) {
		return post(endPoint, urlencodedForm, null, body, headers);
	}

	/**
	 * To PUT the required body into any request and this method returns the
	 * response
	 */
	public Response put(String endPoint, String key, String username, String password, JSONObject currentMessage) {
		Response response = null;
		RequestSpecification httpRequest;
		try {
			RestAssured.baseURI = endPoint + key;
			if (endPoint.contains("https")) {
				httpRequest = RestAssured.given().auth().preemptive().basic(username, password);
				httpRequest.port(443);
			} else {
				httpRequest = RestAssured.given().auth().preemptive().basic(username, password);
			}
			httpRequest.header(contentType, reqResType);
			httpRequest.body(currentMessage);
			response = httpRequest.put(RestAssured.baseURI);
			globalResponse = response;

			Assert.assertTrue(globalResponse.statusCode() == 204);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Rest Assured Exception : " + e.getMessage());
		}
		return response;
	}

	public Response put(String endPoint, Object currentMessage) {
		return put(endPoint, currentMessage.toString());
	}

	public Response put(String endPoint, Object currentMessage, Map<String, String> headers) {
		Response response = null;
		RequestSpecification httpRequest;
		try {
			RestAssured.baseURI = endPoint;
			httpRequest = RestAssured.given().body(currentMessage);

			if (headers != null)
				httpRequest.headers(headers);

			response = httpRequest.when().put(RestAssured.baseURI);

			globalResponse = response;
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Rest Assured Response Exception while performing PUT operation is: " + e.getMessage());
		}
		return response;
	}

	public Response put(String endPoint, String currentMessage) {
		Map<String, String> headers = new HashMap<>();
		headers.put(contentType, reqResType);
		return put(endPoint, currentMessage, headers);
	}

	public Response delete(String endPoint, Map<String, ?> queryParam, Map<String, String> headers) {
		Response response = null;
		try {
			RestAssured.baseURI = endPoint;
			RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
			httpRequest.port(443);
			if (headers != null)
				httpRequest.headers(headers);
			if (queryParam != null)
				httpRequest.params(queryParam);
			httpRequest.body("[]");
			response = httpRequest.delete();
			globalResponse = response;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return response;
		}
	}

	public Response delete(Map<String, String> headers, String endPoint) {
		return delete(endPoint, null, headers);
	}

	public Response delete(String endPoint) {
		return delete(endPoint, null, null);
	}
}