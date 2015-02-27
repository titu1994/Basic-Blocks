package com.somshubra.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileServer {
	private static ServerSocket server;
	public static boolean canContinue = true;
	public static final String FILE_TRANSFERED = "FILE_TRANSFERED";

	public static void main(String args[]) throws IOException {
		server = new ServerSocket(8888, 50, InetAddress.getLocalHost());
		serve();
	}

	private static void serve() {
		while(canContinue) {
			try {
				Socket client = server.accept();
				
				ClientHandler handler = new ClientHandler(client);
				handler.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class ClientHandler extends Thread {
		
		private Socket client;

		public ClientHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				FileChannel fc = null;
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				String filename = in.readLine();
				
				File f = new File(filename);
				FileInputStream fis = new FileInputStream(f);
				fc = fis.getChannel();
				
				long fileSize = f.length();
				
				SyncHeader header = new SyncHeader();
				header.fileSize = fileSize;
				header.fileType = filename.split("\\.")[1];
				header.canExecute = f.canExecute();
				header.canRead = f.canRead();
				header.canWrite = f.canWrite();
				
				
				out.println(header);
				
				String response = in.readLine();
				header = SyncHeader.parse(response);
				
				ByteBuffer fileBuffer = ByteBuffer.allocate(header.clientBufferSize);
				
				int fileCount = 0;
				String temp = "";
				while((fileCount = fc.read(fileBuffer)) != -1) {
					temp = new String(fileBuffer.array());
					out.println(temp);
					fileBuffer.clear();
				}
				
				out.println(FILE_TRANSFERED);
				
				in.close();
				out.close();
				fis.close();
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileServer.canContinue = false;
		}
	}
	
	public static class SyncHeader {
		public long fileSize;
		public String fileType;
		public boolean canExecute;
		public boolean canRead;
		public boolean canWrite;
		
		public int clientBufferSize;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(fileSize + ";");
			sb.append(fileType + ";");
			sb.append(canExecute + ";");
			sb.append(canRead + ";");
			sb.append(canWrite + ";");
			sb.append(clientBufferSize);
			
			return sb.toString();
		}
		
		public static SyncHeader parse(String syncHeader) {
			SyncHeader header = new SyncHeader();
			String[] parts = syncHeader.split(";");
			
			header.fileSize = Long.parseLong(parts[0]);
			header.fileType = parts[1];
			header.canExecute = Boolean.parseBoolean(parts[2]);
			header.canRead = Boolean.parseBoolean(parts[3]);
			header.canWrite = Boolean.parseBoolean(parts[4]);
			header.clientBufferSize = Integer.parseInt(parts[5]);
			
			return header;
		}
	}

}
