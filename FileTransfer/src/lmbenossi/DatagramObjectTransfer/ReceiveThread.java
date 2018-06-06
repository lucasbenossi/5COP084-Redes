package lmbenossi.DatagramObjectTransfer;

import java.net.SocketAddress;

public class ReceiveThread implements Runnable {
	private DatagramObjectTransfer dot;
	Thread thread;
	
	public ReceiveThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void run () {
		int lastReceivedSeq = 0;
		
		while(true) {
			try {
				Packet packet = dot.getSocket().receive();
				
				if(packet.isSyn() && !packet.isAck() && dot.getState().equals(SocketState.LISTEN)){
					synchronized (dot) {
						SocketAddress peerAddress = packet.getPeerAddress();
						Packet ack = PacketFactory.createSynAckPacket(dot.getSeq(), peerAddress, packet.getSeq());
						dot.getSocket().send(ack);
						dot.setState(SocketState.READY);
						lastReceivedSeq = packet.getSeq();
						dot.setPeerAddress(peerAddress);
						dot.notify();
					}
				}
				else if(packet.isSyn() && packet.isAck() && dot.getState().equals(SocketState.SYN_SENT)) {
					SendThread sendThread = dot.getSendThread();
					synchronized(sendThread) {
						sendThread.setAck(packet);
						sendThread.notify();
					}
				}
				else if(packet.isData() && !packet.isAck() && dot.getState().equals(SocketState.READY)) {
					Packet ack = PacketFactory.createDataAckPacket(dot.getSeq(), dot.getPeerAddress(), packet.getSeq());
					
					dot.getSocket().send(ack);
					
					if(packet.getSeq() > lastReceivedSeq) {
						lastReceivedSeq = packet.getSeq();
						dot.getQueue().put(packet);
					}
				}
				else if(packet.isData() && packet.isAck() && dot.getState().equals(SocketState.READY) && packet.getAckseq() == dot.getSendThread().getPacket().getSeq()) {
					SendThread sendThread = dot.getSendThread();
					synchronized(sendThread) {
						sendThread.setAck(packet);
						sendThread.notify();
					}
				}
				else if(packet.isRes()) {
					dot.setState(SocketState.CLOSED);
					break;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Thread getThread() {
		return this.thread;
	}
}
