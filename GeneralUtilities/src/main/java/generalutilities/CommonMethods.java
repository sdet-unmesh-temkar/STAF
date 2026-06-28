package generalutilities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class contains common methods such as wait scenarios for tens seconds,date implementation etc.
 */
public class CommonMethods {
    /**
     * Private constructor to prevent instantiation in other class.
     */
    private CommonMethods() {
    }

    /**
     * This method is used to wait scenarios for ten seconds.
     *
     * @return - List (String type)
     */
    public static List<String> waitScenariosForTenSeconds() {
        List<String> ar = new ArrayList<>();
        ar.add("customer-bcmconnector-service");
        return ar;
    }

    /**
     * This method is used to wait scenarios for two minutes.
     *
     * @return  - List (String type)
     */
    public static List<String> waitScenariosForTwoMinutes() {
        List<String> ar = new ArrayList<>();
        ar.add("productorder-productorder-service");
        return ar;
    }

    /**
     * This method is used to wait scenarios for four minutes.
     *
     * @return - List (String type)
     */
    public static List<String> waitScenariosForFourMinutes() {
        List<String> ar1 = new ArrayList<>();
        ar1.add("orderexecution-flowcontext-service");
        ar1.add("pi-product-service");
        ar1.add("productorder-submitorder-service");
        ar1.add("digitalmass-massoperation-service");
        return ar1;
    }

	/**
     * This method is used to wait scenarios for more minutes.
     *
     * @return - List (String type)
     */
    public static List<String> waitScenariosForMoreMinutes() {
        List<String> ar2 = new ArrayList<>();
        ar2.add("orderexecution-flowcontext-service");
        return ar2;
    }

    /**
     * This method is used to implement date.
     *
     * @return String - offsetDate
     */
    public static String dateImplementation() {
        var date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        var afterAddingMins = new Date(timeInSecs + (120000));
        OffsetDateTime offsetDate = afterAddingMins.toInstant().atOffset(ZoneOffset.UTC);
        return offsetDate.toString();
    }

    /**
     * This method is used to create a usernames list.
     *
     * @return - List of name of the users (String type).
     */
    public static List<String> userNames() {
        List<String> userNames = new ArrayList<>();
        userNames.add("agent-desktop-ad2d1");
        userNames.add("sap-tibco-user");
        userNames.add("mass-integration-user");
        userNames.add("titan-user");
        userNames.add("mint-integration-user");
        userNames.add("migrationr");
        userNames.add("Migration");
        userNames.add("csruser");
        userNames.add("cmpa-integration-user");
        userNames.add("genesys-integration-user");
        return userNames;
    }
}
