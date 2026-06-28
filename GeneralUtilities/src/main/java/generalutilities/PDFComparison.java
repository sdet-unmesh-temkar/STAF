package generalutilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import javax.imageio.ImageIO;

import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;

/**
 * This class perform different operation on pdf comparison.
 * This class consist of a method such as read pdf,capture image,compare pdf etc.
 */
public class PDFComparison {
    private static final Logger LOG = LoggerFactory.getLogger(PDFComparison.class);

    /**
     * Private constructor to prevent instantiation in other class.
     */
    private PDFComparison() {
    }

    /**
     * This method is used to read pdf.
     *
     * @param fileName     - name of the file to be read
     * @return  String     - text in string format
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static String readPDF(String fileName) throws IOException {
        var file = new File(fileName);
        var document = Loader.loadPDF(file);
        var pdfStripper = new PDFTextStripper();
        var text = pdfStripper.getText(document);
        LOG.info("Text: {}", text);
        document.close();
        return text;
    }

    /**
     * This method is used to capture the image in PDF file and stores the image in the location passed to the function.
     *
     * @param fileName     - filename to capture the image
     * @param destName     - path where the captured image stored
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static void imageCapture(String fileName, String destName) throws IOException {
        var pdfUtil = new PDFUtil();
        pdfUtil.setImageDestinationPath(destName);
        pdfUtil.extractImages(fileName, 1);
        LOG.info("Image extracted in the location {}", destName);
    }

    /**
     * This method is used to compare all the pages in the provided PDF files.
     *
     * @param file1        - path of the base pdf to compare all the pages in the provided PDF files
     * @param file2        - Path of pdf file to be compared
     * @param resultPath   - path of stored image if pdfs do not match
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static void comparePDF(String file1, String file2, String resultPath) throws IOException {
        var pdfUtil = new PDFUtil();
        pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfUtil.highlightPdfDifference(true);
        pdfUtil.compareAllPages(true);
        pdfUtil.setImageDestinationPath(resultPath);
        Boolean compResult = pdfUtil.compare(file1, file2);
        if (compResult.equals(true)) {
            LOG.info("Compared PDF's are a match. There won't be any result image generated.");
            Assert.assertTrue(compResult, "PDF's have matched.");
        } else {
            LOG.info("PDF's compared did not match perfectly. PDF comparison result is present at {}", resultPath);
            Assert.assertFalse(compResult, "PDF's did not match.");
        }
    }

    /**
     * This method is used to compare the image from the PDF file and the specified path.
     *
     * @param file         - Path of the pdf file with filename that has an image to compare
     * @param compFile     - Path of the image with filename which is stored in a specific location
     * @throws IOException - an exception occur when user inputs improper data into the program.
     */
    public static void checkPDFLogoImage(String file, String compFile) throws IOException {
        var document = Loader.loadPDF(new File(file));
        var list = document.getPages();
        var pdResources = list.get(1).getResources();
        for (COSName c : pdResources.getXObjectNames()) {
            PDXObject o = pdResources.getXObject(c);
            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
                BufferedImage actualImage = ((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage();
                BufferedImage expectedImage = ImageIO.read(new File(compFile));
                var imgDiff = new ImageDiffer();
                ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
                if (diff.hasDiff()) {
                    LOG.error("Vodafone logo is not present as expected.");
                    Assert.assertFalse(diff.hasDiff(), "Images are not Same");
                } else {
                    LOG.info("Vodafone logo is present as expected.");
                }
            } else {
                LOG.error("The PDF does not contain any image.");
            }
        }
    }
}