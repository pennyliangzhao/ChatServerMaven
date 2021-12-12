package org.chat.server.ui.firestore;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirestoreInitializer {
    public static void initialize() {
        try{
            FileInputStream serviceAccount =
                    new FileInputStream("C:\\firestore\\penny_firestore.json");

            FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                    .setDatabaseUrl("https://studentproject-f74bc.firebaseio.com")
                                            .build();
            FirebaseApp.initializeApp(options);
        }catch(IOException e) {

        }
    }
}
