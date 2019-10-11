import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class parser {
    private String[] args;
    private String cmd;

    parser(){
        cmd = new String();
    }

    public boolean parse(String input){
    	for(int i=0;i<input.length();++i) {
        	if(input.charAt(i)=='>') {
        		input=input.substring(0,i)+" > "+input.substring(i+1,input.length());
        		i++;
        	}
    	}
    	
        input = input.trim().replaceAll(" +", " ");
        String[] temp = input.split(" ");

        int argsLen = temp.length-1;
        this.cmd = temp[0];

        this.args = (argsLen <= 0)? null : new String[argsLen];

        for(int i = 0 ; i < argsLen ; ++i)
            this.args[i] = temp[i+1];

        return this.validateCmd() && this.validateArgs(argsLen);
    }

    private boolean validateCmd() {
        switch (this.cmd){
            case "cat":
            case "rmdir":
            case "mv":
            case "rm":
                return numberOfArquments() != 0;
            case "cp":
                return numberOfArquments() == 2;
            case "cd":
            case "mkdir":
            case "args":
                return numberOfArquments() == 1;
            case "date":
                return numberOfArquments() <= 1;
            case "ls":
            case "more":
            case "help":
            case "pwd":
            case "clear":
            case "exit":
                return numberOfArquments() >= 0;
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

    private boolean validateArgs(int argsLen) {
        for(int i = 0 ; i < argsLen ; ++i){
            File tester = new File(this.args[i]);
            if(!tester.isDirectory() &&
                    !tester.isFile() &&
                    !(this.args[i].compareTo(">") == 0) &&
                    !(this.args[i].compareTo(">>") == 0) &&
                    !(this.args[i].compareTo("|") == 0) &&
                    !(this.args[i].compareTo("more") == 0)) {

                this.args = null;
                this.cmd = null;
                return false;
            }
        }
        return true;
    }

    private int numberOfArquments(){
        int argsLen = this.args.length;
        int numberOfArgs = 0;
        for(int i = 0 ; i < argsLen ; ++i){
            if((this.args[i].compareTo(">") == 0) ||
                    (this.args[i].compareTo(">>") == 0) ||
                    (this.args[i].compareTo("|") == 0))
                break;

            ++numberOfArgs;
        }
        return numberOfArgs;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(System.getProperty("user.dir")); to get the current directory
//        parser p = new parser();
//        String aa[]= {"<abc",">","cde"};
//        boolean res =Stream.of(aa).anyMatch(x-> x=="<");
//        if(res==true) System.out.println("1");
//        else System.out.println("0");
//        
        String input=">a>s>A>s>";
        for(int i=0;i < input.length();i++) {
        	if(input.charAt(i)=='>') {
        		input=input.substring(0,i)+" > "+input.substring(i+1,input.length());
        		i++;
        	}
        	System.out.println(i);
        }
        System.out.println(input);
    }
}
