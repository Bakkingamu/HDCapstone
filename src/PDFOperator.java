/**
 * Created by Justin on 10/1/2017.
 * Modified by Nicolas on 11/5/2017:overloaded RenderImage to indicate a page number, and added renderAll method.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "IOException at PDF Image conversion");
            System.exit(1);
            bim =  new BufferedImage(256, 256,BufferedImage.TYPE_INT_RGB);

        }
    return bim;
    }
    public BufferedImage renderImage(int page){
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim;
        try{
            bim = pdfRenderer.renderImageWithDPI(page,300, ImageType.RGB);
        }catch (IOException e){
            System.out.println("IOException at PDF Image conversion");
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "IOException at PDF Image conversion");
            System.exit(1);
            bim =  new BufferedImage(256, 256,BufferedImage.TYPE_INT_RGB);

        }
        return bim;
    }
    public List<BufferedImage> renderAll(){
        int pages = document.getNumberOfPages();
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<BufferedImage> bims = new ArrayList<BufferedImage>();
        for(int i = 0; i< pages; i++) {
            try {
                bims.add(i, pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB));
            } catch (IOException e) {
                System.out.println("IOException at PDF Image conversion");
                UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "IOException at PDF Image conversion");
                System.exit(1);
                bims.add(i, new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB));

            }
        }
        return bims;
    }
    public List<BufferedImage> renderBatch(int batchSize, int index){
        int pages = document.getNumberOfPages();
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<BufferedImage> bims = new ArrayList<BufferedImage>();
        for(int i = index; i< batchSize; i++) {
            try {
                bims.add(i, pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB));
            } catch (IOException e) {
                System.out.println("IOException at PDF Image conversion");
                UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "IOException at PDF Image conversion");
                System.exit(1);
                bims.add(i, new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB));

            }
        }
        return bims;
    }
    public boolean producerMatch(String producer){
        try{
            return getInfo().getProducer().equals(producer);

        }catch(NullPointerException e){
            return  false;
        }
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
