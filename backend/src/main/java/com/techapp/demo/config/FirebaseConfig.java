package com.techapp.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Configuration
public class FirebaseConfig {

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        String credentialsJson = System.getenv("FCM_CONFIG");

        InputStream serviceAccount;
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            // Fix private key newlines that get corrupted in env variables
            credentialsJson = credentialsJson
                    .replace("\\n", "\n")
                    .replace("\\r", "");
            serviceAccount = new ByteArrayInputStream(
                    credentialsJson.getBytes("UTF-8"));
        } else {
            // Fall back to local file
            serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream(
                            "etechpro-1b433-firebase-adminsdk-fbsvc-289b460cd6.json");
        }

        if (serviceAccount == null) {
            throw new IOException("Firebase credentials not found");
        }

        return GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Arrays.asList(
                        "https://www.googleapis.com/auth/firebase.messaging"
                ));
    }
}
