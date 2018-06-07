package lmbenossi.DatagramObjectTransfer;

import lmbenossi.Main.Globals;

public class ConnectThread implements Runnable {
	DatagramObjectTransfer dot;
	Packet syn;
	Packet ack;
	
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
		Globals.setLostPackets(lost);
	}
	
	public void setAck(Packet ack) {
		this.ack = ack;
	}
	
	public Packet getSyn() {
		return this.syn;
	}
}
