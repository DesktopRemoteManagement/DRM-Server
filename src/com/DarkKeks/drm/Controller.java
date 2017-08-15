package com.DarkKeks.drm;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    private static Controller instance = new Controller();

    private Map<String, SocketThread> clients;
    private Map<MessageId, SocketThread> runningRequests;

    private SocketServer server;

    public void run() {
        this.clients = new ConcurrentHashMap<>();
        this.runningRequests = new ConcurrentHashMap<>();

        server = new SocketServer();
        server.start();
    }

    public void processMessage(SocketThread client, byte[] msg) {
        try {
            Message message = new Message(Security.getDecryptedMessage(msg));
            message.getId().setAddressPart(client.getPublicAddress());

            Log.info("Received message from " + client.getPublicAddress());

            if(!runningRequests.containsKey(message.getId())) {
                String recipient = message.getDestination();
                if(!isServerMessage(recipient)) {
                    if(clients.containsKey(recipient)) {
                        runningRequests.put(message.getId(), client);
                        clients.get(recipient).requestSend(Security.getEncryptedMessage(message));
                        Log.info("Redirected message to recipient");
                    } else {
                        // TODO Response with "Client not active"
                    }
                } else {
                    processServerMessage(client, message);
                }
            } else {
                processResponse(client, message);
            }

        } catch (GeneralSecurityException e) {
            Log.info("Can't decrypt message.");
        }
    }

    private void processResponse(SocketThread client, Message message) {
        try {
            SocketThread requestSender = clients.get(message.getId().getAddress()); // TODO could record be deleted?
            message.getId().setShort();
            requestSender.requestSend(Security.getEncryptedMessage(message));
        } catch (GeneralSecurityException e) {
            Log.error("Can't encrypt message.");
            Log.logException(e);
        }
    }

    private void processServerMessage(SocketThread client, Message message) {
        // TODO add server messages
    }

    private boolean isServerMessage(String recipient) {
        return recipient.equalsIgnoreCase("server");
    }

    public void newConnection(SocketThread client) {
        clients.put(client.getPublicAddress(), client);
    }

    public static Controller getInstance() {
        return instance;
    }
}
