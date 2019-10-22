import java.io.IOException;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        parser p = new parser();
        terminal term = new terminal();
        while (true) {
            System.out.print("Enter the Command : ");
            String command = in.nextLine();
            boolean state = p.parse(command);
            if (state) {
                term.excute(p.getCmd(), p.getArguments());
            }
        }

    }

}