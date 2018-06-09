package lmbenossi.ArgsParser;

import java.util.ArrayList;

public class Parser {
	private static ArrayList<String> argl = null;
	
	public static void parse(String[] argv) {
		Parser.argl = new ArrayList<>(argv.length);
		
		for(String arg : argv) {
			argl.add(arg);
		}
		
		for(Arg arg : Arg.values()) {
			int i = argl.indexOf(arg.toString());
			if(i != -1) {
				argl.remove(i);
				if(arg.isBoolean()) {
					arg.set();
				}
				else if(arg.isString() || arg.isInteger()) {
					if(i < argl.size() && ! argl.get(i).startsWith("-")) {
						arg.set(argl.remove(i));
					}
				}
			}
		}
	}
	
	public static boolean check() {
		boolean ok = false;
		
		if(Arg.SENDER.isSet()) {
			ok = Arg.FILE.isSet() & Arg.HOST.isSet();
		}
		else if(Arg.RECEIVER.isSet()) {
			ok = Arg.FILE.isSet();
		}
		
		if(!argl.isEmpty()) {
			for(String arg : argl) {
				if(arg.startsWith("-")) {
					System.err.println("Parametro não reconhecido: " + arg);
				}
			}
			ok = false;
		}
		
		if(!ok) {
			System.err.println("Uso: FileTransfer.jar -sender -file FILE -host HOST [-port PORT] [-partlen LENGHT] [-tcp] [-window SIZE]");
			System.err.println("     FileTransfer.jar -receiver -file FILE [-port PORT] [-buffer SIZE] [-tcp]");
		}
		
		return ok;
	}
}
