package stepDefinitions;


import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.w3c.dom.Document;
import soapapiutilities.CommonSoapAPIMethods;

/**
 * This class contains methods that are related to SOAP API.
 * It also contains method such as trigger SOAP request, retrieve value from SOAP response , update the SOAP request template.
 */
public class CommonSOAPAPISteps {

    CommonSoapAPIMethods commonSoapAPIMethods = new CommonSoapAPIMethods();



    /**
     * This method is used to trigger SOAP Request
     *
     * @param filePath File path of the request template which is to be triggered . The file should be stored under src/test/resources/
     * @param baseUri  The baseURI of the API that has to be triggered
     * @param basePath The base path of the SOAP request
     * @param header   The header of the SOAP request
     * @param userName Username of the SOAP request
     * @param password Password of the SOAP request
     */

    @When("We send a SOAP Request with the XmlRequestFile {string}, basepath {string}, header {string}, VaultPath username {string}, VaultPath password {string}")
    public void triggerSoapRequest(String filePath, String baseUri, String basePath, String header, String userName, String password) {
        Document document = commonSoapAPIMethods.readSOAPXMLFile(filePath);
        commonSoapAPIMethods.triggerSOAPRequest(document, baseUri, basePath, header, userName, password);

    }

    /**
     * This method is used to update the SOAP request with file path using XML xpath
     *
     * @param filePath The file path of the SOAP xml request template.
     * @param xpath    XML path of the tag which has to be updated
     * @param value    The value that has to be updated
     */
    @When("We update the SOAP Request for the XmlRequestFile {string} at the XMLXpath {string} and with the value {string}")
    public void updateSoapRequest(String filePath, String xpath, String value) {
        commonSoapAPIMethods.updateSOAPRequestUsingXpath(filePath, xpath, value);
    }

    /**
     * This method is used to retrieve values from the SOAP response
     *
     * @param response      The response from which SOAP object has to be retrieved
     * @param xpath         The Xpath of the value which has to be retrieved from the SOAP response
     * @param testContextKey The key value in which testContext data has to be stored
     */
    @When("We retrieve the value from the SOAP Response {string} at the XMLXpath {string} and store it into the test context key {string}")
    public void retrieveValueSoapResponse(Response response, String xpath, String testContextKey) {
        commonSoapAPIMethods.retrieveSoapObjectRestResponse(response, xpath, testContextKey);
    }

}
