package lmbenossi.Main;

import lmbenossi.ArgsParser.*;

public class Main {
	
	public static void main(String[] argv) {
		if(!Parser.parse(argv)) {
			return;
		}
		
		Globals.set();
		
		if(Arg.SENDER.isSet()) {
			Sender.run();
		}
		else if(Arg.RECEIVER.isSet()) {
			Receiver.run();
		}
	}
}





























