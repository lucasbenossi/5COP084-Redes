package lmbenossi.DatagramObjectTransfer;

import java.io.IOException;
import java.net.SocketAddress;
import lmbenossi.ObjectTransfer.ObjectTransfer;

public class DatagramObjectTransfer implements ObjectTransfer {
	
	private DatagramObjectSocket socket;
	private SocketAddress peerAddress;
	private SocketState state;
	private int seq = 0;
	private ReceiveThread receiveThread;
	private SendThread sendThread;
	private PacketQueue queue;
	private int timeout = 2000;
	private int tries = 3;
	
	public DatagramObjectTransfer(int port) throws Exception {
		socket = new DatagramObjectSocket(port);
		setState(SocketState.LISTEN);

		this.queue = new PacketQueue();
		
		receiveThread = new ReceiveThread(this);
		receiveThread.start();
		
		sendThread = new SendThread(this);
	}
	
	public DatagramObjectTransfer(SocketAddress peerAddress) throws Exception {
		this.socket = new DatagramObjectSocket();
		this.peerAddress = peerAddress;
		setState(SocketState.CLOSED);
		
		this.queue = new PacketQueue();
		
		receiveThread = new ReceiveThread(this);
		receiveThread.start();
		
		sendThread = new SendThread(this);
	}
	
	public DatagramObjectSocket getSocket() {
		return this.socket;
	}
	
	public SocketAddress getPeerAddress() {
		return this.peerAddress;
	}
	public void setPeerAddress(SocketAddress peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	public SocketState getState() {
		return this.state;
	}
	public synchronized void setState(SocketState state) {
		this.state = state;
		System.out.println(this.state);
	}
	
	public synchronized int getSeq() {
		seq++;
		return seq;
	}
	
	public ReceiveThread getReceiveThread() {
		return this.receiveThread;
	}
	
	public SendThread getSendThread() {
		return this.sendThread;
	}
	
	public PacketQueue getQueue() {
		return this.queue;
	}
	
	public int getTimeout() {
		return this.timeout;
	}
	
	public int getTries() {
		return this.tries;
	}
	
	public boolean start() {
		Packet syn = PacketFactory.createSynPacket(getSeq(), peerAddress);
		
		setState(SocketState.SYN_SENT);
		
		if(sendThread.send(syn) == false) {
			finish();
			return false;
		}
		setState(SocketState.READY);
		return true;
	}
	
	public boolean send(Object object) {
		Packet packet = PacketFactory.createDataPacket(getSeq(), peerAddress, object);
		
		return sendThread.send(packet);
	}
	
	public Object receive() {
		return queue.take();
	}
	
	public void finish() {
		int seq = getSeq();
		Packet resRemote = PacketFactory.createResPacket(seq, peerAddress);
		Packet resLocal = PacketFactory.createResPacket(seq, socket.getLocalSocketAddress());
		
		setState(SocketState.RES_SENT);
		
		try {
			socket.send(resRemote);
			socket.send(resLocal);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
