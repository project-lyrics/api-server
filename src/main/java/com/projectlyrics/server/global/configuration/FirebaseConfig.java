package com.projectlyrics.server.global.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            FileInputStream key = new FileInputStream("src/main/resources/firebase-key.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(key))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("failed to initialize firebase", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        log.info("firebase messaging initialized");
        return FirebaseMessaging.getInstance();
    }
}
