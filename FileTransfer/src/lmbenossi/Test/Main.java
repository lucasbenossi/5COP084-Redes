package lmbenossi.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import lmbenossi.ArgsParser.*;
import lmbenossi.DatagramObjectTransfer.*;

public class Main {
	public static void main(String[] argv) throws Exception{
		Parser.parse(argv);
		
		if(Arg.SENDER.isSet()) {
			Main.sender();
		}
		else if(Arg.RECEIVER.isSet()) {
			Main.receiver();
		}
	}
	
	public static void sender() throws Exception {
		DatagramObjectSocket socket = new DatagramObjectSocket();
		SocketAddress peerAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 2000);
		
		for(int i = 1; i <= 100; i++) {
			socket.send(PacketFactory.createDataPacket(i, peerAddress, null));
		}
	}
	
	public static void receiver() throws Exception {
		DatagramObjectSocket socket = new DatagramObjectSocket(2000);
		int counter = 0;
		
		while(counter < 100) {
			socket.receive();
			counter++;
		}
	}
}
