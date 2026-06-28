package com.solstice.staf.interframework.integrationtests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.InvalidJsonException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Maps;

import java.util.HashMap;
import java.util.Map;

public class CustomData {

    private static final ThreadLocal<CustomData> instance = ThreadLocal.withInitial(CustomData::new);
    @Getter
    private final Map<String, Object> customDataMap = new HashMap<>();
    private Object customDataJsonValue = "";
    private static final Logger log = LoggerFactory.getLogger(CustomData.class);

    public static CustomData getInstance() {
        return instance.get();
    }

    /**
     * This method is used to set customDataMap.
     *
     * @param customDataMap - scenario custom data in form of map
     */
    public void setCustomDataMap(Map<String, Object> customDataMap) {
        getCustomDataMap().putAll(customDataMap);
        this.customDataJsonValue = "";
    }

    /**
     * This method is used to get customDataJson.
     *
     * @return - customDataJson
     */
    public Object getCustomDataJson() {
        return this.customDataJsonValue;
    }

    /**
     * This method is used to set customDataJson.
     *
     * @param customDataJson - scenario custom data in form of Json
     */
    public void setCustomDataJson(Object customDataJson) {
        this.customDataJsonValue = customDataJson;
        this.customDataMap.clear();

    }

    /**
     * This method is used to cleanup the thread local instance.
     */
    public void unload() {
        instance.remove();
    }

    /**
     * This method checks if the custom data is a Map or Json, else returns empty String.
     *
     * @return map : Returns custom data map if data is present else returns empty String
     */
    public Object getCustomData() {
        if (!customDataMap.isEmpty()) {
            log.info("Custom Data Map: {} ", customDataMap);
            try {
                // Use ObjectMapper to properly serialize complex objects to JSON
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(customDataMap);
                return mapper.readValue(jsonString, Object.class);
            } catch (Exception e) {
                log.error("Failed to serialize custom data map to JSON: {}", e.getMessage());
                // Fallback to raw map if serialization fails
                return Maps.newHashMap(customDataMap);
            }
        }

        if (customDataJsonValue != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(String.valueOf(customDataJsonValue));// Try to parse as JSON Object or JSON Array
                log.info("Custom Data Json: {}", customDataJsonValue);
                return customDataJsonValue;
            } catch (Exception e) {
                log.error("Invalid Json {}", e.getMessage());
                throw new InvalidJsonException("Invalid JSON format.");
            }
        }

        return "";
    }

}
