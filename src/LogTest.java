import java.util.Scanner;
/**
 * Created by Justin on 11/18/2017.
 */
public class LogTest {
    public static void main(String args[]){
        UserDiagnostics.getInstance().logActivity(UserDiagnostics.Constants.APPLICATION_START, "Testing1");
        UserDiagnostics.logActivity(UserDiagnostics.Constants.APPLICATION_START, "Testing");
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Pausing");
        int n = reader.nextInt();
    }
}
