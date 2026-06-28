package apiutilities;

import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * This class contains the javascript object notation (JSON) handling methods.
 * This class perform operation on json such as read,write,parse,compare JSON files.
 */
public class JSONHandlingMethods {
    private static final Logger LOG = LoggerFactory.getLogger(JSONHandlingMethods.class);
    Random ran = new SecureRandom();

    /**
     * This method to read json file and store its content in json object.
     *
     * @param filePath        - path where json file to stored
     * @return                - JSONObject
     * @throws IOException    - an exception occur during a batch update operation or attempting to access a file that does not exist at the specified location
     * @throws ParseException - this is a checked exception, and it occurs when you fail to parse a String that is ought to have a special format.
     */
    public JSONObject readJSONFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser;
        JSONObject jsonObject;
        var reader = new FileReader(filePath);
        jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(reader);
        return jsonObject;
    }

    /**
     * This method is used to capture the value from response/request json body.
     *
     * @param objectToParse - json object in which tag is present
     * @param tagname       - tag whose value has to be fetched
     * @return              - tagValue
     */
    public Object getJSONTagValue(JSONObject objectToParse, String tagname) {
        JSONObject obj = null;
        Object tagValue = null;
        var entry = iterateJSONObject(objectToParse);
        if (entry.getKey().toString().equalsIgnoreCase(tagname)) {
            if (isJSONArray(entry.getValue())) {
                obj = parseJSONArray(convertToJSONArray(entry.getValue()));
            } else if (entry.getValue() instanceof JSONObject) {
                obj = convertToJSONObject(entry.getValue());
            } else {
                tagValue = entry.getValue();
            }
            if (obj != null) {
                if (obj.size() > 1) {
                    var e = iterateJSONObject(obj);
                    var newObj = convertToJSONObject(e.getValue());
                    getJSONTagValue(newObj, tagname);
                } else if (obj.size() == 1) {
                    tagValue = returnTagValue(obj, tagname);
                }
            }
        }
        return tagValue;
    }

    /**
     * This method is used to check if the object returned is json array or json object.
     *
     * @param objectToValidate - object which has to be checked
     * @return                 - boolean (true/false)
     */
    public boolean isJSONArray(Object objectToValidate) {
        var flag = false;
        if (objectToValidate instanceof JSONArray) {
            flag = true;
        }
        return flag;
    }

    /**
     * This method is used to parse json array.
     *
     * @param resultJsonArray - json array to parsed
     * @return                - resultJsonObject
     */
    public JSONObject parseJSONArray(JSONArray resultJsonArray) {
        JSONObject resultJsonObject = null;
        for (var i = 0; i < resultJsonArray.size(); i++) {
            if (isJSONArray(resultJsonArray.get(i))) {
                resultJsonObject = parseJSONArray(convertToJSONArray(resultJsonArray.get(i)));
            } else {
                resultJsonObject = convertToJSONObject(resultJsonArray.get(i));
            }
        }
        return resultJsonObject;
    }

    /**
     * This method is used to type cast parsed object to json object.
     *
     * @param objectToConvert - object that has to be typecast
     * @return                - resultObject
     */
    public JSONObject convertToJSONObject(Object objectToConvert) {
        return (JSONObject) objectToConvert;
    }

    /**
     * This method is used to type cast parsed object to json array.
     *
     * @param objectToConvert - object that has to be typecast
     * @return                - resultJsonArray
     */
    public JSONArray convertToJSONArray(Object objectToConvert) {
        return (JSONArray) objectToConvert;
    }


    /**
     * This method is used iterates element under json.
     *
     * @param objectToBeIterated - json Object to be iterated
     * @return                   - Map.Entry
     */
    public Map.Entry<Object, Object> iterateJSONObject(JSONObject objectToBeIterated) {
        Map.Entry<Object, Object> entry = null;
        var entries = objectToBeIterated.entrySet();
        var it = entries.iterator();
        while (it.hasNext()) {
            entry = (Map.Entry) it.next();
        }
        return entry;
    }

    /**
     * This method is used to return a value corresponding to the tag searched.
     *
     * @param object  - its the json object to be modified
     * @param tagName - tag name value has to be fetched
     * @return        - object
     */
    public Object returnTagValue(JSONObject object, String tagName) {
        return object.get(tagName);
    }

    /**
     * This method is used to write contents to json file.
     *
     * @param file          - file path of json file
     * @param objectToWrite - json object to be written to that file
     */
    public void writeToJSONFile(String file, JSONObject objectToWrite) {
        try (var fileWriter = new FileWriter(file)) {
            fileWriter.write(objectToWrite.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            LOG.error("IOException on writeToJSONFile method: {}", e.getMessage());
        }
    }

    /**
     * This method is used to compare JSON files with actual value to the expected value.
     *
     * @param expected               - expected value to compare JSON files
     * @param actual                 - actual value to compare JSON files
     * @param ignoredValue           - ignored value to compare JSON files
     * @throws IOException           - an exception occur during a batch update operation or an exception that is thrown when an I/O error occurs
     * @throws ParseException        - this is a checked exception it can occur when you fail to parse a String that is ought to have a special format.
     */
    public void compareJSONFiles(String expected, String actual, String ignoredValue) throws IOException, ParseException {
        var ignvalsize = 0;
        var ignoreval = new String[0];
        if (ignoredValue.equals("")) {
            ignvalsize = 0;
        } else {
            if (ignoredValue.contains("~")) {
                ignoreval = ignoredValue.split("~");
            }
            ignvalsize = ignoreval.length;
        }
        var parser = new JSONParser();
        JSONObject data1 = (JSONObject) parser.parse(new FileReader(expected));
        var jsonFile1 = data1.toJSONString();
        JSONObject data2 = (JSONObject) parser.parse(new FileReader(actual));
        var jsonFile2 = data2.toJSONString();
        CustomComparator comparator = null;
        switch (ignvalsize) {
            case 1:
                comparator = new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization(ignoreval[0], (o1, o2) -> true)
                );
                break;
            case 2:
                comparator = new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization(ignoreval[0], (o1, o2) -> true),
                        new Customization(ignoreval[1], (o1, o2) -> true)
                );
                break;
            case 3:
                comparator = new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization(ignoreval[0], (o1, o2) -> true),
                        new Customization(ignoreval[1], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true)
                );
                break;
            case 4:
                comparator = new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization(ignoreval[0], (o1, o2) -> true),
                        new Customization(ignoreval[1], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true)
                );
                break;
            case 5:
                comparator = new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization(ignoreval[0], (o1, o2) -> true),
                        new Customization(ignoreval[1], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true),
                        new Customization(ignoreval[2], (o1, o2) -> true)
                );
                break;
            default:
                comparator = new CustomComparator(JSONCompareMode.LENIENT);
                break;
        }
        JSONAssert.assertEquals(jsonFile1, jsonFile2, comparator);
    }

    /**
     * This method is used to fetch reason name with using group name.
     *
     * @param jsondetails     - to fetch reason name
     * @param groupname       - to fetch reason name by using group name
     * @param parameterPath   - path of the parameter to fetch reason name with using group name
     * @return String         - value
     * @throws ParseException - this exception is throws when user unable to fetch reason name with using group name
     */
    public String fetchReasonNameUsingGroupName(String jsondetails, String groupname, String parameterPath) throws ParseException {
        String value = null;
        JSONArray jsonArray = null;
        String[] groupnamereason = groupname.split("~");
        var jsonParser = new JSONParser();
        Object obj = jsonParser.parse(jsondetails);
        jsonArray = (JSONArray) obj;
        long count = jsonArray.stream().count();
        var groupnameJSON = new JSONArray();
        var groupnameJSONcount = 0;
        for (var i = 0; i < count; i++) {
            JSONObject explrObject = (JSONObject) jsonArray.get(i);
            var js = explrObject.toJSONString();
            if (js.contains(groupnamereason[0]) && js.contains(groupnamereason[1])) {
                groupnameJSON.add(explrObject);
                groupnameJSONcount++;
            }
        }
        value = isGroupNameJsonCount(groupnameJSONcount, value, parameterPath, groupnameJSON);
        return value;
    }

    /**
     * This method is used to fetch reason name using group name excluding parents.
     *
     * @param jsonDetails               - to fetch reason name
     * @param groupName                 - name to fetch reason name using group name excluding parents
     * @param parentNames               - to fetch reason name using group name excluding parents
     * @param parameterPath             - path of the parameter in json file
     * @return String                   - value
     * @throws ParseException           - an exception throws when objects that are sent from clients cannot be parsed or occur during parsing the string
     * @throws NoSuchAlgorithmException - an exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public String fetchReasonNameUsingGroupNameExcludingParents(String jsonDetails, String groupName,
                                                                List<String> parentNames, String parameterPath) throws ParseException, NoSuchAlgorithmException {
        String value = null;
        JSONArray jsonArray = null;
        String[] groupNameReason = groupName.split("~");
        var jsonParser = new JSONParser();
        Object obj = jsonParser.parse(jsonDetails);
        jsonArray = (JSONArray) obj;
        long count = jsonArray.stream().count();
        var groupnameJSON = new JSONArray();
        var groupnameJSONcount = 0;
        List<String> excludedParentIds = new ArrayList<>();
        for (var i = 0; i < count; i++) {
            JSONObject explrObject = (JSONObject) jsonArray.get(i);
            var reasonJson = explrObject.toJSONString();
            var js = new JsonPath(reasonJson);
            for (String parentName : parentNames) {
                if (reasonJson.contains(parentName)) {
                    excludedParentIds.add(js.getString("id"));
                }
            }
        }
        for (var i = 0; i < count; i++) {
            JSONObject explrObject = (JSONObject) jsonArray.get(i);
            var reasonJson = explrObject.toJSONString();
            if (StringUtils.containsIgnoreCase(reasonJson, groupNameReason[0]) && StringUtils.containsIgnoreCase(reasonJson, groupNameReason[1])) {
                for (String parentId : excludedParentIds) {
                    if (!reasonJson.contains(parentId)) {
                        groupnameJSON.add(explrObject);
                        groupnameJSONcount++;
                    }
                }
            }
        }
        if (groupnameJSONcount == 0) {
            LOG.info("GROUP_NAME_JSON_COUNT IS ZERO");
        } else {
            Random rand = SecureRandom.getInstanceStrong();
            var groupnameJSONnumber = rand.nextInt(groupnameJSONcount);
            JSONObject reasonJSONobj = (JSONObject) groupnameJSON.get(groupnameJSONnumber);
            var reasonJSON = reasonJSONobj.toJSONString();
            JsonPathUtility jsonHelper = new JsonPathUtility(reasonJSON);
            value = jsonHelper.getStringElement(parameterPath);
        }
        return value;
    }

    /**
     * This method is used to return value corresponding to json count.
     *
     * @param groupnameJSONcount - its take json count
     * @param parameterPath      - path of the parameter
     * @param groupnameJSON      - it is json array type
     * @return String            - value
     */
    private String isGroupNameJsonCount(int groupnameJSONcount, String value, String parameterPath, JSONArray groupnameJSON) {
        if (groupnameJSONcount == 0) {
            LOG.info("Json count is not greater than 0");
        } else {
            var groupnameJSONnumber = ran.nextInt(groupnameJSONcount);
            JSONObject reasonJSONobj = (JSONObject) groupnameJSON.get(groupnameJSONnumber);
            var reasonJSON = reasonJSONobj.toJSONString();
            var js = new JsonPath(reasonJSON);
            value = js.getString(parameterPath).replace("[", "").replace("]", "");
        }
        return value;
    }

    /**
     * This method is used to fetch reason name using parent name.
     *
     * @param jsonDetails     - to fetch reason name using parent name
     * @param parentName      - parent name to fetch reason name using parent name
     * @param parameterPath   - path of the parameter
     * @return String         - value
     * @throws ParseException - this is a checked exception and it occur when you fail to parse a String that is ought to have a special format.
     */
    public String fetchReasonNameUsingParentName(String jsonDetails, String parentName, String parameterPath) throws ParseException {
        String value = null;
        JSONArray jsonArray = null;
        var jsonParser = new JSONParser();
        Object obj = jsonParser.parse(jsonDetails);
        jsonArray = (JSONArray) obj;
        long count = jsonArray.stream().count();
        var parentRelatedJSON = new JSONArray();
        var parentRelatedJsonCount = 0;
        String parentId = null;
        for (var i = 0; i < count; i++) {
            JSONObject explrObject = (JSONObject) jsonArray.get(i);
            var reasonJson = explrObject.toJSONString();
            var js = new JsonPath(reasonJson);
            if (reasonJson.contains(parentName)) {
                parentId = js.getString("id");
                break;
            }
        }
        for (var i = 0; i < count; i++) {
            JSONObject explrObject = (JSONObject) jsonArray.get(i);
            var js = explrObject.toJSONString();
            if (js.contains(parentId) && js.contains("C1ReasonID")) {
                parentRelatedJSON.add(explrObject);
                parentRelatedJsonCount++;
            }
        }
        if (parentRelatedJsonCount == 0) {
            LOG.info("Json counts should be greater than 0");
        } else {
            var parentJsonNumber = ran.nextInt(parentRelatedJsonCount);
            JSONObject reasonJSONobj = (JSONObject) parentRelatedJSON.get(parentJsonNumber);
            var reasonJSON = reasonJSONobj.toJSONString();
            var jsonHelper = new JsonPathUtility(reasonJSON);
            value = jsonHelper.getStringElement(parameterPath);
        }
        return value;
    }
}