package lmbenossi.DatagramObjectTransfer;
import java.util.LinkedList;

public class PacketQueue {
	private LinkedList<Object> queue;
	
	public PacketQueue() {
		this.queue = new LinkedList<Object>();
	}
	
	public synchronized void put(Object object) {
		this.queue.add(object);
		this.notify();
	}
	
	public synchronized Object take() {
		while(queue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		return queue.remove();
	}
}
