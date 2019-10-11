import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class Terminal {
	String cmd, args[];
	
	Terminal(String cmd,String args[]){
		this.cmd=cmd;
		this.args=args.clone();
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
		
		
	}

}
