package lmbenossi.truco;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Card {
	public static String toString(JsonArray cards) {
		String string = new String();
		for(JsonElement element : cards) {
			JsonObject card = element.getAsJsonObject();
			string += Card.toString(card);
		}
		return string;
	}
	
	private static String toString(JsonObject card) {
		String suit = Suit.getUnicodeChar(card.get("Suit").getAsString());
		String face = Face.getUnicodeChar(card.get("Face").getAsInt());
		String unicode = "1F0" + suit + face;
		int codePoint = Integer.parseInt(unicode, 16);
		if(codePoint >= 0x1F0A0 && codePoint <= 0x1F0FF) {
			return new String(Character.toChars(codePoint)) + " ";
		}
		return "";
	}
	
	private static class Suit {
		public static String getUnicodeChar(String suit) {
			if(suit.equals("Spade")) {
				return "A";
			}
			else if(suit.equals("Heart")) {
				return "B";
			}
			else if(suit.equals("Diamond")) {
				return "C";
			}
			else if(suit.equals("Club")) {
				return "D";
			}
			return "";
		}
	}
	private static abstract class Face {
		public static String getUnicodeChar(int face) {
			if(face == 8) {
				return "D"; // Q
			}
			else if(face == 9) {
				return "B"; // J
			}
			else if(face == 10) {
				return "E"; // K
			}
			return Integer.toString(face);
		}
	}
}
