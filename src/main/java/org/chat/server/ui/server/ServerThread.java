package org.chat.server.ui.server;

import org.chat.server.ui.cipher.CaesarCipher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
                    //Call the decrypt method here
                    CaesarCipher cc = new CaesarCipher();
                    String result = cc.decrypt(request,2);
                    System.out.println("Client message: " + result);
                    protocolHandler(result.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                //Perform the login operation
                //If login success
                ThreadedServer.addClient(username, socket);
                String welcomeText = "Welcome to the chat server!!!!";
                CaesarCipher cc = new CaesarCipher();
                String result = cc.encrypt(welcomeText,2);
                writer.println(result);
                break;
            case MSG:
                String sender = msgArray[1];
                String receiver = msgArray[2];
                String message = msgArray[3];
                Socket recipientSocket = ThreadedServer.getClient(receiver);
                String text = sender+" : "+message;
                sendMessage(recipientSocket, text);
                break;
        }
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        //Call the encrypt method here
        CaesarCipher cc = new CaesarCipher();
        String result = cc.encrypt(message,2);
        writer.println(result);
    }
}
