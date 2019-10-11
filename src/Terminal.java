import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Vector;

import javax.swing.filechooser.FileSystemView;


public class Terminal {
	String cmd, args[],directory,root="";
	
	
	Terminal(String input){
		parser p = new parser();
		p.parse(input);
		cmd=p.getCmd();
		args=p.getArguments().clone();
		
		/*
		 * get the root name*/
		FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] files = File.listRoots();

        File[] roots = fsv.getRoots();
        for (int i = 0; i < roots.length; i++) {
            root+= roots[i].toString();
        }
        
        
	}
	public void cp(String Source,String Dest) throws IOException {   // cp stands for copy file channel faster than java stream
//		InputStream is= new FileInputStream(Source);
//		OutputStream os= new FileOutputStream(Dest);
//		
//		byte transRate[]=new byte[1024];
//		int length;
//		while((length=is.read(transRate))>0) {
//			os.write(transRate,0,length);
//		}
		
		FileChannel is = new FileInputStream(Source).getChannel();
		FileChannel os = new FileOutputStream(Dest).getChannel();
		
		os.transferFrom(is, 0, is.size());
		
		is.close();
		os.close();
		
	}
	
	public void cd(String Dest) {   //cd stands for change directory
		directory = Dest;
	}
	public void cd() {
		directory=root;
	}
	
	public Vector ls() {
		File folders[]=new File(pwd()).listFiles();
		Vector foldersName=new Vector();
		for(File f:folders) {
			foldersName.add(f.toString());
		}
		return foldersName;
	}
	
	public String pwd() {
		return directory;
	}
	
	public void mv(String source, String dest) throws IOException {	//move source to dest
		File check = new File(dest);
		if(check.isFile()) {
			Vector files = new Vector();
			files=ls();
			if(files.contains(check)) {
				if(files.contains(dest)) {
					cp(source,dest);
					//TODO rm(source);		
				}
				else {
					//TODO rename(source,dest);
				}
			}
		}
		else if(check.isDirectory()) {
			//TODO mkdir(dest,source);
			cp(source,dest);
			//TODO rm(source);
		}
		
	}
	
	

}
