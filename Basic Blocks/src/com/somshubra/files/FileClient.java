package com.somshubra.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.somshubra.files.FileServer.SyncHeader;

public class FileClient {
	private static final int BUFFER_CAPACITY = 1024 * 128;
	private static Socket client;

	public static void main(String args[]) throws IOException {
		client = new Socket(InetAddress.getLocalHost(), 8888);
		receive();
	}

	private static void receive() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedReader bb = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Enter file name : ");
			String name = bb.readLine();

			System.out.print("Enter copy name : ");
			String copy = bb.readLine();
			
			out.println(name);
			
			String response = in.readLine();
			SyncHeader header = SyncHeader.parse(response);
			
			header.clientBufferSize = BUFFER_CAPACITY;
			out.println(header);
			
			long fileSize = header.fileSize;
			System.out.println("File Size : " + fileSize);
			
			System.out.println("File Type : " + header.fileType);
			String type = header.fileType;
			
			File f = new File(copy + "." + type);
			f.setExecutable(header.canExecute);
			f.setReadable(header.canRead);
			f.setWritable(header.canWrite);
			
			FileOutputStream fos = new FileOutputStream(f);
			FileChannel fc = fos.getChannel();
			
			ByteBuffer fileBufferArray = ByteBuffer.allocate(BUFFER_CAPACITY);
			String fileStore = "";
			long size = 0;
			
			while(!(fileStore = in.readLine()).equals(FileServer.FILE_TRANSFERED)) {
				fileBufferArray.put(fileStore.getBytes(), 0, fileStore.length());
				fc.write(fileBufferArray);
				fileBufferArray.clear();
			}
			
			System.out.println("File Transfer Completed");
			in.close();
			out.close();
			fc.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
