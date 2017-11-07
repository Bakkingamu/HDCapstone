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
import java.io.IOException;
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
import org.apache.commons.cli.*;

public class main {
    public enum INPUT{
        //enum used instead of bool for readability
        DIR, FILE
    }
    public static void main(String[] args) throws IOException {
        // CLI Options
        Options options = new Options();
        //misc
        options.addOption("v", "verbose" , false, "Verbose - Log messages will be detailed"); //TODO-- Implement verbose
        //tests
        options.addOption("s", "source-test", false, "Source Test - Runs \"Source Test\" to check if a document was scanned in or digital"); //TODO-- Implement source test in Tests.java
        options.addOption("g", "signature-test", false, "Signature Test - Runs \"Signature Test\" to check if a document's signature box is filled"); //TODO-- Implement source test in Tests.java
        //input options
        options.addOption("i", "input-file", true, "Input File - Give this program a file to use");
        options.addOption("d", "directory", true, "Input Directory - Give this program a Directory to use"); //TODO-- Implement directory search
        options.addOption("r", "recursive", false, "Recursive - enable checking of sub-folders if using directory input"); //TODO-- Implement recursion
        //HELP ME
        options.addOption("h", "help", false, "show this message");

        // PARSER object
        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        try
        {
            //PARSE IT
            cmd = parser.parse(options, args);
            //--Option Handling--

            //HELP ME
            if(cmd.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }

            // i and d conflict (either use input file OR directory)
            if(cmd.hasOption("i") == cmd.hasOption("d")){
                System.out.println("ERROR: Please use one input method\n");
                printHelp(options);
                System.exit(1);
            }

            //check which input method
            INPUT inputMethod;
            String path;
            if(cmd.hasOption("i"))
            {
                inputMethod = INPUT.FILE;
                path = cmd.getOptionValue("i");
            }
            else
            {
                inputMethod = INPUT.DIR;
                path = cmd.getOptionValue("d");
            }

            //TESTS
            if(cmd.hasOption("s")){
                switch (inputMethod){
                    case DIR: Tests.SOURCE_TEST_DIR(path, cmd.hasOption("v")); break;
                    case FILE: Tests.SOURCE_TEST(path, cmd.hasOption("v")); break;
                    default: break;
                }
            }
            if(cmd.hasOption("g")){
                switch (inputMethod){
                    case DIR: Tests.SIGNATURE_TEST_DIR(path, cmd.hasOption("v")); break;
                    case FILE: Tests.SIGNATURE_TEST(path, cmd.hasOption("v")); break;
                    default: break;
                }
            }

        }
        catch(ParseException e)
        {
            printHelp(options);
            System.exit(1);
        }
        PDFOperator op = new PDFOperator(PDDocument.load(new File("Complete HIA.pdf")));
        VisionPackage vp = new VisionPackage(VisionPackage.createImageUsingBufImage(op.renderImage()), Type.LABEL_DETECTION);
        vp.TestPrint();
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

    }
   private static void printHelp(Options options){
        String header = "Run useful tests on PDFs\n\n";
        String footer = "\nKSU+HOMEDEPOT 2017";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PDFTest", header, options, footer, true);
    }
}