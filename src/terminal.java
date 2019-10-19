import javax.swing.filechooser.FileSystemView;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Vector;


public class terminal {
    private String cmd=null;
    private Vector<String> args=new Vector<String>();
    private static String directory="E:\\Desktop";
    private static String root="E:\\Desktop";

    terminal (){
    	
    }
    
    terminal(String input) throws IOException {
        parser p = new parser();
        p.parse(input);
        cmd = p.getCmd();
        String gar[] = p.getArguments();
        if(gar!=null) for(String i:gar) args.add(i);
        
//        System.out.println(cmd + " " + args);
        
        /*
         * get the root name*/
//        FileSystemView fsv = FileSystemView.getFileSystemView();

//        File[] roots = fsv.getRoots();
//        root=roots[0].toString();
//        directory= root;
        
        Vector<String> lastOut=new Vector<String>();
        
        int flag=-1;
        
       if(args!=null)
        for(int i=0;i<args.size();++i) {
        	if(args.get(i).equals(">>") || args.get(i).equals(">") || args.get(i).equals("|") ) {
        		flag=i;
        		break;
        	}
        }
        
        if(flag!=-1) {
        	if(flag !=0 ) {
        		Vector<String> tmp=new Vector<String>();
        		for(int i=0;i<flag;++i) tmp.add(args.get(i));
    	        lastOut=command(cmd,tmp);
        	}
        	else {
        		Vector<String> tmp = null;
        		lastOut=command(cmd,tmp);
        	}
        }
        else {
        	if(args==null) {
        		Vector<String> call =null;
        		lastOut=command(cmd,call);
        	}
        	else lastOut=command(cmd,args);
        	
        }
        
        
        if(args!=null)
	        for(int i=0;i<args.size();++i) {
	        	if(args.get(i).equals(">") || args.get(i).equals(">>")) {
	        		if(i<args.size() -3) {
	        			if(args.get(i+2).equals(">") || args.get(i+2).equals(">>")) {
	        				mkFile(args.get(i+1));
	        			}
	        			else {
	        				mkFile(args.get(i+1));
	        				String fileName=args.get(i+1);
	        				File check= new File(fileName);
	        				
	        				if(!check.isDirectory()) {
	        					fileName=pwd().get(0)+'\\'+fileName;
	        				}
	        				
	        				if(lastOut != null) writeToFile(lastOut,fileName,args.get(i).equals(">>"));
	        				lastOut=null;
	        			}
	        		}
        			else {
        				mkFile(args.get(i+1));
        				String fileName=args.get(i+1);
        				File check= new File(fileName);
        				
        				if(!check.isDirectory()) {
        					fileName=pwd().get(0)+'\\'+fileName;
        				}
        				if(lastOut != null) writeToFile(lastOut,fileName,args.get(i).equals(">>"));
        				lastOut=null;
        			}
	        	}
	        	else if(args.get(i).equals("|")) {
	        			lastOut=command(args.get(i+1),lastOut);
	        			
	        	}
	        	
	        }
	        if(lastOut !=null) 
	        	for(int i=0;i<lastOut.size();++i) System.out.println(lastOut.get(i).toString());
	        
	    }
	    
   
    
    public Vector<String> command(String com,Vector<String> arg) throws IOException {
    	
    	int argsLen = (arg==null)?0:arg.size();
    	
    	
    	if(arg==null || arg.size()==0) {
    		switch(com) {
    		case "rm":
    		case "mkdir":
    		case "cat":
    		case "rmdir":
    			
    		return null;
    		}
    	}
    	if(argsLen==1 || arg==null || argsLen==0)
	    	switch (com) {	//multiple argument commands
	    	case "cp":
	    	case "mv":
	    		
	    	return null;
	    	}
    	
    	
		
    	switch(com) {
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
    	case  "date":
    		return date(arg);
    	case "help":
    		help();
        	return null;
    	case "args":
    		Args();
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

    
    public Vector<String> cp(Vector<String> paths) throws IOException {   // cp stands for copy file channel faster than java stream

    	if(paths.size()>2) {
    		File check = new File(paths.get(paths.size()-1));
    		if(!check.isDirectory()) return null;
    		else if(!check.exists()) {
    			mkdir(paths.get(paths.size()-1));
    		}
    		
    		for(int i=0;i<paths.size()-1;++i) {
    			String name="";
				name=getFileName(paths.get(i));
				name = paths.get(paths.size()-1)+'\\'+name;
    			
				File make= new File(name);
				make.createNewFile();
				
				FileChannel source = new FileInputStream(paths.get(i)).getChannel();
				FileChannel dest = new FileOutputStream(name).getChannel();
				
				dest.transferFrom(source, 0, source.size());
				
				source.close();
				dest.close();
	        }
    	}
    	
    	else {
    		File check1 = new File(paths.get(0));
    		if(!check1.isFile()) paths.set(1, pwd().get(0)+'\\'+paths.get(0));
    		
    		File file= new File(paths.get(0));
    		Scanner sc = new Scanner(file);
    		File check = new File(paths.get(1));
    		
    		if(check.isDirectory()) {
    			String newPath=paths.get(1)+'\\'+getFileName(paths.get(0));
    			mkFile(newPath);
    			paths.set(1,newPath);	//set in vector
    		}
    		else if(!check.isFile()) { //check if not created
    			mkFile(paths.get(1));
    		}
    		
    		BufferedWriter out= new BufferedWriter(new FileWriter(paths.get(1),false)); //true for append
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

    public Vector<String> cd(Vector<String> Dest) {   //cd stands for change directory
        if(Dest == null || Dest.size()==0)
        	return cd();
    	String val=Dest.get(0);
        File check = new File(val);
        if(check.isDirectory()) 
        	directory = val;
        
        return null;
    }

    public Vector<String> cd() {
        directory = root;
         return null;
    }

    public Vector<String> ls(Vector<String> arg) {
    	File folders[];
    	Vector<String> tmp = pwd();
    	if(arg!=null && arg.size()!=0) {
    		cd(arg);
    	}
    	String loc = pwd().get(0);
        folders= new File(loc).listFiles();
        Vector<String> names=new Vector<String>();
    	
        for (int i=0;i<folders.length;++i) {
        	names.add(folders[i].toString());
        }
        cd(tmp);
        return names;
    }


    public Vector<String> pwd() {
    	Vector<String> tmp = new Vector<String>();
    	tmp.add(directory);
    	return tmp;
    }
    

    public Vector<String> mv(Vector<String> paths) throws IOException {    //move source to dest
    	for(int i=0;i<paths.size();++i) {
    		File check=new File(paths.get(i));
    		if(!check.isFile() && !check.isDirectory()) paths.set(i, pwd().get(0)+'\\'+paths.get(i));
    	}
    	cp(paths);
    	Vector<String> tmp = new Vector<String>();
    	for(int i=0;i<paths.size()-1;++i) {
    		tmp.add(paths.get(i));
    	}
    	rm(tmp);
        return null;
    }

    public Vector<String> mkdir(Vector<String> args) {
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


    public Vector<String> mkFile(String path) throws IOException {
    	File check = new File(path);
    	if(!check.isDirectory()) {
    		Vector<String> dir = pwd();
	    	path=dir.get(0)+'\\'+path;
    	}
	    File file = new File(path);
    	if(!file.exists()) file.createNewFile();
    	
    	return null;
    }
    
    public Vector<String> mkdir(String args) {
    	
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
    
    public Vector<String> rmdir(Vector<String> args) {
        int argsLen = args.size();
        for(int i= 0 ; i < argsLen ; ++i){
            File Directory = new File(args.get(i));
            if (Directory.listFiles().length > 0)
                System.out.println('\'' + Directory.getPath() + '\'' + " not empty");
            else
                Directory.delete();
        }
        return null;
    }

  
    public Vector<String> rm(File[] args){
        if (args == null) return null;
        int argsLen = args.length;
        for(int i = 0 ; i < argsLen ; ++i){
            if(args[i].isDirectory()) this.rm(args[i].listFiles());
            args[i].delete();
        }
        return null;
    }
    
    public Vector<String> rm(Vector<String> data){
    	for(int j=0;j<data.size();++j) {
    		
    		String source=data.get(j);
	    	if(source.equals(getFileName(source))) {
	    		Vector<String> tmp=pwd();
	    		source=tmp.get(0)+'\\'+source;
	    	}
	    	
	    	if(source.charAt(0)=='*') {
	    		String compare=source.substring(1);
	    		Vector<String> names=ls(null);
	    		for(int i=0;i<names.size();++i) {
	    			String toRemove=names.get(i).substring(names.get(i).length()-4,names.get(i).length());
	    			if(toRemove.equals(compare)) {
	    				File need = new File(toRemove);
	    				File[] tmp = new File[1];
	    				tmp[0]=need;
	    				rm(tmp);
	    			}
	    		}
	    	}
	    	else if(source.charAt(source.length()-1)=='*') {
	    		String compare=source.substring(0,source.length()-1);
	    		Vector<String> names=ls(null);
	    		for(int i=0;i<names.size();++i) {
	    			String toRemove=names.get(i).substring(0,names.get(i).length()-1);
	    			if(toRemove.equals(compare)) {
	    				File need = new File(toRemove);
	    				File[] tmp = new File[1];
	    				tmp[0]=need;
	    				rm(tmp);
	    			}
	    		}
	    		
	    	}
	    	
	    	File args=new File(source);
	        if (args == null) return null;
	        if(args.isDirectory()) this.rm(args.listFiles());
	        args.delete();
    	}
        return null;
    }
    
    public void exit(){
        System.exit(0);
    }


	public Vector<String> cls() {  
		for(int i=0;i<15;++i) System.out.println("");
	    return null;
	   }

	
	public Vector<String> date() {
		Vector<String>Date = new Vector<String>();
		Date.add(java.time.LocalDate.now().toString());
		return Date;
	}
	
	public Vector<String> date(Vector<String> shape) {//month,day,hour,mn,first 2 digits if the year,last 2,seconds
		if(shape == null || shape.size()==0)
			return date();
		String format=shape.get(0);
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
	
	
	public Vector<String> cat(Vector<String> paths) throws FileNotFoundException {
		Vector<String> text=new Vector<String>();
		for(int i=0;i<paths.size();++i) {
			
			File check = new File(paths.get(i));
			if(!check.isFile()) paths.set(i,pwd().get(0)+'\\'+paths.get(i));
			File in = new File(paths.get(i));
			
			Scanner sc=new Scanner(in);
			String data=null;
			while(sc.hasNextLine()) {
				data=sc.nextLine();
				text.add(data);
				System.out.println(data);
			}
			sc.close();
		}
		return text;
	}


	public void writeToFile(Vector<String> input, String fileName,boolean state) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,state));  //true for append
	    for(String i:input) {
	    	writer.write(i);
	    	writer.newLine();
	    }
		
	    writer.close();
	}
	
	
	public String getFileName(String name) {
		for(int i=name.length()-1;i>=0;--i) {
			if(name.charAt(i)=='\\') return name.substring(i+1);
		}
		return name;
	}
	
	
	public void Args() {
		System.out.println("cd : new directory or no args");
		System.out.println("ls : directory or no args");
		System.out.println("cp : source , destination");
		System.out.println("cat : file or more ");
		System.out.println("mkdir : directory ");
		System.out.println("rmdir : directory");
		System.out.println("mv : source , destination");
		System.out.println("rm : file or directory ");
		System.out.println("date : date or no args");
		System.out.println("pwd : no args");
		System.out.println("clear : no args");
		System.out.println("exit : no args");
		
	}
	
	public void help() {
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
		terminal b = new terminal("cd E:\\Downloads");
		terminal t= new terminal(" pwd | mkdir ");
		
		

		
		
	}
	
	void teleport() throws IOException {
		Vector<String> v = new Vector<String>();
		
		terminal();
		cd();
		cp(v);
		mv(v);
		rm(v);
		rmdir(v);
		mkdir(v);
		cd(v);
		pwd();
		cls();
		Args();
		date();
		date(v);
		exit();
		cat(v);
		ls(v);
		command("asdas",v);
		
		
	}
	
}
