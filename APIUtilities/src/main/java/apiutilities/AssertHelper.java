package apiutilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class consist of a methods to add extra useful assert functionality missing in junit.
 * This class contain assertion related method like get/set JsonPathUtility,assert null,assert exist,assert boolean etc.
 */
public class AssertHelper extends generalutilities.CustomSoftAssert {
	private JsonPathUtility jsonPathUtility;
	private static final String MESSAGE_TEMPLATE = "jsonPath: %s, expected: %s, actual: %s";
	private static final String MESSAGE_TEMPLATE_BOOLEAN = "%s -> %s";
	private static final String RESULT_KEY = "result";
	private static final String RESULT_JSON = "{\"result\":";
	private static final String BOOLEAN_VALUE = "Boolean";
	private static final String ARRAY_VALUE = "Array";
	private static final Logger LOG = LoggerFactory.getLogger(AssertHelper.class);

	/**
	 * This method is used to add extra useful assert functionality missing in junit.
	 *
	 * @param jsonBody - to add extra useful assert functionality missing in junit.
	 */
	public AssertHelper(String jsonBody) {
		this.setJsonPathUtility(jsonBody);
	}


	/**
	 * This method is used to add extra useful assert functionality missing in junit.
	 *
	 * @param jsonPathUtility - to add extra useful assert functionality missing in junit
	 */
	public AssertHelper(JsonPathUtility jsonPathUtility) {
		this.setJsonPathUtility(jsonPathUtility);
	}

	/**
	 * This method is used to get json path utility.
	 *
	 * @return - JsonPathUtility
	 */
	public JsonPathUtility getJsonPathUtility() {
		return this.jsonPathUtility;
	}

	/**
	 * This method is used to set json path utility.
	 *
	 * @param jsonPathUtility - to set json path utility
	 */
	public void setJsonPathUtility(JsonPathUtility jsonPathUtility) {
		this.jsonPathUtility = jsonPathUtility;
	}

	/**
	 * This method is used to set json path utility.
	 *
	 * @param jsonBody - to set json path utility
	 */
	public void setJsonPathUtility(String jsonBody) {
		this.setJsonPathUtility(new JsonPathUtility(jsonBody));
	}

	/**
	 * This method is used to check regular expression.
	 *
	 * @param jsonPath            - path of the json file in string to check regular expression.
	 * @param expectedValue       - expected value in string to check regular expression
	 * @param assertMessage       - to check regular expression.
	 * @param isRegularExpression - boolean (true/false)
	 */
	public void assertString(String jsonPath, String expectedValue, String assertMessage, boolean isRegularExpression) {
		var actualValue = String.valueOf(this.getJsonPathUtility().getStringElement(jsonPath));
		if (isRegularExpression) {
			assertTrue(actualValue.matches(expectedValue),
					String.format(MESSAGE_TEMPLATE, jsonPath, expectedValue, actualValue));
		} else {
			assertTrue(actualValue.equals(expectedValue),
					String.format(MESSAGE_TEMPLATE, jsonPath, expectedValue, actualValue));
		}
		LOG.info(assertMessage);
	}

	/**
	 * This method is used to check regular expression is not to be null.
	 *
	 * @param jsonPath      - path of the json file to check regular expression is not to be null
	 * @param assertMessage - to check regular expression is not to be null
	 */
	public void assertNotNull(String jsonPath, String assertMessage) {
		if (!this.getJsonPathUtility().isElementExist(jsonPath)) {
			assertTrue(this.getJsonPathUtility().isElementExist(jsonPath), assertMessage);
		} else {
			assertFalse(this.getJsonPathUtility().isBodyValueNull(jsonPath),
					String.format(MESSAGE_TEMPLATE_BOOLEAN, jsonPath, "NOT NULL"));
		}
	}

	/**
	 * This method is used to verify that the object that is passed is equal to null.
	 *
	 * @param jsonPath      - path of the json file to verify that the object that is passed is equal to null
	 * @param assertMessage - to verify that the object that is passed is equal to null
	 */
	public void assertNull(String jsonPath, String assertMessage) {
		assertTrue(this.getJsonPathUtility().isBodyValueNull(jsonPath),
				String.format(MESSAGE_TEMPLATE_BOOLEAN, jsonPath, "NULL"));
		LOG.info(assertMessage);
	}

	/**
	 * This method is used to check presence of element.
	 *
	 * @param jsonPath      - path of the json file to check presence of element
	 * @param assertMessage - to check presence of element
	 */
	public void assertNotExist(String jsonPath, String assertMessage) {
		assertFalse(this.getJsonPathUtility().isElementExist(jsonPath),
				String.format(MESSAGE_TEMPLATE_BOOLEAN, jsonPath, "NOT EXIST"));
		LOG.info(assertMessage);
	}

	/**
	 * This method is used to check element is present or not.
	 *
	 * @param jsonPath      - path of the json file to check element is present or not
	 * @param assertMessage - to check element is present or not
	 * @return              - boolean (true/false)
	 */
	public boolean assertExist(String jsonPath, String assertMessage) {
		boolean isElementExist = this.getJsonPathUtility().isElementExist(jsonPath);
		assertTrue(isElementExist,
				String.format(MESSAGE_TEMPLATE_BOOLEAN, jsonPath, "EXIST"));
		LOG.info(assertMessage);
		return isElementExist;
	}

	/**
	 * This method is used to check the element is present or not.
	 *
	 * @param jsonPath - path of the json file to check the element is present or not
	 */
	public void assertBoolean(String jsonPath) {
		if (!this.getJsonPathUtility().isElementExist(jsonPath)) {
			assertTrue(false, jsonPath + ": PathNotFoundException.");
		} else {
			var result = this.getJsonPathUtility().getElementAsObject(jsonPath);
			if (result == null) {
				assertTrue(false,
						String.format(MESSAGE_TEMPLATE, jsonPath, BOOLEAN_VALUE, "null"));
			} else if (result.getClass().getCanonicalName().contains(BOOLEAN_VALUE)) {
				assertTrue(true,
						String.format(MESSAGE_TEMPLATE, jsonPath, BOOLEAN_VALUE, BOOLEAN_VALUE));
			} else if (result.getClass().getCanonicalName().contains(ARRAY_VALUE)) {
				try {
					var jsonObject = new JSONObject(RESULT_JSON + result.toString() + "}");
					var arr1 = jsonObject.getJSONArray(RESULT_KEY);
					assertTrue(arr1.get(0).getClass().getCanonicalName().contains(BOOLEAN_VALUE),
							jsonPath + " expected: boolean actual: " + arr1.get(0).getClass().getCanonicalName() + ".");
				} catch (JSONException var5) {
					assertTrue(false, String.format(MESSAGE_TEMPLATE, jsonPath, BOOLEAN_VALUE,
							var5.getMessage()));
				}
			} else {
				assertTrue(false,
						String.format(MESSAGE_TEMPLATE, jsonPath, BOOLEAN_VALUE, "not boolean"));
			}
		}
	}

	/**
	 * This method is used to check element is present or not.
	 *
	 * @param jsonPath - path of the json file to check the element is present or not
	 * @param expected - boolean (true/false)
	 */
	public void assertBoolean(String jsonPath, boolean expected) {
		if (!this.getJsonPathUtility().isElementExist(jsonPath)) {
			assertTrue(false, " PathNotFoundException  jsonPath " + jsonPath);
		} else {
			var result = this.getJsonPathUtility().getElementAsObject(jsonPath);
			if (result == null) {
				assertTrue(false, jsonPath + "expected: " + RESULT_KEY + " actual: null.");
			} else if (!result.getClass().getCanonicalName().contains(BOOLEAN_VALUE)
					&& !result.getClass().getCanonicalName().contains(ARRAY_VALUE)) {
				assertTrue(false, String.format(MESSAGE_TEMPLATE, jsonPath, BOOLEAN_VALUE,
						result.getClass().getCanonicalName()));
			} else if (result.getClass().getCanonicalName().contains(ARRAY_VALUE)) {
				try {
					var jsonObject = new JSONObject(RESULT_JSON + result.toString() + "}");
					var arr1 = jsonObject.getJSONArray(RESULT_KEY);
					assertTrue(Boolean.valueOf(String.valueOf(arr1.get(0))) == expected,
							String.format(MESSAGE_TEMPLATE, jsonPath, expected,
									result));
				} catch (JSONException var6) {
					assertTrue(false, var6.getMessage() + " jsonPath " + jsonPath);
				}
			} else {
				assertEquals(result, expected, " jsonPath " + jsonPath);
			}
		}
	}
}