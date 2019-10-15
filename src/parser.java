import java.io.File;

public class parser {
    private String[] args;
    private String cmd;

    parser() {
        cmd = new String();
    }

    public boolean parse(String input) {
        input = this.reformat(input);
        input = input.trim().replaceAll(" +", " ");
        String[] temp = input.split(" ");
        int argsLen = temp.length - 1;
        this.cmd = temp[0];

        this.args = (argsLen <= 0) ? null : new String[argsLen];
        for (int i = 0; i < argsLen; ++i) {
            this.args[i] = replaceStar(temp[i + 1]);
        }

        return this.validateCommand(0, 2, this.cmd); //this.validateCmd() && this.validateArgs(argsLen);
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
                input = input.substring(0, i) + "|*|" + input.substring(i + 1);
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
            if (arg.charAt(i) == '|') {
                firstQuote = true;
                arg = arg.substring(0, i) + arg.substring(i + 1);
                --argLen;
                --i;
            } else if (arg.charAt(i) == '*' && firstQuote) {
                arg = arg.substring(0, i) + ' ' + arg.substring(i + 1);
            } else if (arg.charAt(i) == '|' && firstQuote) {
                firstQuote = false;
                arg = arg.substring(0, i) + arg.substring(i + 1);
                --argLen;
            }
        }
        return arg;
    }

    private boolean simulateValidation() {
        int argsLen = this.args.length;
        String cmd = this.cmd;
        boolean command = true;
        int len = 0;
        int index = 0;
        int numberOfOperators = 0;
        for (int i = 0; i < argsLen; ++i) {
            ++len;
            if (isOperator(this.args[i])) {
                command = false;
                numberOfOperators++;
                len = 0;
                this.validateCommand(index, len - 1, cmd);
            }
            if (isCommand(this.args[i])) {
                cmd = this.args[i];
                command = true;
                index = i + 1;
                len = 0;
                if(numberOfOperators == 0)
            }
            if (i == argsLen - 1 %%command){
                this.validateCommand(index, len, cmd);
            }else if(i == argsLen-1 && !command)
                this.validateOperator(index, len ,operator);
        }
    }

    private boolean validateCommand(int index, int len, String cmd) {
        int argsLen = args.length;
        for (int i = index; i < index + len && i < argsLen; ++i) {
            switch (cmd) {
                case "cat":
                    System.out.println(this.args[i]);
                    if (!isFile(this.args[i])) return false;
                    break;
                case "rmdir":
                    if (len != 1) return false;
                    if (!isDirectory(this.args[i])) return false;
                    break;
                case "mv":
                    if (i == index + len - 1)
                        if (!isFile(this.args[i]) && !isDirectory(this.args[i]))
                            return false;
                        else if (!isFile(this.args[i])) return false;
                    break;
                case "rm":
                    if (!isFile(this.args[i])) return false;
                    break;
                case "cp":
                    if (len != 2) return false;
                    if (i == index)
                        if (!isFile(this.args[i])) return false;
                        else if (i == index + len - 1)
                            if (!isFile(this.args[i]))
                                if (!isDirectory(this.args[i]))
                                    return false;
                                else this.args[i] += '/' + this.args[i - 1];
                    break;
                case "cd":
                    if (len > 1) return false;
                    else if (!isDirectory(this.args[i])) return false;
                    break;
                case "mkdir":
                    if (len != 1) return false;
                    else if (!isDirectory(this.args[i])) return false;
                    break;
                case "args":
                    if (len != 1) return false;
                    else if (!command.isCommand(this.args[i])) ;
                case "date":
                    if (len > 1) return false;
                    //else if (!isDatePattern(this.args[i])) return false;
                case "ls":
                    if (len > 1) return false;
                    else if (!isDirectory(this.args[i])) return false;
                    break;
                case "more":
                case "help":
                case "pwd":
                case "clear":
                case "exit":
                    if (len > 0) return false;
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

//    private boolean validateOperator(int index, String operator) {
//        int argsLen = this.args.length;
//        int numberOfArgs = 0;
//        for (int i = index; i < argsLen; ++i) {
//            switch (operator) {
//                case ">":
//                case ">>":
//                    if (numberOfArgs > 1)
//                        return false;
//                    if (isFile(this.args[i]))
//                        ++numberOfArgs;
//                    break;
//                case "|":
//                    if (command.isCommand(this.args[i]))
//                        return true && validateCommand(i + 1, this.args[i]);
//            }
//        }
//        return (numberOfArgs == 1) ? true : false;
//    }

    public String getCmd() {
        return this.cmd;
    }

    public String[] getArguments() {
        return this.args;
    }

    private boolean isDirectory(String directoryPath) {
        String path = System.getProperty("user.dir") + directoryPath;
        File directory = new File(directoryPath);
        if (directory.isDirectory()) return true;
        directory = new File(path);
        if (directory.isDirectory()) return true;
        return false;
    }

    private boolean isFile(String filePath) {
        int lastIndexOf = filePath.lastIndexOf(".");
        String path = System.getProperty("user.dir") + filePath;
        File file = new File(filePath);
        if (file.isFile()) return true;
        file = new File(path);
        if (file.isFile()) return true;
        else if (lastIndexOf != -1) {
            if (filePath.charAt(lastIndexOf - 1) == '*') {
                return true;
            }
        }
        return false;
    }

    private boolean isOperator(String operator) {
        return false;
    }

    public static void main(String[] args) {
        //System.out.println(System.getProperty("user.dir")); to get the current directory
        parser p = new parser();
        System.out.println(p.parse("rm *.txt"));
        System.out.println();
    }
}
