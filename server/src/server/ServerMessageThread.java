package server;


import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import framework.Client;
import framework.Const;
import framework.FileThread;
import framework.Message;
import framework.MessageThread;

public class ServerMessageThread extends MessageThread{

	private ArrayList<Client> users;
	private ArrayList<InetSocketAddress> pingRequests;
	
	public static final String SERVER_STORAGE = "C:/Users/Celegma/Desktop/serverStorage/";
	
	private boolean connectionApproved = false;
	
	public ServerMessageThread() throws SocketException, UnknownHostException {
		super(
			new InetSocketAddress(InetAddress.getLocalHost(), Const.SERVER_MESSAGE_PORT),
			new InetSocketAddress(InetAddress.getLocalHost(), Const.SERVER_FILE_PORT),
			new Client(Client.TYPE_SERVER, 
				new InetSocketAddress(InetAddress.getLocalHost(), Const.SERVER_MESSAGE_PORT), 
				new InetSocketAddress(InetAddress.getLocalHost(), Const.SERVER_FILE_PORT)
			)
		);
		
		this.users = new ArrayList<Client>();
		this.pingRequests = new ArrayList<InetSocketAddress>();
			
		System.out.println(
			"Open server on " + getClient().getMessageAddress().getHostName() + " - " +
			"IP: " + getClient().getMessageAddress().getAddress().getHostAddress() + " - " +
			"Port: " + getClient().getMessageAddress().getPort() +
			"\n----------");
	}

	@Override
	public void messageHandler(Message message) {
		
		if(message != null) {
			System.err.println("\n" + message);
			
			if(message.getType() == Message.TYPE_SERVER_MSG) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = " + message.getMessage() + 
					"\n----------");
				
				this.connectionApproved = true;
				Main.cancelTimer();
			}
			else if(message.getType() == Message.TYPE_UPLOAD_FILE) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Request upload file '" + message.getFile().getName() + "'" +
					"\n----------");
				
				try {
					FileThread fileThread = new FileThread(getClient(), FileThread.TYPE_READ, message.getFile(), SERVER_STORAGE + message.getFile().getName());
        			fileThread.start();
					
					sendMessageAll("\n Client " + message.getSender() + "\n Uploaded file '" + message.getFile().getName() + "'");
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				
			}
			else if(message.getType() == Message.TYPE_DOWNLOAD_FILE) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Request download file '" + message.getFile().getName() + "'" +
					"\n----------");
				
				try {
					FileThread fileThread = new FileThread(getClient(), FileThread.TYPE_SEND, message.getFile(), message.getSender().getFileAddress());
					fileThread.start();
					
					sendMessageAll("\n Client " + message.getSender() + "\n Downloaded file '" + message.getFile().getName() + "'");
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			else if(message.getType() == Message.TYPE_LIST_USERS) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Request list of users" +
					"\n----------");
				
				listUsers(message.getSender());
			}
			else if(message.getType() == Message.TYPE_LIST_FILES) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Request list of files" +
					"\n----------");
				
				listFiles(message.getSender());
			}
			else if(message.getType() == Message.TYPE_USER_CONNECTION) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = User '" + message.getSender().getUsername() + "' connected!" +
					"\n----------");
				
				long message_lifetime = new Date().getTime() - message.getDate().getTime();
				System.out.println("> Message lifetime = " + message_lifetime + " ms");
				
				if(message_lifetime > Message.MESSAGE_LIFETIME) {
					System.out.println("> Message reached the maximum lifetime. Message ignored!");
				}
				else {
					addUser(message.getSender());
				}
				
			}
			else if(message.getType() == Message.TYPE_CLIENT_MSG) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = " + message.getMessage() + 
					"\n----------");
			}
			else if(message.getType() == Message.TYPE_ICMP_PING) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = ICMP Ping" +
					"\n----------");
				
				if(message.getSender().getType() == Client.TYPE_CLIENT) {
					addUser(message.getSender());
				}
				
				pong(message.getSender());
				
			}
			else if(message.getType() == Message.TYPE_ICMP_PONG) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = ICMP Pong" +
					"\n----------");
				
				addUser(message.getSender());
				pingRequests.remove(message.getSender().getMessageAddress());
			}
			else {
				System.out.println("Message read: " + message);
			}
		}
		
	}
	
	//Adiciona usuario se ele ainda não estiver na lista
	//Se houver alteração na lista atualiza lista para todos
	public void addUser(Client user) {
		boolean addFlag = true;
		System.out.println("> Add user " + user);
		
		for(Client c: users) {
			if(c.getMessageAddress().equals(user.getMessageAddress())) {
				addFlag = false;
				System.out.println("> User already added!");
				break;
			}
		}
		
		System.out.println("> Notifying user, connection approved or refreshed");
		Message message1 = new Message(Message.TYPE_USER_CONNECTION, getClient());
		getMessageManager().sendMessage(user.getMessageAddress(), message1);
		
		if(addFlag) {
			users.add(user);
			
			System.out.println("> Updating file list to the new user");
			listFiles(user);
			
			System.out.println("> Updating userlist to all");
			for(Client u: users) {
				listUsers(u);
			}
		}
	}
	
	//Remove usuario pelo seu address+port
	//Se houver alteração na lista atualiza lista para todos
	public void removeUser(Client user) {
		removeUserbyAddress(user.getMessageAddress());
	}
	
	//Remove usuario pelo seu address+port
	//Se houver alteração na lista atualiza lista para todos
	public void removeUserbyAddress(InetSocketAddress address) {
		for(Client c: users) {
			if(c.getMessageAddress().equals(address)) {
				System.out.println("> Remove user " + c);
				users.remove(c);
				
				System.out.println("> Updating userlist to all");
				for(Client u: users) {
					listUsers(u);
				}
				
				break;
			}
		}
	}
	
	public void listFiles(Client user) {
		File folder = new File(ServerMessageThread.SERVER_STORAGE);
		File[] listOfFiles = folder.listFiles();
		
		Message message = new Message(Message.TYPE_LIST_FILES, getClient(), listOfFiles);
		getMessageManager().sendMessage(user.getMessageAddress(), message);
	}
	
	public void listUsers(Client user) {
		Message message = new Message(Message.TYPE_LIST_USERS, getClient(), this.users);
		getMessageManager().sendMessage(user.getMessageAddress(), message);
	}
	
	public void showFiles() {
		System.out.println("> Show files");
		File folder = new File(ServerMessageThread.SERVER_STORAGE);
		File[] listOfFiles = folder.listFiles();
		
		for(File f: listOfFiles) {
			System.out.println("> - " + f.getName() + " (" + f.length() + " b)");
		}
	}
	
	public void showUsers() {		
		System.out.println("> Show users - (" + this.users.size() + " online)");
		
		for(Client c: this.users) {
			System.out.println("> - " + c);
		}
	}
	
	public void sendMessageAll(String s) {
		for(Client c: this.users) {
			sendMessage(c, s);
		}
	}
	
	public void sendMessage(Client user, String s) {
		Message message = new Message(Message.TYPE_SERVER_MSG, getClient(), s);
		getMessageManager().sendMessage(user.getMessageAddress(), message);
	}
	
	public void refreshUserlist() {		
		System.out.println("> refresh users");
		
		for(Client c: this.users) {
			ping(c.getMessageAddress());
		}
	}
	
	public void showPingRequests() {		
		System.out.println("> Show 'ping' requests");
		
		for(InetSocketAddress addr: this.pingRequests) {
			System.out.println("> - " + addr);
		}
	}
	
	public void ping(InetSocketAddress address) {		
		System.out.println("> Ping in " + address);
		Message message = new Message(Message.TYPE_ICMP_PING, getClient());
		getMessageManager().sendMessage(address, message);
		
		pingRequests.add(address);
		
		//Verifica se recebeu pong 10s dps
		new Timer().schedule( 
			new TimerTask() {
				@Override
				public void run() {
					
					System.out.println("> Checking if received 'pong' 10 seconds after 'ping'");
					
					boolean pong_flag = true;
					
				    for(InetSocketAddress addr :pingRequests) {
				    	if(addr.equals(address)) {
				    		System.out.println("> 'pong' not received. Removing user if exists and 'ping' request");
				    		removeUserbyAddress(address);
				    		pingRequests.remove(address);
				    		pong_flag = false;
				    		break;
				    	}
				    }
				    
				    if(pong_flag) {
				    	System.out.println("> 'pong' received. 'ping' request removed and user added if not in the userlist, by 'pong' message");
				    }
				}
			}, 
			Message.MESSAGE_LIFETIME 
		);
	}
	
	public void pong(Client user) {
		System.out.println("> Pong to " + user.getMessageAddress());
		Message message = new Message(Message.TYPE_ICMP_PONG, getClient());
		getMessageManager().sendMessage(user.getMessageAddress(), message);
	}
	
	public boolean isConnectionApproved() {
		return connectionApproved;
	}
	
	
	
	
	
}
