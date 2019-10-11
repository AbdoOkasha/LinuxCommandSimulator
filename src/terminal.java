import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class terminal {
    private String cmd;
    private String[] args;
    private String directory;
    private String root;

    terminal(String input) {
        parser p = new parser();
        p.parse(input);
        cmd = p.getCmd();
        args = p.getArguments().clone();

        /*
         * get the root name*/
        FileSystemView fsv = FileSystemView.getFileSystemView();

        File[] roots = fsv.getRoots();
        for (int i = 0; i < roots.length; i++) {
            root += roots[i].toString();
        }
    }

    public String cp(String Source, String Dest) throws IOException {   // cp stands for copy file channel faster than java stream

        FileChannel inputStream = new FileInputStream(Source).getChannel();
        FileChannel otuputStream = new FileOutputStream(Dest).getChannel();

        otuputStream.transferFrom(inputStream, 0, inputStream.size());

        inputStream.close();
        otuputStream.close();
        return null;

    }

    public String cd(String Dest) {   //cd stands for change directory
        directory = Dest;
        return null;
    }

    public String cd() {
        directory = root;
         return null;
    }

    public Vector ls() {
        File folders[] = new File(pwd()).listFiles();
        Vector foldersName = new Vector();
        for (File f : folders) {
            foldersName.add(f.toString());
        }
        return foldersName;
    }

    public String pwd() {
        return directory;
    }

    public String mv(String source, String dest) throws IOException {    //move source to dest
        File check = new File(dest);
        if (check.isFile()) {
            Vector files = new Vector();
            files = ls();
            if (files.contains(check)) {
                if (files.contains(dest)) {
                    cp(source, dest);
                    rm(source);
                } 
                else {
                	File oldFile = new File(source);
                	File newFile = new File (dest);
                	oldFile.renameTo(newFile);
                }
            }
        } else if (check.isDirectory()) {
            mkdir(dest);
            cp(source, dest);
            rm(source);
        }
        return null;
    }

    public String mkdir(String[] args) {
        int argsLen = args.length;
        for (int i = 0; i < argsLen; ++i) {
            File directory = new File(args[i]);
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

    public String mkdir(String args) {
    	
            File directory = new File(args);
            if (!directory.exists()) {
                if (!directory.mkdir()){
                    System.out.println("A file name can not contain any of the following characters \n" + "\\ / : * < > |");
                }
            } else if (directory.isFile()) {
                System.out.println('\'' + directory.getPath() + '\'' + " is not a Directory");
                
            } else if (directory.exists()) {
                System.out.println('\'' + directory.getPath() + '\'' + " already exists");

            }
            return null;
    }
    
    public String rmdir(String[] args) {
        int argsLen = args.length;
        for(int i= 0 ; i < argsLen ; ++i){
            File Directory = new File(args[i]);
            if (Directory.listFiles().length > 0)
                System.out.println('\'' + Directory.getPath() + '\'' + " not empty");
            else
                Directory.delete();
        }
        return null;
    }

    public String rm(File[] args){
        if (args == null) return null;
        int argsLen = args.length;
        for(int i = 0 ; i < argsLen ; ++i){
            if(args[i].isDirectory()) this.rm(args[i].listFiles());
            args[i].delete();
        }
        return null;
    }
    
    public String rm(String source){
    	File args=new File(source);
        if (args == null) return null;
           if(args.isDirectory()) this.rm(args.listFiles());
           args.delete();
   
        return null;
    }
    
    public void exit(){
        System.exit(0);
    }

	public String cls() {  
	    System.out.print("\033[H\033[2J");  
	    System.out.flush();  
	    return null;
	   }

	
	public String date() {
		return java.time.LocalDate.now().toString();
	}
	
	public String date(String Format) {
		SimpleDateFormat formatter = new SimpleDateFormat(Format);  
	    Date date = new Date();  
	    return formatter.format(date);  
		
	}
	
	public String cat(String[] paths) throws FileNotFoundException {
		InputStream is;
		String text=null;
		for(int i=0;i<paths.length;++i) {
			is=new FileInputStream(paths[i]);
			text+=is.toString();
		}
		return text;
	}

}
