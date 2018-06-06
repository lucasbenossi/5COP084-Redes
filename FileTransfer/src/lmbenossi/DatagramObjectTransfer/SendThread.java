package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class SendThread implements Runnable{
	private Packet packet;
	private Packet ack;
	private DatagramObjectTransfer dot;
	private Thread thread;
	
	public SendThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public boolean send(Packet packet) {
		this.packet = packet;
		this.ack = null;
		thread = new Thread(this);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.ack != null;
	}
	
	public void run() {
		int lost = 0;
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
		Globals.setLostPackets(lost);
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