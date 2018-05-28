package lmbenossi.FileTransfer;
import java.util.Scanner;

public class Main {
	public static final int PARTLEN = 1024;
	public static final int PORT = 2000;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("1 - Sender");
		System.out.println("2 - Receiver");
		System.out.print("Escolha o modo de operação: ");
		int mode = scan.nextInt();
		scan.nextLine();
		
		if(mode == 1) {
			Sender.run(scan);
		} 
		else if(mode == 2) {
			Receiver.run(scan);
		}
		else {
			System.out.println("Modo inválido.");
		}
		
		scan.close();
	}
}





























