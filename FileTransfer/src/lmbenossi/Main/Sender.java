package lmbenossi.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import lmbenossi.ArgsParser.Arg;
import lmbenossi.DatagramObjectTransfer.*;
import lmbenossi.ObjectTransfer.ObjectTransfer;
import lmbenossi.TransmissionObjectTransfer.TransmissionObjectTransfer;

public class Sender {
	public static void run() {
		int port = Globals.PORT;
		int partlen = Globals.PARTLEN;
		
		File file = null;
		FileInputStream fis = null;
		while(fis == null) {
			try {
				file = new File(Arg.FILE.getValue());
				fis = new FileInputStream(file);
				
			} catch (Exception e) {
				System.out.println("Arquivo inválido.");
				return;
			}
		}
		
		SocketAddress peerAddress = null;
		while(peerAddress == null) {
			try {
				peerAddress = new InetSocketAddress(InetAddress.getByName(Arg.HOST.getValue()), port);
			} catch (Exception e) {
				System.out.println("IP inválido");
				return;
			}
		}
		
		long tamanho = file.length();
		int partes = (int) (tamanho / partlen);
		int totalPartes = partes;
		if( tamanho % partlen != 0 ) {
			totalPartes++;
		}

		System.out.println("Tamanho do arquivo: " + tamanho);
		System.out.println("Partes: " + totalPartes);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		try {
			int i;
			for (i = 0; i < partes; i++) {
				byte[] bytes = new byte[partlen];
				fis.read(bytes);
				fragments.add(i, new Fragment(totalPartes, i+1, bytes));
			}

			if (tamanho % partlen != 0) {
				byte[] bytes = new byte[(int) (tamanho - partes * partlen)];
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
		
		ObjectTransfer objTransfer = null;
		try {
			if(Globals.TCP) {
				objTransfer = new TransmissionObjectTransfer(peerAddress);
			}
			else {				
				objTransfer = new DatagramObjectTransfer(peerAddress);
			}
			
			
			if(objTransfer.connect()) {
				for(Fragment fragment : fragments) {
					if(!objTransfer.send(fragment)) {
						System.out.println("Receiver parou de responder");
						break;
					}
				}
				objTransfer.finish();
				
				if(objTransfer instanceof DatagramObjectTransfer) {
					DatagramObjectTransfer dot = (DatagramObjectTransfer) objTransfer;
					if(dot.error()) {
						System.out.println("Erro");
					}
				}
			}
			else {
				System.out.println("Receiver não respondeu");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Pacotes perdidos: " + Globals.lostPackets);
	}
}
