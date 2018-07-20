package client;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import framework.Const;

public class Main {
	
	private static ClientMessageThread client;
	private static boolean on;
	private static final int TIME_TO_START = 10000; //ms
	private static Timer timer;
	
	public static void main(String[] args) {
		System.out.println("CLIENT");
		
		Scanner scanner = new Scanner(System.in);
		
		timer = new Timer();
		on = false;
		
		try {
			while(scanner.hasNextLine()){
				String command = scanner.nextLine();
				System.out.println("Command: " + command);
				
				if(!on && (command.matches(Const.RUN_CLIENT_REGEX))) {
					
					String params[] = command.split(" ");
					
					InetSocketAddress server_addr_message = new InetSocketAddress(params[2], Const.SERVER_MESSAGE_PORT);
					InetSocketAddress server_addr_file = new InetSocketAddress(params[2], Const.SERVER_FILE_PORT);
					
					client = new ClientMessageThread(params[1], server_addr_message, server_addr_file);
					client.start();
					
					System.out.println("> Connecting...");
					
					//Verifica se recebeu 'connection approved' TIME_TO_START ms dps
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							System.out.println("> Checking if received 'connection approved' after 10 seconds");
							
							if(client.isConnectionApproved()) {
								on = true;
								System.out.println("> received");
							}
							else {
								System.out.println("> not received");
								System.out.println("> Disconnected!");
								
								client.disconnect();
							}
						}
					}, TIME_TO_START);
					
				}
				else if(on && (command.matches(Const.RUN_CLIENT_REGEX))) {
					System.out.println("Already connected!");
				}
				else if(command.matches(Const.STOP_REGEX)) {
					if(client.isAlive()) {
						client.disconnect();
					}
					
					break;
				}
				else if(on && command.equals("list users")) {
					client.requestListUsers();
				}
				else if(on && command.equals("list files")) {
					client.requestListFiles();
				}
				else if(on && command.matches(Const.PING_REGEX)) {
					String[] params = command.split(" ")[1].replaceAll(" ", "").split(":");					
					client.ping(new InetSocketAddress(params[0], Integer.parseInt(params[1])));
				}
				else if(on && command.equals("ping server")){
					client.ping(client.getServerAddressMessage());
				}
				else if(on && command.matches(Const.DOWNLOAD_REGEX)) {
					int fileNumber = Integer.parseInt(command.split(" ")[1]);
					client.requestDownloadFile(fileNumber);
				}
				else if(on && command.matches(Const.UPLOAD_REGEX)) {
					client.requestUploadFile();
				}
				else {
					System.out.println("Invalid command!");
				}
			}
			
		} catch (SocketException e) {
			System.err.println("SocketException= " + e.getMessage());
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException= " + e.getMessage());
		} catch (Exception e){
			System.err.println("Exception= " + e.getMessage());
		} finally {
			timer.cancel();
			
			if(scanner != null) {
				scanner.close();
			}
		        
			on = false;
			System.out.println("Programa 'client' encerrado");
		}
		
		
	}
	
	public static void cancelTimer() {
		timer.cancel();
		on = true;
	}
	
	
}
