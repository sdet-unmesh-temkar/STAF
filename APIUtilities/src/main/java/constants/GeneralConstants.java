package constants;

import generalutilities.EnvironmentDataLoader;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class contains a method related to general constants
 * This class consist of a method such as get password, get user, get channel name etc.
 */
public class GeneralConstants {
    public static final String GENERATE_TOKEN_URL = "/auth/realms/apigw/protocol/openid-connect/token";
    public static final String HEADER_PARAM_CONTENT_TYPE = "content-type";
    public static final String HEADER_CONTENT_TYPE_VALUE_AUTHORIZATION = "application/x-www-form-urlencoded";
    public static final String QUERY_PARAM_SALES_CHANNEL = "salesChannel";
    public static final String FORM_PARAM_CLIENT_ID = "client_id";
    public static final String FORM_PARAM_GRANT_TYPE = "grant_type";
    public static final String FORM_PARAM_USERNAME = "username";
    public static final String FORM_PARAM_PASSWORD = "password";
    public static final String FORM_PARAM_CLIENT_ID_VALUE = "apigw";
    public static final String FORM_PARAM_GRANT_TYPE_VALUE = "password";
    private static Map<String, String> envirnoment = EnvironmentDataLoader.getInstance().getEnvironment();
    public static final String SYS_PASSWORD = envirnoment.get("STAF_UTILITIES/SYS_PASSWORD");
    public enum SalesChannel {
        CALL_CENTER("csruser", SYS_PASSWORD, "CallCenter"),
        MIGRATION("modmigration", SYS_PASSWORD, "ModMigration"),
        DUNNING("dunninguser", SYS_PASSWORD, "Dunning"),
        WEBSALE("posuser", SYS_PASSWORD, "Websale"),
        TELESALE("posuser", SYS_PASSWORD, "Telesale"),
        AGENT_DESKTOP("agent-desktop-ad2d1", SYS_PASSWORD, "Retail"),
        TITAN_USER("titan-user", SYS_PASSWORD,"titanUser") ;
        private String user;
        private String password;
        private String salesChannel;

        /**
         * Private constructor to prevent direct instantiation in other class and it provide different value to distinct object.
         */
        SalesChannel(String user, String password, String salesChannel) {
            this.user = user;
            this.password = password;
            this.salesChannel = salesChannel;
        }

        /**
         * This method is used to get the password.
         *
         * @return - password
         */
        public String getPassword() {
            return password;
        }

        /**
         * This method is used to get sales channel.
         *
         * @return - salesChannel
         */
        public String getSalesChannel() {
            return salesChannel;
        }

        /**
         * This method is used to get user.
         *
         * @return - user
         */
        public String getUser() {
            return user;
        }

        /**
         * This method has the implementation to get by sales channel.
         *
         * @param salesChannel - to get by sales channel
         * @return String      - SalesChannel
         */
        public static SalesChannel getBySalesChannel(String salesChannel) {
            for (SalesChannel sc : values()) {
                if (salesChannel.equalsIgnoreCase(sc.salesChannel)) {
                    return sc;
                }
            }
            throw new NoSuchElementException();
        }
    }
}