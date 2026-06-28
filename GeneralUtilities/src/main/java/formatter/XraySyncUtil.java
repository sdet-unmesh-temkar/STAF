package formatter;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.shaded.gherkin.messages.internal.gherkin.GherkinDocumentBuilder;
import io.cucumber.shaded.gherkin.messages.internal.gherkin.Parser;
import io.cucumber.shaded.messages.IdGenerator;
import io.cucumber.shaded.messages.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class XraySyncUtil implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(XraySyncUtil.class);
    private static final String JIRA_TAG_CHARACTERS = "((?i)@SOL)(\\D)*-(\\d)+";
    private static final String INTERFRAMEWORK_JIRA_TAG_CHARACTERS = "((?i)@InterframeworkJiraKey:)(\\D)*-(\\d)+";

    private static final Map<String, Boolean> featureWrittenMap = new HashMap<>();
    private static final Map<String, Boolean> backgroundWrittenMap = new HashMap<>();
    private static final Set<String> writtenScenarios = new HashSet<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleCaseStarted);
    }

    private void handleCaseStarted(TestCaseStarted event) {
        prepareFeatureFiles(event);
    }

    private static void prepareFeatureFiles(TestCaseStarted event) {
        try {
            String featurePath = event.getTestCase().getUri().getPath();
            String featureFileContent = Files.readString(Path.of(System.getProperty("os.name").contains("Windows") ? featurePath.substring(1) : featurePath));

            IdGenerator idGenerator = new IdGenerator.Incrementing();
            Parser<GherkinDocument> parser = new Parser<>(new GherkinDocumentBuilder(idGenerator));
            GherkinDocument gherkinDocument = parser.parse(featureFileContent);

            Scenario scenario = null;
            for (FeatureChild child : gherkinDocument.getFeature().getChildren()) {
                if (child.getScenario() != null && Objects.equals(child.getScenario().getName(), event.getTestCase().getName())) {
                    scenario = child.getScenario();
                    break;
                }
            }

            if (scenario == null || writtenScenarios.contains(scenario.getName())) return;
            writtenScenarios.add(scenario.getName());

            boolean isJiraTagged = hasJiraTag(event.getTestCase().getTags());

            // Always write to existing/
            writeScenarioToFile(event, gherkinDocument, scenario, featurePath, "new");

            // Write to new/ only if JIRA tagged
            if (isJiraTagged) {
                writeScenarioToFile(event, gherkinDocument, scenario, featurePath, "existing");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeScenarioToFile(TestCaseStarted event, GherkinDocument gherkinDocument, Scenario scenario, String originalPath, String folderType) throws IOException {


        // Base folder where scenarios will be saved
        String baseTargetPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "scenarios" + File.separator + folderType;


        // Use originalPath to preserve folder structure
        File originalFile = new File(originalPath);
        String relativePath = originalFile.getPath().replace(System.getProperty("user.dir") , "");


        // Construct the new path under target/scenarios
        File featureFile = new File(baseTargetPath, relativePath);

        // Ensure parent directories exist
        File parentDir = featureFile.getParentFile();
        if (!parentDir.exists()) parentDir.mkdirs();

        String fileKey = folderType + "_" + featureFile.getName();

        // This file does not contain sensitive data. It stores only test case steps.
        BufferedWriter bw = new BufferedWriter(new FileWriter(featureFile, true));  // NOSONAR


        if (Boolean.FALSE.equals(featureWrittenMap.getOrDefault(fileKey, false))) {
            writeFeature(bw, gherkinDocument);
            featureWrittenMap.put(fileKey, true);
        }

        if (Boolean.FALSE.equals(backgroundWrittenMap.getOrDefault(fileKey, false))) {
            writeBackground(bw, gherkinDocument, event);
            backgroundWrittenMap.put(fileKey, true);
        }

        writeTags(bw, scenario);
        writeScenario(bw, scenario);
        writeSteps(bw, event, scenario, gherkinDocument);
        bw.newLine();
        bw.close();
    }

    private static boolean hasJiraTag(List<String> tags) {
        return tags.stream().anyMatch(tag -> tag.matches(JIRA_TAG_CHARACTERS) || tag.matches(INTERFRAMEWORK_JIRA_TAG_CHARACTERS));
    }

    /**
     * This method is used to get list of tags, keyword and feature name associated with test scenario
     * @param bw - this param is used write tags into feature file
     * @param gherkinDocument - this param is used get scenario details
     * @throws IOException - this is used to capture IO exception
     */
    private static void writeFeature(BufferedWriter bw, GherkinDocument gherkinDocument) throws IOException {
        List<Tag> scenarioTags = gherkinDocument.getFeature().getTags();
        for (Tag tag : scenarioTags) {
            bw.write(tag.getName() + " ");
        }
        bw.newLine();
        bw.write(gherkinDocument.getFeature().getKeyword() + ": " + gherkinDocument.getFeature().getName());
        bw.newLine();
        bw.newLine();
    }

    private static void writeBackground(BufferedWriter bw, GherkinDocument gherkinDocument, TestCaseStarted event) throws IOException {

        String featurePath = event.getTestCase().getUri().getPath();
        String osAdjustedPath = System.getProperty("os.name").contains("Windows") ? featurePath.substring(1) : featurePath;
        for (FeatureChild child : gherkinDocument.getFeature().getChildren()) {
            if (child.getBackground() != null) {
                Background background = child.getBackground();
                bw.write(background.getKeyword() + ": " + (background.getName() != null ? background.getName() : ""));
                bw.newLine();

                int previousStepLocation = background.getLocation().getLine().intValue()+1;
                int currentStepLocation;

                for (Step step : background.getSteps()) {

                    currentStepLocation = step.getLocation().getLine().intValue();
                        while (currentStepLocation - previousStepLocation != 0) {
                            bw.write(getLineText(osAdjustedPath, previousStepLocation));
                            bw.newLine();
                            previousStepLocation++;
                        }
                    previousStepLocation++;

                    writePickleSteps(bw, step);
                    writeDataTable(bw, step);
                }
                bw.newLine();
                break;
            }
        }
    }

    private static String getLineText(String filePath, int lineNumber) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));
        if (lineNumber > 0 && lineNumber <= lines.size()) {
            return lines.get(lineNumber - 1); // Line numbers are 1-based
        } else {
            throw new IllegalArgumentException("Line number " + lineNumber + " is out of range for file: " + filePath);
        }
    }

    /**
     * This method is used to write list of scenario tags in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @param scenario - this param is used to fetch scenario details
     * @throws IOException - this is used to capture IO exception
     */
    private static void writeTags(BufferedWriter bw, Scenario scenario) throws IOException {
        List<Tag> scenarioTags = scenario.getTags();
        for (Tag tag : scenarioTags) {
            if (tag.getName().matches(JIRA_TAG_CHARACTERS) || tag.getName().matches(INTERFRAMEWORK_JIRA_TAG_CHARACTERS)) {
                bw.write(tag.getName() + " ");
            }
        }
        for (Tag tag : scenarioTags) {
            if (!tag.getName().matches(JIRA_TAG_CHARACTERS) && !tag.getName().matches(INTERFRAMEWORK_JIRA_TAG_CHARACTERS)) {
                bw.write(tag.getName() + " ");
            }
        }
        bw.newLine();
    }

    /**
     * This method is used to write scenario name in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @throws IOException - this is used to capture IO exception
     */
    private static void writeScenario(BufferedWriter bw, Scenario scenario) throws IOException {
        bw.write(scenario.getKeyword() + ": " + scenario.getName());
        bw.newLine();
    }

    /**
     * This method is used to write all scenario steps in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @param event - this event is used to fetch scenario details
     */
    private static void writeSteps(BufferedWriter bw, TestCaseStarted event, Scenario scenario, GherkinDocument gherkinDocument) throws IOException {
        for (Step testStep : scenario.getSteps()) {
            writeTestStepToFeatureFile(bw, testStep);
        }
        writeScenarioOutlineExample(bw, event, scenario, gherkinDocument);
    }

    /**
     * This method is used to write scenario step in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @throws IOException - this is used to capture IO exception
     */
    private static void writeTestStepToFeatureFile(BufferedWriter bw, Step testStep) throws IOException {
        writePickleSteps(bw, testStep);
        writeDataTable(bw, testStep);
    }

    /**
     * This method is used to write scenario step in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @throws IOException - this is used to capture IO exception
     */
    private static void writePickleSteps(BufferedWriter bw, Step testStep) throws IOException {
        bw.write(testStep.getKeyword() + testStep.getText());
        bw.newLine();
    }

    private static void writeDataTable(BufferedWriter bw, Step testStep) throws IOException {
        if (testStep.getDataTable() != null) {
            for (TableRow tableRow : testStep.getDataTable().getRows()) {
                bw.write("    | ");
                tableRow.getCells().forEach(tableCell -> {
                    try {
                        bw.write(tableCell.getValue() + " | ");
                    } catch (IOException e) {
                        LOG.error("IOException on writeDataTable method: {}", e.getMessage());
                    }
                });
                bw.newLine();
            }
        }
    }

    /**
     * This method is used to write scenario outline example in feature file which is use for Xray Sync
     * @param bw - this param is used write tags into feature file
     * @param event - this event is used to fetch scenario details
     * @throws IOException - this is used to capture IO exception
     */
    private static void writeScenarioOutlineExample(BufferedWriter bw, TestCaseStarted event, Scenario scenario, GherkinDocument gherkinDocument) throws IOException {
        if (!scenario.getExamples().isEmpty()) {

            for (Examples examples : scenario.getExamples()) {
                if (!examples.getTags().isEmpty()) {

                    for (Tag tag : examples.getTags()) {
                        if (event.getTestCase().getTags().contains(tag.getName())) {
                            try {
                                bw.write(tag.getName());
                                bw.newLine();
                            } catch (IOException e) {
                                LOG.error("IOException on writeScenarioOutlineExample method: {}", e.getMessage());
                            }
                        }
                    }
                    boolean flag = true;
                    for (Tag tag : examples.getTags()) {
                        if (event.getTestCase().getTags().contains(tag.getName()) && (flag)) {
                            bw.write(examples.getKeyword() + ":");
                            bw.newLine();
                            bw.write("  |");
                            for (TableCell tableCell : examples.getTableHeader().getCells()) {
                                bw.write(tableCell.getValue() + " |");
                            }
                            bw.newLine();

                            for (TableRow tableRow : examples.getTableBody()) {
                                bw.write("  |");
                                for (TableCell cellValue : tableRow.getCells()) {
                                    bw.write(cellValue.getValue() + " |");
                                }
                                bw.newLine();
                            }
                            flag = false;
                        }
                    }
                } else {
                    List<String> allTags = new ArrayList<>();
                    gherkinDocument.getFeature().getTags().forEach(tag ->
                            allTags.add(tag.getName())
                    );
                    scenario.getTags().forEach(tag ->
                            allTags.add(tag.getName())
                    );

                    if (event.getTestCase().getTags().equals(allTags)) {
                        bw.write(examples.getKeyword() + ":");
                        bw.newLine();
                        bw.write("  |");
                        for (TableCell tableCell : examples.getTableHeader().getCells()) {
                            bw.write(tableCell.getValue() + " |");
                        }
                        bw.newLine();

                        for (TableRow tableRow : examples.getTableBody()) {
                            bw.write("  |");
                            for (TableCell cellValue : tableRow.getCells()) {
                                bw.write(cellValue.getValue() + " |");
                            }
                            bw.newLine();
                        }
                    }
                }
            }
        }

    }
}
