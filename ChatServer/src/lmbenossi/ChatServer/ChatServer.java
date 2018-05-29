package lmbenossi.ChatServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;

public class ChatServer {
	private static HashSet<PrintStream> streams;
	private static int count;
	
	public static void main(String[] args) {
		streams = new HashSet<PrintStream>();
        count = 0;
		
        ServerSocket listener = null;
        try {
        	listener = new ServerSocket(2000);
        	
        	System.out.println("Server started");
            
            ServerOutputHandle input = new ServerOutputHandle();
            input.start();
            
            try {
            	while(true) {
                	new ClientHandler(listener.accept()).start();
                }
            } catch (Exception e) {}
            
            input.interrupt();
            listener.close();
        } catch(Exception e) {
        	System.out.println("ERRO: "+e);
        } finally {
        	try {
        		listener.close();
        	} catch(Exception e) {}
        	
        }
    }
	
	private static class ClientHandler extends Thread {
		private Socket socket;
		PrintStream stream;
		int id;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
			try {
				this.stream = new PrintStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("ERRO: "+e);
			}
			ChatServer.streams.add(stream);
			ChatServer.count++;
			this.id = count;
			System.out.println("Client "+this.id+" "+socket.getInetAddress().getHostAddress());
		}
		
		@Override
		public void run() {
			try {
				Scanner scan = new Scanner(socket.getInputStream());
				while(scan.hasNextLine()) {
					String line = scan.nextLine();
					System.out.println(line);
					for(PrintStream stream : streams) {
						stream.println(line);
					}
				}
				scan.close();
			} catch(Exception e) {
				System.out.println("ERRO: "+e);
			} finally {
				synchronized (streams) {
					ChatServer.streams.remove(stream);
				}
				System.out.println("Client "+this.id+" disconnected");
				this.interrupt();
			}
			
		}
	}
	
	private static class ServerOutputHandle extends Thread {
		@Override
		public void run() {
			Scanner scan;
			try {
				scan = new Scanner(System.in);
				while(scan.hasNextLine()) {
					String line = "Server: "+scan.nextLine();
					System.out.println(line);
					for(PrintStream stream : streams) {
						stream.println(line);
					}
				}
				scan.close();
			} catch (Exception e) {
				System.out.println("ERRO: "+e);
			}
		}
	}
}
