import java.io.File;
import java.io.IOException;

public class parser {
    private String[] args;
    private String cmd;
    private int argsLen;

    parser() {
        cmd = new String();
    }

    public boolean parse(String input) throws IOException {
        input = this.reformat(input);
        input = input.trim().replaceAll(" +", " ");        //replace multiple spaces with only 1
        String[] temp = input.split(" ");
        int argsLen = temp.length - 1;
        this.cmd = temp[0];

        this.args = (argsLen <= 0) ? null : new String[argsLen];

        for (int i = 0; i < argsLen; ++i) {
            this.args[i] = replaceStar(temp[i + 1]);
        }
        this.argsLen = argsLen - 1;
        if (this.validateCommand() && this.checkNumberOfArgs())
            return true;
        else {
            this.args = null;
            this.cmd = null;
            return false;
        }
    }

    private String reformat(String input) {
        int inputLen = input.length();
        boolean firstQuote = false;
        for (int i = 0; i < inputLen; ++i) {
            if (input.charAt(i) == '\"') {
                firstQuote = true;
                input = input.substring(0, i) + input.substring(i + 1);
                --inputLen;
            } else if (input.charAt(i) == ' ' && firstQuote) {
                input = input.substring(0, i) + "\"*\"" + input.substring(i + 1); // replace space with "*" and remove quotes
                inputLen += 2;
            } else if (input.charAt(i) == '\"' && firstQuote) {
                firstQuote = false;
                input = input.substring(0, i) + input.substring(i + 1);
                --inputLen;
            } else if ((input.charAt(i) == '|')) {
                input = input.substring(0, i) + " " + input.charAt(i) + " " + input.substring(i + 1);
                ++i;
                inputLen += 2;
            } else if ((i + 1) <= (inputLen - 1) && (input.charAt(i) == '>') && (input.charAt(i + 1) == '>')) {
                input = input.substring(0, i) + " " + input.charAt(i) + input.charAt(i + 1) + " " + input.substring(i + 2);
                i += 2;
                inputLen += 2;
            } else if ((i + 1) <= (inputLen - 1) && (input.charAt(i) == '>') && (input.charAt(i + 1) != '>')) {
                input = input.substring(0, i) + " " + input.charAt(i) + " " + input.substring(i + 1);
                ++i;
                inputLen += 2;

            }
        }
        return input;
    }

    private String replaceStar(String arg) {
        int argLen = arg.length();
        boolean firstQuote = false;
        for (int i = 0; i < arg.length(); ++i) {
            if (arg.charAt(i) == '\"') {
                firstQuote = true;
                arg = arg.substring(0, i) + arg.substring(i + 1);
                --argLen;
                --i;
            } else if (arg.charAt(i) == '*' && firstQuote) {
                arg = arg.substring(0, i) + ' ' + arg.substring(i + 1);
            } else if (arg.charAt(i) == '\"' && firstQuote) {
                firstQuote = false;
                arg = arg.substring(0, i) + arg.substring(i + 1);
                --argLen;
            }
        }
        return arg;
    }

    private boolean validateCommand() throws IOException {
        String tempCmd = this.cmd;
        int argsLen = (args == null) ? 0 : args.length;
        boolean lastArgument = false;
        String operator = new String();
        for (int i = 0; i < argsLen; ++i) {
            if ((i == (argsLen - 1)) || (((i + 1) < argsLen) && (isOperator(this.args[i]))))
                lastArgument = true;
            if (isOperator(this.args[i])) {
                tempCmd = this.args[i];        // u mean i+1 ? if so >>,> doesn't require command after it only | does
                operator = this.args[i];
                continue;
            } else if (isCommand(this.args[i])) {
                if (operator.compareTo("|") == 0)
                    tempCmd = this.args[i];
                continue;
            }
            switch (tempCmd) {
                case "cat":
                    if (!isFile(this.args[i])) return false;
                    break;
                case "rmdir":
                    if (!isDirectory(this.args[i])) return false;
                    break;
                case "mv":
                    if (lastArgument) {
                        if (!isFile(this.args[i]) && !isDirectory(this.args[i]))
                            return false;
                    } else if (!isFile(this.args[i])) return false;
                    break;
                case "rm":
                    if (!isFile(this.args[i])) return false;
                    break;
                case "cp":
                    if (lastArgument) {
                        if (!isFile(this.args[i])) {
                            if (!isDirectory(this.args[i]))
                                return false;
                        }
                    } else if (!isFile(this.args[i])) return false;
                    break;
                case "cd":
                    if (!isDirectory(this.args[i])) return false;
                    break;
                case "mkdir":
                    if (!isDirectory(this.args[i])) return false;
                    break;
                case "args":
                    if (!isCommand(this.args[i])) ;
                case "date":
                    //TODO else if (!isDatePattern(this.args[i])) return false;
                case "ls":
                    if (!isDirectory(this.args[i])) return false;
                    break;
                case "more":
                case "help":
                case "pwd":
                case "clear":
                case "exit":
                    break;
                case ">":
                case ">>":
                    if (!isFile(this.args[i])) return false;
                    break;
                case "|":
                    if (!isCommand(this.args[i])) return false;
            }
            lastArgument = false;
        }
        return true;
    }

    private boolean isCommand(String cmd) {
        switch (cmd) {
            case "cat":
            case "rmdir":
            case "mv":
            case "rm":
            case "cp":
            case "cd":
            case "mkdir":
            case "args":
            case "date":
            case "ls":
            case "more":
            case "help":
            case "pwd":
            case "clear":
            case "exit":
                return true;
        }
        return false;
    }

    private boolean checkNumberOfArgs() {
        int counter = 0;
        boolean valid = true;
        String operator = this.cmd;
        if(args==null) {
        	return numberOfArgsValid(operator, 0);
        }
        for (int i = 0; i < this.argsLen; ++i) {
            counter++;
            if (isOperator(this.args[i]) || isCommand(this.args[i])) {
                counter--;
                if (i != 0 && !numberOfArgsValid(operator, counter)) {
                    System.out.println("few argument for " + "\'" + operator + "\'");
                    return false;
                }
                operator = this.args[i];
                counter = 0;
            } else if (i == (this.argsLen - 1)) {
                if (!numberOfArgsValid(operator, counter)) {
                    System.out.println("few argument for " + "\'" + operator + "\'");
                    return false;
                }
            }

        }
        return true;
    }

    private boolean numberOfArgsValid(String cmd, int counter) {
        switch (cmd) {
            case "cat":
                return counter <= Integer.MAX_VALUE;
            case "rmdir":
                return (counter <= Integer.MAX_VALUE && counter > 1);
            case "mv":
                return (counter <= Integer.MAX_VALUE && counter > 1);
            case "rm":
                return (counter <= Integer.MAX_VALUE && counter > 0);
            case "cp":
                return (counter <= Integer.MAX_VALUE && counter > 1);
            case "cd":
                return counter <= 1;
            case "mkdir":
                return counter == 1;
            case "args":
                return counter == 1;
            case "date":
                return counter <= 1;
            case "ls":
                return counter <= Integer.MAX_VALUE;
            case "more":
                return counter == 0;
            case "help":
                return counter == 0;
            case "pwd":
                return (counter <= Integer.MAX_VALUE);
            case "clear":
                return counter == 0;
            case "exit":
                return counter == 0;
            case ">":
                return counter == 1;
            case ">>":
                return counter == 1;
            case "|":
                return counter == 1;
        }
        return false;
    }

    public String getCmd() {
        return this.cmd;
    }

    public String[] getArguments() {
        if (args == null) return null;
        return args;
    }

    private boolean isDirectory(String directoryPath) {
        String path = System.getProperty("user.dir") + directoryPath;
        File directory = new File(directoryPath);
        if (directory.isDirectory()) return true;
        directory = new File(path);
        if (directory.isDirectory()) return true;
        return false;
    }

    private boolean isFile(String filePath) throws IOException {
        boolean createFile = false;
        int lastIndexOf = filePath.lastIndexOf(".");
        String path = System.getProperty("user.dir") + filePath;
        File file = new File(filePath);
        if (file.isFile()) return true;
        else if (!file.exists()) {
            createFile = file.createNewFile();
            if (createFile) {
                file.delete();
                return true;
            }
        }
        file = new File(path);
        if (file.isFile()) return true;
        else if (!file.exists()) {
            createFile = file.createNewFile();
            if (createFile) {
                file.delete();
                return true;
            }
        } else if (lastIndexOf != -1)            //can't understand e.e
            if (filePath.charAt(lastIndexOf - 1) == '*')
                return true;
        return false;
    }

    private boolean isOperator(String operator) {
        switch (operator) {
            case ">":
            case ">>":
            case "|":
                return true;
        }
        return false;
    }

//    public static void main(String[] args) throws IOException {
////        //System.out.println(System.getProperty("user.dir")); to get the current directory
//        parser p = new parser();
//        System.out.println(p.parse("cd C:\\Users"));
////        System.out.println();
//    }
}