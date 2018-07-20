package server;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import framework.Const;

public class Main {
	
	private static boolean on;
	private static ServerMessageThread server;
	private static final int TIME_TO_START = 500; //ms
	private static Timer timer;
	
	public static void main(String[] args) {
		
		System.out.println("SERVER");
		
		Scanner scanner = new Scanner(System.in);
		timer = new Timer();
		on = false;
		
		try {
			while(scanner.hasNextLine()){
				String command = scanner.nextLine();
				System.out.println("Command: " + command);
				
				if(!on && (command.matches(Const.RUN_SERVER_REGEX))) {
					server = new ServerMessageThread();
					server.start();
					
					System.out.println("> Connecting...");
					
					//Verifica se recebeu 'connection approved' 10s dps
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							System.out.println("> Checking if received 'connection approved' after 10 seconds");
							
							if(server.isConnectionApproved()) {
								on = true;
								System.out.println("> received");
							}
							else {
								System.out.println("> not received");
								System.out.println("> Disconnected!");
								
								server.disconnect();
							}
						}
					}, TIME_TO_START);
					
				}
				else if(on && (command.matches(Const.RUN_SERVER_REGEX))) {
					System.out.println("Already connected!");
				}
				else if(command.matches(Const.STOP_REGEX)) {
					if(server.isAlive()) {
						server.disconnect();
					}
					
					break;
				}
				else if(command.matches(Const.PING_REGEX)) {
					String[] params = command.split(" ")[1].replaceAll(" ", "").split(":");					
					server.ping(new InetSocketAddress(params[0], Integer.parseInt(params[1])));
				}
				else if(command.equals("show users")) {
					server.showUsers();
				}
				else if(command.equals("show files")) {
					server.showFiles();
				}
				else if(command.equals("show ping requests")) {
					server.showPingRequests();
				}
				else if(command.equals("refresh userlist")) {
					server.refreshUserlist();
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
