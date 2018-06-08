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
	
	public SendThread(DatagramObjectTransfer dot) {
		this.dot = dot;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void waitToFinish() {
		lock.lock();
		queue.put(null);
		try {
			finished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
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
				lock.lock();
				finished.signal();
				lock.unlock();
				break;
			}
			
			if(packet.isRes()) {
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
				break;
			}
			
			Globals.incrementLostPackets(lost);
		}
	}
	
	public void setAck(Packet ack) {
		lock.lock();
		
		this.ack = ack;
		ackReceived.signal();
		
		lock.unlock();
	}
}