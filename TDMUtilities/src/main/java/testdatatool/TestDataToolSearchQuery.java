package testdatatool;


/**
 * Enum representing different search queries for the Test Data Tool.
 * Each enum constant encapsulates a specific set of search parameters.
 */
public enum TestDataToolSearchQuery {
    /**
     * Search for an active customer.
     */
    ACTIVE_CUSTOMER(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .build()),
    /**
     * Search for an active customer with Delphi ID.
     */
    ACTIVE_CUSTOMER_WITH_DELPHI_ID(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasDelphiId("true")
            .build()),
    /**
     * Search for an active customer with OSF and Delphi IDs.
     */
    ACTIVE_CUSTOMER_WITH_OSF_ID(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .build()),
    /**
     * Search for an active customer with GigaZuhause 250 Cable product.
     */
    CUSTOMER_WITH_GZ250(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("GigaZuhause 250 Cable")
            .build()),
    /**
     * Search for an active customer with GigaZuhause 500 Cable product.
     */
    CUSTOMER_WITH_GZ500(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("GigaZuhause 500 Cable")
            .build()),
    /**
     * Search for an active customer with GigaZuhause 100 Cable product.
     */
    CUSTOMER_WITH_GZ100(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("GigaZuhause 100 Cable")
            .build()),
    /**
     * Search for an active customer with GigaZuhause 1000 Cable product.
     */
    CUSTOMER_WITH_GZ1000(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("GigaZuhause 1000 Cable")
            .build()),
    /**
     * Search for an active customer with Vodafone Station Option product.
     */
    CUSTOMER_WITH_VF_STATION(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("Vodafone Station Option")
            .build()),
    /**
     * Search for an active customer with HomeBox FRITZ!Box 6670 product.
     */
    CUSTOMER_WITH_FRITZ6670(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("HomeBox FRITZ!Box 6670")
            .build()),
    /**
     * Search for an active customer with HomeBox FRITZ!Box 6690 product.
     */
    CUSTOMER_WITH_FRITZ6690(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemName("HomeBox FRITZ!Box 6690")
            .build()),
    /**
     * Search for an active customer with MAC address.
     */
    CUSTOMER_WITH_MAC_ADDRESS(TestToolData.builder()
            .customerStatus("Active")//NOSONAR
            .orderHasOsfId("true")
            .orderHasDelphiId("true")
            .orderItemHasMacAddress("true")
            .build());

    /**
     * The search parameters for this query.
     */
    private final TestToolData testToolData;

    /**
     * Constructs a TestDataToolSearchQuery enum constant with the given search parameters.
     * @param testToolData The search parameters for the query.
     */
    TestDataToolSearchQuery(TestToolData testToolData) {
        this.testToolData = testToolData;
    }

    /**
     * Returns the search parameters associated with this query.
     * @return TestToolData object containing search parameters.
     */
    public TestToolData getTestToolData() {
        return testToolData;
    }
}
