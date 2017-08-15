package com.DarkKeks.drm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(Config.PORT);

            Log.info("Started SocketServer");

            while(true) {
                Socket socket = server.accept();
                new SocketThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
