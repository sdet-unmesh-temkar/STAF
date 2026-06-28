package generalutilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * This class contain a method related to custom soft assert such as onAssertSuccess and onAssertFailure .
 */
public class CustomSoftAssert extends SoftAssert {
    private static final Logger LOG = LoggerFactory.getLogger(CustomSoftAssert.class);

    /**
     * This method is used to log info into report on success.
     *
     * @param assertCommand - IAssert to log info into report on success
     */
    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        LOG.info("Assert:PASS\n {} \n {} \n {}", assertCommand.getMessage(), assertCommand.getExpected(), assertCommand.getActual());
    }

    /**
     * This method is used to logs error in report on assert failure.
     *
     * @param assertCommand - IAssert to logs error in report on assert failure
     * @param ex            - AssertionError to logs error in report on assert failure
     */
    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        LOG.error("Assert:FAIL\n {} \n {} \n {}", assertCommand.getMessage(), assertCommand.getExpected(), assertCommand.getActual());
    }

    /**
     * This method used to logs expected and actual status code in the report.
     *
     * @param expected - list of expected value of status code to logs expected and actual status code in the report
     * @param actual   - actual value of the status code to logs expected and actual status code in the report
     */
    public void assertStatusCode(List<Integer> expected, String actual) {
        super.assertTrue(expected.contains(Integer.parseInt(actual)), "Response code: Expected: " + expected.toString() + " Actual: " + actual);
        super.assertAll();
    }
}
