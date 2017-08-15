package com.DarkKeks.drm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketThread extends Thread {

    private Socket client;

    private DataInputStream in;
    private DataOutputStream out;

    private final Queue<byte[]> messageQueue;

    public SocketThread(Socket client) throws ConnectException {
        this.client = client;
        Log.info("New connection: " + getPublicAddress());

        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            throw new ConnectException("Can't get input/output stream.");
        }

        messageQueue = new LinkedBlockingQueue<>();

        Controller.getInstance().newConnection(this);
    }

    public String getPublicAddress() {
        return String.format("%s:%s",
                client.getInetAddress().getHostAddress(),
                client.getPort());
    }

    @Override
    public void run() {
        try {
            while(true) {
                if(!messageQueue.isEmpty()) {
                    byte[] message;
                    synchronized (messageQueue) {
                        message = messageQueue.poll();
                    }
                    sendMessage(message);
                }
                if(in.available() > 0) {
                    readMessage();
                }
            }
        } catch (Exception e) {
            Log.info("SocketThread stopped.");
            Log.logException(e);
        }
    }

    private void readMessage() throws IOException {
        int size = in.readInt();
        byte[] msg = new byte[size];
        in.readFully(msg);

        Controller.getInstance().processMessage(this, msg);
    }

    private void sendMessage(byte[] msg) throws IOException {
        out.writeInt(msg.length);
        out.write(msg);
    }

    public void requestSend(byte[] msg) {
        synchronized (messageQueue) {
            messageQueue.add(msg);
        }
    }
}
