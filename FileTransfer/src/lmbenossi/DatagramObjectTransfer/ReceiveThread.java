package lmbenossi.DatagramObjectTransfer;

import java.net.SocketAddress;

public class ReceiveThread implements Runnable {
	private DatagramObjectTransfer dot;
	Thread thread;
	PacketQueue queue = new PacketQueue();
	
	public ReceiveThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public Packet receive() {
		return queue.take();
	}
	
	public void run () {
		int lastReceivedSeq = 0;
		
		while(true) {
			try {
				Packet received = dot.getSocket().receive();
				
				if(received.isSyn() && !received.isAck() && dot.getState().equals(SocketState.LISTEN)){
					synchronized (dot) {
						SocketAddress peerAddress = received.getPeerAddress();
						Packet ack = PacketFactory.createSynAckPacket(dot.getSeq(), peerAddress, received.getSeq());
						dot.getSocket().send(ack);
						dot.setState(SocketState.READY);
						lastReceivedSeq = received.getSeq();
						dot.setPeerAddress(peerAddress);
						dot.notify();
					}
				}
				else if(received.isSyn() && received.isAck() && dot.getState().equals(SocketState.SYN_SENT) && received.getAckseq() == dot.getConnectThread().getSyn().getSeq()) {
					ConnectThread connectThread = dot.getConnectThread();
					synchronized(connectThread) {
						connectThread.setAck(received);
						connectThread.notify();
					}
				}
				else if(received.isData() && !received.isAck() && dot.getState().equals(SocketState.READY)) {
					Packet ack = PacketFactory.createDataAckPacket(dot.getSeq(), dot.getPeerAddress(), received.getSeq());
					
					dot.getSocket().send(ack);
					
					if(received.getSeq() > lastReceivedSeq) {
						lastReceivedSeq = received.getSeq();
						queue.put(received);
					}
				}
				else if(received.isData() && received.isAck() && dot.getState().equals(SocketState.READY) && received.getAckseq() == dot.getSendThread().getPacket().getSeq()) {
					SendThread sendThread = dot.getSendThread();
					synchronized(sendThread) {
						sendThread.setAck(received);
						sendThread.notify();
					}
				}
				else if(received.isRes()) {
					dot.getSendThread().stop();
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
