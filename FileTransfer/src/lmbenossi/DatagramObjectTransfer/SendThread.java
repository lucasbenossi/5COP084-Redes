package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class SendThread implements Runnable{
	private Packet packet;
	private Packet ack;
	private DatagramObjectTransfer dot;
	private Thread thread;
	private PacketQueue queue = new PacketQueue();
	private Thread waiting;
	
	public SendThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void waitToFinish() {
		waiting = Thread.currentThread();
		synchronized (waiting) {
			queue.put(null);
			try {
				waiting.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		this.queue.put(PacketFactory.createResPacket(0, null));
	}
	
	public boolean send(Packet packet) {
		if(!thread.isAlive()) {
			return false;
		}
		
		if(packet == null) {
			return false;
		}
		
		queue.put(packet);
		
		return true;
	}
	
	public void run() {
		while(true) {
			packet = queue.take();
			ack = null;
			int lost = 0;
			
			if(packet == null) {
				synchronized (waiting) {
					waiting.notify();
				}
				continue;
			}
			
			if(packet.isRes()) {
				break;
			}
			
			try {
				synchronized(this) {	
					for(int i = 0; i < dot.getTries(); i++) {
						dot.getSocket().send(packet);
						lost++;
						this.wait(dot.getTimeout());
						if(this.ack != null) {
							lost--;
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(this.ack == null) {
				break;
			}
			
			Globals.incrementLostPackets(lost);
		}
	}
	
	public void setAck(Packet ack) {
		this.ack = ack;
	}
	
	public Packet getPacket() {
		return this.packet;
	}
	
	public Thread getThread() {
		return this.thread;
	}
}