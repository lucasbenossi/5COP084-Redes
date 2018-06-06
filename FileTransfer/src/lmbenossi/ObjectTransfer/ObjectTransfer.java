package lmbenossi.ObjectTransfer;

public interface ObjectTransfer {
	public void listen();
	public boolean connect();
	public boolean send(Object object);
	public Object receive();
	public void finish();
}
