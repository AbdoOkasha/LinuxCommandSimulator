import java.io.IOException;

public class main {

	public static void main(String[] args) throws IOException {
		parser p = new parser();
		boolean state = p.parse("exit");
		if(state) {
			String com = p.getCmd();
			String[] arg= p.getArguments();
//			System.out.println(com);

			terminal b = new terminal(com,arg);
		}

	}

}
