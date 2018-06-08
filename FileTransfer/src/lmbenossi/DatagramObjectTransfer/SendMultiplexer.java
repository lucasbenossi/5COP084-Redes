package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class SendMultiplexer implements Runnable {
	private SendThread[] threads;
	private int n = Globals.WINDOW;
	private PacketQueue ackQueue;
	private Thread thread;
	
	public SendMultiplexer(DatagramObjectTransfer dot) {
		this.threads = new SendThread[n];
		this.ackQueue = new PacketQueue();
		
		for(int i = 0; i < n; i++) {
			threads[i] = new SendThread(dot);
		}
	}
	
	public void start() {
		for(SendThread thread : threads) {
			thread.start();
		}
		this.thread = new Thread(this);
		thread.start();
	}
	
	public void waitToFinish() {
		for(SendThread thread : threads) {
			thread.waitToFinish();
		}
	}
	
	public void stop() {
		for(SendThread thread : threads) {
			thread.stop();
		}
		ackQueue.put(null);
	}
	
	public void send(Packet packet) {
		if(packet != null) {
			int i = packet.getSeq() % n;
			threads[i].send(packet);
		}
	}
	
	@Override
	public void run() {
		while(true) {
			Packet ack = ackQueue.take();
			
			if(ack == null) {
				break;
			}
			
			int i = ack.getAckseq() % n;
			threads[i].setAck(ack);
		}
	}
	
	public void setAck(Packet ack) {
		ackQueue.put(ack);
	}
	
	public boolean error() {
		boolean error = false;
		for(SendThread thread : threads) {
			error |= thread.error();
		}
		return error;
	}
}
