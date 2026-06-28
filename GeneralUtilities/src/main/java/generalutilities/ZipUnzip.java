package generalutilities;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class contains a methods related to zip and unzip the file.
 */
public class ZipUnzip {
    private static final Logger LOG = LoggerFactory.getLogger(ZipUnzip.class);

    /**
     * Private constructor to prevent direct instantiation in other class.
     */
    private ZipUnzip() {
    }

    /**
     * This method compresses the single file to zip format.
     *
     * @param file        - file to be zipped
     * @param outputPath  - for zipped file output
     * @param zipFileName - name to be given to zipped file output
     */
    public static void zipFile(File file, String outputPath, String zipFileName) {
        if (!zipFileName.contains(".zip")) {
            zipFileName = zipFileName + ".zip";
        }
        try (FileOutputStream fos = new FileOutputStream(outputPath + zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file)) {
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            LOG.error("Exception {}", e.getMessage());
        }
    }

    /**
     * This method is used to zip file with password.
     *
     * @param filesPath         - file path of the zip file
     * @param zippedFolderName  - name of the folder which contain zip file
     * @param password          - password of the zip file
     * @return                  - zippedFolderName
     */
    public static String zipWithPassword(String filesPath, String zippedFolderName, String password) {
        filesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + filesPath;
        zippedFolderName = System.getProperty("user.dir") + File.separator + "target" + File.separator + zippedFolderName;
        var directoryPath = new File(filesPath);
        String[] contents = directoryPath.list();
        if (contents.length == 0) {
            return "files not found to zip";
        }
        ArrayList<File> files = new ArrayList<>();
        for (String filename : contents) {
            files.add(new File(filesPath + filename));
        }
        try (var zipFile = new ZipFile(zippedFolderName);) {
            zipFile.setPassword(password.toCharArray());
            var zp = new ZipParameters();
            zp.setCompressionMethod(CompressionMethod.DEFLATE);
            zp.setCompressionLevel(CompressionLevel.NORMAL);
            zp.setEncryptionMethod(EncryptionMethod.AES);
            zp.setEncryptFiles(true);
            zp.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipFile.addFiles(files, zp);
        } catch (ZipException e) {
            LOG.error("ZipException occured: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException occured: {}", e.getMessage());
        }
        return zippedFolderName;
    }

    /**
     * This method is used to unzip file with password.
     *
     * @param zipFilePath   - path of the unzipped file
     * @param destFilePath  - destination path of the unzipped file
     * @param password      - password of the unzipped file to open
     * @return              - destFilePath
     */
    public static String unzip(String zipFilePath, String destFilePath, String password) {
        String destPath = getFileName(zipFilePath);
        LOG.info("Destination {}", destPath);
        try (var zipFile = new ZipFile(zipFilePath);) {
            // If it is encrypted then provide password
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(destPath);
        } catch (IOException e) {
            LOG.error("IOException occurred: {}", e.getMessage());
        }
        return destFilePath;
    }

    /**
     * This method is used to get the file name from the zipped file by removing .zip extension
     *
     * @param filePath  - path of the file to get the file name
     * @return String   - name of the file
     */
    private static String getFileName(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("."));
    }
  }
