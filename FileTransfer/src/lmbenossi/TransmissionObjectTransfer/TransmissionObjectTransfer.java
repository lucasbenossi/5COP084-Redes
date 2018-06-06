package lmbenossi.TransmissionObjectTransfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import lmbenossi.ObjectTransfer.ObjectTransfer;

public class TransmissionObjectTransfer implements ObjectTransfer {
	Socket socket;
	SocketAddress peerAddress;
	int port;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public TransmissionObjectTransfer(int port) throws IOException {
		this.peerAddress = null;
		this.port = port;
	}
	
	public TransmissionObjectTransfer(SocketAddress peerAddress) {
		this.peerAddress = peerAddress;
		this.socket = new Socket();
	}
	
	@Override
	public void listen() {
		try {
			ServerSocket listener = new ServerSocket(this.port);
			System.out.println("LISTEN");
			this.socket = listener.accept();
			System.out.println("CONNECTED");
			this.peerAddress = socket.getRemoteSocketAddress();
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean connect() {
		if(this.peerAddress == null) {
			return false;
		}
		try {
			this.socket.connect(this.peerAddress);
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.ois = new ObjectInputStream(this.socket.getInputStream());
			System.out.println("CONNECTED");
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean send(Object object) {
		try {
			this.oos.writeObject(object);
			System.out.println("Sent " + object.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public Object receive() {
		try {
			Object object = ois.readObject();
			System.out.println("Received " + object.toString());
			return object;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void finish() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
