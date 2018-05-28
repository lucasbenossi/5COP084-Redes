package lmbenossi.DatagramObjectTransfer;
import java.io.Serializable;
import java.net.SocketAddress;

public class Packet implements Serializable {
	private static final long serialVersionUID = 1996373901054374743L;
	private int seq;
	private SocketAddress peerAddress;
	private boolean syn;
	private boolean res;
	private boolean data;
	private boolean ack;
	private Object object;
	private int ackseq;
	
	public Packet(int seq, SocketAddress peerAddress) {
		this.seq = seq;
		this.peerAddress = peerAddress;
		this.syn = false;
		this.res = false;
		this.data = false;
		this.ack = false;
		this.object = null;
		this.ackseq = 0;
	}
	
	public int getSeq() {
		return this.seq;
	}
	
	public SocketAddress getPeerAddress() {
		return this.peerAddress;
	}
	public void setPeerAddress(SocketAddress peerAddress) {
		this.peerAddress = peerAddress;
	}

	public boolean isSyn() {
		return syn;
	}
	public void setSyn(boolean syn) {
		this.syn = syn;
	}
	public void setSyn() {
		this.syn = true;
	}

	public boolean isRes() {
		return res;
	}
	public void setRes(boolean res) {
		this.res = res;
	}
	public void setRes() {
		this.res = true;
	}

	public boolean isData() {
		return data;
	}
	public void setData(boolean data) {
		this.data = data;
	}
	public void setData() {
		this.data = true;
	}

	public boolean isAck() {
		return ack;
	}
	public void setAck(boolean ack) {
		this.ack = ack;
	}
	public void setAck() {
		this.ack = true;
	}

	public Object getObject() {
		return this.object;
	}
	public void setObejct(Object object) {
		this.object = object;
	}
	
	public int getAckseq() {
		return this.ackseq;
	}
	public void setAckseq(int ackseq) {
		this.ackseq = ackseq;
	}
	
	@Override
	public String toString() {
		String string = new String();
		
		if(isSyn() && !isAck()) {
			string += "SYN ";
		}
		if(isSyn() && isAck()) {
			string += "SYN-ACK ";
		}
		if(isRes()) {
			string += "RES ";
		}
		if(isData() && !isAck()) {
			string += "DATA ";
		}
		if(isData() && isAck()) {
			string += "DATA-ACK ";
		}
		string += "seq=" + getSeq() + " ";
		if(isAck()) {
			string += "ackseq=" + getAckseq() + " ";
		}
		if(getObject() != null) {
			string += getObject().toString();
		}
		
		return string.trim();
	}
}
