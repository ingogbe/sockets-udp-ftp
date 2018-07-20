package client;


import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import framework.Client;
import framework.FileManager;
import framework.FileThread;
import framework.Message;
import framework.MessageThread;

public class ClientMessageThread extends MessageThread{
	
	private boolean connectionApproved = false;
	private File listFiles[] = null;
	
	//TODO: Para testes, e rodar tudo localhost usa-se o mesmo IP e portas diferentes
	private static final int messagePort = new Random().nextInt((65535 - 4000) + 1) + 4000;
	private static final int filePort = messagePort + 1;
	
	public ClientMessageThread(String username, InetSocketAddress server_addr_msg, InetSocketAddress server_addr_file) throws SocketException, UnknownHostException {
		super(
			server_addr_msg,
			server_addr_file,
			new Client(Client.TYPE_CLIENT, 
				new InetSocketAddress(InetAddress.getLocalHost(), messagePort), 
				new InetSocketAddress(InetAddress.getLocalHost(), filePort),
				username
			)
		);
		
		System.out.println(
			"Open client on " + getClient().getMessageAddress().getHostName() + " - " +
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
			}
			else if(message.getType() == Message.TYPE_UPLOAD_FILE) {
				
			}
			else if(message.getType() == Message.TYPE_DOWNLOAD_FILE) {
				
			}
			else if(message.getType() == Message.TYPE_LIST_USERS) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Users list refreshed");
				
				for(Client u: message.getListUsers()) {
					System.out.println("- " + u.getUsername() + " (" + u.getMessageAddress() + ")");
				}
				
				System.out.println("----------");
			}
			else if(message.getType() == Message.TYPE_LIST_FILES) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = File list refreshed");
				
				listFiles = message.getListFiles();
				
				for(int i = 0; i < listFiles.length; i++) {
					System.out.println("- [" + i + "] - " + listFiles[i].getName() + " (" + listFiles[i].length() + " b)");
				}
				
				System.out.println("----------");
			}
			else if(message.getType() == Message.TYPE_USER_CONNECTION) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = Connection approved or refreshed!" +
					"\n----------");
				
				this.connectionApproved = true;
				Main.cancelTimer();
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
				
				pong(message.getSender());
			}
			else if(message.getType() == Message.TYPE_ICMP_PONG) {
				System.out.println(
					"Message read [" + message.getDate() + "]\n" +
					"Sender  = " + message.getSender() + "\n" +
					"Content = ICMP Pong" +
					"\n----------");
			}
			else {
				System.out.println("Message read: " + message);
			}
		}
		
	}
	
	public void requestListUsers() {
		System.out.println("> Request userlist");
		Message message = new Message(Message.TYPE_LIST_USERS, getClient());
		getMessageManager().sendMessage(getServerAddressMessage(), message);
	}
	
	public void requestListFiles() {
		System.out.println("> Request list of files");
		Message message = new Message(Message.TYPE_LIST_FILES, getClient());
		getMessageManager().sendMessage(getServerAddressMessage(), message);
	}
	
	public void requestDownloadFile(int fileNumber) {
		System.out.println("> Request download file");
		Message message = new Message(Message.TYPE_DOWNLOAD_FILE, getClient(), listFiles[fileNumber]);
		
		try {
			//Abre chooser para escolher onde será salvo o arquivo que recebeu
    		JFileChooser jc = new JFileChooser();
    		String extension = FileManager.getFileExtension(message.getFile());
    		
    		FileNameExtensionFilter filter = new FileNameExtensionFilter(extension, extension);
    		jc.setFileFilter(filter);
    		jc.setSelectedFile(new File(message.getFile().getName()));
    		jc.setAcceptAllFileFilterUsed(false);
        	
        	int returnValue = jc.showSaveDialog(null);
        	
        	if (returnValue == JFileChooser.APPROVE_OPTION) {
            	File f = jc.getSelectedFile();
            	
            	if(FileManager.getFileExtension(f).equals(FileManager.getFileExtension(message.getFile()))) {
            		FileThread fileThread = new FileThread(getClient(), FileThread.TYPE_READ, message.getFile(), f.getAbsolutePath());
        			fileThread.start();
        			getMessageManager().sendMessage(getServerAddressMessage(), message);
            	}
            	else {
            		System.out.println("Files (server and local) extension doesn't match!");
            	}
        		
            }
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	
	public void requestUploadFile() {
		System.out.println("> Request upload file");
		
		try {
			//Abre chooser para escolher onde será salvo o arquivo que recebeu
    		JFileChooser jc = new JFileChooser();
    		jc.setAcceptAllFileFilterUsed(false);
        	
        	int returnValue = jc.showSaveDialog(null);
        	
        	if (returnValue == JFileChooser.APPROVE_OPTION) {
            	File f = jc.getSelectedFile();
            	
            	Message message = new Message(Message.TYPE_UPLOAD_FILE, getClient(), f);
            	getMessageManager().sendMessage(getServerAddressMessage(), message);
            	
            	FileThread fileThread = new FileThread(getClient(), FileThread.TYPE_SEND, f, getServerAddressFile());
    			fileThread.start();
    			
    			
            }
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void ping(InetSocketAddress address) {
		System.out.println("> Ping in " + address);
		Message message = new Message(Message.TYPE_ICMP_PING, getClient());
		getMessageManager().sendMessage(address, message);
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
