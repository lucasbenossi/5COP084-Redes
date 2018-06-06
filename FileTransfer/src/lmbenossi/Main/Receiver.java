package lmbenossi.Main;
import java.io.File;
import java.io.FileOutputStream;

import lmbenossi.DatagramObjectTransfer.*;
import lmbenossi.ObjectTransfer.ObjectTransfer;
import lmbenossi.TransmissionObjectTransfer.TransmissionObjectTransfer;
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

		ObjectTransfer objTransfer = null;
		try {
			if(Globals.TCP) {
				objTransfer = new TransmissionObjectTransfer(port);
			}
			else {				
				objTransfer = new DatagramObjectTransfer(port);
			}
			
			objTransfer.listen();
			
			while(true) {
				Fragment fragment = (Fragment) objTransfer.receive();
				if(fragment == null) {
					System.out.println("Sender parou de enviar");
					break;
				}
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
