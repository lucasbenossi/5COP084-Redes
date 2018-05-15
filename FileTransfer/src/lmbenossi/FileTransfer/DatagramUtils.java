package lmbenossi.FileTransfer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class DatagramUtils {
	public static DatagramPacket toDatagramPacket(Object object, SocketAddress peerAddress) throws IOException {
		byte[] objectBytes = ByteUtils.toByteArray(object);
		return new DatagramPacket(objectBytes, objectBytes.length, peerAddress);
	}
	
	public static void send(DatagramSocket socket, Packet packet, SocketAddress peerAddress) throws IOException {
		socket.send(DatagramUtils.toDatagramPacket(packet, peerAddress));
		System.out.println("Sent "+packet);
	}
}
