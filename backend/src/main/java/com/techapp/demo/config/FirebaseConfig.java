package com.techapp.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Configuration
public class FirebaseConfig {

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        InputStream serviceAccount = getClass()
                .getClassLoader()
                .getResourceAsStream("etechpro-1b433-firebase-adminsdk-fbsvc-289b460cd6.json");

        return GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Arrays.asList(
                        "https://www.googleapis.com/auth/firebase.messaging"
                ));
    }
}
