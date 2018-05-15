package lmbenossi.FileTransfer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class TransferPeer {
	private enum SocketState{
		CLOSED, LISTEN, READY, SYN_SENT, RES_SENT;
	}
	
	private DatagramSocket socket;
	private SocketAddress peerAddress;
	private SocketState state;
	private int seq = 0;
	private ReceiveThread receiveThread;
	private SendThread sendThread;
	private PacketQueue queue;
	
	public TransferPeer(int port) throws Exception {
		socket = new DatagramSocket(port);
		setState(SocketState.LISTEN);

		this.queue = new PacketQueue();
		
		receiveThread = new ReceiveThread();
		receiveThread.start();
		
		sendThread = new SendThread();
	}
	
	public TransferPeer(SocketAddress peerAddress) throws Exception {
		this.socket = new DatagramSocket();
		this.peerAddress = peerAddress;
		setState(SocketState.CLOSED);
		
		this.queue = new PacketQueue();
		
		receiveThread = new ReceiveThread();
		receiveThread.start();
		
		sendThread = new SendThread();
	}
	
	private synchronized void setState(SocketState state) {
		this.state = state;
		System.out.println(this.state);
	}
	public SocketState getState() {
		return this.state;
	}
	
	private synchronized int getSeq() {
		seq++;
		return seq;
	}
	
	private class ReceiveThread extends Thread {
		public void run () {
			byte[] buffer = new byte[2048];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
			int lastReceivedSeq = 0;
			
			while(true) {
				try {
					socket.receive(datagram);
					
					Packet packet = (Packet) ByteUtils.toObject(datagram.getData());
					System.out.println("Received " + packet);
					
					if(packet.isSyn() && !packet.isAck()){
						peerAddress = datagram.getSocketAddress();
						Packet ack = new Packet(getSeq());
						ack.setSyn();
						ack.setAck();
						ack.setAckseq(packet.getSeq());
						DatagramUtils.send(socket, ack, peerAddress);
						setState(SocketState.READY);
						lastReceivedSeq = packet.getSeq();
					}
					else if(packet.isSyn() && packet.isAck() && state.equals(SocketState.SYN_SENT)) {
						synchronized(sendThread) {
							sendThread.setAck(packet);
							sendThread.notify();
						}
					}
					else if(packet.isData() && !packet.isAck() && state.equals(SocketState.READY)) {
						Packet ack = new Packet(getSeq());
						ack.setData();
						ack.setAck();
						ack.setAckseq(packet.getSeq());
						
						DatagramUtils.send(socket, ack, peerAddress);
						
						if(packet.getSeq() > lastReceivedSeq) {
							lastReceivedSeq = packet.getSeq();
							queue.put(packet.getObject());
						}
					}
					else if(packet.isData() && packet.isAck() && state.equals(SocketState.READY) && packet.getAckseq() == sendThread.getPacket().getSeq()) {
						synchronized(sendThread) {
							sendThread.setAck(packet);
							sendThread.notify();
						}
					}
					else if(packet.isRes()) {
						setState(SocketState.CLOSED);
						break;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class SendThread implements Runnable{
		private Packet packet;
		private Packet ack;
		
		public boolean send(Packet packet) {
			this.packet = packet;
			this.ack = null;
			Thread thread = new Thread(this);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(this.ack == null) {
				return false;
			}
			return true;
		}
		
		public void run() {
			try {
				synchronized(this) {	
					for(int i = 0; i < Main.TENTATIVAS; i++) {
						DatagramUtils.send(socket, packet, peerAddress);
						this.wait(Main.WAIT_TIMEOUT);
						if(this.ack != null) {
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void setAck(Packet ack) {
			this.ack = ack;
		}
		
		public Packet getPacket() {
			return this.packet;
		}
	}
	
	public boolean start() {
		Packet syn = new Packet(getSeq());
		syn.setSyn();
		
		setState(SocketState.SYN_SENT);
		
		if(sendThread.send(syn) == false) {
			finish();
			return false;
		}
		setState(SocketState.READY);
		return true;
	}
	
	public boolean send(Object object) {
		Packet packet = new Packet(getSeq());
		packet.setData();
		packet.setObejct(object);
		
		return sendThread.send(packet);
	}
	
	public Object receive() {
		return queue.take();
	}
	
	public void finish() {
		Packet res = new Packet(getSeq());
		res.setRes();
		
		setState(SocketState.RES_SENT);
		
		try {
			DatagramUtils.send(socket, res, peerAddress);
			DatagramUtils.send(socket, res, socket.getLocalSocketAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
