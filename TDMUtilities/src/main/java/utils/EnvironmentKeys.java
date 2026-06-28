package utils;

public class EnvironmentKeys {
    EnvironmentKeys() {
    } // private constructor to prevent instantiation

    public static final String XB3_TRACE_ID = "IntegrationTests/XB3TraceId";
    public static final String X_API_KEY = "IntegrationTests/xAPIKey";
    public static final String X_AD_AUTHORIZATION = "IntegrationTests/xADAuthorization";

    public static class D1 {

        private D1() {
        }

        public static final String D1_API_HOST = "IntegrationTests/D1ApiHost";
        public static final String D1_API_TOKEN_HOST = "IntegrationTests/D1ApiKeycloakURL";
        public static final String D1_USER_NAME = "IntegrationTests/D1UserName";
        public static final String D1_PASSWORD = "IntegrationTests/D1Password";
        public static final String TEST_DATA_TOOL_HOST = "IntegrationTests/TestDataToolHost";
    }

    public static class CDH {
        private CDH() {
        }

        public static final String CDH_HOST = "IntegrationTests/CdhHost";
        public static final String CDH_TOKEN_PATH = "IntegrationTests/cdhApiToken";
    }

    public static class DXL {
        private DXL() {
        }

        public static final String DXL_HOST = "DXL/DxlHost";
        public static final String DXL_TOKEN_PATH = "DXL/dxl_Token";
    }

    public static class MRT {
        private MRT() {
        }

        public static final String MRT_HOST = "IntegrationTests/MrtHost";
        public static final String MRT_TOKEN_PATH = "Routing/MRT";

    }

    public static class MINT {
        private MINT() {
        }

        public static final String MINT_HOST = "MINT/MintHost";
        public static final String MINT_PARTNER_AUTH = "MINT/AUTH";
        public static final String MINT_FUNCTIONAL_EMAIL = "MINT/VerificationEmail";
        public static final String MINT_PASSWORD = "MINT/MintPassword";
        public static final String MINT_REGISTRATION_URL = "MINT/MintRegistrationURL";
    }

    public static class SOM {
        private SOM() {
        }

        public static final String SOM_API_HOST = "SOM/SomHost";
        public static final String SOM_USERNAME = "SOM/OHUserName";
        public static final String SOM_PASSWORD = "SOM/OHPassword";
    }

    public static class SOI {
        private SOI() {
        }

        public static final String SOI_TOKEN_API_HOST = "SOI/SoiTokenHost";
        public static final String SOI_API_HOST = "SOI/SoiHost";
        public static final String SOI_HEADER_TOKEN= "SOI/HeaderToken";
        public static final String SOI_ENRICH_API_HOST = "SOI/SoiEnrichHost";
    }

    public static class Apigee {
        private Apigee() {
        }

        public static final String APIGEE_HOST = "IntegrationTests/ApigeeHost";
        public static final String APIGEE_CLIENT_ID = "IntegrationTests/ApigeeClientId";
        public static final String APIGEE_CLIENT_SECRET = "IntegrationTests/ApigeeClientSecret";
    }

}
