/**
 * Created by Justin on 9/12/2017.
 */
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Scanner;
import org.apache.commons.cli.*;
import java.util.Arrays;

public class main {
    public enum INPUT{
        //enum used instead of bool for readability
        DIR, FILE
    }
    public static void main(String[] args) throws IOException {
        UserDiagnostics.initLog();
        UserDiagnostics.logActivity(UserDiagnostics.Constants.APPLICATION_START, "\'Starting application with the settings( " + Arrays.toString(args) +" )\'");
        // CLI Options
        Options options = new Options();
        //misc
        options.addOption("v", "verbose" , false, "Verbose - Log messages will be detailed");
        //tests
        options.addOption("s", "source-test", false, "Source Test - Runs \"Source Test\" to check if a document was scanned in or digital");
        options.addOption("g", "signature-test", false, "Signature Test - Runs \"Signature Test\" to check if a document's signature box is filled");
        //input options
        options.addOption("i", "input-file", true, "Input File - Give this program a file to use");
        options.addOption("d", "directory", true, "Input Directory - Give this program a Directory to use");
        //HELP ME
        options.addOption("h", "help", false, "show this message");

        // PARSER object
        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        try
        {
            //PARSE IT
            cmd = parser.parse(options, args);
            //--Option Handling--

            //HELP ME
            if(cmd.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }

            // i and d conflict (either use input file OR directory)
            if(cmd.hasOption("i") == cmd.hasOption("d")){
                UserDiagnostics.logActivity(UserDiagnostics.Constants.FORCE_CRASH, "\'Application closing - One input method music be selected\'");
                System.out.println("ERROR, Please use one input method\n");
                printHelp(options);
                System.exit(1);
            }

            //check which input method
            INPUT inputMethod;
            String path;
            if(cmd.hasOption("i"))
            {
                inputMethod = INPUT.FILE;
                path = cmd.getOptionValue("i");
                if(cmd.hasOption('v')){
                    System.out.println("Using single file input");
                }

            }
            else
            {
                inputMethod = INPUT.DIR;
                path = cmd.getOptionValue("d");
                if(cmd.hasOption('v'))
                    System.out.println("Using directory input");
            }

            //TESTS
            if(cmd.hasOption("s")){
                switch (inputMethod){
                    case DIR: Tests.SOURCE_TEST_DIR(path, cmd.hasOption("v")); break;
                    case FILE: Tests.SOURCE_TEST(path, cmd.hasOption("v")); break;
                    default: break;
                }
            }
            if(cmd.hasOption("g")){
                switch (inputMethod){
                    case DIR: Tests.SIGNATURE_TEST_DIR(path, cmd.hasOption("v")); break;
                    case FILE: Tests.SIGNATURE_TEST(path, cmd.hasOption("v")); break;
                    default: break;
                }
            }
            if(cmd.hasOption('v'))
                UserDiagnostics.logActivity(UserDiagnostics.Constants.APPLICATION_TERMINATE, "\'Finished Processing\'");
        }
        catch(ParseException e)
        {
            printHelp(options);
            System.exit(1);
        }
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("\nPress Enter to Exit");
        int n = reader.nextInt();
    }
   private static void printHelp(Options options){
        String header = "Run useful tests on PDFs\n\n";
        String footer = "\nKSU+HOMEDEPOT 2017";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PDFTest", header, options, footer, true);
    }
}