/**
 * Created by Justin on 9/12/2017.
 */
import com.google.auth.Credentials;
//log entries
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.util.Scanner;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
//pdf
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.io.File;
import org.apache.pdfbox.*;
// Google Cloud
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) throws Exception{
        PDFOperator op = new PDFOperator(PDDocument.load(new File("Complete HIA.pdf")));
        VisionPackage vp = new VisionPackage(VisionPackage.createImageUsingBufImage(op.renderImage()), Type.LABEL_DETECTION);
        vp.TestPrint();
        /*
        //Post to log entries
        Logger logger = LoggerFactory.getLogger("LE");
        logger.debug("Hello world.");

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);


        //pause system
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Pausing");
        int n = reader.nextInt();
        */
    }
}