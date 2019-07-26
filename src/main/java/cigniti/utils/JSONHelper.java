package cigniti.utils;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

public class JSONHelper {

	/**
	 * To read message from the supplied path and returns it as a
	 * org.json.simple.JSONObject
	 */
	public static JSONObject messageAsSimpleJson(String messageFilePath) {
		JSONObject jsonobj;
		try {
			JSONParser parser = new JSONParser();
			jsonobj = (JSONObject) parser.parse(new FileReader(messageFilePath));
			return jsonobj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray messageAsSimpleJsonArray(String messageFilePath) {
		JSONArray jsonArray;
		try {
			JSONParser parser = new JSONParser();
			jsonArray = (JSONArray) parser.parse(new FileReader(messageFilePath));
			return jsonArray;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * To read message from the supplied path and returns it as a
	 * org.json.JSONObject
	 * 
	 * @param messageFilePath
	 * @return
	 */
	public static org.json.JSONObject messageAsActualJson(String messageFilePath) {
		try {
			File file = new File(messageFilePath);
			String messageFromLocalData = FileUtils.readFileToString(file, "utf-8");
			return new org.json.JSONObject(messageFromLocalData);
		} catch (Exception e) {
			return null;
		}
	}

	public static org.json.JSONArray messageAsActualJsonArray(String messageFilePath) {
		try {
			File file = new File(messageFilePath);
			String messageFromLocalData = FileUtils.readFileToString(file, "utf-8");
			return new org.json.JSONArray(messageFromLocalData);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * To compare org.json.simple JSON messages
	 */
	public static void compareJSONMessages(JSONObject expected, JSONObject actual) {
		Set expectedKes = expected.keySet();
		for (Object key : expectedKes) {
			Assert.assertEquals(actual.get(key.toString()), expected.get(key.toString()));
		}
	}

	/**
	 * To compare org.json.simple JSON and org.json JSON messages
	 */
	public static void compareJSONMessages(JSONObject expected, org.json.JSONObject actual) {
		Set expectedKes = expected.keySet();
		for (Object expectedKey : expectedKes) {
			String modifiedExpectedKey = expectedKey.toString().substring(0, 1).toUpperCase()
					+ expectedKey.toString().substring(1).toLowerCase();
			Assert.assertEquals(actual.get(modifiedExpectedKey), expected.get(expectedKey));
		}
	}

	/**
	 * To compare org.json.simple JSON, org.json JSON messages and a Hashmap
	 */
	public static void compareJSONMessages(JSONObject expected, org.json.JSONObject actual, Map modifiedLocalKeySet) {
		Set expectedKes = expected.keySet();
		for (Object expectedKey : expectedKes) {
			String modifiedExpectedKey = (modifiedLocalKeySet.get(expectedKey) == null) ? expectedKey.toString()
					: modifiedLocalKeySet.get(expectedKey).toString();
			Assert.assertEquals(actual.get(modifiedExpectedKey), expected.get(expectedKey));
		}
	}

	public static JsonObject mapAsJson(Map<String, String> map) {
		JsonObject object = null;
		Set<String> keys = map.keySet();
		for (String key : keys) {
			object.addProperty(key, map.get(key));
		}
		return object;
	}

	public static org.json.JSONObject xmlAsJson(String xmlString) {
		return XML.toJSONObject(xmlString);
	}
}
