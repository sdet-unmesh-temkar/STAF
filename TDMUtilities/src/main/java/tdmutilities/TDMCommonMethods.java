package tdmutilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.datatable.DataTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
/**
 * This class perform operations related to TDM Common Methods
 * This class contains methods to perform different operations to implement TDM APIs successfully
 */
public class TDMCommonMethods {

  private static final Logger LOG = LoggerFactory.getLogger(TDMCommonMethods.class);

  /**
   * This method creates suitable Body format for Generating Entity List.
   * @param dataTable       - contains Customer details with relevant parameters
   * @param sourceEnv       - source environment of customers
   * @return                - JSONObject json
   */

  public JSONObject listToJSONObject(DataTable dataTable, String sourceEnv) {
    List<Map<String, String>> mapValue = dataTable.asMaps(String.class, String.class);
    JSONObject json = new JSONObject();
    json.put("sourceEnv", sourceEnv);

    JSONArray array = new JSONArray();

    for (int i = 0; i < mapValue.size(); i++) {
      JSONObject item = new JSONObject();
      item.put("param", mapValue.get(i).get("parameter"));
      item.put("operator", mapValue.get(i).get("operator"));
      item.put("value", mapValue.get(i).get("value"));
      array.put(item);

    }
    json.put("params", array);
    LOG.info("JsonBody : {}", json);
    return json;
  }

  /**
   *  This method creates suitable body format for Fetching Customers from feature file.
   * @param dataTable     - Customers, provided from feature file
   * @return              - jsonObject is returned to create json body.
   */

  public JSONObject datatableToJSONObject(DataTable dataTable) {
    List<String> list = dataTable.asList();
    String listAsString = list.stream().map(String::valueOf).collect(Collectors.joining(","));


    JSONObject jsonObject = new JSONObject();
    jsonObject.put("entitieslist", listAsString);
    LOG.info("Jsonbody: {}", jsonObject);

    return jsonObject;

  }


  /**
   * This method reads property files
   * @return               - Map list
   * @throws IOException   - an I/O exception thrown if unable to read property file
   */

  public Map<String, String> tdmReadPropertyFile( ) throws IOException {
    Map<String, String> list = new HashMap<>();
    String pathOfProperties = "./src/test/resources/constants.properties";
    Properties properties = new Properties();
    try (FileReader reader = new FileReader(pathOfProperties)) {
      properties.load(reader);
      for (String key : properties.stringPropertyNames()) {
        list.put(key, properties.getProperty(key));
      }

    } catch (FileNotFoundException e) {
      LOG.warn("exception :",e);
    }
    return list;
  }

  /**
   * This method gets number of successfully created Customers from response body.
   * @param response          - String form of response.
   * @param keyOfResponse     - key of innerObject to add in created customers.
   * @return                  - int customerCount
   */

  public int getNumberOfCustomersIds(String response, String keyOfResponse) {
    JsonElement jsonElement= JsonParser.parseString(response);
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    int customerCount = 0;
    for (String key : jsonObject.keySet()) {
      JsonObject innerObject = jsonObject.get(key).getAsJsonObject();
      if (innerObject.has(keyOfResponse)) {
        customerCount++;
      }
    }
    return customerCount;
  }

  /**
   * This method creates a JSONObject that contains only the customer details from the response body.
   * Response body could contain information other than customer details.
   * @param jsonBody  - Response body
   * @return          - jsonObject is returned to fetch correct customer details
   */
  public JSONObject createCustomerList(String jsonBody) {

    JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
    JsonObject resultMap = new JsonObject();

    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      String key = entry.getKey();
      JsonElement value = entry.getValue();

      if (!key.equals("Entity_Status_Details")) {
        resultMap.add(key, value);
      }
    }
    return new JSONObject (resultMap.toString());
  }

  /**
   *  This method saves the Customer ID and generated customer details of the first customer in JSONObject format.
   * @param responseBody      - Response body
   * @return                  - jsonObject is returned to fetch first customer details
   */
  public  JsonObject getFirstCustomer(String responseBody) {

    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
    String firstEntryKey = jsonObject.keySet().iterator().next();
    JsonObject firstEntryValue = jsonObject.getAsJsonObject(firstEntryKey);
    JsonObject firstCustomerDetails = new JsonObject();
    firstCustomerDetails.add(firstEntryKey, firstEntryValue);

    return firstCustomerDetails;


  }

}
