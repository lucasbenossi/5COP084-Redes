package lmbenossi.FileTransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class DatagramObjectSocket {
	private DatagramSocket socket;
	private byte[] buffer = new byte[2048];
	private DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
	
	public DatagramObjectSocket(int port) throws SocketException {
		this.socket = new DatagramSocket(port);
	}
	public DatagramObjectSocket() throws SocketException {
		this.socket = new DatagramSocket();
	}
	
	public void send(Packet packet) throws IOException {
		byte[] packetBytes = ByteUtils.toByteArray(packet);
		DatagramPacket packetDatagram = new DatagramPacket(packetBytes, packetBytes.length, packet.getPeerAddress());
		
		socket.send(packetDatagram);
		System.out.println("Sent " + packet);
	}
	public Packet receive() throws IOException, ClassNotFoundException {
		socket.receive(datagram);
		Packet packet = (Packet) ByteUtils.toObject(datagram.getData());
		System.out.println("Received " + packet);
		packet.setPeerAddress(datagram.getSocketAddress());
		return packet;
	}
	
	public SocketAddress getLocalSocketAddress() {
		return this.socket.getLocalSocketAddress();
	}
}
