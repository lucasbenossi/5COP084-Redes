package lmbenossi.DatagramObjectTransfer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lmbenossi.Main.Globals;

public class SendThread implements Runnable{
	private Packet packet;
	private Packet ack;
	private DatagramObjectTransfer dot;
	private Thread thread;
	private PacketQueue queue = new PacketQueue();
	private ReentrantLock lock = new ReentrantLock();
	private Condition finished = lock.newCondition();
	private Condition ackReceived = lock.newCondition();
	private boolean error = false;
	
	public SendThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void waitToFinish() {
		if(thread != null && thread.isAlive()) {
			lock.lock();
			queue.put(null);
			try {
				finished.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.unlock();
		}
	}
	
	public void stop() {
		this.queue.put(PacketFactory.createResPacket(0, null));
	}
	
	public void send(Packet packet) {
		if(packet != null) {
			queue.put(packet);
		}
	}
	
	public void run() {
		while(true) {
			packet = queue.take();
			ack = null;
			int lost = 0;
			
			if(packet == null) {
				signal();
				continue;
			}
			
			if(packet.isRes()) {
				signal();
				break;
			}
			
			try {
				lock.lock();
				
				for(int i = 0; i < dot.getTries(); i++) {
					dot.getSocket().send(packet);
					lost++;
					ackReceived.await(dot.getTimeout(), TimeUnit.MILLISECONDS);
					if(this.ack != null) {
						lost--;
						break;
					}
				}
				
				lock.unlock();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(this.ack == null) {
				this.error = true;
				signal();
				break;
			}
			
			Globals.incrementLostPackets(lost);
		}
	}

	private void signal() {
		lock.lock();
		finished.signal();
		lock.unlock();
	}
	
	public void setAck(Packet ack) {
		lock.lock();
		
		this.ack = ack;
		ackReceived.signal();
		
		lock.unlock();
	}
	
	public boolean error() {
		return this.error;
	}
}