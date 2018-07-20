package framework;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class Client implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_SERVER = 1;
	public static final int TYPE_CLIENT = 2;
	
												// Quais tipo de clients usam cada atributo
	private int type;							// 1, 2
	private String username;					// 2
	private InetSocketAddress messageAddress;	// 1, 2
	private InetSocketAddress fileAddress;		// 1, 2
	
	//TYPE_SERVER
	public Client(int type, InetSocketAddress messageAddress, InetSocketAddress fileAddress) {
		super();
		this.type = type;
		this.messageAddress = messageAddress;
		this.fileAddress = fileAddress;
		this.username = "";
	}
	
	//TYPE_CLIENT
	public Client(int type, InetSocketAddress messageAddress, InetSocketAddress fileAddress, String username) {
		super();
		this.type = type;
		this.messageAddress = messageAddress;
		this.fileAddress = fileAddress;
		this.username = username;
	}

	public int getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}
	
	public InetSocketAddress getMessageAddress() {
		return messageAddress;
	}

	public InetSocketAddress getFileAddress() {
		return fileAddress;
	}
	
	@Override
	public String toString() {
		if(this.type == Client.TYPE_CLIENT) {
			return "Client [type= " + type + "-TYPE_CLIENT , username= " + username + ", message_address=" + messageAddress
					+ ", file_address=" + fileAddress + "]";
		}
		else if(this.type == Client.TYPE_SERVER) {
			return "Client [type= " + type + "-TYPE_SERVER], message_address=" + messageAddress 
					+ ", file_address=" + fileAddress + "]";
		}
		else {
			return "Client [type=" + type + ", username=" + username + ", message_address=" + messageAddress
					+ ", file_address=" + fileAddress + "]";
		}
		
	}

	
	
}
