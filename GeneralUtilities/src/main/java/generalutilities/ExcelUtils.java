package generalutilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class perform operation on excel sheets.
 * This class contain methods such as getColumnData, getCellData, writeToExcel etc.
 */
public class ExcelUtils {
    private XSSFSheet sheet = null;
    private XSSFRow row = null;
    String str1 = "/ExcelWorkbook/";
    String str2 = ".xlsx";
    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);


    /**
     * This method is used to read specific column from excel file.
     *
     * @param excelFile   - name of the excel file
     * @param sheetName   - read specific sheetName from excel file
     * @param columnIndex - read specific columnIndex from excel file
     * @param rowNum      - read specific rowNum from excel file
     * @return list       - columnData
     */
    public List<String> getColumnData(String excelFile, String sheetName, int columnIndex, int rowNum) {
        ArrayList<String> columnData = null;
        var file = new File(this.getClass().getResource(str1 + excelFile + str2).toString().replace("/", "\\").split(":", 2)[1]);
        try (var ios = new FileInputStream(file);
             var workbook = new XSSFWorkbook(ios)) {
            sheet = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = sheet.iterator();
            columnData = new ArrayList<>();
            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIterator.next();
                    if ((row.getRowNum() > rowNum) && (cell.getColumnIndex() == columnIndex)) {
                        //To filter column headings && To match column index
                        columnData.add(cell.getStringCellValue());
                    }
                }
            }
            LOG.trace("Column Data :{}", columnData);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return columnData;
    }

    /**
     * This method is used to read specific column from excel file.
     *
     * @param excelFile - name of the file to read specific column from excel
     * @param celNum    - to read specific column from excel file
     * @return String   - column data
     */
    public String getCellData(String excelFile, int celNum) {
        Cell actualData = null;
        var file = new File(this.getClass().getResource(str1 + excelFile + str2).toString().replace("/", "\\").split(":", 2)[1]);
        try (var ios = new FileInputStream(file);
             var workbook = new XSSFWorkbook(ios);) {
            sheet = workbook.getSheetAt(0);
            for (Row cells : sheet) {
                row = (XSSFRow) cells;
                actualData = row.getCell(celNum);
                LOG.debug("Excel Cell Data is {}", actualData);
            }
        } catch (Exception e) {
            LOG.error("Exception {}", e.getMessage());
        }
        if (actualData != null)
            return actualData.toString();
        else
            return null;
    }

    /**
     * This method is used to write data into excel file.
     *
     * @param excelFile  - file name in which we have to write data
     * @param actualData - actual data into the file
     */
    public void writeToExcel(String excelFile, String actualData) {
        var file = new File(this.getClass().getResource(str1 + excelFile + str2).toString().replace("/", "\\").split(":", 2)[1]);

        try (var ios = new FileInputStream(file);
             var workbook = new XSSFWorkbook(ios)) {
            sheet = workbook.getSheetAt(0);
            row = sheet.createRow(0);
            row.createCell(0).setCellValue(actualData);
            var outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {

            LOG.error("Exception {}",e.getMessage());
        }
    }
}
