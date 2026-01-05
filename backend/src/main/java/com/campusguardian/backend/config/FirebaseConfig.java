package com.campusguardian.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) return;

            InputStream serviceAccount;

            // 🔥 1️⃣ Try ENV first (Render / Railway)
            String firebaseEnv = System.getenv("FIREBASE_SERVICE_ACCOUNT");

            if (firebaseEnv != null && !firebaseEnv.isBlank()) {
                serviceAccount = new ByteArrayInputStream(
                        firebaseEnv.getBytes(StandardCharsets.UTF_8)
                );
                System.out.println("🔥 Firebase key loaded from ENV");
            }
            // 🔥 2️⃣ Fallback to local file
            else {
                serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "campusguardian-8620a-firebase-adminsdk-fbsvc-bb2757a6b8.json"
                        );

                if (serviceAccount == null) {
                    throw new RuntimeException("Firebase key not found (ENV or resources)");
                }

                System.out.println("🔥 Firebase key loaded from resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("🔥 Firebase initialized");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firebase init failed");
        }
    }
}