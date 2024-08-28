package com.projectlyrics.server.global.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-file}")
    private String keyFile;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        initializeFirebaseApp();
        return FirebaseMessaging.getInstance();
    }

    private void initializeFirebaseApp() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream key = new FileInputStream(keyFile);
                log.info("firebase key file loaded");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(key))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("firebase messaging initialized");
            }
        } catch (IOException e) {
            log.error("failed to initialize firebase", e);
        }
    }
}
