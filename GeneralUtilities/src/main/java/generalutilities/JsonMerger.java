package generalutilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;
import java.util.*;


/**
 * This class consist of methods related to merging the json files after parallel execution.
 * This class contain methods such as createCucumberJsonFile, mergeJsonFiles, createIntegratedReportFolder etc.
 */
public class JsonMerger {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMerger.class);
    private static final String JSON_FILE_NAME = "JsonReport.json";
    private static final String TARGET_FOLDER = "target";
    private static final String EXTENT_REPORT_FOLDER = "ExtentReport";
    private static final String INTEGRATED_REPORT_FOLDER = "IntegratedReport";
    private static final String EXTENT_REPORT_HTML = "ExtentReport.html";
    private static final String SCREENSHOTS_FOLDER = "Screenshots";


    /**
     * The Main method is the entry point of an executable program.
     *
     * @param args - It stores Java command-line arguments and is an array of type
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static void main(String[] args) throws IOException{
        JsonMerger.createCucumberJsonFile();
        var extentReportFolder = new File(TARGET_FOLDER + File.separator + EXTENT_REPORT_FOLDER);
        assert(extentReportFolder.exists());
        List<File> extentReportFolderList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(extentReportFolder.listFiles())));
        createIntegratedReportFolder(extentReportFolderList);
    }

    /**
     * This method is used to create merged cucumber json files after parallel execution.
     *
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static void createCucumberJsonFile() throws IOException {

        File testResourcesFolder = new File (TARGET_FOLDER + File.separator + "generated-test-sources" + File.separator +"cucumber");
        List <File> runnerFilesList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(testResourcesFolder.listFiles())));

        if (runnerFilesList.isEmpty()) {
            LOG.error("Provided test tag could not get executed");
        } else {
            File targetFolder = new File (TARGET_FOLDER);
            List <File> targetFolderFilesList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(targetFolder.listFiles())));
            List <String> targetFolderCucumberJsonFilesList = new ArrayList<>();
            for (File eachFile : targetFolderFilesList) {
                String fileName = eachFile.getName();
                if (fileName.contains(".json") && (!fileName.contains("cucumber"))) {
                    targetFolderCucumberJsonFilesList.add(eachFile.getAbsolutePath());
                    String [] fileNameArray = fileName.split(".json");
                    Files.delete(Paths.get(TARGET_FOLDER + File.separator + fileNameArray[0]));
                }
            }
            String mergedCucumberJson = JsonMerger.mergeJsonFiles(targetFolderCucumberJsonFilesList);
            if (!mergedCucumberJson.isEmpty()) {
                try (var myWriter = new FileWriter(TARGET_FOLDER + File.separator + "cucumber.json")) {
                    myWriter.write(mergedCucumberJson);
                } catch (IOException e) {
                    LOG.error("Exception on createMergedCucumberJsonFile method Json File Write {}", e.getMessage());
                }
            }
        }

    }

    /**
     * This method is used to create integrated report folder files (json file, html file and ScreenShots folder) from extent reports which have been created after parallel execution.
     * After/During Integrated Report folder files is created/creating, extent reports folder will be deleted. End of the process Extent report folder has only Integrated Report Folder.
     *
     * @param extentReportFolderList - The list of the extent report folder files/folders which have been created after parallel execution.
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */

    private static void createIntegratedReportFolder(List<File> extentReportFolderList) throws IOException {

        List<String> extentReportJsonFilesList = new ArrayList<>();

        for (File currentExtentReportFolder : extentReportFolderList) {
            String currentExtentReportFolderPath =  currentExtentReportFolder.getAbsolutePath();
            var currentExtentReportJsonFile = new File (currentExtentReportFolderPath + File.separator + JSON_FILE_NAME);
            var currentExtentReportScreenshotsFolder = new File (currentExtentReportFolderPath + File.separator + SCREENSHOTS_FOLDER);
            var currentExtentReportHtmlFile = new File (currentExtentReportFolderPath + File.separator + EXTENT_REPORT_HTML);
            if (currentExtentReportJsonFile.exists())
                extentReportJsonFilesList.add(currentExtentReportJsonFile.getAbsolutePath());
            if (currentExtentReportHtmlFile.exists())
                Files.delete(Paths.get(currentExtentReportHtmlFile.getAbsolutePath()));
            if (currentExtentReportScreenshotsFolder.exists())
                modifyCurrentExtendReportFolderFiles(currentExtentReportFolder);
        }

        String mergedExtentReportJson = mergeJsonFiles(extentReportJsonFilesList);
        String integratedReportFolderPath = TARGET_FOLDER + File.separator + EXTENT_REPORT_FOLDER + File.separator + INTEGRATED_REPORT_FOLDER;
        File integratedReportFolder = new File(integratedReportFolderPath);
        if (!integratedReportFolder.exists()) {
            integratedReportFolder.mkdirs();
        }

        if (!mergedExtentReportJson.isEmpty()) {
            try (var myWriter = new FileWriter(integratedReportFolderPath + File.separator + "json.json")) {//NOSONAR
                myWriter.write(mergedExtentReportJson);
            } catch (IOException e) {
                LOG.error("Exception on createIntegratedReportFolder method Json File Write {}", e.getMessage());
            }
        }

        extentReportFoldersCleanUp(extentReportFolderList);

        String integratedJsonFilePath =(new File(TARGET_FOLDER + File.separator + EXTENT_REPORT_FOLDER + File.separator + INTEGRATED_REPORT_FOLDER + File.separator + "json.json")).getAbsolutePath();

        ExtentSparkReporter mergedExtentReport = new ExtentSparkReporter(TARGET_FOLDER + File.separator + EXTENT_REPORT_FOLDER + File.separator + INTEGRATED_REPORT_FOLDER + File.separator + EXTENT_REPORT_HTML);
        ExtentReports extentMerged = new ExtentReports();
        try {
            extentMerged.createDomainFromJsonArchive(integratedJsonFilePath);
            extentMerged.attachReporter(mergedExtentReport);
        } catch (IOException e) {
            LOG.error("Exception on createIntegratedReportFolder method extent report create {} : ", e.getMessage());
        }
        extentMerged.flush();
        injectDropdown();
    }

    /**
     * This method is used to inject dropdown functionality into the final integrated Extent report HTML file
     * by modifying the jQuery script, unless the skipExpandCollapse flag is set to true either via system property
     * or through the extentReportFlag.properties file.
     * If the flag is set to true, the dropdown injection will be skipped and logged accordingly.
     *
     * No parameters are required for this method. All required paths and flags are derived internally.
     * Any I/O exceptions encountered during the process will be caught and logged.
     *
     * @throws IOException - an exception may occur while reading the properties file or updating the HTML file.
     */
    private static void injectDropdown() {
        try {
            String skipExpandCollapse = System.getProperty("skipExpandCollapse");
            boolean shouldInjectDropdown = true;

            if (skipExpandCollapse == null) {
                ReportAndLogging reportAndLogging = new ReportAndLogging();
                if (reportAndLogging.checkFlagPropertyFileExist()) {
                    Properties props = reportAndLogging.fileSpecificUtilities.readPropertyFile("extentReportFlag.properties");
                    skipExpandCollapse = props.getProperty("skipExpandCollapse", "false");
                }
            }

            if ("true".equalsIgnoreCase(skipExpandCollapse)) {
                LOG.info("Dropdown injection skipped due to skipExpandCollapse flag.");
                shouldInjectDropdown = false;
            }

            if (shouldInjectDropdown) {
                Path integratedReportPath = Paths.get(
                        TARGET_FOLDER, EXTENT_REPORT_FOLDER, INTEGRATED_REPORT_FOLDER
                );
                ReportAndLogging reportAndLogging = new ReportAndLogging();
                reportAndLogging.updateJQueryInHTML(integratedReportPath);
                LOG.info("Dropdown injection completed for final report.");
            }
        } catch (IOException e) {
            LOG.error("Failed to inject dropdown into final report: {}", e.getMessage());
        }
    }

    /**
     * This method is used to modify extent report folder files (screenshots file name, json file) if extent report folder has screenshots.
     *
     * @param currentExtentReportFolder - This the current extent report folder which has screenshots.
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    private static void modifyCurrentExtendReportFolderFiles(File currentExtentReportFolder) throws IOException {

        var currentExtentReportFolderPath = currentExtentReportFolder.getAbsolutePath();
        var currentExtentReportFolderName = currentExtentReportFolder.getName().trim();
        var screenShotsFolder = new File( currentExtentReportFolderPath + File.separator + SCREENSHOTS_FOLDER);

        if (screenShotsFolder.exists()) {
            List <File> screenShotsFolderFileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(screenShotsFolder.listFiles())));

            for (File screenShotsFile : screenShotsFolderFileList) {
                var screenShotsFileCurrentPath = screenShotsFile.getAbsolutePath();
                var screenShotsFileCurrentName = screenShotsFile.getName();
                var screenShotsFileNewName = currentExtentReportFolderName + "_" + screenShotsFileCurrentName;
                var screenShotsFileNewPath = currentExtentReportFolderPath + File.separator + SCREENSHOTS_FOLDER + File.separator + screenShotsFileNewName;

                try {
                    Files.move(Paths.get(screenShotsFileCurrentPath), Paths.get(screenShotsFileNewPath));
                    File source = new File(screenShotsFileNewPath);
                    File dest = new File(System.getProperty("user.dir") + File.separator + TARGET_FOLDER + File.separator +EXTENT_REPORT_FOLDER + File.separator + INTEGRATED_REPORT_FOLDER + File.separator + SCREENSHOTS_FOLDER + File.separator + screenShotsFileNewName);
                    FileUtils.moveFile(source, dest);
                } catch (IOException e) {
                    LOG.error("Exception on checkEachReportScreenShots method screenShots folder move {}:", e.getMessage());
                }

                String jsonFileString = new String(Files.readAllBytes(Paths.get(currentExtentReportFolderPath + File.separator + JSON_FILE_NAME)));
                String finalJson = jsonFileString.replace(screenShotsFileCurrentName, screenShotsFileNewName);
                try (var myWriter = new FileWriter(currentExtentReportFolderPath + File.separator + JSON_FILE_NAME)) {
                    myWriter.write(finalJson);
                } catch (IOException e) {
                    LOG.error("Exception on modifyReportJsonFile method Json File Write {}", e.getMessage());
                }
            }
            Files.delete(screenShotsFolder.toPath());
        }
    }


    /**
     * This method is used to merge json Files after parallel execution.
     *
     * @param jsonFilesList - The list of the json file paths.
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static String mergeJsonFiles(List <String> jsonFilesList) throws IOException {
        String[] mergedJsonFilesArray = new String[jsonFilesList.size()];
        for (int i = 0; i < jsonFilesList.size(); i++) {
            String cucumberJsonFileString = new String(Files.readAllBytes(Paths.get(jsonFilesList.get(i))));
            int firstIndex = cucumberJsonFileString.indexOf("{");
            int lastIndex = cucumberJsonFileString.lastIndexOf("}") + 1;
            mergedJsonFilesArray[i] = cucumberJsonFileString.substring(firstIndex, lastIndex);
            Files.delete(Paths.get(jsonFilesList.get(i)));
        }
        return Arrays.toString(mergedJsonFilesArray);
    }

    /**
     * This method is used to remove/delete extent report folders except integrated report folder.
     *
     * @param extentReportFolderList - The list of the extent report folders which had been created after parallel execution.
     */
    private static void extentReportFoldersCleanUp(List<File> extentReportFolderList) {
        try {
            for (File eachExtentReportFolder : extentReportFolderList) {
                String [] folderFiles = eachExtentReportFolder.list();
                assert folderFiles != null;
                for(String file : folderFiles){
                    File currentFile = new File(eachExtentReportFolder.getPath() , file);
                    Files.delete(currentFile.toPath());
                }
                Files.delete(eachExtentReportFolder.toPath());
            }

        } catch (Exception e) {
            LOG.error("Exception on extentReportFoldersCleanUp {} : ", e.getMessage());
        }
    }

}

