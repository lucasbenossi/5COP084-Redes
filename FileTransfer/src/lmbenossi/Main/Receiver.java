package lmbenossi.Main;
import java.io.File;
import java.io.FileOutputStream;

import lmbenossi.DatagramObjectTransfer.*;
import lmbenossi.ArgsParser.*;

public class Receiver {
	public static void run() {
		int port = Globals.PORT;
		
		File file = null;
		FileOutputStream fos = null;
		while(fos == null) {
			try {
				file = new File(Arg.FILE.getValue());
				fos = new FileOutputStream(file);
				
			} catch (Exception e) {
				System.out.println("Arquivo inválido.");
				return;
			}
		}

		DatagramObjectTransfer peer = null;
		try {
			peer = new DatagramObjectTransfer(port);
			
			while(true) {
				Fragment fragment = (Fragment) peer.receive();
				fos.write(fragment.getData());
				if(fragment.getNum() == fragment.getTotal()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
