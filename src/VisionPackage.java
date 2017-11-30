/**
 * Created by Justin on 10/1/2017.
 * Modified by Nicolas on 11/5/2017:overloaded constructor, addRequest, and CreateImageUsingBufImage  currently unused.
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
import java.nio.Buffer;
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
    //overloaded unused
    public VisionPackage(List<Image> images, Type type) {
        this();
        addRequest(images,type);
    }
    // Creates Client
    private void createConnection(){
        try {
            vision = ImageAnnotatorClient.create();
        }
        catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Error establishing connection with Google Cloud - make sure GOOGLE_APPLICATION_CREDENTIALS system variable is set");
            System.out.println("Error establishing connection with Google Cloud - make sure GOOGLE_APPLICATION_CREDENTIALS system variable is set");
            System.exit(1);
        }
    }
    // add a request to the list
    public void addRequest(Image i, Type t){
        Feature feat = Feature.newBuilder().setType(t).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(i).build();
        requestPackage.add(request);
    }
    //overloaded unused
    public void addRequest(List<Image> images, Type t){
        Feature feat = Feature.newBuilder().setType(t).build();
        for(Image image: images) {
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
            requestPackage.add(request);
        }
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
            ImageIO.write(bim,"jpg",os);
        }catch (IOException e){
            UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Error converting buffered image to png");
            System.out.println("Error converting buffered image to png");
            System.exit(1);
        }
        ByteString imageByteString = ByteString.copyFrom(os.toByteArray());
        Image img = Image.newBuilder().setContent(imageByteString).build();
        return img;
    }
    //Overloaded method unused
    public static List<Image> createImageUsingBufImage(List<BufferedImage> bims){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<Image> imgs = new ArrayList<>();
        for(BufferedImage buf: bims) {
            try {
                ImageIO.write(buf, "jpg", os);
            } catch (IOException e) {
                UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "Error converting buffered image to png");
                System.out.println("Error converting buffered image to png");
                System.exit(1);
            }
            ByteString imageByteString = ByteString.copyFrom(os.toByteArray());
            Image img = Image.newBuilder().setContent(imageByteString).build();
            imgs.add(img);
        }
        return imgs;
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
