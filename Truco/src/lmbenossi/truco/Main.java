package lmbenossi.truco;

import java.io.PrintStream;
import java.io.PrintWriter;
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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		try {
			Socket socket = new Socket("187.18.122.147", 2000);
			Scanner tcpIn = new Scanner(socket.getInputStream());
			PrintStream tcpOut = new PrintStream(socket.getOutputStream(), true);
			Scanner stdIn = new Scanner(System.in);
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
				
				if(player.get("Active").getAsBoolean()) {
					JsonArray cards = player.get("Cards").getAsJsonArray();
					System.out.println(gson.toJson(cards));
					System.out.println("Insira a jogada");
					tcpOut.println(stdIn.nextLine());
				}
			}
			
			stdIn.close();
			tcpIn.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
