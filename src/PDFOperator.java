/**
 * Created by Justin on 10/1/2017.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFOperator {
    private PDDocument document;
    public PDFOperator(PDDocument document){
        this.document = document;
    }
    // Create image from pdf (only first page currently, change later)
    public BufferedImage renderImage(){
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim;
        try{
            bim = pdfRenderer.renderImageWithDPI(0,300, ImageType.RGB);
        }catch (IOException e){
            System.out.println("IOException at PDF Image conversion");
            bim =  new BufferedImage(256, 256,BufferedImage.TYPE_INT_RGB);

        }
    return bim;
    }
    public boolean producerMatch(String producer){
        return getInfo().getProducer().equals(producer);
    }
    public boolean hasText(){
        String content = getText();
        if(content.length() > 0)
            return true;
        else
            return false;
    }
    public String getText(){
        try{
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }catch (IOException e){
            return null;
        }
    }
    //some getters
    public PDDocument getDocument(){
        return document;
    }
    public PDDocumentInformation getInfo(){
        return document.getDocumentInformation();
    }
}
