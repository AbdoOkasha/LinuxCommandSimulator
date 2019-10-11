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
        File[] files = File.listRoots();

        File[] roots = fsv.getRoots();
        for (int i = 0; i < roots.length; i++) {
            root += roots[i].toString();
        }
    }

    public void cp(String Source, String Dest) throws IOException {   // cp stands for copy file channel faster than java stream
//		InputStream is= new FileInputStream(Source);
//		OutputStream os= new FileOutputStream(Dest);
//
//		byte transRate[]=new byte[1024];
//		int length;
//		while((length=is.read(transRate))>0) {
//			os.write(transRate,0,length);
//		}

        FileChannel inputStream = new FileInputStream(Source).getChannel();
        FileChannel otuputStream = new FileOutputStream(Dest).getChannel();

        otuputStream.transferFrom(inputStream, 0, inputStream.size());

        inputStream.close();
        otuputStream.close();

    }

    public void cd(String Dest) {   //cd stands for change directory
        directory = Dest;
    }

    public void cd() {
        directory = root;
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

    public void mv(String source, String dest) throws IOException {    //move source to dest
        File check = new File(dest);
        if (check.isFile()) {
            Vector files = new Vector();
            files = ls();
            if (files.contains(check)) {
                if (files.contains(dest)) {
                    cp(source, dest);
                    //TODO rm(source);
                } else {
                    //TODO rename(source,dest);
                }
            }
        } else if (check.isDirectory()) {
            //TODO mkdir(dest,source);
            cp(source, dest);
            //TODO rm(source);
        }

    }

    public void mkdir(String[] args) {
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
    }

    public void rmdir(String[] args) {
        int argsLen = args.length;
        for(int i= 0 ; i < argsLen ; ++i){
            File Directory = new File(args[i]);
            if (Directory.listFiles().length > 0)
                System.out.println('\'' + Directory.getPath() + '\'' + " not empty");
            else
                Directory.delete();
        }
    }

    public void rm(File[] args){
        if (args == null)return;
        int argsLen = args.length;
        for(int i = 0 ; i < argsLen ; ++i){
            if(args[i].isDirectory())this.rm(args[i].listFiles());
            args[i].delete();
        }
    }

    public void exit(){
        System.exit(0);
    }

	public void cls() {  
	    System.out.print("\033[H\033[2J");  
	    System.out.flush();  
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
