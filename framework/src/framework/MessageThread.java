package framework;


import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import framework.Client;
import framework.Message;
import framework.MessageManager;

public abstract class MessageThread extends Thread{

	private MessageManager messageManager;
	private DatagramSocket socket;
	private InetSocketAddress serverAddressMessage;
	private InetSocketAddress serverAddressFile;
	private Client client;
	private boolean running;
	
	public MessageThread(InetSocketAddress serverAddressMessage, InetSocketAddress serverAddressFile, Client client) throws SocketException, UnknownHostException {
		super();
		
		this.serverAddressMessage = serverAddressMessage;
		this.serverAddressFile = serverAddressFile;
		this.client = client;
		this.messageManager = null;
		
		this.socket = new DatagramSocket(client.getMessageAddress());
		this.socket.setReuseAddress(true);
	}
	
	public void disconnect() {
		this.running = false;
		this.socket.close();
	}
	
	public void run() {
		this.running = true;
		
		this.messageManager = new MessageManager(this.socket);
		
		// Primeira mensagem de conexão (config)
		if(this.client.getType() == Client.TYPE_CLIENT) {
			Message message_send = new Message(Message.TYPE_USER_CONNECTION, this.client);
			this.messageManager.sendMessage(this.serverAddressMessage, message_send);
		}
		else if(this.client.getType() == Client.TYPE_SERVER){
			Message message_send = new Message(Message.TYPE_SERVER_MSG, this.client, "Server Online!");
			this.messageManager.sendMessage(this.serverAddressMessage, message_send);
		}
		else {
			this.running = false;
			Message message_send = new Message(Message.TYPE_SERVER_MSG, this.client, "Cliente não reconhecido! Desconectando");
			this.messageManager.sendMessage(this.serverAddressMessage, message_send);
		}
		
		while(this.running) {
			Message message_read = this.messageManager.readMessage();
			messageHandler(message_read);
		}
		
	}
	
	public abstract void messageHandler(Message message);

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public Client getClient() {
		return client;
	}

	public boolean isRunning() {
		return running;
	}

	public InetSocketAddress getServerAddressMessage() {
		return serverAddressMessage;
	}

	public InetSocketAddress getServerAddressFile() {
		return serverAddressFile;
	}
	
	
	
	
	
}
