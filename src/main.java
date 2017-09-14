/**
 * Created by Justin on 9/12/2017.
 */
/*
* Comment by Nick on 9/14/2017
* */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
public class main {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger("LE");
        logger.debug("Hello world.");

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a number: ");
        int n = reader.nextInt();

    }
}
//Hi everyone ~Tim