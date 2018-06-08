package lmbenossi.DatagramObjectTransfer;

import java.net.SocketAddress;

public class PacketFactory {
	public static Packet createSynPacket(int seq, SocketAddress peerAddress) {
		Packet packet = new Packet(seq, peerAddress);
		packet.setSyn();
		return packet;
	}
	public static Packet createSynAckPacket(int seq, SocketAddress peerAddress, int ackseq) {
		Packet packet = new Packet(seq, peerAddress);
		packet.setSyn();
		packet.setAck();
		packet.setAckseq(ackseq);
		return packet;
	}
	public static Packet createResPacket(int seq, SocketAddress peerAddress) {
		Packet packet = new Packet(seq, peerAddress);
		packet.setRes();
		return packet;
	}
	public static Packet createDataPacket(int seq, SocketAddress peerAddress, Object object, int dataseq) {
		Packet packet = new Packet(seq, peerAddress);
		packet.setData();
		packet.setObejct(object);
		packet.setDataseq(dataseq);
		return packet;
	}
	public static Packet createDataAckPacket(int seq, SocketAddress peerAddress, int ackseq) {
		Packet packet = new Packet(seq, peerAddress);
		packet.setData();
		packet.setAck();
		packet.setAckseq(ackseq);
		return packet;
	}
}
