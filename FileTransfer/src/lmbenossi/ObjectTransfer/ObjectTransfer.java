package lmbenossi.ObjectTransfer;

public interface ObjectTransfer {
	public boolean start();
	public boolean send(Object object);
	public Object receive();
	public void finish();
}
