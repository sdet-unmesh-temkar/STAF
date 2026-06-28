package apiutilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class consist of a method related to API response tracking.
 * This class contain API response related method such as set data,get data,get list, set list, calculate performance matrix etc.
 */
public class APIResponseTracking {
    private static List<String[]> list = Collections.synchronizedList(new ArrayList<>());
    private static List<String[]> listFinal = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Object> apiResDataSet = Collections.synchronizedMap(new HashMap<>());
    private static APIResponseTracking apiResData = APIResponseTracking.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(APIResponseTracking.class);
    private static final String COMMA = ",";
    private static final String DEFAULT_SEPARATOR = COMMA;
    private static final String DOUBLE_QUOTES = "\"";
    private static final String EMBEDDED_DOUBLE_QUOTES = "\"\"";
    private static final String NEW_LINE_UNIX = "\n";
    private static final String NEW_LINE_WINDOWS = "\r\n";
    private static final String STATUSCODE = "StatusCode";

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private APIResponseTracking() {
    }

    /**
     * This method is used to set data.
     *
     * @param key   - key to be set
     * @param value - its value to set data
     */
    public void setData(String key, Object value) {
        apiResDataSet.put(key, value);
    }

    /**
     * This method is used to get data.
     *
     * @param key - key to be set
     * @return    - object to get data
     */
    public Object getData(String key) {
        return apiResDataSet.get(key);
    }

    /**
     * Private static inner class which is loaded when getInstance() is called for the first time.
     */
    private static class APIResponseTrackingInitializer {
        private static final APIResponseTracking instance = new APIResponseTracking();
    }

    /**
     * This method will return the singleton instance of API response tracking class.
     *
     * @return - singleton instance of API response tracking class
     */
    public static APIResponseTracking getInstance() {
        return APIResponseTrackingInitializer.instance;
    }

    /**
     * This method is used to convert given file into csv format.
     *
     * @param line    - convert given file into csv format
     * @return string - convertToCsvFormat
     */
    public String convertToCsvFormat(final String[] line) {
        return convertToCsvFormat(line, DEFAULT_SEPARATOR);
    }

    /**
     * This method is used to convert given file into csv format.
     *
     * @param line      - to convert given file into csv format
     * @param separator - separator to convert given file into csv format
     * @return string   - convertToCsvFormat
     */
    public String convertToCsvFormat(final String[] line, final String separator) {
        return convertToCsvFormat(line, separator, true);
    }

    /**
     * This method is used to convert given file into csv format.
     *
     * @param line      - to convert given file into csv format
     * @param separator - splitting the character of a string
     * @param quote     - to enclosed character
     * @return String   - file into CSV format
     */
    // if quote = true, all fields are enclosed in double quotes
    public String convertToCsvFormat(
            final String[] line,
            final String separator,
            final boolean quote) {
        return Stream.of(line)                              // convert String[] to stream
                .map(l -> formatCsvField(l, quote))         // format CSV field
                .collect(Collectors.joining(separator));    // join with a separator

    }

    /**
     * This method is used to format csv field
     *
     * @param field   - to format csv field
     * @param quote   - for the splitting character
     * @return String - result
     */
    // put your extra login here
    public String formatCsvField(final String field, final boolean quote) {
        String result = field;
        if (result.contains(COMMA)
                || result.contains(DOUBLE_QUOTES)
                || result.contains(NEW_LINE_UNIX)
                || result.contains(NEW_LINE_WINDOWS)) {
            // if field contains double quotes, replace it with two double quotes \"\"
            result = result.replace(DOUBLE_QUOTES, EMBEDDED_DOUBLE_QUOTES);
            // must wrap by or enclosed with double quotes
            result = DOUBLE_QUOTES + result + DOUBLE_QUOTES;

        } else {
            // should all fields enclosed in double quotes
            if (quote) {
                result = DOUBLE_QUOTES + result + DOUBLE_QUOTES;
            }
        }
        return result;
    }

    /**
     * This method is used to write content to csv file.
     *
     * @param list         - list of file in string to write content to csv file
     * @param fileName     - name of the file to write content to csv file
     * @throws IOException - an exception throws during a batch update operation or an exception that is thrown when an I/O error occurs.
     */
    // a standard FileWriter, CSV is a normal text file
    public void writeToCsvFile(List<String[]> list, String fileName) throws IOException {
        File destinationFolder = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "apiPerformance");
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }
        File file = new File(destinationFolder + File.separator + fileName + ".csv");
        List<String> collect = list.stream()
                .map(this::convertToCsvFormat)
                .toList();
        // CSV is a normal text file, need a writer
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : collect) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    /**
     * This method is used to create CSV data and add headers into object to write in the CSV file.
     *
     * @return - array list
     */
    public List<String[]> createCsvDataSpecial() {
        //add headers into object to write in csv file
        String[] header = {"URL", "BasePath", "MethodType", "Involk", "Min", "Max", "Median", "Line90", "Average", STATUSCODE, "Min in ms", "Max in ms", "Median in ms", "Line90 in ms", "Average in ms"};
        //ArrayList String type object to store/add API performance data
        ArrayList<String> recordData = new ArrayList<>(); ////ArrayList declaration and initialization
        //add "API Response URL" into string type array object
        recordData.add((String) apiResData.getData("APIRespURL")); //adding elements
        //get API Response Base path from API response hash map
        String apiRespBasePath = (String) apiResData.getData("APIRespBasePath");
        //Replace actual data with "{data}" from base path and modify
        if (!apiRespBasePath.equals("")) {
            String stringTemp1 = apiRespBasePath.replace("[\\d]", "").replace("//", "/{data}/").replace("_", "{data}");
            String stringTemp2 = apiRespBasePath.replace("[^\\d.]", "");
            if (stringTemp1.contains("v.")) {
                stringTemp2 = stringTemp2.substring(0, 3);
                apiRespBasePath = stringTemp1.replace("v.", "v" + stringTemp2);
            } else if (stringTemp1.contains("v1")) {
                stringTemp2 = stringTemp2.substring(0, 1);
                apiRespBasePath = stringTemp1.replace("v1", "v" + stringTemp2);
            } else if (stringTemp1.contains("v2")) {
                stringTemp2 = stringTemp2.substring(0, 1);
                apiRespBasePath = stringTemp1.replace("v2", "v" + stringTemp2);
            }
        }
        //add "API Base Path" into string type array object
        recordData.add(apiRespBasePath);
        //get Method type from response hashmap and add "API Method Type" into string type array object
        recordData.add((String) apiResData.getData("APIRespMethType"));
        //add "API Invol count" into string type array object
        recordData.add("1");
        //get API start time/min time from response hashmap and add "API Response Start time/Min time" into string type array object
        recordData.add((String) apiResData.getData("APIRespStart"));
        //get max time/end time from response hashmap and add "API Response End time/Max Time" into string type array object
        recordData.add((String) apiResData.getData("APIRespEnd"));
        //get total time/median time from response hashmap and add "API Response Total time/Median time" into string type array object
        recordData.add(apiResData.getData("APIRespTime") + "\t");
        //add "API Line90 time" into string type array object
        recordData.add("");
        //add "API Average time" into string type array object
        recordData.add("");
        //get status code from response hashmap and add "API Response Status Code" into string type array object
        recordData.add((String) apiResData.getData("APIRespStatusCode"));
        recordData.add("");
        recordData.add("");
        recordData.add("");
        recordData.add("");
        recordData.add("");
        // ArrayList to String Array conversion
        String[] record1 = new String[recordData.size()];
        for (int j = 0; j < recordData.size(); j++) {
            record1[j] = recordData.get(j);
        }
        // add header , if header is not available into list
        if (list.isEmpty()) {
            list.add(header);
        }
        //add record into list
        list.add(record1);
        return list;
    }

    /**
     * This method is used to return list of data.
     *
     * @return - list of data
     */
    public static List<String[]> getList() {
        return list;
    }

    /**
     * This method is used to calculate performance.
     *
     * @param list         - to calculate performance
     * @throws IOException - an exception throws during a batch update operation or an exception that is thrown when an I/O error occurs.
     */
    public void calculatePerformance(List<String[]> list) throws IOException {
        if (!list.isEmpty()) {
            writeToCsvFile(list, "monitor");
            List<String[]> abc = list;
            for (int i = 0; i < abc.size(); i++) {
                var count = 0;
                String[] a = abc.get(i);
                List<Long> perMatList = new ArrayList<>();
                ArrayList<String> arrayList = new ArrayList<>();
                for (int j = 0; j < abc.size(); j++) {
                    String[] b = abc.get(j);
                    if (a[1].equals(b[1])) {
                        count++;
                        if (!(a[5].contains("Max")) && (!(a[5].equals("0")))) {
                            if (b[5].contains("ms")) {//
                                perMatList.add((Long.parseLong(String.valueOf(convertToMilliseconds(b[5])))));
                            } else {
                                perMatList.add((Long.parseLong(b[5])));
                            }
                        }
                        if (!(a[9].contains(STATUSCODE))) {
                            arrayList.add(b[9]);
                        }
                    }
                }
                if (!(a[1].contains("BasePath"))) {
                    a[3] = Integer.toString(count);
                }
                if (!(a[9].contains(STATUSCODE))) {
                    HashSet<String> hashSet = new HashSet<>(arrayList);
                    a[9] = hashSet.toString().replace("[", "").replace("]", "");
                }
                if (!(a[8].contains("Average")) && (!(a[5].equals("0")) && a[5].length() > 0)) {
                    long sum = 0;
                    long avg;
                    for (int k = 0; k < perMatList.size(); k++) {
                        sum += perMatList.get(k);
                    }
                    avg = sum / perMatList.size();
                    a[8] = convertMilliseconds(avg);
                    a[14] = String.valueOf(avg);
                }
                if (!(a[7].contains("Line90")) && (!(a[5].equals("0")) && a[5].length() > 0)) {
                    Collections.sort(perMatList);
                    int line90Index = (int) Math.floor((double) (perMatList.size() - 1) * 9 / 10);
                    a[7] = convertMilliseconds(perMatList.get(line90Index));
                    a[13] = (perMatList.get(line90Index)).toString();
                }
                if (!(a[6].contains("Median")) && (!(a[5].equals("0")) && a[5].length() > 0)) {
                    Collections.sort(perMatList);
                    int medianIndex = (int) Math.floor((double) (perMatList.size() - 1) / 2);
                    a[6] = convertMilliseconds(perMatList.get(medianIndex));
                    a[12] = (perMatList.get(medianIndex)).toString();
                }
                if (!(a[5].contains("Max")) && (!(a[5].equals("0")) && a[5].length() > 0)) {
                    Collections.sort(perMatList);
                    a[5] = convertMilliseconds(perMatList.get(perMatList.size() - 1));
                    a[11] = (perMatList.get(perMatList.size() - 1)).toString();
                }
                if (!(a[4].contains("Min")) && (!(a[5].equals("0")) && a[5].length() > 0)) {
                    Collections.sort(perMatList);
                    a[4] = convertMilliseconds(perMatList.get(0));
                    a[10] = perMatList.get(0).toString();
                }
                String[] y;
                int iteration = 0;
                for (int x = 0; x < listFinal.size(); x++) {
                    y = listFinal.get(x);
                    if ((y[1].equals(a[1]))) {
                        iteration++;
                    }
                }
                if (iteration == 0) {
                    listFinal.add(a);
                }
            }
        }
        writeToCsvFile(listFinal, "apiPerformanceMatrics");
    }

    /**
     * This method is used to convert time in millisecond.
     *
     * @param timeInMilliseconds - convert time in millisecond.
     * @return String            - time in millisecond
     */
    public static String convertMilliseconds(long timeInMilliseconds) {
        var sign = "";
        if (timeInMilliseconds < 0) {
            sign = "-";
            timeInMilliseconds = Math.abs(timeInMilliseconds);
        }
        long hours = (timeInMilliseconds / (1000 * 60 * 60)) % 24;
        long minutes = (timeInMilliseconds / (1000 * 60)) % 60;
        long seconds = (timeInMilliseconds / 1000) % 60;
        long millis = timeInMilliseconds % 1000;
        final StringBuilder formatted = new StringBuilder(20);
        formatted.append(sign);
        if (hours != 0) formatted.append(String.format("%02d hr", hours));
        if (minutes != 0) formatted.append(String.format(" %02d min", minutes));
        if (seconds != 0) formatted.append(String.format(" %02d sec", seconds));
        if (millis != 0) formatted.append(String.format(" %03d ms", millis));
        return formatted.toString();
    }

    /**
     * This method is used to convert time in milliseconds.
     *
     * @param time - time required to convert in milliseconds
     * @return     - timeInMilliSeconds
     */
    public static long convertToMilliseconds(String time) {
        String timeInHMSM = time;
        int hrIndex = 0;
        int minIndex = 0;
        int secIndex = 0;
        int msIndex = 0;
        int hrs = 0;
        int min = 0;
        int sec = 0;
        int ms = 0;
        if (timeInHMSM.contains("hr")) {
            hrIndex = timeInHMSM.indexOf("hr");
            hrs = Integer.parseInt(timeInHMSM.substring(0, hrIndex - 1).replace(" ", ""));
        }
        if (timeInHMSM.contains("min")) {
            minIndex = timeInHMSM.indexOf("min");
            if (timeInHMSM.contains("hr")) {
                min = Integer.parseInt(timeInHMSM.substring(hrIndex + 2, minIndex - 1).replace(" ", ""));
            } else {
                min = Integer.parseInt(timeInHMSM.substring(0, minIndex - 1).replace(" ", ""));
            }
        }
        if (timeInHMSM.contains("sec")) {
            secIndex = timeInHMSM.indexOf("sec");
            if (timeInHMSM.contains("min")) {
                sec = Integer.parseInt(timeInHMSM.substring(minIndex + 3, secIndex - 1).replace(" ", ""));
            } else {
                sec = Integer.parseInt(timeInHMSM.substring(0, secIndex - 1).replace(" ", ""));
            }
        }
        if (timeInHMSM.contains("ms")) {
            msIndex = timeInHMSM.indexOf("ms");
            if (timeInHMSM.contains("sec")) {
                ms = Integer.parseInt(timeInHMSM.substring(secIndex + 3, msIndex - 1).replace(" ", ""));
            } else {
                ms = Integer.parseInt(timeInHMSM.substring(0, msIndex - 1).replace(" ", ""));
            }
        }
        long hrss = (long) hrs * 60 * 60 * 1000;
        long minu = (long) min * 60 * 1000;
        long seco = (long) sec * 1000;
        long miliSec = ms;
        long timeInMilliSeconds = hrss + minu + seco + miliSec;
        LOG.info("In Millisecond : {}", timeInMilliSeconds);
        return timeInMilliSeconds;
    }
}