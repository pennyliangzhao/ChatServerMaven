package org.chat.server.ui.server;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.chat.server.ui.cipher.CaesarCipher;
import org.chat.server.ui.firestore.objects.Message;
import org.chat.server.ui.firestore.objects.Messages;
import org.chat.server.ui.firestore.objects.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ServerThread extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private static final String REG = "REG", MSG = "MSG";

    public ServerThread(Socket s) throws IOException {
        this.socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            while (socket.isConnected()) {
                String request = reader.readLine();
                if (request != null) {
                    //Decrypt messages
                    String result = CaesarCipher.decrypt(request,2);
                    System.out.println("Client message: " + result);
                    protocolHandler(result);
                }
            }
        } catch (Exception e) {
            System.out.println("Client left");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void protocolHandler(String msg) throws IOException {

        String[] msgArray = msg.split(":");
        String operation = msgArray[0];


        switch (operation){
            case REG:
                String username = msgArray[1];
                String password = msgArray[2];
                String decryptedPassword = CaesarCipher.decrypt(password, 2);
                //Perform the login operation
                //If login success
                String response = "";
                if(loginAuth(username, decryptedPassword)) {
                    ThreadedServer.addClient(username, socket);
                    response = "LOGIN:SUCCESS";
                } else {
                    response = "LOGIN:FAILED";
                }
                String result = CaesarCipher.encrypt(response,2);
                writer.println(result);
                break;
            case MSG:
                String sender = msgArray[1];
                String receiver = msgArray[2];
                String message = msgArray[3];
                Socket recipientSocket = ThreadedServer.getClient(receiver);
                String text = sender+" : "+message;
                //Encrypt the message
                sendMessage(recipientSocket, text);
                String encryptedMsg = CaesarCipher.encrypt(message,2);
                String encryptedSender = CaesarCipher.encrypt(sender,2);
                String encryptedReceiver = CaesarCipher.encrypt(receiver,2);
                //Update received messages in firestore
                updateFirestore("received_messages",receiver, encryptedSender, encryptedMsg);
                //Update sent messages in firestore
                updateFirestore("sent_messages",sender, encryptedReceiver, encryptedMsg);
                break;
        }
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        String encryptedMsg = CaesarCipher.encrypt(message,2);
        writer.println(encryptedMsg);
    }

    private void updateFirestore(String collection, String receiver,String sender, String text) {
        try {
            Message msg = new Message(sender, text);
            ArrayList<Message> msgs = new ArrayList<>();
            msgs.add(msg);
            Messages messages = new Messages(msgs);

            Firestore db = FirestoreClient.getFirestore();
            DocumentReference documentReference = db.collection(collection).document(receiver);
            ApiFuture<WriteResult> arrayUnion = documentReference.update("messages", FieldValue.arrayUnion(messages));
            System.out.println("Database updated: "+ arrayUnion.get().getUpdateTime());
        }catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean loginAuth(String user, String password) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("users").document(user);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                User usr = document.toObject(User.class);
                return CaesarCipher.decrypt(usr.getPassword(), 2).equals(password);
            }
        }catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
