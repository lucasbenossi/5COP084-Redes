package lmbenossi.DatagramObjectTransfer;

import java.net.SocketAddress;

public class ReceiveThread implements Runnable {
	private DatagramObjectTransfer dot;
	Thread thread;
	OrderedPacketQueue queue;
	
	public ReceiveThread(DatagramObjectTransfer dot) {
		this.dot = dot;
		this.queue = new OrderedPacketQueue(0);
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public Packet receive() {
		return queue.take();
	}
	
	public void run () {
		while(true) {
			try {
				Packet received = dot.getSocket().receive();
				
				if(received.isSyn() && !received.isAck() && dot.getState().equals(SocketState.LISTEN)){
					synchronized (dot) {
						SocketAddress peerAddress = received.getPeerAddress();
						Packet ack = PacketFactory.createSynAckPacket(dot.getSeq(), peerAddress, received.getSeq());
						dot.getSocket().send(ack);
						dot.setState(SocketState.READY);
						dot.setPeerAddress(peerAddress);
						dot.notify();
					}
				}
				else if(received.isSyn() && received.isAck() && dot.getState().equals(SocketState.SYN_SENT)) {
					dot.getConnectThread().setAck(received);
				}
				else if(received.isData() && !received.isAck() && dot.getState().equals(SocketState.READY)) {
					Packet ack = PacketFactory.createDataAckPacket(dot.getSeq(), dot.getPeerAddress(), received.getSeq());
					
					dot.getSocket().send(ack);
										
					queue.put(received);
				}
				else if(received.isData() && received.isAck() && dot.getState().equals(SocketState.READY)) {
					dot.getSendMultiplexer().setAck(received);
				}
				else if(received.isRes()) {
					dot.getSendMultiplexer().stop();
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
