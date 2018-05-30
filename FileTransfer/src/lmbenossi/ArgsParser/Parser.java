package lmbenossi.ArgsParser;

import java.util.ArrayList;

public class Parser {
	private static ArrayList<String> argsList = new ArrayList<>();
	
	public static boolean parse(String[] argv) {
		for(String arg : argv) {
			argsList.add(arg);
		}
		
		for(Arg arg : Arg.values()) {
			int i = argsList.indexOf(arg.toString());
			if(i != -1) {
				if(arg.isBoolean()) {
					arg.set();
				}
				else if(arg.isString() || arg.isInteger()) {
					int j = i + 1;
					if(j < argsList.size() && ! argsList.get(j).startsWith("-")) {
						arg.set(argsList.remove(j));
					}
				}
				argsList.remove(i);
			}
		}
		
		return check();
	}
	
	private static boolean check() {
		boolean ok = false;
		
		if(Arg.SENDER.isSet()) {
			ok = Arg.FILE.isSet() & Arg.HOST.isSet();
		}
		else if(Arg.RECEIVER.isSet()) {
			ok = Arg.FILE.isSet();
		}
		
		if(!argsList.isEmpty()) {
			for(String arg : argsList) {
				if(arg.startsWith("-")) {
					System.err.println("Parametro não reconhecido: " + arg);
				}
			}
			ok = false;
		}
		
		if(!ok) {
			System.err.println("Uso: FileTransfer.jar -sender -file FILE -host HOST [-port PORT] [-partlen LENGHT] [-tcp]");
			System.err.println("     FileTransfer.jar -receiver -file FILE [-port PORT] [-buffer SIZE] [-tcp]");
		}
		
		return ok;
	}
}
