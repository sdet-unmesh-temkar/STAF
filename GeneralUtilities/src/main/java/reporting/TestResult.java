package reporting;

import io.cucumber.plugin.event.Status;

/**
 * Encapsulates the outcome of a single test case execution.
 * Stores the JIRA tag used to track the test, the human-readable test name, its execution status, any error or skip reason (truncated if necessary),
 * and the run identifier for interframework scenarios.
 */

public class TestResult {
    private final String jiraTag;
    private final String name;
    private final Status status;
    private final String reason;
    private final String runId;

    /**
     * Creates a new TestResult.
     *
     * @param jiraTag the JIRA identifier associated with this test case
     *                (for example, "@SOL-123" or "@InterframeworkJiraKey:")
     * @param name    the human-readable name of the test case
     * @param status  the execution status (PASSED, FAILED, or SKIPPED)
     * @param reason  the error message or pass/skip reason; truncated to 250 characters
     * @param runId   the interframework run ID, or an empty string for normal tests
     */
    public TestResult(String jiraTag, String name, Status status, String reason, String runId) {
        this.jiraTag = jiraTag;
        this.name    = name;
        this.status  = status;
        this.reason  = reason;
        this.runId   = runId;
    }

    /**
     * Returns the JIRA tag used to track this test case.
     *
     * @return the JIRA identifier string
     */
    public String getJiraTag() {
        return jiraTag;
    }

    /**
     * Returns the descriptive name of the test case.
     *
     * @return the test case name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the execution status of the test case.
     *
     * @return a {@link Status} enum value (PASSED, FAILED, or SKIPPED)
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the reason for failure, skip, or success note.
     *
     * @return a string describing why the test failed or was skipped,
     *         or a pass confirmation message
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the run identifier for interframework scenarios.
     *
     * @return the interframework run ID, or an empty string if not applicable
     */
    public String getRunId() {
        return runId;
    }

}
