package lmbenossi.Teste;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lmbenossi.ArgsParser.*;
import lmbenossi.DatagramObjectTransfer.*;
import lmbenossi.Main.Fragment;

public class Teste {
	public static void main(String[] argv) throws Exception{
		Parser.parse(argv);
		
		if(Arg.TCP.isSet()) {
			if(Arg.SENDER.isSet()) {
				Teste.senderTcp();
			}
			else if(Arg.RECEIVER.isSet()) {
				Teste.receiverTcp();
			}
		}
		else {
			if(Arg.SENDER.isSet()) {
				Teste.sender();
			}
			else if(Arg.RECEIVER.isSet()) {
				Teste.receiver();
			}
		}
		
	}
	
	public static void sender() throws Exception {
		DatagramObjectSocket socket = new DatagramObjectSocket();
		
		for(int i = 0; i < 10000; i++) {
			Fragment fragment = new Fragment(10000, i, new byte[1024]);
			Packet packet = PacketFactory.createDataPacket(i, new InetSocketAddress(Arg.HOST.getValue(), 2000), fragment, i);
			socket.send(packet);
		}
	}
	
	public static void receiver() throws Exception {
		DatagramObjectSocket socket = new DatagramObjectSocket(2000);
		
		for(int i = 0; i < 10000; i++) {
			socket.receive();
		}
	}
	
	public static void senderTcp() throws Exception {
		Socket socket = new Socket(Arg.HOST.getValue(), 2000);
		
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		
		for(int i = 0; i < 10000; i++) {
			Fragment fragment = new Fragment(10000, i, new byte[1024]);
			oos.writeObject(fragment);
			System.out.println("Sent " + fragment);
		}
		
		socket.close();
	}
	
	public static void receiverTcp() throws Exception {
		ServerSocket listener = new ServerSocket(2000);
		Socket socket = listener.accept();
		listener.close();
		
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		
		for(int i = 0; i < 10000; i++) {
			Fragment fragment = (Fragment) ois.readObject();
			System.out.println("Received " + fragment);
		}
	}
}
