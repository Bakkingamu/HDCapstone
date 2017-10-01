/**
 * Created by Justin on 10/1/2017.
 */

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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VisionPackage {
    private List<AnnotateImageRequest> requestPackage;
    private BatchAnnotateImagesResponse response;
    private ImageAnnotatorClient vision;
    // Create request List and connection
    public VisionPackage(){
        requestPackage = new ArrayList<>();
        createConnection();
    }
    // one liner package creation constructor
    public VisionPackage(Image image, Type type) {
        this();
        addRequest(image,type);
    }
    // Creates Client
    private void createConnection(){
        try {
            vision = ImageAnnotatorClient.create();
        }
        catch (IOException e){
            System.out.println("IOException at createConnection"); //replace with logentries
        }
    }
    // add a request to the list
    public void addRequest(Image i, Type t){
        Feature feat = Feature.newBuilder().setType(t).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(i).build();
        requestPackage.add(request);
    }
    // send all requests to Google and stores response
    public  void send(){
        response = vision.batchAnnotateImages(requestPackage);
    }
    // response getter
    public BatchAnnotateImagesResponse getResponse(){
        return  response;
    }
    // wrapper
    public BatchAnnotateImagesResponse sendAndReceive(){
        send();
        return getResponse();
    }
    // easy buf image to Google Image format
    public static Image createImageUsingBufImage(BufferedImage bim){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            ImageIO.write(bim,"png",os);
        }catch (IOException e){
            System.out.println("Couldn't convert buffered image to png"); //replace with logentries
        }
        ByteString imageByteString = ByteString.copyFrom(os.toByteArray());
        Image img = Image.newBuilder().setContent(imageByteString).build();
        return img;
    }
    //print response (From google example)
    public void printResponse(BatchAnnotateImagesResponse response){
        List<AnnotateImageResponse> responses = response.getResponsesList();
        for (AnnotateImageResponse res : responses) {
            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                annotation.getAllFields().forEach((k, v) ->
                        System.out.printf("%s : %s\n", k, v.toString()));
            }
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                annotation.getAllFields().forEach((k, v) ->
                        System.out.printf("%s : %s\n", k, v.toString()));
            }
        }
    }
    // Test functions (add a request first)
    public void TestPrint(){
        printResponse(sendAndReceive());
    }
}
