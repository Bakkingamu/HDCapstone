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
        PDDocument doc = PDDocument.load(new File("image.pdf"));
        PDFOperator op = new PDFOperator(doc);
        BufferedImage buf = op.renderImage();
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
                                int height = v.get(3).getY() - v.get(0).getY();
                                int width = v.get(1).getX() - v.get(0).getX();
                                pSigLocations.add(searchForSigBox(buf, v.get(0), height, width));
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
        int imageArea = 3 * bim.getWidth();
        System.out.println("Beginning signature check.");
        for(int h = bim.getHeight()/3; h < bim.getHeight(); h += h){
            for(int w = 0; w < bim.getWidth(); w++){
                Color c = new Color (bim.getRGB(w,h));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red+green+blue <= MAX_BLACK_VALUE){
                    blackPixels++;
                    //if over 20% of the image is black pixels there is a high likelihood of a signature.
                    if(blackPixels >= bim.getWidth() / 10) return true;
                }

            }
        }//end for loop
        System.out.print(blackPixels +" black pixels in an Area of "+ imageArea+"Pixels");
        return false;
    }//end CheckForSignature
    private static BufferedImage searchForSigBox(BufferedImage bim, Vertex v, int h, int w){
        int MAX_BLACK_VALUE = 382; //((255 * 3) / 2) rounded down
        int NOT_WHITE_VALUE =615; //wild guess
        int red;
        int blue;
        int green;
        int colorValue = 0;
        int Xpos = v.getX();
        int Ypos = v.getY();
        Color currentPixel = new Color(bim.getRGB(Xpos, Ypos));
        red = currentPixel.getRed();
        blue = currentPixel.getBlue();
        green = currentPixel.getGreen();
        colorValue = red+green+blue;

            //Searching above signature until we hit NOT white space.(bottom of the box).==================bottom=======
            while (colorValue > NOT_WHITE_VALUE){
                Ypos--;
                currentPixel = new Color(bim.getRGB(Xpos, Ypos));
                red = currentPixel.getRed();
                blue = currentPixel.getBlue();
                green = currentPixel.getGreen();
                colorValue = red+green+blue;
            }
            //going until we reach the other side of the bottom border of the box.=========================bottom=======
            while (colorValue < NOT_WHITE_VALUE){
                Ypos--;
                currentPixel = new Color(bim.getRGB(Xpos, Ypos));
                red = currentPixel.getRed();
                blue = currentPixel.getBlue();
                green = currentPixel.getGreen();
                colorValue = red+green+blue;
            }

            /*Ypos++;
            currentPixel = new Color(bim.getRGB(Xpos, Ypos));
            red = currentPixel.getRed();
            blue = currentPixel.getBlue();
            green = currentPixel.getGreen();
            colorValue = red+green+blue;*/
            //Searching to the left until we hit white space.(bottom left corner of the box).=========BOTTOM LEFT CORNER
            /*while (colorValue < NOT_WHITE_VALUE){
                Xpos--;
                currentPixel = new Color(bim.getRGB(Xpos, Ypos));
                red = currentPixel.getRed();
                blue = currentPixel.getBlue();
                green = currentPixel.getGreen();
                colorValue = red+green+blue;
                offPixel = new Color(bim.getRGB(Xpos, Ypos-1));
                red = offPixel.getRed();
                blue = offPixel.getBlue();
                green = offPixel.getGreen();
                anchorColor = red+blue+green;
            }
            Xpos++;
            anchorX = Xpos;
            currentPixel = new Color(bim.getRGB(Xpos, Ypos));
            red = currentPixel.getRed();
            blue = currentPixel.getBlue();
            green = currentPixel.getGreen();
            colorValue = red+green+blue;
            offPixel = new Color(bim.getRGB(Xpos, Ypos-1));
            red = offPixel.getRed();
            blue = offPixel.getBlue();
            green = offPixel.getGreen();
            anchorColor = red+blue+green;
            //Searching up until we hit white space(top left corner of the box).=========================TOP LEFT CORNER
            while(colorValue == NOT_WHITE_VALUE){
                Ypos--;
                height++;
                currentPixel = new Color(bim.getRGB(Xpos, Ypos));
                red = currentPixel.getRed();
                blue = currentPixel.getBlue();
                green = currentPixel.getGreen();
                colorValue = red+green+blue;
                offPixel = new Color(bim.getRGB(Xpos+1, Ypos));
                red = offPixel.getRed();
                blue = offPixel.getBlue();
                green = offPixel.getGreen();
                anchorColor = red+blue+green;
            }
            Ypos++;
            anchorY = Ypos;
            currentPixel = new Color(bim.getRGB(Xpos, Ypos));
            red = currentPixel.getRed();
            blue = currentPixel.getBlue();
            green = currentPixel.getGreen();
            colorValue = red+green+blue;
            offPixel = new Color(bim.getRGB(Xpos+1, Ypos));
            red = offPixel.getRed();
            blue = offPixel.getBlue();
            green = offPixel.getGreen();
            anchorColor = red+blue+green;
            //Searching right until we hit white space(top right corner of the box).====================TOP RIGHT CORNER
            while(colorValue < NOT_WHITE_VALUE){
                Xpos++;
                width++;
                currentPixel = new Color(bim.getRGB(Xpos, Ypos));
                red = currentPixel.getRed();
                blue = currentPixel.getBlue();
                green = currentPixel.getGreen();
                colorValue = red+green+blue;
                offPixel = new Color(bim.getRGB(Xpos, Ypos+1));
                red = offPixel.getRed();
                blue = offPixel.getBlue();
                green = offPixel.getGreen();
                anchorColor = red+blue+green;
            }*/

        return bim.getSubimage(
                Xpos,
                Ypos-h,
                w,
                h);

    }
}
