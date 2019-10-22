import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Vector;


public class terminal {
    private String cmd = null;
    private Vector<String> args = new Vector<String>();
    private static String root = System.getProperty("user.dir");
    private static String directory = root;

    terminal() {

    }

    public void excute(String command, String[] arguments) throws IOException {
        this.cmd = command;
        String gar[] = arguments;
        if (gar != null) for (String i : gar) args.add(i);

        Vector<String> lastOut = new Vector<String>();

        int flag = -1;

        if (args != null)
            for (int i = 0; i < args.size(); ++i) {
                if (args.get(i).equals(">>") || args.get(i).equals(">") || args.get(i).equals("|")) {
                    flag = i;
                    break;
                }
            }

        if (flag != -1) {        //get the output of the first command save it in lastOut
            if (flag != 0) {
                Vector<String> tmp = new Vector<String>();
                for (int i = 0; i < flag; ++i) tmp.add(args.get(i));
                lastOut = command(cmd, tmp);
            } else {
                Vector<String> tmp = null;
                lastOut = command(cmd, tmp);
            }
        } else {        // no operators found
            if (args == null) {
                Vector<String> call = null;
                lastOut = command(cmd, call);
            } else lastOut = command(cmd, args);

        }

        if (args != null)
            for (int i = 0; i < args.size(); ++i) {
                if (args.get(i).equals(">") || args.get(i).equals(">>")) {
                    if (i < args.size() - 3) {// check if it could be more operators
                        if (args.get(i + 2).equals(">") || args.get(i + 2).equals(">>")) {//check if there more operators
                            mkFile(args.get(i + 1));
                        } else {    //it was the last operator write the output in lastOut in the file
                            mkFile(args.get(i + 1));
                            String fileName = args.get(i + 1);
                            File check = new File(fileName);

                            if (!check.isDirectory()) {
                                fileName = pwd().get(0) + '\\' + fileName;
                            }

                            if (lastOut != null) writeToFile(lastOut, fileName, args.get(i).equals(">>"));
                            lastOut = null;
                        }
                    } else { //last operator so save output in file
                        mkFile(args.get(i + 1));
                        String fileName = args.get(i + 1);
                        File check = new File(fileName);

                        if (!check.isDirectory()) {
                            fileName = pwd().get(0) + '\\' + fileName;
                        }
                        if (lastOut != null) writeToFile(lastOut, fileName, args.get(i).equals(">>"));
                        lastOut = null;
                    }
                } else if (args.get(i).equals("|")) { //perform the command with its input = last output
                    lastOut = command(args.get(i + 1), lastOut);

                }

            }
        if (lastOut != null)
            for (int i = 0; i < lastOut.size(); ++i) System.out.println(lastOut.get(i).toString());
    }

    private Vector<String> command(String com, Vector<String> arg) throws IOException {

        int argsLen = (arg == null) ? 0 : arg.size();


        if (arg == null || arg.size() == 0) {
            switch (com) {
                case "rm":
                case "mkdir":
                case "cat":
                case "rmdir":

                    return null;
            }
        }
        if (argsLen == 1 || arg == null || argsLen == 0)
            switch (com) {    //multiple argument commands
                case "cp":
                case "mv":

                    return null;
            }


        switch (com) {
            case "exit":
                exit();
                return null;
            case "more":
                return more(arg);
            case "rm":
                return rm(arg);
            case "mkdir":
                return mkdir(arg);
            case "cat":
                return cat(arg);
            case "rmdir":
                return rmdir(arg);
            case "pwd":
                return pwd();
            case "cd":
                return cd(arg);
            case "date":
                return date(arg);
            case "help":
                help();
                return null;
            case "arg":
                Args(arg);
                return null;
            case "cls":
                cls();
                return null;
            case "ls":
                return ls(arg);
            case "cp":
                return cp(arg);
            case "mv":
                return mv(arg);

        }
        return null;
    }


    private Vector<String> cp(Vector<String> paths) throws IOException {   // cp stands for copy file channel faster than java stream

        if (paths.size() > 2) {
            File check = new File(paths.get(paths.size() - 1));
            if (!check.isDirectory()) return null;
            else if (!check.exists()) {
                mkdir(paths.get(paths.size() - 1));
            }

            for (int i = 0; i < paths.size() - 1; ++i) {
                String name = "";

                name = getFile(paths.get(i));        //get file name only and append it to the directory
                name = paths.get(paths.size() - 1) + '\\' + name;


                File make = new File(name);
                make.createNewFile();

                FileChannel source = new FileInputStream(paths.get(i)).getChannel();
                FileChannel dest = new FileOutputStream(name).getChannel();

                dest.transferFrom(source, 0, source.size());

                source.close();
                dest.close();
            }
        } else {

            File file = new File(paths.get(0));
            Scanner sc = new Scanner(file);
            System.out.println("jkegwkgnwkegjweng" + paths.get(0));
            File check = new File(paths.get(1));

            if (check.isDirectory()) {
                String newPath = paths.get(1) + '\\' + getFile(paths.get(0));
                mkFile(newPath);
                paths.set(1, newPath);    //set in vector
            } else if (!check.isFile()) { //check if not created
                check.createNewFile();
            }

            System.out.println(paths.get(1));
            BufferedWriter out = new BufferedWriter(new FileWriter(paths.get(1), false)); //true for append
            while (sc.hasNextLine()) {
                String in = sc.nextLine();
                System.out.println(in);
                out.write(in);
                out.newLine();
            }
            sc.close();
            out.close();
        }

        return null;

    }

    private Vector<String> cd(Vector<String> Dest) {   //cd stands for change directory
        if (Dest == null || Dest.size() == 0)
            return cd();
        String val = Dest.get(0);
        File check = new File(val);
        if (check.isDirectory())
            directory = val;

        return null;
    }

    private Vector<String> cd() {
        directory = root;
        return null;
    }

    private Vector<String> ls(Vector<String> arg) {
        File folders[];
        Vector<String> tmp = pwd();
        Vector<String> names = new Vector<String>();
        if (arg != null && arg.size() != 0) {
            Vector<String> newLoc = new Vector<String>();
            newLoc.add("");
            for (int i = 0; i < arg.size(); ++i) {
                newLoc.set(0, arg.get(i));
                names.add("path -> " + newLoc.get(0));
                cd(newLoc);

                String loc = pwd().get(0);
                folders = new File(loc).listFiles();
                if (folders != null && folders.length > 0)
                    for (int j = 0; j < folders.length; ++j) {
                        names.add(folders[j].toString());
                    }
                names.add("\n\n");

            }
        } else {
            String loc = pwd().get(0);
            folders = new File(loc).listFiles();
            if (folders != null && folders.length > 0)
                for (int j = 0; j < folders.length; ++j) {
                    names.add(folders[j].toString());
                }

        }
        cd(tmp);
        return names;
    }

    private Vector<String> pwd() {
        Vector<String> tmp = new Vector<String>();
        tmp.add(directory);
        return tmp;
    }


    private Vector<String> mv(Vector<String> paths) throws IOException {    //move source to dest
        for (int i = 0; i < paths.size(); ++i) {
            if (paths.get(i).lastIndexOf('\\') == -1) paths.set(i, pwd().get(0) + '\\' + paths.get(i));
        }
        cp(paths);
        Vector<String> tmp = new Vector<String>();
        for (int i = 0; i < paths.size() - 1; ++i) {
            tmp.add(paths.get(i));
        }
        rm(tmp);
        return null;
    }

    private Vector<String> mkdir(Vector<String> args) {
        int argsLen = args.size();
        for (int i = 0; i < argsLen; ++i) {
            File directory = new File(args.get(i));
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.out.println("A file name can not contain any of the following characters \n" + "\\ / : * < > |");
                    break;
                }
            } else if (directory.isFile()) {
                System.out.println('\'' + directory.getPath() + '\'' + " is not a Directory");
                break;
            } else if (directory.exists()) {
                System.out.println('\'' + directory.getPath() + '\'' + " already exists");
                break;
            }
        }
        return null;
    }

    private Vector<String> mkFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) file.createNewFile();

        return null;
    }

    private Vector<String> mkdir(String args) {

        File directory = new File(args);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                System.out.println("A file name can not contain any of the following characters \n" + "\\ / : * < > |");
            }
        } else if (directory.isFile()) {
            System.out.println('\'' + directory.getPath() + '\'' + " is not a Directory");

        } else if (directory.exists()) {
            System.out.println('\'' + directory.getPath() + '\'' + " already exists");

        }
        return null;
    }

    private Vector<String> rmdir(Vector<String> args) {
        int argsLen = args.size();
        for (int i = 0; i < argsLen; ++i) {
            File Directory = new File(args.get(i));
            if (Directory.listFiles().length > 0)
                System.out.println('\'' + Directory.getPath() + '\'' + " not empty");
            else
                Directory.delete();
        }
        return null;
    }

    private Vector<String> rm(Vector<String> data) {
        for (int j = 0; j < data.size(); ++j) {

            String source = data.get(j);
            int dot = source.lastIndexOf('.');
            int pathStartsAt = source.lastIndexOf('\\');
            if (source.charAt(dot + 1) == '*') {

                Vector<String> toRemove = new Vector<String>();

                if (pathStartsAt != -1) {    //directory
                    String compare = source.substring(pathStartsAt, dot);
                    Vector<String> dir = new Vector<String>();
                    dir.add(getFileDirectory(source));
                    Vector<String> files = ls(dir);
                    for (String i : files) {
                        if (getFileName(i).equals(compare))
                            toRemove.add(i);
                    }
                } else {        //file
                    String compare = source.substring(0, dot);
                    Vector<String> files = ls(null);
                    for (String i : files) {
                        if (getFileName(i).equals(compare))
                            toRemove.add(i);
                    }
                }
                rm(toRemove);
            } else if (source.charAt(dot - 1) == '*') {
                Vector<String> toRemove = new Vector<String>();

                if (pathStartsAt != -1) {    //directory
                    String compare = source.substring(dot + 1);
                    Vector<String> dir = new Vector<String>();
                    dir.add(getFileDirectory(source));
                    Vector<String> files = ls(dir);
                    for (String i : files) {
                        if (getFileExtension(i).equals(compare))
                            toRemove.add(i);
                    }
                } else {        //file
                    String compare = source.substring(dot + 1);
                    Vector<String> files = ls(null);
                    for (String i : files) {
                        if (getFileExtension(i).equals(compare))
                            toRemove.add(i);
                    }
                }
                rm(toRemove);

            }

            File args = new File(source);
            if (args == null) return null;
            args.delete();
        }
        return null;
    }

    private Vector<String> more(Vector<String> in) throws FileNotFoundException {
        Scanner again = new Scanner(System.in);
        for (int i = 0; i < in.size(); ++i) {
            Vector<String> out = new Vector<String>();
            File file = new File(in.get(i));
            if (file.isFile()) {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    String data = sc.nextLine();
                    out.add(data);
                }
                System.out.println((in.get(i) + "\n"));
                for (int j = 0, k = 10; j < out.size(); ++j) {
                    if (k == 0) {
                        System.out.println("enter more to continue or any other character to close ");
                        String choise = again.nextLine();
                        if (choise.equals("more")) k = 10;
                        else return null;
                    }
                    System.out.println(out.get(j));
                    k--;
                }
            }

        }
        return null;
    }

    private void exit() {
        System.exit(0);
    }

    private Vector<String> cls() {
        for (int i = 0; i < 15; ++i) System.out.println("");
        return null;
    }

    private Vector<String> date() {
        Vector<String> Date = new Vector<String>();
        Date.add(java.time.LocalDate.now().toString());
        return Date;
    }

    private Vector<String> date(Vector<String> shape) {//month,day,hour,mn,first 2 digits if the year,last 2,seconds
        if (shape == null || shape.size() == 0)
            return date();
        String format = shape.get(0);
        Calendar cal = Calendar.getInstance();
        int month = Integer.parseInt(format.substring(0, 2));
        int day = Integer.parseInt(format.substring(2, 4));
        int hour = Integer.parseInt(format.substring(4, 6));
        int mn = Integer.parseInt(format.substring(6, 8));
        int year = Integer.parseInt(format.substring(8, 12));
        int sec = Integer.parseInt(format.substring(12, 14));

        cal.set(year, month, day, hour, mn, sec);

        return null;
    }

    private Vector<String> cat(Vector<String> paths) throws FileNotFoundException {
        Vector<String> text = new Vector<String>();
        for (int i = 0; i < paths.size(); ++i) {

            File check = new File(paths.get(i));
            if (!check.isFile()) {
                continue;
            }
            File in = new File(paths.get(i));

            Scanner sc = new Scanner(in);
            String data = null;
            while (sc.hasNextLine()) {
                data = sc.nextLine();
                text.add(data);
            }
            sc.close();
        }
        return text;
    }

    private void writeToFile(Vector<String> input, String fileName, boolean state) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, state));  //true for append
        for (String i : input) {
            writer.write(i);
            writer.newLine();
        }

        writer.close();
    }

    private String getFileName(String name) {
        int j = 0;
        for (int i = name.length() - 1; i >= 0; --i) {
            if (name.charAt(i) == '.') j = i;
            if (name.charAt(i) == '\\') return name.substring(i + 1, j);
        }
        return name;
    }

    private String getFile(String name) {
        for (int i = name.length() - 1; i >= 0; --i) {
            if (name.charAt(i) == '\\') return name.substring(i + 1);
        }
        return name;
    }

    public String getFileExtension(String name) {
        for (int i = name.length() - 1; i >= 0; --i) {
            if (name.charAt(i) == '.') return name.substring(i + 1);
        }
        return name;
    }

    private String getFileDirectory(String source) {
        return source.substring(0, source.lastIndexOf('\\') - 1);
    }

    private void Args(Vector<String> in) {
        switch (in.get(0)) {
            case "cd":
                System.out.println("cd : new directory or no args");
                return;
            case "ls":
                System.out.println("ls : directory or no args");
                return;
            case "cp":
                System.out.println("cp : source , destination");
                return;
            case "cat":
                System.out.println("cat : file or more ");
                return;
            case "mkdir":
                System.out.println("mkdir : directory ");
                return;
            case "rmdir":
                System.out.println("rmdir : directory");
                return;
            case "mv":
                System.out.println("mv : source , destination");
                return;
            case "rm":
                System.out.println("rm : file or directory ");
                return;
            case "date":
                System.out.println("date : date or no args");
                return;
            case "pwd":
                System.out.println("pwd : no args");
                return;
            case "cls":
                System.out.println("clear : no args");
                return;
            case "exit":
                System.out.println("exit : no args");
                return;
        }

    }

    private void help() {
        System.out.println("cd : changes the current directory to another one. ");
        System.out.println("ls :  list each given file or directory name. Directory contents are sorted alphabetically. For ls, files are by default listed in columns, sorted vertically, if the standard output is a terminal; otherwise, they are listed one per line.");
        System.out.println("cp : cp copies each other given file into a file with the same name in that directory. Otherwise, if only two files are given, it copies the first onto the second. It is an error if the last argument is not a directory and more than two files are given. By default, it does not copy directories.");
        System.out.println("cat : Concatenate files and print on the standard output.");
        System.out.println("more : print the rest of content on the screen ");
        System.out.println("mkdir : mkdir creates a directory with each given name. By default, the mode of created directories is 0777 minus the bits set in the umask.");
        System.out.println("rmdir : rmdir removes each given empty directory. If any nonoption argument does not refer to an existing empty directory, it is an error.");
        System.out.println("mv : If the last argument names an existing directory, mv moves each other given file into a file with the same name in that directory. Otherwise, if only two files are given, it moves the first onto the second. It is an error if the last argument is not a directory and more than two files are given. It can move only regular files across file systems. If a destination file is unwritable, the standard input is a tty, and the –f or --force option is not given, mv prompts the user for whether to overwrite the file. If the response does not begin with y or Y, the file is skipped.");
        System.out.println("rm : rm removes each specified file. By default, it does not remove directories. If a file is unwritable, the standard input is a tty, and the -f or --force option is not given, rm prompts the user for whether to remove the file. If the response does not begin with y or Y, the file is skipped.");
        System.out.println("args : List all command arguments");
        System.out.println("date : To display or to set the date and time of the system. The format for setting date is [MMDDhhmm[[CC]YY][.ss]]");
        System.out.println("pwd : Display current user directory.");
        System.out.println("clear : This command can be called to clear the current terminal screen and it can be redirected to clear the screen of some other terminal.");
        System.out.println("exit : stop all");
    }

	public static void main(String [] args) throws IOException {
//			terminal t= new terminal("ls C:\\Users\\Abdo\\Documents\\GitHub\\LinuxCommandSimulator\\tst | cat");
//
////		File tst = new File("C:\\Users\\Abdo\\Documents\\GitHub\\LinuxCommandSimulator\\*.txt");
////		if(tst.isFile()) System.out.println("file");
//
//
//
//
//
	}

    void teleport() throws IOException {
        Vector<String> v = new Vector<String>();

        terminal s = new terminal();
        cd();
        cp(v);
        mv(v);
        rm(v);
        rmdir(v);
        mkdir(v);
        cd(v);
        pwd();
        cls();
        Args(v);
        date();
        date(v);
        exit();
        cat(v);
        ls(v);
        command("asdas", v);


    }

}
