import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Justin on 10/4/2017.
 */
public class Tests {
    //list of digital producers for the documents ?perhaps read from a config later?
    public final static String digitalProducers[] = {"Smart Communications"};
    //optional overload requiring no verbose input
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

    //META TEST - Checks metadata of document against list
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
    //optional overload requiring no verbose input
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
}
