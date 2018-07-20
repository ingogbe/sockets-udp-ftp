package framework;


import java.io.File;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import framework.Client;

public class FileThread extends Thread{

	private FileManager fileManager;
	private DatagramSocket socket;
	private Client client;
	private int type;
	private File file;
	private InetSocketAddress destination;
	private String savePath;
	
	public static final int TYPE_READ = 1;
	public static final int TYPE_SEND = 2;
	
	public FileThread(Client client, int type, File file, String savePath) throws SocketException, UnknownHostException {
		super();
		
		this.client = client;
		this.type = type;
		this.file = file;
		this.savePath = savePath;
		this.destination = null;
		this.fileManager = null;
		
		this.socket = new DatagramSocket(client.getFileAddress());
		this.socket.setReuseAddress(true);
	}
	
	public FileThread(Client client, int type, File file, InetSocketAddress destination) throws SocketException, UnknownHostException {
		super();
		
		this.client = client;
		this.type = type;
		this.file = file;
		this.destination = destination;
		this.savePath = null;
		this.fileManager = null;
		
		this.socket = new DatagramSocket(client.getFileAddress());
		this.socket.setReuseAddress(true);
	}
	
	public void disconnect() {
		this.socket.close();
	}
	
	public void run() {
		
		System.out.println("Open file server " + getClient());
		
		this.fileManager = new FileManager(this.socket);
		
		if(this.type == FileThread.TYPE_READ) {
			System.out.println("Reading file: ");
			this.fileManager.readFile(this.file, this.savePath);
		}
		else if(this.type == FileThread.TYPE_SEND) {
			System.out.println("Sending file: ");
			this.fileManager.sendFile(this.file, this.destination);
		}
		
		System.out.println("Close file server " + getClient());
		
		disconnect();
		
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public Client getClient() {
		return client;
	}
	
	
	
}
