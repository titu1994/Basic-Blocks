package com.somshubra.concurrency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Yue on 12-Mar-16.
 */
public class ConcurrentWriter {

    private BlockingQueue<byte[]> queue;
    private WriteThread thread;

    private Lock lock = new ReentrantLock();
    private Condition isNotEmpty = lock.newCondition();

    public ConcurrentWriter(File file) throws FileNotFoundException {
        queue = new LinkedBlockingQueue<>();
        thread = new WriteThread(file);
        thread.start();
    }

    public void write(byte data[]) {
        queue.add(data);
        isNotEmpty.signal();
    }

    public void finish() {
        thread.finish();
        isNotEmpty.signal();
    }

    private class WriteThread extends Thread {
        private File file;
        private FileOutputStream fis;
        private boolean continueWriting;

        public WriteThread(File file) throws FileNotFoundException {
            this.file = file;
            this.fis = new FileOutputStream(file, true);
            this.continueWriting = true;
        }

        public synchronized void finish() {
            this.continueWriting = false;
            try {
                this.fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (continueWriting) {
                lock.lock();

                try {
                    while (queue.isEmpty() && continueWriting) {
                        isNotEmpty.await();
                    }

                    if (!continueWriting)
                        return;

                    fis.write(queue.poll());

                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception while waiting for queue to be not empty");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

    }

}
