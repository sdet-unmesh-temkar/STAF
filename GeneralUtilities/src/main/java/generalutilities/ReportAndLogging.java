package generalutilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consist of method related to reporting and logging.
 * This class contains a method such as get/set scenario,log step in jira,log assert value etc.
 */
public class ReportAndLogging {

    private static final Logger LOG = LoggerFactory.getLogger(ReportAndLogging.class);
    private static final ThreadLocal<Scenario> scenario = new ThreadLocal<>();
    private static final ThreadLocal<Properties> prop= new ThreadLocal<>();
    public ReportAndLogging() {
        ThreadLocalRegistry.register(scenario);
        ThreadLocalRegistry.register(prop);
    }
    FileSpecificUtilities fileSpecificUtilities = new FileSpecificUtilities();

    private static final String FLAGFILE = "extentReportFlag.properties";
    private static final String SKIPLOGSTEPINJIRA = "skipLogStepInJira";
    private static final String FALSEFLAGVALUE = "false";
    private static final String ONLYSTEPREPORT = "onlyStepReport";
    private static final String REQUEST = "Request";
    private static final String REQUESTPREFIX = "<div class=\"container\"><details><summary style=\"color:RebeccaPurple;\"><b>";
    private static final String NONREQUESTPREFIX = "<div class=\"container\"><details><summary style=\"color:darkcyan;\"><b>";
    private static final String SUFFIX = "</div></details></div><hr style=\"margin-bottom: 7px; margin-top: 9px;\">";
    private static final String MIDFIX = ":</b></summary>" + "\n" +"<div>";
    private static final String EXCEPTION = "Exception {}";
    private static final String TARGET = "target";
    private static final String EXTENT_REPORT = "ExtentReport";
    private static final String INTEGRATED_REPORT = "IntegratedReport";
    private static final String EXTENT_REPORT_HTML = "ExtentReport.html";
    private static final String DEBUG = "DEBUG";




    /**
     * This method is used to get scenario name.
     *
     * @return  - Scenario
     */
    public static Scenario getScenario() {
        return scenario.get();
    }

    /**
     * This method is used to cleanup the thread local instance.
     */
    public void unload() {
        scenario.remove();
        prop.remove();
    }
    /**
     * This method is used to set scenario name.
     *
     * @param gScenario - to set scenario name
     */
    public static void setScenario(Scenario gScenario) {
        scenario.set(gScenario);
    }

    /**
     * This method is used to log additional step in xray execution.
     *
     * @param value - to log additional step in xray execution
     */
    public void logStepInJira(String value) {
        String flag = "";
        try{
            flag = System.getProperty(SKIPLOGSTEPINJIRA);
            boolean fileCheck =checkFlagPropertyFileExist();
            if(flag == null && fileCheck){
                prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
                 flag = prop.get().getProperty(SKIPLOGSTEPINJIRA);
            }
            if(flag == null){flag = FALSEFLAGVALUE;}

            if (flag.equalsIgnoreCase("true") || isSkipLog()) {
                LOG.info("Steps will not log in Jira");
            } else {
                scenario.get().attach(value.getBytes(), "text/plain", "Log for X-Ray");
            }
        }catch(Exception ex){
            LOG.error(EXCEPTION, ex.getMessage());
        }
    }

    /**
     * This method is used to determine whether logging steps in Jira should be skipped based on the SKIPLOGSTEPINJIRA system property.
     * It inspects the current thread's stack trace and checks if any class resource path contains a module name specified in the property.
     */
    private boolean isSkipLog() {//NOSONAR
        String skipProp = System.getProperty(SKIPLOGSTEPINJIRA, "").toLowerCase();
        if (skipProp.isEmpty()) {
            return false;
        }
        boolean isSkipped = false;
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : st) {
            String className = e.getClassName();
            if (!className.isEmpty()) {
                String path = "";
                try {
                    Class<?> caller = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
                    java.net.URL resource = caller.getResource(caller.getSimpleName() + ".class");
                    if (resource != null) {
                        path = resource.toString().toLowerCase();
                    } else {
                        continue;
                    }
                } catch (ClassNotFoundException ex) {
                    LOG.error(ex.getMessage());
                }
                if (System.getProperty(SKIPLOGSTEPINJIRA) != null) {
                    String[] modules = System.getProperty(SKIPLOGSTEPINJIRA).split(",");
                    for (String module : modules) {
                        module = module.trim().toLowerCase();
                        if (!path.isEmpty() && path.toLowerCase().contains(module)) {
                            isSkipped = true;
                            break;
                        }
                    }
                }
            }
        }
        return isSkipped;
    }

    /**
     * This method is used to log additional step in xray execution.
     *
     * @param value - to log additional step in xray execution
     * @param type  - to log step in jira
     */
    public void logStepInJira(String value, String type) {
        var flag = "";
        try{
            flag = System.getProperty(SKIPLOGSTEPINJIRA);
            boolean fileCheck =checkFlagPropertyFileExist();
            if(flag == null && fileCheck){
                prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
                flag = prop.get().getProperty(SKIPLOGSTEPINJIRA);
            }
            if(flag == null){flag = FALSEFLAGVALUE;}

            if(flag.equals(FALSEFLAGVALUE)){
                scenario.get().attach(value.getBytes(), type, "Log for X-Ray");
            }else {
                LOG.info("Steps will not log in Jira as Flag set to false");
            }
        }catch(Exception ex){
            LOG.error(EXCEPTION, ex.getMessage());
        }
    }

    /**
     * This method is used to log additional step in xray execution.
     *
     * @param value - value to log additional steps into Jira
     * @param type  - to log additional step in xray execution
     * @param msg   - string which needs to be printed
     */
    public void logStepInJira(String value, String type, String msg) {
        var flag = "";
        try{
            flag = System.getProperty(SKIPLOGSTEPINJIRA);
            boolean fileCheck =checkFlagPropertyFileExist();
            if(flag == null && fileCheck){
                prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
                flag = prop.get().getProperty(SKIPLOGSTEPINJIRA);
            }
            if(flag == null){flag = FALSEFLAGVALUE;}

            if(flag.equals(FALSEFLAGVALUE)){
                scenario.get().attach(value.getBytes(), type, msg);
            }else {
                LOG.info("Steps will not log in Jira as Flag set to false");
            }
        }catch(Exception ex){
            LOG.error(EXCEPTION, ex.getMessage());
        }
    }

    /**
     * This method is used to log request/response body in HTML report.
     *
     * @param body         - to log type of body ex. Request body/Response body
     * @param bodyName     - to log type of body name ex.Request body/Response body
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public void logBodyInTxtArea(String bodyName, String body) throws IOException {
        var mapper = new ObjectMapper();
        var alignedBody = "";
        var htmlPrefix = "<textarea style='background-color:LightCyan; overflow-y: auto; height: 80px;  ' disabled>";
        var htmlSuffix = "</textarea>";
        if (!body.isEmpty()) {
            try {
                var jsonObject = mapper.readValue(body, Object.class);
                alignedBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                String html = htmlPrefix + alignedBody + htmlSuffix;
                if(bodyName.contains(REQUEST)){
                    addStepToReport(REQUESTPREFIX + bodyName + MIDFIX + "\n" +"<div>"+ html+SUFFIX);
                }else{
                    addStepToReport(NONREQUESTPREFIX + bodyName + MIDFIX + html+SUFFIX);
                }

            } catch (JsonProcessingException exc) {
                String html = htmlPrefix + body + htmlSuffix;
                if(bodyName.contains(REQUEST)){
                    addStepToReport(REQUESTPREFIX + bodyName + MIDFIX + html+SUFFIX);
                }else{
                    addStepToReport(NONREQUESTPREFIX + bodyName + MIDFIX + html+SUFFIX);
                }
            }
        }
    }

    /**
     * This method is used to log header name and it's value in table.
     *
     * @param headerName - string specifies type of header to log header name and it's value in table
     * @param headers    - to log header
     */
    public void logHeaderInTable(String headerName, Map<String, String> headers) {
        var html = new StringBuilder("<table class=\"table-sm m-1 table-bordered\"><tr><th>Name</th><th>Value</th></tr>");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }

        html.append("</table>");
        if(headerName.contains(REQUEST)){
            addStepToReport(REQUESTPREFIX + headerName + MIDFIX + html + SUFFIX);
        }else{
            addStepToReport(NONREQUESTPREFIX + headerName + MIDFIX + html + SUFFIX);
        }
    }

    /**
     * This method is used to log assert values in extent report.
     *
     * @param assertName    - to log assert values in extent report
     * @param expectedValue - expected value to log assert values in extent report
     * @param actualValue   - string which specifies actual value of assert
     * @param jsonPath      - path of the json file to log assert values in extent report
     * @param assertMessage - string which prints assert message
     */
    public void logAssertValues(String assertName, String expectedValue, String actualValue, String jsonPath, String assertMessage) {
        var body = "";

        if (assertName.equals("assertNotNull") || assertName.equals("assertNull") || assertName.equals("assertExist") || assertName.equals("assertNotExist")) {

            body = "JSONPath:" + jsonPath + "\nAssertion type: " + assertName + "\nExpected Value: " + expectedValue + " \nActual Value:" + actualValue + "\n" + assertMessage;
        } else {
            body = "Assertion type: " + assertName + "\nExpected Value: " + expectedValue + " \nActual Value:" + actualValue;
        }

        String html = "<textarea style='background-color:white; overflow-y: auto; height: 65px;  ' disabled>" + body + "</textarea>";
        addStepToReport("<b>Assertion logs:</b>" + "\n" + html);
    }

    /**
     * Formats log messages with special styling for URLs and Response Bodies before adding them to extent report
     *
     * @param logMessage The log message to format.
     * @return The formatted log message.
     */
    public String formatLogMessage(String logMessage) {
        if (logMessage.contains("URL :")) {
            // Add right side arrow for URLs
            return "<b>&#x2192</b> " + logMessage;
        }
        else if (logMessage.contains("Response Body: {")) {
            // Collapsible response body block
            return "<hr style=\"margin-bottom: 7px; margin-top: 9px;\"><div class=\"container\">" +
                    "<details><summary><b>Response Body" + ":</b></summary>" + "\n" +"<div>" + logMessage + SUFFIX;
        }
        else {
            // Default formatting for other log messages
            return logMessage;
        }
    }

    /**
     * Logs the message into the extent report based on the specified log level.
     * Logs the message only if the provided log level is at or above the configured report detail level.
     *
     * @param reportLog    The log message to be added.
     * @param reportLogLvl The log level of the message (e.g., "DEBUG", "INFO", "WARN").
     */
    public void addStepToReport(String reportLog, String reportLogLvl) {
        // Retrieve the configured report detail level from system properties, defaulting to "DEBUG"
        String reportDetailLevel = System.getProperty("reportDetailLevel", DEBUG);

        // Define a hierarchy of log levels where DEBUG > INFO > WARN
        List<String> levels = Arrays.asList("WARN", "INFO", DEBUG);

        // Determine the index of the configured log level and the log level being passed
        int configuredIndex = levels.indexOf(reportDetailLevel.toUpperCase());
        int logIndex = levels.indexOf(reportLogLvl.toUpperCase());

        // Only log messages that are at or above the configured report detail level in hierarchy
        if (logIndex <= configuredIndex) {
            ExtentCucumberAdapter.addTestStepLog(formatLogMessage(reportLog));
        }
    }

    /**
     * Adds a log to the extent report at the default "DEBUG" level.
     *
     * @param reportLog The log message to be added.
     */
    public void addStepToReport(String reportLog) {
        addStepToReport(reportLog, DEBUG);
    }

    /**
     * This method is used to add screenshot to the reports.
     *
     * @param fileName     - name of the file to add screenshot to the reports
     * @throws IOException - an exception occur when user inputs improper data into the program
     */
    public void addScreenshotToReport(String fileName) throws IOException {
        ExtentCucumberAdapter.addTestStepScreenCaptureFromPath(fileName);
    }

    /**
     * The copy reports API is used to copy one or more reports from one database to another within the same account or even across user accounts.
     */
    public void copyReport() {

        var cwd = System.getProperty("user.dir");
        var reportPath = new File(cwd + File.separator + TARGET + File.separator + EXTENT_REPORT + File.separator);
        List<File> pathList = new ArrayList<>(Arrays.asList(reportPath.listFiles()));
        TreeMap<Date, String> dateList = new TreeMap<>();

        for (File list : pathList) {
            var folderName = Paths.get(list.toString());
            var timeStamp = "";
            timeStamp = folderName.toString().substring(folderName.toString().lastIndexOf(File.separator) + 1);
            try {
                dateList.put(new SimpleDateFormat("dd-MM-yyyy HH-mm-ss.SSS").parse(timeStamp), timeStamp);
            } catch (ParseException pex) {
                LOG.info("The date format is incorrect. Please correct it in the extent.properties file from your repository!");
            }
        }
        LOG.trace("Report date: {}", dateList);

        if (!dateList.isEmpty()) {
            String finalReportFolder = dateList.get(dateList.lastKey());
            var oldFileName = Paths.get(cwd + File.separator + TARGET + File.separator + EXTENT_REPORT + File.separator + finalReportFolder);
            var newFileName = Paths.get(cwd +  File.separator + TARGET + File.separator + EXTENT_REPORT + File.separator + INTEGRATED_REPORT + File.separator);

            try {
                    var flag = System.getProperty(ONLYSTEPREPORT);
                    var flag1 = System.getProperty("skipExpandCollapse");
                    boolean fileCheck =checkFlagPropertyFileExist();
                    if(flag==null && flag1==null && fileCheck){
                        prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
                        flag = prop.get().getProperty(ONLYSTEPREPORT);
                        flag1 = prop.get().getProperty("skipExpandCollapse");
                    }

                    if(flag == null){ flag = FALSEFLAGVALUE;}
                    if(flag1 == null){flag1 = FALSEFLAGVALUE;}

                    if(flag.equals("true")){
                        LOG.info("Only Steps names will be printed on html");
                    }else if(flag1.equals("true")){
                        LOG.info("Expand and Collapse feature will not be available as flag set to true");
                    }else {
                        updateJQueryInHTML(oldFileName);
                    }

                FileUtils.copyDirectory(oldFileName.toFile(), newFileName.toFile());
                //Create zipped Integrated ExtentReport if report is more than 10 MB
                File extentReport = new File(cwd + File.separator + TARGET + File.separator + EXTENT_REPORT + File.separator + INTEGRATED_REPORT + File.separator + EXTENT_REPORT_HTML);
                if (extentReport.length() > 10000000) {
                    LOG.info("ZIP is generated as report is more than 10MB");
                    ZipUnzip.zipFile(extentReport, cwd + File.separator + TARGET + File.separator + EXTENT_REPORT + File.separator + INTEGRATED_REPORT + File.separator, "ExtentReportZip");
                }

            } catch (IOException ioe) {
                LOG.error("Unable to create Integrated report ");
                LOG.error(EXCEPTION, ioe.getMessage());
            }
        } else {
            LOG.info("Report is not generated");
        }
    }

    /**
     * This method is used to update tags in HTML reports.
     *
     * @param  filePath    - path of the file to update tags in html reports
     * @throws IOException - an exception occur when user inputs improper data into the program     *
     */
    public void updateJQueryInHTML(Path filePath) throws IOException {

        var classStep = "<div class=\"step";
        StringBuilder contentBuilder = new StringBuilder();

        try (FileReader fileReader = new FileReader(filePath + File.separator + EXTENT_REPORT_HTML); BufferedReader in = new BufferedReader(fileReader)) {
            String str;
            while ((str = in.readLine()) != null) {
                if (str.contains(classStep) && !(str.contains("<div class=\"step fail"))) {
                    str = str.replace(classStep, "</details></div>\n" + classStep);
                }

                if (str.contains("<span>Given") || str.contains("<span>When") || str.contains("<span>Then") || str.contains("<span>And") || str.contains("<span>But") || str.contains("<span>*")) {
                    str = str.replace(str, "<b>" + str + "</b>");
                    contentBuilder.append(str);
                    contentBuilder.append("\n");
                    contentBuilder.append("<div class=\"container\"><details><summary style=\"color:blue;\">" + "Click for More step details" + ":</summary>");
                } else {
                    contentBuilder.append(str);
                    contentBuilder.append("\n");
                }
            }
        }catch (IOException e) {
            LOG.error(EXCEPTION, e.getMessage());
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(filePath + File.separator + EXTENT_REPORT_HTML);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            BufferedWriter htmlWriter =  new BufferedWriter(outputStreamWriter))   {
            String content = String.valueOf(contentBuilder);
            htmlWriter.write(content);

        }catch(Exception e){
            LOG.error(EXCEPTION, e.getMessage());
        }

    }

    /**
     * This method is used to get the flags of file exist for only failed step report.
     *
     * @return - the flags of file exist for only failed step report
     */
    public String getTheFlag() {
        var fileExist = true;
        var flag = "";
        try {
            flag = System.getProperty("onlyFailedStepReport");
            if (flag == null) {
                prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
                flag = prop.get().getProperty("onlyFailedStepReport");
            }
            }catch(Exception e){
                fileExist = false;
            }
            return fileExist + ":" + flag;
    }

    /**
     * This method is used to check whether flag property file exist or not.
     *
     * @return - boolean (true/false)
     */
    public boolean checkFlagPropertyFileExist(){
        try{
            prop.set(fileSpecificUtilities.readPropertyFile(FLAGFILE));
        }catch(Exception e){
            return false;
        }
        return true;
    }

}
