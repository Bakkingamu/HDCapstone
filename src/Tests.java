import com.google.cloud.vision.v1.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    //list of digital producers for the documents TODO--read from a config later
    public final static String digitalProducers[] = {"Smart Communications"};

    //---SOURCE TEST
    public static void SOURCE_TEST_DIR(String directoryname){
        SOURCE_TEST_DIR(directoryname, false);
    }
    public static void SOURCE_TEST_DIR(String directoryname, boolean verbose){
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path dir = Paths.get(currentPath.toString(), directoryname);
        if(verbose)
            System.out.println("Source testing directory - " + dir);
        try(Stream<Path> paths = Files.walk(dir)){
            List<String> files = paths.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for(String s : files){
                if(verbose)
                    System.out.println("Starting source test on " + s);
                SOURCE_TEST(s, verbose);
            }
        }catch (IOException e){

        }
        //TODO-- more logs
    }
    public static boolean SOURCE_TEST(String filename){return SOURCE_TEST(filename, false);}
    public static boolean SOURCE_TEST(String filename, boolean verbose){
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
            if(verbose)
                System.out.println("Digital confidence level - " + confidence + "%");
            doc.close();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }
    public static boolean CONTENT_TEST(PDFOperator pdf){return CONTENT_TEST(pdf, false);}
    public static boolean CONTENT_TEST(PDFOperator pdf, boolean verbose){
        String content = pdf.getText().replaceAll("\\s+",""); //remove white space
        if (verbose) {
            System.out.println(content.length() + " characters in document");
        }
        return (content.length() > 0);
    }
    public static boolean META_TEST(PDFOperator pdf){return META_TEST(pdf, false);}
    public static boolean META_TEST(PDFOperator pdf, boolean verbose){
        boolean hit = false;
        //checks the producer of the document against every producer in the list
        for(String s : digitalProducers)
        {
            if(pdf.producerMatch(s))
                hit = true;
        }
        //print result
        if(verbose){
            if(hit)
                System.out.println("Document has digital metadata");
            else
                System.out.println("Document has no digital metadata");
        }
        return hit;
        //TODO-- log entries
    }

    //---SIGNATURE
    public static void SIGNATURE_TEST_DIR(String directoryname){
        SIGNATURE_TEST_DIR(directoryname, false);
    }
    public static void SIGNATURE_TEST_DIR(String directoryname, boolean verbose){
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path dir = Paths.get(currentPath.toString(), directoryname);
        if(verbose)
            System.out.println("Source testing directory - " + dir);
        try(Stream<Path> paths = Files.walk(dir)){
            List<String> files = paths.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for(String s : files){
                if(verbose)
                    System.out.println("Starting source test on " + s);
                SIGNATURE_TEST(s, verbose);
            }
        }catch (IOException e){

        }
    }
    public static void SIGNATURE_TEST(String filename){
        try{
            SIGNATURE_TEST(filename, false);
        }catch (IOException e){

        }
    }
    public static void SIGNATURE_TEST(String filename, boolean verbose) throws IOException{
        List<BufferedImage> pSigLocations = new ArrayList<>();
        List<AnnotateImageResponse> responses = new ArrayList<>();
        BufferedImage bim;
        List<BufferedImage> bims;
        VisionPackage pack;
        BatchAnnotateImagesResponse response;

        int i=0;
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
                    System.out.println("\nSignature location #"+index+" likely has a signature.");
                }else{
                    System.out.println("\nSignature location #"+index+" likely has a signature.");
                }
                index++;
            }

        } catch (IOException e){
            //TODO : log error msg "file not found" or "could not load 'filename'"
        }

        return;
    }//end SIGNATURE_TEST
    private static List<BufferedImage> FindSignature(List<AnnotateImageResponse> responses, BufferedImage buf){
        List<BufferedImage> possibleSignatures= new ArrayList<>();
        List<Vertex> v;
        BoundingPoly sigNearbyLocation;
        int pageIndex = 0;
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
                                System.out.println("signature text found on page "+pageIndex+".");
                                System.out.println("at "+ sigNearbyLocation);
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
                                System.out.println("signature text found on page "+pageIndex+".");
                                System.out.println("at "+ sigNearbyLocation);
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
                    System.out.println("Counting sufficiently dark pixels...");
                    System.out.print(blackPixels+" ");
                    //if over 20% of the image is black pixels there is a high likelyhood of a signature.
                    if(blackPixels >= bim.getWidth() * bim.getHeight() / 5){ return true;}
                }
            }
        }//end for loop
        return false;
    }//end CheckForSignature
}
