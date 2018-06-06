package lmbenossi.DatagramObjectTransfer;

public class SendThread implements Runnable{
	private Packet packet;
	private Packet ack;
	private DatagramObjectTransfer dot;
	
	public SendThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
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
		return this.ack != null;
	}
	
	public void run() {
		try {
			synchronized(this) {	
				for(int i = 0; i < dot.getTries(); i++) {
					dot.getSocket().send(packet);
					this.wait(dot.getTimeout());
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