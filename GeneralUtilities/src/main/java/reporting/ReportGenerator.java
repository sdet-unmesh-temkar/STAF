package reporting;

import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import io.cucumber.plugin.event.Status;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the creation of HTML execution reports and charts for normal and interframework test runs.
 * Generates summary.html, pieChart.jpeg, testCaseDetails.html, interframeworkSummary.html, interframeworkPieChart.jpeg, and
 * interframeworkTestCaseDetails.html under {@code target/reportFiles}.
 */

public class ReportGenerator {
    private static final String OUTPUT_DIR = "target/reportFiles";
    // CSS class constants for table formatting
    private static final String CLASS_STATUS_CELL = "status-cell";
    private static final String CLASS_TOTAL_RAW = "total-row";
    // File paths
    private static final String SUMMARY_CSS_PATH = "/css/summary-report.css";
    private static final String TEST_CASE_CSS_PATH = "/css/test-case-details.css";
    private static final String REPORT_OUTPUT_DIR = "target/reportFiles/";
    private static final Logger LOG = LoggerFactory.getLogger(ReportGenerator.class);

    // Private constructor to prevent instantiation
    private ReportGenerator() {
    }

    /**
     * Drives report generation for both normal and interframework test results.
     *
     * @param normalResults         list of standard {@link TestResult} entries
     * @param interResults          list of interframework {@link TestResult} entries
     * @param additionalInfoExists  {@code true} if additionalInfo.txt is present
     * @param additionalInfoMap     mapping of test names to additional info values
     */
    public static void generateFiles(List<TestResult> normalResults, List<TestResult> interResults, boolean additionalInfoExists, Map<String, String> additionalInfoMap) {
        ensureOutputDir();
        // Normal tests
        if (!normalResults.isEmpty()) {
            // summary html file
            generateSummaryReport(normalResults,"summary.html","Execution Summary");
            // pie chart
            generatePieChart(normalResults);
            // normal testcase details html file
            produceTestcaseDetails("testCaseDetails.html", "Execution Details", normalResults,false, additionalInfoExists, additionalInfoMap);
        }
        // Interframework tests
        if (!interResults.isEmpty()) {
            // summary html file
            generateSummaryReport(interResults,"interframeworkSummary.html","Interframework Initiate flow Execution Summary");
            // pie chart
            generatePieChart(interResults);
            // interframework testcase details file, include Run ID column
            produceTestcaseDetails("interframeworkTestCaseDetails.html", "Interframework Initiate flow Execution Details", interResults,true, additionalInfoExists, additionalInfoMap);
        }
    }

    /**
     * Ensures that the output directory exists, creating it if necessary.
     *
     * @throws IllegalStateException if the directory cannot be created
     */
    private static void ensureOutputDir() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Could not create report output directory: " + OUTPUT_DIR);
        }
        else {
            LOG.debug(OUTPUT_DIR+" -- directory is already exists");
        }
    }

    /**
     * Counts how many {@link TestResult} entries match the given status.
     *
     * @param list list of {@link TestResult}
     * @param s    status to filter by
     * @return the count as a float
     */
    @SuppressWarnings("java:S1905") // Intentional primitive cast from long to float
    private static float countByStatus(List<TestResult> list, Status s) {
        return (float) list.stream().filter(r -> r.getStatus() == s).count();
    }

//====================================================================================================================================================

    /**
     * Generates the summary HTML report.
     */
    public static void generateSummaryReport(List<TestResult> testData, String fileName,String title) {
        String summaryHtml = createSummaryHtml(testData,title);
        writeHtmlToFile(summaryHtml, fileName);
    }

    /**
     * Writes HTML content to a file in the reports directory.
     */
    private static void writeHtmlToFile(String html, String fileName) {
        File outputFile = new File(REPORT_OUTPUT_DIR + fileName);
        try {
            FileUtils.writeStringToFile(outputFile, html, Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("IOException writing file {}: {}", fileName, e.getMessage());
        }
    }

    /**
     * Creates the summary HTML using HtmlBuilder.
     */
    private static String createSummaryHtml(List<TestResult> testData,String title) {
        String css = loadCssFromFile(SUMMARY_CSS_PATH);
        HtmlBuilder builder = new HtmlBuilder()
                .doctype()
                .html("en")
                .head()
                .title(title)
                .metaCharset("UTF-8")
                .meta("viewport", "width=device-width, initial-scale=1.0")
                .style(css)
                .closeTag() // close head
                .body()
                .h3(title + ":");
        addSummaryTable(builder, testData);
        builder.p("Note: Percentage values are rounded to two decimal places.").closeAll();
        return formatHtmlOutput(builder.toString());
    }

    /**
     * Utility method to load CSS content from a file in the resources directory.
     */
    private static String loadCssFromFile(String cssPath) {
        try {
            InputStream inputStream = ReportGenerator.class.getResourceAsStream(cssPath);
            if (inputStream == null) {
                LOG.warn("CSS file not found: {}", cssPath);
                return "";
            }

            byte[] cssBytes = inputStream.readAllBytes();
            inputStream.close();
            return new String(cssBytes, Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Failed to load CSS file {}: {}", cssPath, e.getMessage());
            return "";
        }
    }

    /**
     * Adds the summary statistics table to the HTML builder.
     */
    private static void addSummaryTable(HtmlBuilder builder, List<TestResult>  testData) {
        builder.table("inner-table")
                .tr(null)
                .th("Status", "status-header")
                .th("Count", "count-header")
                .th("%", "percentage-header")
                .closeTag(); // close tr

        // Add data rows
        float passed  = countByStatus(testData, Status.PASSED);
        float failed  = countByStatus(testData, Status.FAILED);
        float skipped = countByStatus(testData, Status.SKIPPED);
        float total   = passed + failed + skipped;

        addSummaryRow(builder, "Passed", (int) countByStatus(testData, Status.PASSED), (int) total, null);
        addSummaryRow(builder, "Failed", (int) countByStatus(testData, Status.FAILED), (int) total, null);
        addSummaryRow(builder, "Skipped", (int) countByStatus(testData, Status.SKIPPED), (int) total, null);
        addSummaryRow(builder, "Total", (int) total, (int) total, CLASS_TOTAL_RAW);

        builder.closeTag(); // close table
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

    /**
     * Adds a single row to the summary table with optional row CSS class.
     */
    private static void addSummaryRow(HtmlBuilder builder, String status, int count, int total, String rowClass) {
        double percentage = total > 0 ? (100.0 * count) / total : 0;
        builder.tr(rowClass)
                .td(status, CLASS_STATUS_CELL)
                .td(DECIMAL_FORMAT.format(count), null)
                .td(DECIMAL_FORMAT.format(percentage) + "%", null)
                .closeTag(); // close tr
    }


    /**
     * Formats HTML string with proper indentation for readability.
     */
    private static String formatHtmlOutput(String html) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(html);
            doc.outputSettings().indentAmount(4).prettyPrint(true);
            return doc.html();
        } catch (Exception e) {
            LOG.warn("Failed to format HTML, returning original: {}", e.getMessage());
            return html;
        }
    }

    /**
     * Generates a pie chart representing test case status distribution.
     */
    public static void generatePieChart(List<TestResult>  testData) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Pass", countByStatus(testData, Status.PASSED));
        dataset.setValue("Fail", countByStatus(testData, Status.FAILED));
        dataset.setValue("Skip", countByStatus(testData, Status.SKIPPED));

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        configurePieChartColors(chart);

        saveChartAsJpeg(chart, "pieChart.jpeg");
    }

    /**
     * Configures the colors for different sections of the pie chart.
     */
    private static void configurePieChartColors(JFreeChart chart) {
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setSectionPaint("Pass", Color.GREEN);
        plot.setSectionPaint("Fail", Color.RED);
        plot.setSectionPaint("Skip", Color.YELLOW);
    }

    /**
     * Saves the chart as a JPEG file.
     */
    private static void saveChartAsJpeg(JFreeChart chart, String fileName) {
        File chartFile = new File(REPORT_OUTPUT_DIR + fileName);
        try {
            ChartUtils.saveChartAsJPEG(chartFile, chart, 300, 200);
        } catch (IOException e) {
            LOG.error("IOException in saveChartAsJpeg: {}", e.getMessage());
        }
    }

    /**
     * Builds detailed test-case HTML, including optional additional info and interframework run IDs.
     *
     * @param fileName             output HTML file name
     * @param title                page title/header
     * @param results              list of {@link TestResult}
     * @param isInterframework     {@code true} to include run ID column
     * @param additionalInfoExists {@code true} if additional info column appears
     * @param additionalInfoMap    map of test name → additional info text
     */

    public static void produceTestcaseDetails(String fileName, String title, List<TestResult> results, boolean isInterframework, boolean additionalInfoExists, Map<String,String> additionalInfoMap) {
        String css = loadCssFromFile(TEST_CASE_CSS_PATH);
        HtmlBuilder builder = new HtmlBuilder()
                .doctype()
                .html("en")
                .head()
                .metaCharset("UTF-8")
                .meta("viewport", "width=device-width, initial-scale=1.0")
                .title(title)
                .style(css)
                .closeTag() // close head
                .body()
                .h3(title + ":");
        // creating table
        builder.table(null)
                .tr(null)
                .th("Sr.No", null)
                .th("Test Case Id", null)
                .th("Test Case Name", null)
                .th("Status", null)
                .th("Reason", null);
        // Insert Additional Info column only file exists
        if (additionalInfoExists) {
            builder.th("Additional Info", null);
        }
        // Insert Run ID column only for interframework tests
        if (isInterframework) {
            builder.th("Test Suite ID", null);
        }
        builder.closeTag();
        int idx = 1;
        String statusStyle;
        String statusIcon;
        for (TestResult r : results) {
            if (r.getStatus().toString().contains("PASS")) {
                statusStyle = "style=\"color: green; font-weight: bold;\"";
                statusIcon = "<span style=\"color: green;\">✔ </span>";
            } else {
                statusStyle = "style=\"color: red; font-weight: bold;\"";
                statusIcon = "<span style=\"color: red;\">✗ </span>";
            }
            builder.tr(null)
                    .td(String.valueOf(idx++), null)
                    .td(r.getJiraTag(), null)
                    .td(r.getName(), null)
                    .raw("<td " + statusStyle + ">" + statusIcon + r.getStatus().toString() + "</td>")
                    .td(r.getReason(), null);
            if (additionalInfoExists) {
                String info = additionalInfoMap.getOrDefault(r.getName(), "");
                builder.addRawCell(info);
            }
            if (isInterframework) {
                builder.td(r.getRunId(), null);
            }
            builder.closeTag();
        }
        builder.closeTag(); // close table
       String testcaseDetailHtml= formatHtmlOutput(builder.toString());
       writeHtmlToFile(testcaseDetailHtml, fileName);
    }
}
