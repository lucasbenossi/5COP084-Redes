package lmbenossi.DatagramObjectTransfer;
import java.util.LinkedList;

public class PacketQueue {
	private LinkedList<Packet> queue;
	
	public PacketQueue() {
		this.queue = new LinkedList<>();
	}
	
	public synchronized void put(Packet packet) {
		this.queue.add(packet);
		this.notify();
	}
	
	public synchronized Packet take() {
		while(queue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		return queue.remove();
	}
}
