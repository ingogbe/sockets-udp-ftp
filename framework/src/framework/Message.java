package framework;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_SERVER_MSG = 1;
	public static final int TYPE_UPLOAD_FILE = 2;
	public static final int TYPE_DOWNLOAD_FILE = 3;
	public static final int TYPE_LIST_USERS = 4;
	public static final int TYPE_LIST_FILES = 5;
	public static final int TYPE_USER_CONNECTION = 6;
	public static final int TYPE_CLIENT_MSG = 7;
	public static final int TYPE_ICMP_PING = 8;
	public static final int TYPE_ICMP_PONG = 9;
	
	public static final int MESSAGE_LIFETIME = 10000; //ms
	
											// Quais tipo de mensagem usam cada atributo
	private int type; 						// 1, 2, 3, 4, 5, 6, 7, 8, 9
	private Client sender; 					// 1, 2, 3, 4, 5, 6, 7, 8, 9 
	private Date date; 						// 1, 2, 3, 4, 5, 6, 7, 8, 9 (Construtor coloca automáticamente, não é necessário enviar)
	private String message; 				// 1 e 7
	private File file; 						// 2 e 3
	private ArrayList<Client> listUsers; 	// 4
	private File[] listFiles; 				// 5
	
	
	//TYPE_SERVER_MSG = 1 e TYPE_CLIENT_MSG = 7
	public Message(int type, Client sender, String message) {
		super();
		this.date = new Date();
		
		this.type = type;
		this.message = message;
		this.sender = sender;
		
		this.file = null;
		this.listFiles = null;
		this.listUsers = null;
	}
	
	//TYPE_UPLOAD_FILE = 2 e TYPE_DOWNLOAD_FILE = 3
	public Message(int type, Client sender, File file) {
		super();
		this.date = new Date();
		
		this.type = type;
		this.sender = sender;
		this.file = file;
		
		this.message = null;
		this.listFiles = null;
		this.listUsers = null;
	}

	//TYPE_LIST_USERS = 4
	public Message(int type, Client sender, ArrayList<Client> listUsers) {
		super();
		this.date = new Date();
		
		this.type = type;
		this.sender = sender;
		this.listUsers = listUsers;
		
		this.file = null;
		this.message = null;
		this.listFiles = null;
	}
	
	//TYPE_LIST_FILES = 5
	public Message(int type, Client sender, File[] listFiles) {
		super();
		this.date = new Date();
		
		this.type = type;
		this.sender = sender;
		this.listFiles = listFiles;
		
		this.file = null;
		this.message = null;
		this.listUsers = null;
	}
	
	//TYPE_USER_CONNECTION = 6
	//TYPE_LIST_USERS = 4 (Para um client solicitar lista de clients conectados)
	//TYPE_LIST_FILES = 5 (Para um client solicitar lista de arquivos)
	//TYPE_ICMP_PING = 8 (Para um server verificar se o usuario ainda está online, ou client verificar se server está online)
	//TYPE_ICMP_PONG = 8 (Resposta do client ou server para a mensagem TYPE_ICMP_PING)
	public Message(int type, Client sender) {
		super();
		this.date = new Date();
		
		this.type = type;
		this.sender = sender;
		
		this.listFiles = null;
		this.file = null;
		this.message = null;
		this.listUsers = null;
	}
	
	//Retorna a data no formato "dd-MM-yyyy HH:mm:ss"
	public Date getDate() {
		return date;
	}
	
	//Retorna a data no formato "dd-MM-yyyy HH:mm:ss"
	public String getStringDate() {
		String str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(this.date);
		return str;
	}

	public int getType() {
		return type;
	}

	public Client getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public File getFile() {
		return file;
	}

	public ArrayList<Client> getListUsers() {
		return listUsers;
	}

	public File[] getListFiles() {
		return listFiles;
	}

	@Override
	public String toString() {
		if(this.type == Message.TYPE_SERVER_MSG) {
			return "Message [type= " + type + "-TYPE_SERVER_MSG, sender= " + sender + ", date=" + getStringDate() + ", message= " + message + "]";
					
		}
		else {
			return "Message [type=" + type + ", sender=" + sender + ", date=" + getStringDate() + ", message=" + message + ", file="
					+ file + ", listUsers=" + listUsers + ", listFiles=" + Arrays.toString(listFiles) + "]";
		}
		
		
		
	}
	
	
	
}
