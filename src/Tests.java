import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.io.IOException;

/**
 * Created by Justin on 10/4/2017.
 */
public class Tests {
    //list of digital producers for the documents ?perhaps read from a config later?
    public final static String digitalProducers[] = {"Smart Communications"};
    //optional overload requiring no verbose input
    public static void SOURCE_TEST(String filename){SOURCE_TEST(filename, false);}
    public static void SOURCE_TEST(String filename, boolean verbose){
        try
        {
            PDFOperator pdfOperator = new PDFOperator(PDDocument.load(new File(filename)));
            boolean hit = false;
            //checks the producer of the document against every producer in the list
            for(String s : digitalProducers)
            {
                if(pdfOperator.producerMatch(s))
                    hit = true;
            }
            //print result
            if(hit)
                System.out.println(filename + " - Document is digital");
            else
                System.out.println(filename + " - Document is scanned");

        }
        catch(IOException e)
        {
            //log
        }
        //TODO-- finish
    }
    //optional overload requiring no verbose input
    public static void SOURCE_TEST_DIR(String directoryname){
        SOURCE_TEST_DIR(directoryname, false);
    }
    public static void SOURCE_TEST_DIR(String directoryname, boolean verbose){
        //TODO-- implement
    }
}
