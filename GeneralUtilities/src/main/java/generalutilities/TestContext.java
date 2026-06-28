package generalutilities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class is used to store test case or scenario level data. If you want to share data across different steps, then this class can be used.
 * This class contains methods such as get/set context, get/set property etc.
 */
public class TestContext<T> extends HashMap<String, T> {

    private static final ThreadLocal<TestContext<?>> context = ThreadLocal.withInitial(TestContext::new);
    private static final Logger log = LoggerFactory.getLogger(TestContext.class);

    /**
     * Private constructor to prevent instantiation in other class.
     */
    private TestContext() {
        super();
        ThreadLocalRegistry.register(context);
    }

    /**
     * This method is used to get the current context instance local to the thread.
     *
     * @return - test context object local to the thread
     */

    private static TestContext getContext() {
        return context.get();
    }

    /**
     * This method is used to set this class instance local to the thread.
     *
     * @param context - to test context object
     */
    private static void setContext(TestContext context) {
        TestContext.context.set(context);
    }

    /**
     * This method is used to get the instance of this class.
     *
     * @return - TestContext
     */
    public static TestContext getInstance() {
        return getContext();
    }

    /**
     * This method is used to cleanup the thread local instance.
     */

    public void unload() {
        context.remove();
    }

    /**
     * This method is used to set the value of property.
     *
     * @param property - property key param name to set value into property
     * @param value    - actual value of property key param name
     */
    public void setProperty(String property, T value) {
        this.put(property, value);
    }

  /**
   * This method is used to get the value of the property.
   *
   * @param property - property name to get the value of the property
   * @return T       - propertyValue
   * @throws IllegalArgumentException if the property key is not contained
   */
  public T getProperty(String property) {
    //check if key exist in TestContext
    if (!this.containsKey(property)) {
      throw new IllegalArgumentException("Key '" + property + "' is not present in TestContext");
    }
    //If key exist as exception was not thrown, we assign value
    var propertyValue = this.get(property);

    //if value is null we log warning:
    if (Objects.isNull(propertyValue)) {
      log.warn("Value for the key {} is null", property);
    }
    return propertyValue;
  }


    /**
     * This method is used to get the set of all the properties.
     *
     * @return keySet - Set of properties
     */
    public Set<String> getPropertiesSet() {
        return this.keySet();
    }

    /**
     * This method is used to get the properties of the map.
     *
     * @return Map - properties
     */
    public Map<String, T> getPropertiesMap() {
        return this;
    }

    /**
     * This method is used to check if the property exists in the context.
     *
     * @param propertyName - to check if the property exists in the context
     * @return             - boolean (true/false)
     */
    public boolean isPropertyPresent(String propertyName) {
        return this.get(propertyName) != null;
    }
}