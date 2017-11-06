//THIS CLASS IS A TEST CLASS AND IS NOT TO BE USED IN THE PROTOTYPE.
import com.google.cloud.vision.v1.*;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sigTestMain {
    private int MAX_BLACK_VALUE = 382; // ((255 * 3) / 2) rounded down
    public static void main(String[] args) throws IOException {
        List<AnnotateImageResponse> responses = new ArrayList();
        PDDocument doc = PDDocument.load(new File("HS 299 Fencing Agreement.pdf"));
        PDFOperator op = new PDFOperator(doc);
        BufferedImage buf = op.renderImage(0);
        VisionPackage pack = new VisionPackage(VisionPackage.createImageUsingBufImage(buf), Feature.Type.DOCUMENT_TEXT_DETECTION);
        BatchAnnotateImagesResponse response = pack.sendAndReceive();
        responses = response.getResponsesList();
        List<BufferedImage> pSigLocations = new ArrayList();
        List<Vertex> v;
        BoundingPoly sigNearbyLocation;
        int i =0;
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
                return;
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
                                System.out.println("signature text found.");
                                System.out.println("at "+ sigNearbyLocation);
                                v = sigNearbyLocation.getVerticesList();
                                pSigLocations.add(
                                        buf.getSubimage(
                                                v.get(i).getX(),
                                                v.get(i).getY() - 75,
                                                500,
                                                62)
                                );
                                //Image cropping testing
                                try{
                                    File outputFile = new File(i+"saved.png");
                                    ImageIO.write(pSigLocations.get(i), "png", outputFile);
                                }catch (IOException e){
                                    System.out.println("shit.");
                                }
                                i++;
                            }
                        }//end word loop
                    }//end paragraph loop
                }//end block loop
            }//end page loop
        }//end responses loop
        //TODO call sigchecker near sigNearbyLocation
        for(BufferedImage bim: pSigLocations){
            if(CheckForSignature(bim)){
                System.out.println("\nhas sig");
            }else{
                System.out.println("\nno sig");
            }
        }
    }//end main

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
                    System.out.print(blackPixels+" ");
                    //if over 20% of the image is black pixels there is a high likelyhood of a signature.
                    if(blackPixels >= bim.getWidth() * bim.getHeight() / 5){ return true;}
                }
            }
        }//end for loop
        return false;
    }//end CheckForSignature
}
