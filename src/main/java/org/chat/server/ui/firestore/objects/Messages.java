package org.chat.server.ui.firestore.objects;

import java.util.ArrayList;

public class Messages {
    private ArrayList<Message> messages = new ArrayList<>();

    public Messages() {
    }

    public Messages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
