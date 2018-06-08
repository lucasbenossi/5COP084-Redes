package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class ConnectThread implements Runnable {
	private DatagramObjectTransfer dot;
	private Packet syn;
	private Packet ack;
	
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
			synchronized(this) {	
				for(int i = 0; i < dot.getTries(); i++) {
					dot.getSocket().send(this.syn);
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
		Globals.incrementLostPackets(lost);
	}
	
	public synchronized void setAck(Packet ack) {
		this.ack = ack;
		if (this.ack.getAckseq() == this.syn.getSeq()) {
			this.notify();
		}
	}
}
