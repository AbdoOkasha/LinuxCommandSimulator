import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;


public class terminal {
    private String cmd;
    private String[] args;
    private String directory="F:\\";
    private String root;

    terminal (){
    	
    }
    
    terminal(String input) throws IOException {
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
        String[] lastOut=null;
        int lastCom=0;
        
        int flag=-1;
        
        for(int i=0;i<args.length;++i) {
        	if(args[i].equals(">>") || args[i].equals(">") || args[i].equals("|") ) {
        		flag=i;
        		break;
        	}
        }
        
        if(flag!=-1) {
	        String tmp[]=Arrays.copyOfRange(args, 0, flag-1);
	        lastOut=command(cmd,tmp);
        }
        else lastOut=command(cmd,args);
        
        for(int i=0;i<args.length;++i) {
        	if(args[i].equals(">") || args[i].equals(">>")) {
        		if(i>args.length -2) {
        			if(args[i+2].equals(">") || args[i+2].equals(">>")) {
        				mkFile(args[i+1]);
        			}
        			else {
        				mkFile(args[i+1]);
        				writeToFile(lastOut,args[i+1],args[i].equals(">>"));
        				lastOut=null;
        			}
        		}
        	}
        	else if(args[i].equals("|")) {
        		if(lastCom!=0) 
	        		lastOut=command(args[lastCom],lastOut);
	        		
        		else 
        			lastOut=command(cmd,lastOut);
        		
        		lastCom=+1;
        	}
        	
        }
        
    }
    
    public String[] command(String com,String arg) {
    	
    	
    	return null;
    }

    public String[] command(String com,String[] arg) {
    	
    	
    	return null;
    }
    
    public String cp(String[] paths) throws IOException {   // cp stands for copy file channel faster than java stream

    	if(paths.length>2) {
    		File check = new File(paths[paths.length-1]);
    		if(!check.isDirectory()) return null;
    		else if(!check.exists()) {
    			mkdir(paths[paths.length-1]);
    		}
    		
    		for(int i=0;i<paths.length-1;++i) {
    			String name="";
				name=getFileName(paths[i]);
				name = paths[paths.length-1]+name;
    			
				File make= new File(name);
				make.createNewFile();
				
				FileChannel source = new FileInputStream(paths[i]).getChannel();
				FileChannel dest = new FileOutputStream(name).getChannel();
				
				dest.transferFrom(source, 0, source.size());
				
				source.close();
				dest.close();
	        }
    	}
    	
    	else {
    		File file= new File(paths[0]);
    		Scanner sc = new Scanner(file);
    		File check = new File(paths[1]);
    		
    		if(check.isDirectory()) {
    			String newPath=paths[1]+getFileName(paths[0]);
    			mkFile(newPath);
    			paths[1]=newPath;
    		}
    		else if(!check.isFile()) { //check if not created
    			mkFile(paths[1]);
    		}
    		
    		BufferedWriter out= new BufferedWriter(new FileWriter(paths[1],false)); //true for append
    		while(sc.hasNextLine()) {
    			String in = sc.nextLine();
    			out.write(in);
    			out.newLine();
    		}
    		sc.close();
    		out.close();
    	}
    	
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

    public String[] ls() {
        File folders[] = new File(pwd()).listFiles();
        String [] names=new String[folders.length];
        
        for (int i=0;i<folders.length;++i) {
        	names[i]=folders[i].toString();
        }
        return names;
    }

    public String pwd() {
        return directory;
    }

    public String mv(String paths[]) throws IOException {    //move source to dest
    	cp(paths);
    	for(int i=0;i<paths.length-1;++i) {
    		rm(paths[i]);
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

    public String mkFile(String path) throws IOException {
    	File file = new File(path);
    	if(!file.exists()) file.createNewFile();
    	
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
    	if(source.equals(getFileName(source))) {
    		source=pwd()+source;
    	}
    	
    	if(source.charAt(0)=='*') {
    		String compare=source.substring(1);
    		String [] names=ls();
    		for(int i=0;i<names.length;++i) {
    			if(names[i].substring(names[i].length()-4,names[i].length()).equals(compare)) rm(names[i]);
    		}
    	}
    	else if(source.charAt(source.length()-1)=='*') {
    		String compare=source.substring(0,source.length()-1);
    		String [] names=ls();
    		for(int i=0;i<names.length;++i) {
    			if(names[i].substring(0,names[i].length()-1).equals(compare)) rm(names[i]);
    		}
    		
    	}
    	
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
		for(int i=0;i<15;++i) System.out.println("");
	    return null;
	   }

	public String date() {
		return java.time.LocalDate.now().toString();
	}
	
	public String date(String format) {//month,day,hour,mn,first 2 digits if the year,last 2,seconds
		Calendar cal = Calendar.getInstance(); 
		int month=Integer.parseInt(format.substring(0,2));
		int	day=Integer.parseInt(format.substring(2,4));
		int hour=Integer.parseInt(format.substring(4,6));
		int	mn=Integer.parseInt(format.substring(6,8));
		int	year=Integer.parseInt(format.substring(8,12));
		int	sec=Integer.parseInt(format.substring(12,14));
		
		cal.set(year, month, day, hour, mn, sec);
		
		return null;
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

	public void writeToFile(String[] input, String fileName,boolean state) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,state));  //true for append
	    
	    for(String i:input)
	    	writer.write(i);
		
	    writer.close();
	}
	
	public String getFileName(String name) {
		for(int i=name.length()-1;i>=0;--i) {
			if(name.charAt(i)=='\\') return name.substring(i+1);
		}
		return name;
	}
	
	public static void main(String [] args) throws IOException {
		terminal t= new terminal();
		String [] arg = {"F:\\tst2.txt","F:\\tst1.txt"};
		t.cls();
	}
	
}
