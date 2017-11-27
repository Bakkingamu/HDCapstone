import com.google.cloud.vision.v1.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Justin on 10/4/2017.
 * Modified by Nicolas on 11/5/2017:added SIGNATURE_TEST and associated private methods.
 */
public class Tests {
    public static int subImageWidth = 500;
    public static int subImageHeight= 62;
    public static int subImageDisplacement = 75;

    //---SOURCE TEST
    public static void SOURCE_TEST_DIR(String directoryname){
        SOURCE_TEST_DIR(directoryname, false);
    }
    public static void SOURCE_TEST_DIR(String directoryname, boolean verbose){
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path dir = Paths.get(currentPath.toString(), directoryname);
        if(verbose){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Source testing directory [\'" + dir + "\']");
            System.out.println("Source testing directory [\'" + dir + "\']");

        }
        try(Stream<Path> paths = Files.walk(dir)){
            List<String> files = paths.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for(String s : files){
                SOURCE_TEST(s, verbose);
            }
        }catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Couldn't open directory [\'" + directoryname + "\']");
            System.out.println("Couldn't open directory [\'" + directoryname + "\']");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static boolean SOURCE_TEST(String filename){return SOURCE_TEST(filename, false);}
    public static boolean SOURCE_TEST(String filename, boolean verbose){
        UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Source Test - [\'" + filename + "\']");
        System.out.println("\n----[SOURCE TEST]---- \n[\'" + filename + "\']");
        float confidence = 0;
        PDFOperator pdf;
        try{
            PDDocument doc = PDDocument.load(new File(filename));
            pdf = new PDFOperator(doc);
            if(META_TEST(pdf,verbose))
                confidence += 100;
            if(CONTENT_TEST(pdf,verbose))
                confidence += 100;
            confidence /= 2f;
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "[\'" + filename + "\'] - Digital confidence level - " + confidence + "%");
            System.out.println("[\'" + filename + "\'] - Digital confidence level - " + confidence + "%");
            doc.close();
            return true;
        }
        catch (IOException e)
        {
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Couldn't open file [\'" + filename + "\']");
            System.out.println("Couldn't open file [\'" + filename + "\']");
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
    public static boolean CONTENT_TEST(PDFOperator pdf){return CONTENT_TEST(pdf, false);}
    public static boolean CONTENT_TEST(PDFOperator pdf, boolean verbose){
        String content = pdf.getText().replaceAll("\\s+",""); //remove white space
        if (verbose) {
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, content.length() + " characters in document");
            System.out.println(content.length() + " characters in document");
        }
        if(content.length() > 300){
            if(verbose){
                UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Content test: 100%");
                System.out.println("Content test - 100%");
            }
            return true;
        }
        else{
            double conf = content.length()/300.0;
            if(verbose){
                UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Content test: " + conf + "%");
                System.out.println("Content test - " + conf + "%");

            }
            return (conf > .5);
        }
    }
    public static boolean META_TEST(PDFOperator pdf){return META_TEST(pdf, false);}
    public static boolean META_TEST(PDFOperator pdf, boolean verbose){
        boolean hit = false;
        try{
            BufferedReader reader = new BufferedReader(new FileReader("cfg/producers.txt"));
            String line = reader.readLine();
            while(line != null){
                if(pdf.producerMatch(line)){
                    hit = true;
                }
                line = reader.readLine();
            }
            if(verbose){
                if(hit){
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "MetaData test: 100%");
                    System.out.println("MetaData test - 100%");
                }
                else{
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "MetaData test: 0%");
                    System.out.println("MetaData test - 0%");
                }
            }
        }
        catch (FileNotFoundException e)
        {
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Producer configuration file missing: cfg/producers.txt");
            System.out.println("Producer configuration file missing: cfg/producers.txt");
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e)
        {
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "IOException at META_TEST");
            System.out.println("IOException at META_TEST");
            e.printStackTrace();
            System.exit(1);

        }
        return hit;
    }

    //---SIGNATURE
    public static void SIGNATURE_TEST_DIR(String directoryname){
        SIGNATURE_TEST_DIR(directoryname, false);
    }
    public static void SIGNATURE_TEST_DIR(String directoryname, boolean verbose){
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path dir = Paths.get(currentPath.toString(), directoryname);
        if(verbose){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Signature testing directory [\'" + dir + "\']");
            System.out.println("Signature  testing directory [\'" + dir + "\']");

        }
        try(Stream<Path> paths = Files.walk(dir)){
            List<String> files = paths.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for(String s : files){
                SIGNATURE_TEST(s, verbose);
            }
        }catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Couldn't open directory [\'" + directoryname + "\']");
            System.out.println("Couldn't open directory [\'" + directoryname + "\']");
            e.printStackTrace();
        }
    }
    public static void SIGNATURE_TEST(String filename){
        try{
            SIGNATURE_TEST(filename, false);
        }catch (IOException e){

        }
    }
    public static void SIGNATURE_TEST(String filename, boolean verbose) throws IOException{
        if(verbose){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Signature Test -  [\'" + filename + "\']");
            System.out.println("\n----[SIGNATURE TEST]---- \n[\'" + filename + "\']");
        }
        List<BufferedImage> pSigLocations = new ArrayList<>();
        List<AnnotateImageResponse> responses = new ArrayList<>();
        BufferedImage bim;
        List<BufferedImage> bims;
        VisionPackage pack;
        BatchAnnotateImagesResponse response;

        try {
            PDDocument doc = PDDocument.load(new File(filename));
            PDFOperator op = new PDFOperator(doc);

            if(doc.getNumberOfPages() == 1 ) {
                bim = op.renderImage();
                pack = new VisionPackage(VisionPackage.createImageUsingBufImage(bim), Feature.Type.DOCUMENT_TEXT_DETECTION);
                response = pack.sendAndReceive();
                responses = response.getResponsesList();
                pSigLocations = FindSignature(responses, bim);
            } else {
                bims = op.renderAll();
                for(BufferedImage image: bims) {
                    pack = new VisionPackage(VisionPackage.createImageUsingBufImage(image), Feature.Type.DOCUMENT_TEXT_DETECTION);
                    response = pack.sendAndReceive();
                    responses.addAll(response.getResponsesList());
                }
                pSigLocations = FindSignature(responses, bims);
            }

            int index=0;
            for(BufferedImage image: pSigLocations){
                if(CheckForSignature(image)){
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "\nSignature location #"+index+" likely has a signature.");
                    System.out.println("\nSignature location #"+index+" likely has a signature.");
                }else{
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "\nSignature location #"+index+" likely does not have a signature.");
                    System.out.println("\nSignature location #"+index+" likely does not have a signature.");
                }
                index++;
            }

        } catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Couldn't open file [\'" + filename + "\']");
            System.out.println("Couldn't open file [\'" + filename + "\']");
            e.printStackTrace();
            System.exit(1);
        }

        return;
    }//end SIGNATURE_TEST
    public static void BATCH_SIGNATURE_TEST(String filename, boolean verbose) throws IOException{
        if(verbose){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "Signature Test -  [\'" + filename + "\']");
            System.out.println("\n----[SIGNATURE TEST]---- \n[\'" + filename + "\']");
        }
        List<BufferedImage> pSigLocations = new ArrayList<>();
        List<AnnotateImageResponse> responses = new ArrayList<>();
        BufferedImage bim;
        List<BufferedImage> bims;
        VisionPackage pack;
        BatchAnnotateImagesResponse response;
        List<AnnotateImageRequest> requests;

        try {
            PDDocument doc = PDDocument.load(new File(filename));
            PDFOperator op = new PDFOperator(doc);

            if(doc.getNumberOfPages() == 1 ) {
                bim = op.renderImage();
                pack = new VisionPackage(VisionPackage.createImageUsingBufImage(bim), Feature.Type.DOCUMENT_TEXT_DETECTION);
                response = pack.sendAndReceive();
                responses = response.getResponsesList();
                pSigLocations = FindSignature(responses, bim);
            } else {
                bims = op.renderAll();
                for(BufferedImage image: bims) {
                    Image img = VisionPackage.createImageUsingBufImage(image);
                    AnnotateImageRequest request =
                            AnnotateImageRequest.newBuilder().addFeatures(Feature.Type.DOCUMENT_TEXT_DETECTION).setImage(img).build();
                    requests.add(request);
                    /*pack = new VisionPackage(VisionPackage.createImageUsingBufImage(image), Feature.Type.DOCUMENT_TEXT_DETECTION);
                    response = pack.sendAndReceive();
                    responses.addAll(response.getResponsesList());*/
                }
                try (ImageAnnotatorClient client = ImageAnnotatorClient.create()){
                    response = client.batchAnnotateImages(requests);
                    responses = response.getResponsesList();
                    client.close();
                }
                pSigLocations = FindSignature(responses, bims);
            }

            int index=0;
            for(BufferedImage image: pSigLocations){
                if(CheckForSignature(image)){
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "\nSignature location #"+index+" likely has a signature.");
                    System.out.println("\nSignature location #"+index+" likely has a signature.");
                }else{
                    UserDiagnostics.logActivity(UserDiagnostics.Constants.INTERESTING_EVENT, "\nSignature location #"+index+" likely does not have a signature.");
                    System.out.println("\nSignature location #"+index+" likely does not have a signature.");
                }
                index++;
            }

        } catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Couldn't open file [\'" + filename + "\']");
            System.out.println("Couldn't open file [\'" + filename + "\']");
            e.printStackTrace();
            System.exit(1);
        }

        return;
    }//end SIGNATURE_TEST
    private static List<BufferedImage> FindSignature(List<AnnotateImageResponse> responses, BufferedImage buf){
        List<BufferedImage> possibleSignatures= new ArrayList<>();
        List<Vertex> v;
        BoundingPoly sigNearbyLocation;
        int pageIndex = 0;
        int sigIndex = 0;
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
            }
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page: annotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    for (Paragraph para : block.getParagraphsList()) {
                        for (Word word: para.getWordsList()) {
                            String wordText = "";
                            for (Symbol symbol: word.getSymbolsList()) {
                                wordText = wordText + symbol.getText();
                            }//symbol
                            if(wordText.contains("Signature")){
                                sigIndex++;
                                sigNearbyLocation = word.getBoundingBox();
                                System.out.println("signature# "+sigIndex+" text found on page "+pageIndex+".");
                                v = sigNearbyLocation.getVerticesList();
                                possibleSignatures.add(
                                        buf.getSubimage(
                                                v.get(0).getX(),
                                                v.get(0).getY() - subImageDisplacement,
                                                subImageWidth,
                                                subImageHeight)
                                );
                                v.clear();
                            }
                        }//end word loop
                    }//end paragraph loop
                }//end block loop
                pageIndex++;
            }//end page loop
        }//end responses loop
        return possibleSignatures;
    }//end findSignature(single image)

    private static List<BufferedImage> FindSignature(List<AnnotateImageResponse> responses, List<BufferedImage> bufs){
        List<BufferedImage> possibleSignatures= new ArrayList<>();
        List<Vertex> v;
        BoundingPoly sigNearbyLocation;
        int pageIndex = 0;
        int sigIndex = 0;
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
            }
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page: annotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    for (Paragraph para : block.getParagraphsList()) {
                        for (Word word: para.getWordsList()) {
                            String wordText = "";
                            for (Symbol symbol: word.getSymbolsList()) {
                                wordText = wordText + symbol.getText();
                            }//symbol
                            if(wordText.contains("Signature")){
                                sigNearbyLocation = word.getBoundingBox();
                                System.out.println("signature# "+sigIndex+" text found on page "+pageIndex+".");
                                v = sigNearbyLocation.getVerticesList();
                                possibleSignatures.add(
                                        bufs.get(pageIndex).getSubimage(
                                                v.get(0).getX(),
                                                v.get(0).getY() - subImageDisplacement,
                                                subImageWidth,
                                                subImageHeight)
                                );
                            }
                        }//end word loop
                    }//end paragraph loop
                }//end block loop
                pageIndex++;
            }//end page loop
        }//end responses loop
        return possibleSignatures;
    }//end findSignature(multiple image)
    private static Boolean CheckForSignature(BufferedImage bim){
        int MAX_BLACK_VALUE = 382; //((255 * 3) / 2) rounded down
        int blackPixels= 0;
        System.out.println("Beginning signature check.");
        for(int h = 0; h < bim.getHeight(); h++){
            for(int w = 0; w < bim.getWidth(); w++){
                Color c = new Color (bim.getRGB(w,h));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red+green+blue <= MAX_BLACK_VALUE){
                    blackPixels++;
                    //if over 20% of the image is black pixels there is a high likelyhood of a signature.
                    if(blackPixels >= bim.getWidth() * bim.getHeight() / 5){
                        //System.out.println(blackPixels/(bim.getWidth()*bim.getHeight()/5)+"% of subimage is composed of black pixels");
                        return true;}
                }
            }
        }//end for loop
        return false;
    }//end CheckForSignature
}
