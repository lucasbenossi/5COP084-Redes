package chatClient;

import java.util.Scanner;
import java.io.PrintStream;
import java.lang.Exception;
import java.net.Socket;

public class ChatClient {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Name: ");
		String name = scan.nextLine();
		
		Socket socket = null;
		try {
			socket = new Socket("localhost", 2000);
			
			OutputHandle output = new OutputHandle(socket, scan, name);
			InputHandle input = new InputHandle(socket);
			
			output.start();
			input.start();
			
			try {
				output.join();
			} catch (Exception e) {
			} finally {
				input.interrupt();
			}
			
			socket.close();
		} catch (Exception e) {
			System.out.println("ERRO: "+e);
		}
		
		scan.close();
	}
	
	private static class OutputHandle extends Thread {
		private Socket socket;
		private Scanner scan;
		private String name;
		
		public OutputHandle(Socket socket, Scanner scan, String name) {
			this.socket = socket;
			this.scan = scan;
			this.name = name;
		}
		
		@Override
		public void run() {
			try {
				PrintStream stream = new PrintStream(socket.getOutputStream());
				
				stream.println(name+" joined the session");
				
				while(scan.hasNextLine()) {
					stream.println(name+": "+scan.nextLine());
				}
			} catch(Exception e) {
				System.out.println("ERRO: "+e);
			}
		}

	}
	
	private static class InputHandle extends Thread {
		private Socket socket;
		
		public InputHandle(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			Scanner scan;
			try {
				scan = new Scanner(socket.getInputStream());
				while(scan.hasNextLine()) {
					System.out.println(scan.nextLine());
				}
				scan.close();
			} catch (Exception e) {
				System.out.println("ERRO: "+e);
			}
		}
	}
}
