import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class parser {
    private String[] args;
    private String cmd;

    parser(){
        cmd = new String();
    }

    public boolean parse(String input){
        input = input.trim().replaceAll(" +", " ");
        String[] temp = input.split(" ");

        int argsLen = temp.length-1;
        this.cmd = temp[0];

        this.args = new String[(argsLen <= 0)? 1 : argsLen];

        for(int i = 0 ; i < argsLen ; ++i)
            this.args[i] = temp[i+1];

        return validateCmd() && validateArgs(argsLen);
    }

    private boolean validateArgs(int argsLen) {
        for(int i = 0 ; i < argsLen ; ++i){
            File tester = new File(args[i]);
            if(!tester.isDirectory() &&
                    !tester.isFile() &&
                    !(args[i].compareTo(">") == 0) &&
                    !(args[i].compareTo(">>") == 0) &&
                    !(args[i].compareTo("|") == 0) &&
                    !(args[i].compareTo("more") == 0)) {

                this.args = null;
                this.cmd = null;
                return false;
            }
        }
        return true;
    }

    private boolean validateCmd() {
        switch (this.cmd){
            case "cd":
            case "ls":
            case "cp":
            case "cat":
            case "more":
            case "mkdir":
            case "rmdir":
            case "mv":
            case "rm":
            case "args":
            case "date":
            case "help":
            case "pwd":
            case "clear":
                return true;
            default:
                this.args = null;
                this.cmd = null;
                return false;
        }
    }

    public String getCmd(){
        return this.cmd;
    }

    public String[] getArguments(){
        return this.args;
    }

    public static void main(String[] args) {
        //System.out.println(System.getProperty("user.dir")); to get the current directory
        parser p = new parser();
        System.out.println(p.parse(">"));
        System.out.println(p.getCmd());
    }
}
