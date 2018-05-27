package lmbenossi.FileTransfer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import lmbenossi.UDP.*;

public class Sender {
	public static void run(Scanner scan) {
		File file = null;
		FileInputStream fis = null;
		while(fis == null) {
			try {
				System.out.print("Insira o caminho do arquivo: ");
				String path = scan.nextLine();
				file = new File(path);
				fis = new FileInputStream(file);
				
			} catch (Exception e) {
				System.out.println("Arquivo inválido.");
			}
		}
		
		long tamanho = file.length();
		int partes = (int) (tamanho / Main.PARTLEN);
		int totalPartes = partes;
		if( tamanho % Main.PARTLEN != 0 ) {
			totalPartes++;
		}

		System.out.println("Tamanho do arquivo: " + tamanho);
		System.out.println("Partes: " + totalPartes);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		try {
			int i;
			for (i = 0; i < partes; i++) {
				byte[] bytes = new byte[Main.PARTLEN];
				fis.read(bytes);
				fragments.add(i, new Fragment(totalPartes, i+1, bytes));
			}

			if (tamanho % Main.PARTLEN != 0) {
				byte[] bytes = new byte[(int) (tamanho - partes * Main.PARTLEN)];
				fis.read(bytes);
				fragments.add(i, new Fragment(totalPartes, i+1, bytes));
			}
		} catch (IOException e) {
			System.out.println("ERRO: " + e);
		}
		
		try {
			fis.close();
		} catch (Exception e) {
			System.out.println("ERRO: "+e);
		}
		
		SocketAddress peerAddress = null;
		while(peerAddress == null) {
			try {
				System.out.print("Insira o IP do receiver: ");
				peerAddress = new InetSocketAddress(InetAddress.getByName(scan.nextLine()), Main.PORT);
			} catch (Exception e) {
				System.out.println("IP inválido");
			}
		}
		
		DatagramTransfer peer = null;
		try {
			peer = new DatagramTransfer(peerAddress);
			if(peer.start()) {
				for(Fragment fragment : fragments) {
					if(!peer.send(fragment)) {
						System.out.println("Receiver parou de responder");
						break;
					}
				}
				peer.finish();
			}
			else {
				System.out.println("Receiver não respondeu");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
