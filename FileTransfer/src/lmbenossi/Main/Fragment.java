package lmbenossi.Main;
import java.io.Serializable;

public class Fragment implements Serializable {
	private static final long serialVersionUID = 5055889592140581344L;
	private int total;
	private int num;
	private byte[] data;
	
	public Fragment(int total, int num, byte[] data) {
		this.total = total;
		this.num = num;
		this.data = data;
	}
	
	public int getTotal() {
		return this.total;
	}
	public int getNum() {
		return this.num;
	}
	public byte[] getData() {
		return this.data;
	}
	
	@Override
	public String toString() {
		return "Fragment num=" + getNum() + " total=" + getTotal();
	}
}
