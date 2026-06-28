package generalutilities;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class perform CSV file related operation.
 * This class consist methods such as read csv file,convert csv file to map,read csv file from custom separator etc.
 */
public class CSVUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(CSVUtilities.class);

    /**
     * This method is used to read CSV file.
     *
     * @param filePath     - path of the file where csv file is available
     * @return String      - it return the lines from the file to read CSV file
     * @throws IOException - an exception occur during a batch update operation or exceptions that are thrown when an I/O error occurs
     */
    public static StringBuilder readCSVFile(String filePath) throws IOException {
        var line = new StringBuilder();
        try (var fileReader = new FileReader(filePath);
             var csvReader = new CSVReader(fileReader)) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    LOG.trace(cell, "%s\n");
                    line.append(cell).append("\n");
                }
            }
    } catch (IOException | CsvValidationException e) {
        LOG.error(e.getMessage());
    }
        return line;
    }

    /**
     * This method is used to read CSV file with custom separator.
     *
     * @param file      - path where csv file is kept to read CSV file with custom separator
     * @param seperator - char type custom separator to read CSV file with custom separator
     * @return String   - it return the lines from the file
     */
    public StringBuilder readDataFromCustomSeparator(String file, char seperator) {
        List<String> al = new ArrayList<>();
        var line = new StringBuilder();
        try (var fileReader = new FileReader(file)) {
            // Create an object of file reader class with CSV file as a parameter.
            // Create csvParser object with parameter
            // Custom separator semi-colon
            var parser = new CSVParserBuilder().withSeparator(seperator).build();

            // Create csvReader object with parameter
            // FileReader and parser
            var csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(parser)
                    .build();
            // Read all data at once
            List<String[]> allData = csvReader.readAll();
            // Print Data.
            for (String[] row : allData) {
                for (String cell : row) {
                    al.add(cell);
                    line.append(cell).append(",");
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return line;
    }

    /**
     * This method is used to write data at once in the file.
     *
     * @param filePath - path where csv file is kept to write data at once in the file
     * @param data     - string array with comma separated values
     */
    public void writeDataAtOnce(String filePath, String[] data) {
        // First create file object for file placed at location
        // Specified by filepath
        var file = new File(filePath);
        try {
            // Create FileWriter object with file as parameter
            var outputFile = new FileWriter(file, true);
            // Create CSVWriter object filewriter object as parameter
            var writer = new CSVWriter(outputFile);
            // Create a List which contains String array
            writer.writeNext(data);
            // Closing writer connection
            writer.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * This method is used to read CSV files and return CSV files to Map.
     *
     * @param csvFile                - to convert into maps
     * @return                       - List (string type)
     * @throws FileNotFoundException - this is an exception occurs when a file path specified for accessing file does not exist or is inaccessible
     */
    public List<Map<String, String>> csvToMap(String csvFile) throws FileNotFoundException {
        var fileReader = new FileReader(csvFile);
        var scanner1 = new Scanner(fileReader);
        var lines = 0;
        try (var reader = new BufferedReader(new FileReader(csvFile))) {
            String lineContent = null;
            while ((lineContent = reader.readLine()) != null) lines++;
            LOG.trace("no. of lines {}", lines);
        } catch (IOException e) {
            LOG.error("Exception {}", e.getMessage());
        }
        String[] keys = scanner1.nextLine().split(",");
        LOG.trace("length of keys: {}", keys.length);
        List<Map<String, String>> rows = new ArrayList<>();
        while (scanner1.hasNextLine()) {
            String[] values = scanner1.nextLine().split(",");

            for (var i = 0; i < keys.length; i++) {
                Map<String, String> resultMap1 = new HashMap<>();
                resultMap1.put(keys[i], values[i]);
                rows.add(i, resultMap1);
            }
        }
        LOG.trace("Rows {}", rows);
        scanner1.close();
        return rows;
    }

    /**
     * This method is used to read data from custom separator,customized HashMap of two csv files.
     *
     * @param header               - to read data from custom separator
     * @param file                 - to read data
     * @param seperator            - char type custom separator to read CSV file with custom separator
     * @return                     - List (string type)
     * @throws ArithmeticException - this is an exception occur when wrong mathematical or arithmetic operation appears in the code during run time
     */
    public List<Map<String, String>> readDataFromCustomSeparator(String header, String file, char seperator) throws ArithmeticException {
        List<Map<String, String>> as = new ArrayList<>();
        List<String> al = new ArrayList<>();
        List<String> alHeader = new ArrayList<>();
        // Create an object of file reader class with CSV file as a parameter.
        try (var fileReader = new FileReader(file);
             var fileReader1 = new FileReader(header)) {
            // Create csvParser object with
            // Custom separator semi-colon
            var parser = new CSVParserBuilder().withSeparator(seperator).build();
            // Create csvReader object with parameter
            // FileReader and parser
            var csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(parser)
                    .build();
            var lines = 0;
            try (var reader = new BufferedReader(new FileReader(file))) {
                String lineContent = null;
                while ((lineContent = reader.readLine()) != null) lines++;
            }
            // Read all data at once
            List<String[]> allData = csvReader.readAll();
            // Print Data.
            for (String[] row : allData) {
                al.addAll(Arrays.asList(row));
            }
            var csvReader1 = new CSVReaderBuilder(fileReader1)
                    .withCSVParser(parser)
                    .build();
            // Read all data at once
            List<String[]> allData1 = csvReader1.readAll();
            // Print Data.
            for (String[] row : allData1) {
                alHeader.addAll(Arrays.asList(row));
            }
            var offCount = 0;
            LOG.trace("al.size {}", al.size());
            if (lines > 0) {
                offCount = al.size() / lines;
            }
            LOG.trace("offcount {}", offCount);
            var k = 0;
            for (var j = 0; j < lines; j++) {
                Map<String, String> resultMap1 = new HashMap<>();
                for (var i = 0; i < alHeader.size(); i++) {
                    resultMap1.put(alHeader.get(i), al.get(i + k));
                }
                as.add(j, resultMap1);
                k = k + offCount;
            }
        } catch (Exception e) {
            LOG.error("Exception {}", e.getMessage());
        }
        return as;
    }
}
