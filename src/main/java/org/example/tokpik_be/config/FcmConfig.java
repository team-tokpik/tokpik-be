package org.example.tokpik_be.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FcmConfig {

    @Value("${FCM_ACCOUNT_KEY_PATH}")
    private String fcmAccountKeyPath;
    
    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource resource = new ClassPathResource(fcmAccountKeyPath);

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
            .build();

        return FirebaseMessaging.getInstance(FirebaseApp.initializeApp(firebaseOptions));
    }
}
