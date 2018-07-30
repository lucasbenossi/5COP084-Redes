package lmbenossi.truco;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
	public static void main(String[] args) {
		JsonParser parser = new JsonParser();
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Scanner stdIn = new Scanner(System.in);
		
		System.out.print("Insira o ip do servidor: ");
		String host = stdIn.nextLine();
		System.out.println();
		
		try {
			Socket socket = new Socket(host, 2000);
			Scanner tcpIn = new Scanner(socket.getInputStream());
			PrintStream tcpOut = new PrintStream(socket.getOutputStream(), true);
			
			int id = Integer.parseInt(tcpIn.nextLine());
			
			while(tcpIn.hasNextLine()) {
				String json = tcpIn.nextLine();
				JsonObject object = parser.parse(json).getAsJsonObject();
				JsonArray playerStates = object.get("PlayerStates").getAsJsonArray();
				JsonObject player = null;
				for(JsonElement element : playerStates ) {
					if(element.getAsJsonObject().get("ID").getAsInt() == id) {
						player = element.getAsJsonObject();
					}
				}
				
				System.out.println("Score:");
				System.out.println("\tTentos: " + player.get("Tentos").toString());
				System.out.println("\tMãos: " + player.get("Maos").toString());
				System.out.println("Mesa: " + Card.toString(object.get("TableCards").getAsJsonArray()));
				System.out.println("Cartas: " + Card.toString(player.get("Cards").getAsJsonArray()));
				
				if(player.get("Active").getAsBoolean()) {
					System.out.print("Insira a jogada: ");
					tcpOut.println(stdIn.nextInt() - 1);
				}
				
				System.out.println();
			}
			
			stdIn.close();
			tcpIn.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
