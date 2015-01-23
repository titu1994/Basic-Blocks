package com.somshubra.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResultWriter {

	private PrintWriter pw;
	private ExecutorService executor;
	
	public ResultWriter(String filename) throws IOException {

		String appendedPath = filename + ".txt";

		File file = new File(appendedPath);
		
		FileWriter writer = new FileWriter(file, true); //Important to append
		BufferedWriter br = new BufferedWriter(writer);

		pw = new PrintWriter(br, true); //Important to auto-flush
		
		executor = Executors.newCachedThreadPool();
	}
	
	public synchronized void write(final String data) throws InterruptedException, ExecutionException {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				pw.println(data);
			}
		};
		
		executor.execute(r);
	}

	public synchronized void closeWriter() {
		executor.shutdown();
		pw.close();
		pw = null;
	}

}
