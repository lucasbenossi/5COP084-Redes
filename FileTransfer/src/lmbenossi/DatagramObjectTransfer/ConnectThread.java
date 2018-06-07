package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class ConnectThread implements Runnable {
	private DatagramObjectTransfer dot;
	private Packet syn;
	private Packet ack;
	private Object lock = new Object();
	
	public ConnectThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public boolean sendSyn(Packet syn) {
		this.syn = syn;
		this.ack = null;
		
		Thread thread = new Thread(this);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return this.ack != null;
	}
	
	@Override
	public void run() {
		int lost = 0;
		try {
			synchronized(lock) {	
				for(int i = 0; i < dot.getTries(); i++) {
					dot.getSocket().send(this.syn);
					lost++;
					lock.wait(dot.getTimeout());
					if(this.ack != null) {
						lost--;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Globals.incrementLostPackets(lost);
	}
	
	public void setAck(Packet ack) {
		synchronized (lock) {			
			this.ack = ack;
			lock.notify();
		}
	}
	
	public Packet getSyn() {
		return this.syn;
	}
}
