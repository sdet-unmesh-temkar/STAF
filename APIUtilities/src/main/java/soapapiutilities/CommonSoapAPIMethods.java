package soapapiutilities;

import de.mwvb.base.xml.XMLDocument;
import generalutilities.TestContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.List;

/**
 * This class contain common soap API related methods.
 * This class perform operation such as create,read,update,and delete (or CRUD) operations,respectively.
 */

public class CommonSoapAPIMethods {

    private static final Logger LOG = LoggerFactory.getLogger(CommonSoapAPIMethods.class);
    TestContext<Object> testContext = TestContext.getInstance();
    Document document;

    /**
     * This method is used to return the specified resource of input XML file in the form of InputStream object.
     *
     * @param requestBodyFile - to return the specified resource of input XML file in the form of InputStream object
     * @return - InputStream
     */
    public InputStream setDataInXML(String requestBodyFile) {
        InputStream inputStream;
        inputStream = this.getClass().getResourceAsStream(File.separator + "XMLFiles" + File.separator + requestBodyFile.trim() + ".xml");
        return inputStream;
    }

    /**
     * This method is used to convert SOAP XML to Document
     *
     * @param filePath - File path of the request template. The file should be stored under src/test/resources/
     * @return dcoument - It returns the template file as a document.
     */
    public Document readSOAPXMLFile(String filePath) {
        XMLDocument xml = new XMLDocument(new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + filePath + ".xml"));
        String xmlString = xml.toString();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error("ParserConfigurationException on readSOAPXMLFile method first block: {}", e.getMessage());
        } finally {
            xml.close();
        }

        InputSource is = new InputSource(new StringReader(xmlString));
        if (builder != null) {
            try {
                document = builder.parse(is);
            } catch (SAXException | IOException e) {
                LOG.error("Exception on readSOAPXMLFile method second block: {}", e.getMessage());
            }
        } else {
            LOG.info("Error in XML document");
        }
        LOG.debug("Request Template: {}", document.toString());
        return document;

    }


    /**
     * This method is used to trigger SOAP Request
     *
     * @param document - the request body that has to be triggered
     * @param baseUri  - The baseURI of the API that has to be triggered
     * @param basePath - The base path of the SOAP request
     * @param header   - The header of the SOAP request
     * @param userName - Username of the SOAP request
     * @param password - Password of the SOAP request
     * @return - Returns the response of the triggered SOAP request
     */
    public Response triggerSOAPRequest(Document document, String baseUri, String basePath, String header, String userName, String password) {
        String stringBody = convertDocumentToString(document);
        Response response = RestAssured.given()
                .baseUri(baseUri)
                .basePath(basePath)
                .header("SOAPAction", header)
                .header("Content-Type", "text/xml; charset=utf-8")
                .auth().preemptive().basic(userName, password)
                .body(stringBody)
                .post();
        String printResponse = response.asPrettyString();
        LOG.info("Response: {}", printResponse);
        return response;
    }

    /**
     * This method is used to trigger SOAP Request
     *
     * @param document  - the request body that has to be triggered
     * @param baseUri   - The baseURI of the API that has to be triggered
     * @param basePath  - The base path of the SOAP request
     * @param header    - The header of the SOAP request
     * @param userName  - Username of the SOAP request
     * @param password  - Password of the SOAP request
     * @param proxyHost - Proxy that is required to trigger the SOAP request
     * @return - Returns the response of the triggered SOAP request
     */

    public Response triggerSOAPRequest(Document document, String baseUri, String basePath, String header, String userName, String password, String proxyHost, int proxyPort) {
        String stringBody = convertDocumentToString(document);
        Response response = RestAssured.given()
                .baseUri(baseUri)
                .basePath(basePath)
                .header("SOAPAction", header)
                .header("Content-Type", "text/xml; charset=utf-8")
                .auth().preemptive().basic(userName, password)
                .proxy(proxyHost, proxyPort)
                .body(stringBody)
                .post();
        String printResponse = response.asPrettyString();
        LOG.info("Response: {}", printResponse);
        return response;
    }

    /**
     * This method converts Document to String
     *
     * @param document - The document that needs to be converted to string
     * @return - Its returns string
     */
    public String convertDocumentToString(Document document) {
        try {
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String printBody = writer.toString();
            LOG.info("Body: {}", printBody);
            return writer.toString();
        } catch (TransformerException ex) {
            LOG.error("TransformerException Exception on convertDocumentToString method: {} ", ex.getMessage());
            return null;
        }
    }

    /**
     * The method updates the SOAP template with the value that are passed in the method for the xml xpath
     *
     * @param document - The document that have to be updated with values
     * @param tagName  - The tag which has to be updated with values
     * @param value    - The value that has to be updated for the tag
     * @return - It returns the document updated with value that is passed
     */
    public Document updateSOAPRequestUsingTagName(Document document, String tagName, String value) {
        LOG.info("Xpath to be updated {}", tagName);
        LOG.info("Value to be update {}", value);
        Element idElement = (Element) document.getElementsByTagName(tagName).item(0);
        idElement.setTextContent(value);
        LOG.info("Updated Request: {} ", convertDocumentToString(document));
        return document;

    }

    /**
     * This method is used to update the SOAP request with file path using XML xpath
     *
     * @param filePath The file path of the SOAP xml request template.
     * @param xpath    XML path of the tag which has to be updated
     * @param value    The value that has to be updated
     * @return - It returns the document with updated values
     */
    public Document updateSOAPRequestUsingXpath(String filePath, String xpath, String value) {
        try {
            document = readSOAPXMLFile(filePath);
            document.getDocumentElement().normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = null;
            expression = xPath.compile(xpath);
            Node iNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if (iNode != null) {
                iNode.setTextContent(value);
            }
            LOG.info("Updated Request using Xpath: {}", convertDocumentToString(document));
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException on updateSOAPRequestUsingXpath method: {}", e.getMessage());
        }
        return document;

    }

    /**
     * This method is used to update the SOAP request with file path using list of XML xpath
     *
     * @param filePath - The file path of the SOAP xml request template
     * @param xpath    - List of xpaths that has to be updated
     * @param value    - List of values that has to be updated
     * @return -  It returns the document with updated values
     */
    public Document updateSOAPRequestUsingXpath(String filePath, List<String> xpath, List<String> value) {
        document = readSOAPXMLFile(filePath);
        Document doc = document;
        if (xpath.size() == value.size()) {
            for (int i = 0; i < xpath.size(); i++) {
                doc = updateSOAPRequestUsingXpath(doc, xpath.get(i), value.get(i));
            }
        } else {
            LOG.info("Size of Xpath is not equal to size of value");
        }
        return document;
    }


    /**
     * This method is used to update SOAP request in the document form with values for the XML Xpath
     *
     * @param document - The SOAP request that has to be updated.
     * @param xpath    - Path in the tag in the XML which has to be updated
     * @param value    - The value that has to be updated
     * @return - It returns the document with updated values
     */
    public Document updateSOAPRequestUsingXpath(Document document, String xpath, String value) {
        try {
            document.getDocumentElement().normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = null;
            expression = xPath.compile(xpath);
            Node iNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if (iNode != null) {
                iNode.setTextContent(value);
            }
            LOG.info("Updated Request: {}", convertDocumentToString(document));
        } catch (XPathExpressionException e) {
            LOG.info("No an Xpath expression");
            LOG.error("XPathExpressionException on updateSOAPRequestUsingXpath method: {}", e.getMessage());
        }
        return document;
    }

    /**
     * This method is used to update SOAP request in the document form with values for the XML Xpath
     *
     * @param document - The SOAP request that has to be updated.
     * @param xpath    - List of xpaths that has to be updated
     * @param value    - List of values that has to be updated
     * @return -  It returns the document with updated values
     */
    public Document updateSOAPRequestUsingXpath(Document document, List<String> xpath, List<String> value) {
        Document doc = document;
        if (xpath.size() == value.size()) {
            for (int i = 0; i < xpath.size(); i++) {
                doc = updateSOAPRequestUsingXpath(doc, xpath.get(i), value.get(i));
            }
        } else {
            LOG.info("Size of Xpath is not equal to size of value");
        }
        return document;
    }

    /**
     * This method is used to convert Response to Document
     *
     * @param response - The response that has to be converted to document
     * @return - Returns document
     */
    public Document convertReponsetoDocument(Response response) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response.asPrettyString()));
            Document responseDoc = null;
            responseDoc = db.parse(is);
            LOG.debug("Response is converted to Document{} ", convertDocumentToString(responseDoc));
            return responseDoc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("Exception on convertReponsetoDocument method: {}", e.getMessage());
        }
        return null;
    }


    /**
     * This method is used to retrieve values from the SOAP response
     *
     * @param response      The response from which SOAP object has to be retrieved
     * @param xpath         The Xpath of the value which has to be retrieved from the SOAP response
     * @param testContextKey The key value in which testContext data has to be stored
     */
    public void retrieveSoapObjectRestResponse(Response response, String xpath, String testContextKey) {
        Document responseDoc = null;
        responseDoc = convertReponsetoDocument(response);
        retrieveSoapOjectFromDocument(responseDoc, xpath, testContextKey);
    }


    /**
     * This method is to retrieve values of the SOAP Object from the document
     *
     * @param document      - The document from which value of the SOAP object must be retrieved
     * @param xpath         - The Xpath of the value which has to be retrieved
     * @param testContextKey The key value in which testContext data has to be stored
     * @throws XPathExpressionException - Exception throws when the xpath is not in the right format
     */
    public void retrieveSoapOjectFromDocument(Document document, String xpath, String testContextKey) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expressionXpath = xpath + "/text()";
        XPathExpression expression = null;
        try {
            expression = xPath.compile(expressionXpath);
            String resultValue = (String) expression.evaluate(document, XPathConstants.STRING);
            testContext.setProperty(testContextKey, resultValue);
            LOG.info(xpath, ":", resultValue);
            if (resultValue == null) {
                LOG.info(xpath, " is not present");
            }
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException on retrieveSoapOjectFromDocument method: {}", e.getMessage());

        }

    }

    /**
     * This method is used to the get the response code
     *
     * @param response - The response for which response code has to be fetched
     * @return - Returns the response code
     */
    public int getResponseCode(Response response) {
        return response.getStatusCode();
    }

}