package lmbenossi.DatagramObjectTransfer;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;

import lmbenossi.ObjectTransfer.ObjectTransfer;

public class DatagramObjectTransfer implements ObjectTransfer {
	
	private DatagramObjectSocket socket;
	private SocketAddress peerAddress;
	private SocketState state;
	private int seq = 0;
	private ReceiveThread receiveThread = new ReceiveThread(this);
	private SendThread sendThread = new SendThread(this);
	private PacketQueue queue = new PacketQueue();
	private int timeout = 2000;
	private int tries = 3;
	
	public DatagramObjectTransfer(int port) throws SocketException {
		this.socket = new DatagramObjectSocket(port);
		this.peerAddress = null;
		setState(SocketState.CLOSED);
	}
	
	public DatagramObjectTransfer(SocketAddress peerAddress) throws SocketException {
		this.socket = new DatagramObjectSocket();
		this.peerAddress = peerAddress;
		setState(SocketState.CLOSED);
	}

	@Override
	public synchronized void listen() {
		setState(SocketState.LISTEN);
		receiveThread.start();
		try {
			while(this.peerAddress == null) {
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized boolean connect() {
		if(this.peerAddress == null) {
			return false;
		}
		
		this.receiveThread.start();
		
		Packet syn = PacketFactory.createSynPacket(getSeq(), peerAddress);
		
		setState(SocketState.SYN_SENT);
		
		if(sendThread.send(syn) == false) {
			finish();
			return false;
		}
		setState(SocketState.READY);
		return true;
	}

	@Override
	public synchronized boolean send(Object object) {
		Packet packet = PacketFactory.createDataPacket(getSeq(), peerAddress, object);
		
		return sendThread.send(packet);
	}

	@Override
	public synchronized Object receive() {
		return queue.take();
	}

	@Override
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
		
		try {
			Thread thread = this.receiveThread.getThread(); 
			if(thread.isAlive()) {
				thread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramObjectSocket getSocket() {
		return this.socket;
	}
	
	public SocketAddress getPeerAddress() {
		return this.peerAddress;
	}
	public synchronized void setPeerAddress(SocketAddress peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	public SocketState getState() {
		return this.state;
	}
	public synchronized void setState(SocketState state) {
		this.state = state;
		System.out.println(this.state);
	}
	
	public int getSeq() {
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
}
