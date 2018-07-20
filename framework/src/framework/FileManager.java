package framework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class FileManager {
	public final static int MAX_PACKAGE_SIZE = 65536; // Em bytes. Limite do tamanho de arquivo (64kb)
	
	public final static int PACKAGE_SIZE = 100; // Em bytes.
	
	private DatagramSocket senderSocket;
	
	public FileManager(DatagramSocket senderSocket) {
		this.senderSocket = senderSocket;
	}
	
	public void readFile(File file, String savePath) {
		try {
			
			byte [] buffer  = new byte [(int) file.length()];
			
			FileOutputStream fos = new FileOutputStream(savePath);			
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			int n_bytes = buffer.length;
			int packageCounter = 0;
			float averageTax = 0;
			
			for(int offset = 0; offset < n_bytes; offset = offset + PACKAGE_SIZE) {
				long initDownload = System.nanoTime();
				
				int currentPackageSize = 0;
				DatagramPacket packet = null;
				
				if((offset + PACKAGE_SIZE) > n_bytes) {
	        		currentPackageSize = n_bytes - offset;
	        		packet = new DatagramPacket(buffer, offset, currentPackageSize);
	        	}
	        	else {
	        		currentPackageSize = PACKAGE_SIZE;
	        		packet = new DatagramPacket(buffer, offset, currentPackageSize);
	        	}
				
				packageCounter++;
				this.senderSocket.receive(packet);
				
				long endDownload = System.nanoTime();
				float transferTax = calcTransferTax(n_bytes, offset, initDownload, endDownload, PACKAGE_SIZE);
				
				System.out.println("\nPackage " + packageCounter);
				System.out.println("Offset=" + offset + " N_bytes=" + n_bytes + " Package_size=" + currentPackageSize);
				System.out.println("Current tax: " + new BigDecimal(transferTax).toPlainString() + " kbps | ");
            	System.out.println("--------");
            	
            	averageTax = averageTax + transferTax;
			}
			
			System.out.println(String.format("%.2f", (averageTax / packageCounter)) + " kbps");
			
			bos.write(buffer, 0 , n_bytes);
			
			fos.close();
			bos.close();
			
		} catch (SocketTimeoutException e) {
			System.err.println("readFile SocketTimeoutException= " + e.getMessage());
		} catch (IOException e) {
			System.err.println("readFile IOException= " + e.getMessage());
		} 
	}
	
	
	public void sendFile(File file, InetSocketAddress destinationAddress) {		
		try {
			byte [] buffer  = new byte [(int) file.length()];
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			bis.read(buffer, 0, buffer.length);
			
			int n_bytes = buffer.length;
			int packageCounter = 0;
			float averageTax = 0;
			
			for(int offset = 0; offset < n_bytes; offset = offset + PACKAGE_SIZE) {
				
				long initDownload = System.nanoTime();
				
				int currentPackageSize = 0;
				DatagramPacket packet = null;
				
				if((offset + PACKAGE_SIZE) > n_bytes) {
	        		currentPackageSize = n_bytes - offset;
	        		packet = new DatagramPacket(buffer, offset, currentPackageSize, destinationAddress);
	        	}
	        	else {
	        		currentPackageSize = PACKAGE_SIZE;
	        		packet = new DatagramPacket(buffer, offset, currentPackageSize, destinationAddress);
	        	}
				
				packageCounter++;
				this.senderSocket.send(packet);
				
	
				long endDownload = System.nanoTime();
				float transferTax = calcTransferTax(n_bytes, offset, initDownload, endDownload, PACKAGE_SIZE);
				
				System.out.println("\nPackage " + packageCounter);
				System.out.println("Offset=" + offset + " N_bytes=" + n_bytes + " Package_size=" + currentPackageSize);
				System.out.println("Current tax: " + new BigDecimal(transferTax).toPlainString() + " kbps | ");
            	System.out.println("--------");
            	
            	averageTax = averageTax + transferTax;
			}
			
			System.out.println(String.format("%.2f", (averageTax / packageCounter)) + " kbps");
			
			fis.close();
			bis.close();
			
		} catch (SocketTimeoutException e) {
			System.err.println("sendFile SocketTimeoutException= " + e.getMessage());
		} catch (IOException e) {
			System.err.println("sendFile SocketTimeoutException= " + e.getMessage());
		}
	}
	
	//Funcão para pegar a extensão de um arquivo (File) de seu nome
	public static String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	public static float calcTransferTax(int n_bytes, int offset, long init, long end, int packageSize) {
		float tempoTransfer_ns = (end - init);
		
		float tempoTransfer_ms = tempoTransfer_ns / 1000000;
		
        float tempoTransfer_s = tempoTransfer_ms / 1000;
        
        float kb_package = ((float) packageSize) / 1024;
        
        float taxaTransfer = kb_package / tempoTransfer_s;
        
        return taxaTransfer;
	}
}
