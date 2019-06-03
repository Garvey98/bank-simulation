package edu.bit.felinae;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainQueue {
    private boolean shutdown = false;
    private static MainQueue instance = new MainQueue();
    private LinkedBlockingQueue<String> queue;
    private MainQueue(){
        queue = new LinkedBlockingQueue<>();
    }
    public static MainQueue getInstance(){
        return instance;
    }
    public void enqueue(String sess_id){
        queue.offer(sess_id);
    }
    public void dequeue() {
        try {
            queue.take();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public String poll() {
        try{
            return queue.poll(500, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }
    public boolean getShutdown() {
        return shutdown;
    }
    public void setShutdown() {
        shutdown = true;
    }
}
