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
	private int dataseq = -1;
	private ReceiveThread receiveThread = new ReceiveThread(this);
	private SendMultiplexer sendMultiplexer = new SendMultiplexer(this);
	private ConnectThread connectThread = new ConnectThread(this);
	private int timeout = 300;
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
		this.receiveThread.start();
		try {
			while(this.peerAddress == null) {
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.sendMultiplexer.start();
	}
	
	@Override
	public boolean connect() {
		if(this.peerAddress == null) {
			return false;
		}
		
		this.receiveThread.start();
		
		Packet syn = PacketFactory.createSynPacket(getSeq(), peerAddress);
		
		setState(SocketState.SYN_SENT);
		
		if(! connectThread.sendSyn(syn)) {
			finish();
			return false;
		}
		setState(SocketState.READY);
		this.sendMultiplexer.start();
		return true;
	}

	@Override
	public boolean send(Object object) {
		Packet packet = PacketFactory.createDataPacket(getSeq(), peerAddress, object, getDataseq());
		
		sendMultiplexer.send(packet);
		
		return true;
	}

	@Override
	public Object receive() {
		return receiveThread.receive().getObject();
	}

	@Override
	public void finish() {
		int seq = getSeq();
		Packet resRemote = PacketFactory.createResPacket(seq, peerAddress);
		Packet resLocal = PacketFactory.createResPacket(seq, socket.getLocalSocketAddress());
		
		sendMultiplexer.waitToFinish();
		
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
	public void setPeerAddress(SocketAddress peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	public SocketState getState() {
		return this.state;
	}
	public void setState(SocketState state) {
		this.state = state;
		System.out.println(this.state);
	}
	
	public int getSeq() {
		seq++;
		return seq;
	}
	
	public int getDataseq() {
		dataseq++;
		return dataseq;
	}
	
	public ReceiveThread getReceiveThread() {
		return this.receiveThread;
	}
	
	public SendMultiplexer getSendMultiplexer() {
		return this.sendMultiplexer;
	}
	
	public ConnectThread getConnectThread() {
		return this.connectThread;
	}
	
	public int getTimeout() {
		return this.timeout;
	}
	
	public int getTries() {
		return this.tries;
	}
	
	public boolean error() {
		return this.sendMultiplexer.error();
	}
}
