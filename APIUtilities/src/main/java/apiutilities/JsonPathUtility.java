package apiutilities;

import com.jayway.jsonpath.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;
import java.util.regex.Pattern;


/**
 * This class contains the JavaScript Object Notation (JSON) handling related method.
 * This class perform operation on JSON such as isElement present,delete/add element,get the array Size etc.
 */
public class JsonPathUtility {
	private String jsonBody;
	private static final Logger LOG = LoggerFactory.getLogger(JsonPathUtility.class);

	/**
	 * Constructor having string type jsonBody argument is used to initialise Object of class JsonPathUtility.
	 *
	 * @param jsonBody - initialise Object of class JsonPathUtility
	 */
	public JsonPathUtility(String jsonBody) {
		this.jsonBody = jsonBody;
	}

	/**
	 * This method is used to get element as string list from the json path.
	 *
	 * @param jsonPath - to get element as string list from the json path
	 * @return         - List (string type)
	 */
	public List<String> getElementAsStringList(String jsonPath) {
		Configuration conf = Configuration.defaultConfiguration()
				.addOptions(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
		Assert.assertTrue(this.isJsonPathValid(jsonPath), "jsonPath valid: " + jsonPath);
		Assert.assertTrue(this.isElementExist(jsonPath), "jsonPath: " + jsonPath + " element exist");
		return JsonPath.using(conf).parse(this.jsonBody).read(jsonPath, new Predicate[0]);
	}

	/**
	 * This method is used to get string element from given json file path.
	 *
	 * @param jsonPath - to get string element from given json file path
	 * @return         - string element from given json file path
	 */
	public String getStringElement(String jsonPath) {
		return String.valueOf(this.getElementAsStringList(jsonPath).get(0));
	}

	/**
	 * This method is used to get boolean element from given json file path.
	 *
	 * @param jsonPath - path of json file to get boolean element from given json file path
	 * @return         - boolean (true/false)
	 */
	public boolean getBooleanElement(String jsonPath) {
		return Boolean.getBoolean(this.getElementAsStringList(jsonPath).get(0));
	}

	/**
	 * This method is used to get json body.
	 *
	 * @return - jsonBody
	 */
	public String getBody() {
		return this.jsonBody;
	}

	/**
	 * This method is used to validate json file path.
	 *
	 * @param jsonPath - to validate json file path.
	 * @return         - boolean (true/false)
	 */
	private boolean isJsonPathValid(String jsonPath) {
		try {
			JsonPath.compile(jsonPath);
		} catch (InvalidPathException e) {
            LOG.error("Invalid JSONPath: {}", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * This method is used to check if the element exists in that path.
	 *
	 * @param jsonPath - path of json file
	 * @return         - boolean (true/false)
	 */
	public boolean isElementExist(String jsonPath) {
		var elementExist = true;
		if (this.getBody().isEmpty()) {
			return false;
		} else {
			try {
				Object result = JsonPath.parse(this.getBody()).read(jsonPath);
				if (result != null && result.getClass().getCanonicalName().contains("JSONArray")) {
					var resultJSONArray = (new JSONObject("{\"result\":" + result.toString() + "}"))
							.getJSONArray("result");
					if ((jsonPath.contains("[") || jsonPath.contains("..")) && resultJSONArray.length() == 0) {
						elementExist = false;
					}
				}
			} catch (PathNotFoundException var5) {
				elementExist = false;
			}
			return elementExist;
		}
	}

	/**
	 * This method is used to check if the body has value. In the implementation of method "IsElementExist" method used for assertion if file exists in this path.
	 *
	 * @param jsonPath - path of the Json file
	 * @return boolean - true/false
	 */
	public boolean isBodyValueNull(String jsonPath) {
		Assert.assertTrue(this.isElementExist(jsonPath));
		try {
			Object result = JsonPath.parse(this.jsonBody).read(jsonPath);
			if (result != null && result != JSONObject.NULL) {
				if (result.getClass().getCanonicalName().contains("Array")) {
					var jsonObject = new JSONObject("{\"result\":" + result.toString() + "}");
					var arr1 = jsonObject.getJSONArray("result");
					if (arr1.length() > 0) {
						return arr1.get(0) == null || arr1.get(0) == JSONObject.NULL;
					} else {
						LOG.error("json path not found");
						return false;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		} catch (PathNotFoundException var5) {
			LOG.error("json path not found");
			return false;
		}
	}

	/**
	 * This method is used to get size of the array.
	 *
	 * @param jsonPath - path of the file
	 * @return         - size of the array in integer
	 */
	public int getArraySize(String jsonPath) {
		if (this.isElementExist(jsonPath)) {
			try {
				return Integer.parseInt(
						String.valueOf(this.getElementAsObject(jsonPath + ".length()")).replaceAll(String.valueOf(Pattern.compile("[\\[|\\]]")), ""));
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		return -1;
	}

	/**
	 * This method is used to get element as object from json files path.
	 *
	 * @param jsonPath - to get element as object from json files path
	 * @return         - Object
	 */
	public Object getElementAsObject(String jsonPath) {
		return JsonPath.parse(this.jsonBody).read(jsonPath);
	}

	/**
	 * This method is used to check is element contain json string.
	 *
	 * @param jsonPath -  path of the file
	 * @return         -  boolean (true/false)
	 */
	public boolean isElementJsonString(String jsonPath) {
		var myObject = this.getElementAsObject(jsonPath);
		return myObject instanceof String;
	}

	/**
	 * This method is used to update element value.
	 *
	 * @param jsonPath - path of the file to update element value
	 * @param value    - passing Object value to update element value
	 * @return         - String
	 */
	public String updateElementValue(String jsonPath, Object value) {
		try {
			var result = JsonPath.parse(this.jsonBody).set(jsonPath, value).jsonString();
			this.jsonBody = result;
			return result;
		} catch (Exception var5) {
			LOG.error("JSON value update FAILED");
			LOG.error(var5.getMessage());
			return "{}";
		}
	}

	/**
	 * This method is used to add element to JSON object.
	 *
	 * @param jsonPath - path of the file
	 * @param key      - key to be set to add element to JSON object
	 * @param value    - its value
	 */
	public void addElementToJSONObject(String jsonPath, String key, String value) {
		try {
			var result = JsonPath.parse(this.jsonBody)
					.put(jsonPath, key, JsonPath.parse(value).json()).jsonString();
			this.jsonBody = result;
		} catch (Exception var5) {
			LOG.error("JSON value add FAILED.");
			LOG.error(var5.getMessage());
		}

	}

	/**
	 * This method is used to add element to json array.
	 *
	 * @param jsonPath - path of file to add element into json array
	 * @param key      - key to be set to add element into json array
	 */
	public void addElementToJSONArray(String jsonPath, String key) {
		var newJson = "";

		try {
			if (key == null || key.isEmpty() || key.trim().charAt(0) != '{' && key.trim().charAt(0) != '[') {
				newJson = JsonPath.parse(this.jsonBody).add(jsonPath, key).jsonString();
			} else {
				newJson = JsonPath.parse(this.jsonBody).add(jsonPath, JsonPath.parse(key).json())
						.jsonString();
			}
			this.jsonBody = newJson;
		} catch (Exception var5) {
			LOG.error("JSON value add FAILED");
			LOG.error(var5.getMessage());
		}
	}

	/**
	 * This method is used to delete the element from the json file.
	 *
	 * @param jsonPath - path of Json file to delete the element from the json file
	 */
	public void deleteElement(String jsonPath) {
		try {
			var result = JsonPath.parse(this.jsonBody).delete(jsonPath).jsonString();
			this.jsonBody = result;
		} catch (Exception var3) {
			LOG.error("JSON value delete FAILED");
			LOG.error(var3.getMessage());
		}
	}
}