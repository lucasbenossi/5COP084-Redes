package lmbenossi.FileTransfer;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class Receiver {
	public static void run(Scanner scan) {
		File file = null;
		FileOutputStream fos = null;
		while(fos == null) {
			try {
				System.out.print("Insira o caminho do arquivo: ");
				String path = scan.nextLine();
				file = new File(path);
				fos = new FileOutputStream(file);
				
			} catch (Exception e) {
				System.out.println("Arquivo inválido.");
			}
		}

		TransferPeer peer = null;
		try {
			peer = new TransferPeer(Main.PORT);
			
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
