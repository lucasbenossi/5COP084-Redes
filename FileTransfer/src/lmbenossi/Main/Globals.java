package lmbenossi.Main;

import lmbenossi.ArgsParser.*;

public class Globals {
	public static int PORT = 2000;
	public static int PARTLEN = 1024;
	public static int BUFFERSIZE = 2048;
	public static boolean TCP = false;
	
	public static void set() {
		if(Arg.PORT.isSet()) {
			Globals.PORT = Arg.PORT.getInteger();
		}
		
		if(Arg.PARTLEN.isSet()) {
			Globals.PARTLEN = Arg.PARTLEN.getInteger();
		}
		
		if(Arg.RECEIVER.isSet() && Arg.BUFFER.isSet()) {
			Globals.BUFFERSIZE = Arg.BUFFER.getInteger();
		}
		
		if(Arg.TCP.isSet()) {
			Globals.TCP = true;
		}
	}
}
