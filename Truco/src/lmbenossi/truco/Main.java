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
		Scanner stdIn = new Scanner(System.in);
		
		System.out.print("Insira o ip do servidor: ");
		String host = stdIn.nextLine();
//		String host = "localhost";
		System.out.println();
		
		try {
			Socket socket = new Socket(host, 2000);
			Scanner tcpIn = new Scanner(socket.getInputStream());
			PrintStream tcpOut = new PrintStream(socket.getOutputStream(), true);
			
			int id = Integer.parseInt(tcpIn.nextLine());
			
			while(tcpIn.hasNextLine()) {
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = tcpIn.nextLine();
				JsonObject object = parser.parse(json).getAsJsonObject();
//				String pretty = gson.toJson(object);
//				System.err.println(pretty);
				JsonArray playerStates = object.get("PlayerStates").getAsJsonArray();
				System.out.println("Score\tPlayer\tTentos\tMãos");
				JsonObject player = null;
				for(JsonElement element : playerStates ) {
					JsonObject playerFor = element.getAsJsonObject();
					int maosFor = playerFor.get("Maos").getAsInt();
					int tentosFor = playerFor.get("Tentos").getAsInt();
					int idFor = playerFor.get("ID").getAsInt();
					if(idFor == id) {
						player = playerFor;
					}
					System.out.printf("\t%d\t%d\t%d\n", idFor + 1, tentosFor, maosFor);
				}
				System.out.println("Mesa:\t" + Card.toString(object.get("TableCards").getAsJsonArray()));
				System.out.println("Cartas:\t" + Card.toString(player.get("Cards").getAsJsonArray()));
				
				if(player.get("Active").getAsBoolean()) {
					System.out.print("Insira a jogada: ");
					tcpOut.println(stdIn.nextInt() - 1);
//					tcpOut.println(1);
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
