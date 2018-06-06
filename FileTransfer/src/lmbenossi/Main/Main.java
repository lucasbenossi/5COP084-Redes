package lmbenossi.Main;

import lmbenossi.ArgsParser.*;

public class Main {
	
	public static void main(String[] argv) {
		Parser.parse(argv);
		
		if(!Parser.check()) {
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





























