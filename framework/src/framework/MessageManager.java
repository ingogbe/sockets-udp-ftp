package framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class MessageManager {
	
	public final static int MESSAGE_SIZE = 262144; //Em bytes. 256kb = 0,25mb
	
	private DatagramSocket senderSocket;
	
	byte buffer[] = new byte[MESSAGE_SIZE];
	
	public MessageManager(DatagramSocket senderSocket) {
		this.senderSocket = senderSocket;
	}
	
	
	public Message readMessage() {
		Message message = null;
		
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			this.senderSocket.receive(packet);
			
			ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(buffer);
			
			ObjectInputStream objectInput = new ObjectInputStream(byteArrayInput);
			message = (Message) objectInput.readObject();
			
			byteArrayInput.close();
			objectInput.close();
		} catch (SocketTimeoutException e) {
			System.err.println("readMessage SocketTimeoutException= " + e.getMessage());
		} catch (IOException e) {
			System.err.println("readMessage IOException= " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("readMessage ClassNotFoundException= " + e.getMessage());
		}
		
		return message;
	}
	
	
	public void sendMessage(InetSocketAddress destinationAddress, Message message) {		
		try {
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream(MessageManager.MESSAGE_SIZE);
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput);
			
			objectOutput.flush();
			objectOutput.writeObject(message);
			
			byte[] data = byteArrayOutput.toByteArray();
			
			DatagramPacket packet = new DatagramPacket(data, data.length, destinationAddress);
			
			this.senderSocket.send(packet);
			
		} catch (SocketTimeoutException e) {
			System.err.println("sendMessage SocketTimeoutException= " + e.getMessage());
		} catch (IOException e) {
			System.err.println("sendMessage SocketTimeoutException= " + e.getMessage());
		}
	}
	
	
}
