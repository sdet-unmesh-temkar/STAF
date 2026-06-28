package testdatatool;

/**
 * Represents search parameters for customer and order data used in Test Data Tool queries.
 * Provides explicit getters for each parameter and a manual builder for flexible object construction.
 * <p>
 * Example usage:
 * <pre>
 *     TestToolData data = TestToolData.builder()
 *         .customerStatus("Active")
 *         .orderHasOsfId("true")
 *         .build();
 * </pre>
 * </p>
 */
public class TestToolData {
    /**
     * Customer status (e.g., "Active").
     */
    private String customerStatus;
    /**
     * Whether the customer has a valid email.
     */
    private String customerHasValidEmail;
    /**
     * Whether the customer has a migration process.
     */
    private String customerHasMigrationProcess;
    /**
     * Whether the customer has a MyVodafone account.
     */
    private String customerHasMyVodafoneAccount;
    /**
     * Whether the order has an OSF ID.
     */
    private String orderHasOsfId;
    /**
     * Whether the order has a Delphi ID.
     */
    private String orderHasDelphiId;
    /**
     * Name of the order item (e.g., product name).
     */
    private String orderItemName;
    /**
     * Whether the order item has a MAC address.
     */
    private String orderItemHasMacAddress;

    /**
     * Returns the customer status.
     * @return Customer status string.
     */
    public String getCustomerStatus() {
        return customerStatus;
    }

    /**
     * Returns whether the customer has a valid email.
     * @return Valid email flag as string.
     */
    public String getCustomerHasValidEmail() {
        return customerHasValidEmail;
    }

    /**
     * Returns whether the customer has a migration process.
     * @return Migration process flag as string.
     */
    public String getCustomerHasMigrationProcess() {
        return customerHasMigrationProcess;
    }

    /**
     * Returns whether the customer has a MyVodafone account.
     * @return MyVodafone account flag as string.
     */
    public String getCustomerHasMyVodafoneAccount() {
        return customerHasMyVodafoneAccount;
    }

    /**
     * Returns whether the order has an OSF ID.
     * @return OSF ID flag as string.
     */
    public String getOrderHasOsfId() {
        return orderHasOsfId;
    }

    /**
     * Returns whether the order has a Delphi ID.
     * @return Delphi ID flag as string.
     */
    public String getOrderHasDelphiId() {
        return orderHasDelphiId;
    }

    /**
     * Returns the name of the order item.
     * @return Order item name string.
     */
    public String getOrderItemName() {
        return orderItemName;
    }

    /**
     * Returns whether the order item has a MAC address.
     * @return MAC address flag as string.
     */
    public String getOrderItemHasMacAddress() {
        return orderItemHasMacAddress;
    }

    /**
     * Returns a new Builder instance for constructing TestToolData objects.
     * @return Builder for TestToolData.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Manual builder for TestToolData, allowing flexible configuration of search parameters.
     */
    public static class Builder {
        private String customerStatus;
        private String customerHasValidEmail;
        private String customerHasMigrationProcess;
        private String customerHasMyVodafoneAccount;
        private String orderHasOsfId;
        private String orderHasDelphiId;
        private String orderItemName;
        private String orderItemHasMacAddress;

        /**
         * Sets the customer status.
         * @param customerStatus Customer status string.
         * @return Builder instance.
         */
        public Builder customerStatus(String customerStatus) {
            this.customerStatus = customerStatus;
            return this;
        }

        /**
         * Sets whether the customer has a valid email.
         * @param customerHasValidEmail Valid email flag as string.
         * @return Builder instance.
         */
        public Builder customerHasValidEmail(String customerHasValidEmail) {
            this.customerHasValidEmail = customerHasValidEmail;
            return this;
        }

        /**
         * Sets whether the customer has a migration process.
         * @param customerHasMigrationProcess Migration process flag as string.
         * @return Builder instance.
         */
        public Builder customerHasMigrationProcess(String customerHasMigrationProcess) {
            this.customerHasMigrationProcess = customerHasMigrationProcess;
            return this;
        }

        /**
         * Sets whether the customer has a MyVodafone account.
         * @param customerHasMyVodafoneAccount MyVodafone account flag as string.
         * @return Builder instance.
         */
        public Builder customerHasMyVodafoneAccount(String customerHasMyVodafoneAccount) {
            this.customerHasMyVodafoneAccount = customerHasMyVodafoneAccount;
            return this;
        }

        /**
         * Sets whether the order has an OSF ID.
         * @param orderHasOsfId OSF ID flag as string.
         * @return Builder instance.
         */
        public Builder orderHasOsfId(String orderHasOsfId) {
            this.orderHasOsfId = orderHasOsfId;
            return this;
        }

        /**
         * Sets whether the order has a Delphi ID.
         * @param orderHasDelphiId Delphi ID flag as string.
         * @return Builder instance.
         */
        public Builder orderHasDelphiId(String orderHasDelphiId) {
            this.orderHasDelphiId = orderHasDelphiId;
            return this;
        }

        /**
         * Sets the name of the order item.
         * @param orderItemName Order item name string.
         * @return Builder instance.
         */
        public Builder orderItemName(String orderItemName) {
            this.orderItemName = orderItemName;
            return this;
        }

        /**
         * Sets whether the order item has a MAC address.
         * @param orderItemHasMacAddress MAC address flag as string.
         * @return Builder instance.
         */
        public Builder orderItemHasMacAddress(String orderItemHasMacAddress) {
            this.orderItemHasMacAddress = orderItemHasMacAddress;
            return this;
        }

        /**
         * Builds and returns a TestToolData instance with the configured parameters.
         * @return TestToolData instance.
         */
        public TestToolData build() {
            TestToolData data = new TestToolData();
            data.customerStatus = this.customerStatus;
            data.customerHasValidEmail = this.customerHasValidEmail;
            data.customerHasMigrationProcess = this.customerHasMigrationProcess;
            data.customerHasMyVodafoneAccount = this.customerHasMyVodafoneAccount;
            data.orderHasOsfId = this.orderHasOsfId;
            data.orderHasDelphiId = this.orderHasDelphiId;
            data.orderItemName = this.orderItemName;
            data.orderItemHasMacAddress = this.orderItemHasMacAddress;
            return data;
        }
    }
}
