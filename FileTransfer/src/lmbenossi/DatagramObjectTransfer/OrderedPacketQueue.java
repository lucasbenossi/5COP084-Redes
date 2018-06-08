package lmbenossi.DatagramObjectTransfer;

import java.util.Hashtable;

public class OrderedPacketQueue {
	private Hashtable<Integer, Packet> table;
	private Integer dataseq;
	
	public OrderedPacketQueue(int initialDataseq) {
		this.table = new Hashtable<>(20000);
		this.dataseq = new Integer(initialDataseq);
	}
	
	public void put(Packet packet) {
		Integer dataseq = new Integer(packet.getDataseq());
		
		synchronized (table) {
			table.put(dataseq, packet);
			if(dataseq.equals(this.dataseq)) {
				table.notify();
			}
		}
	}
	
	public Packet take() {
		synchronized (table) {
			Packet packet = table.get(this.dataseq);
			
			if(packet == null) {
				try {
					table.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				packet = table.get(this.dataseq);
			}
			this.dataseq++;
			
			return packet;
		}
	}
}
