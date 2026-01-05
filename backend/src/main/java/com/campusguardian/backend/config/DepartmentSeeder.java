package com.campusguardian.backend.config;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@DependsOn("firebaseConfig")   // ensures FirebaseConfig runs first
public class DepartmentSeeder {

    @PostConstruct
    public void seedDepartments() {

        try {
            // 🔒 Hard safety check
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("⚠️ Firebase not initialized. Skipping department seeding.");
                return;
            }

            Firestore db = FirestoreClient.getFirestore();

            Map<String, String> departments = new HashMap<>();
            departments.put("academic", "Academic Issues");
            departments.put("hostel", "Hostel / Accommodation");
            departments.put("canteen", "Canteen / Food");
            departments.put("transport", "Transport");
            departments.put("library", "Library");
            departments.put("personal", "Personal Issues");

            for (Map.Entry<String, String> entry : departments.entrySet()) {
                String id = entry.getKey();

                // ⚠️ Do NOT call Firestore aggressively on startup
                if (!db.collection("departments").document(id).get().get().exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", id);
                    data.put("name", entry.getValue());

                    db.collection("departments")
                            .document(id)
                            .set(data)
                            .get();
                }
            }

            System.out.println("✅ Departments seeded successfully");

        } catch (Exception e) {
            // ❌ DO NOT crash the app if Firebase auth/network glitches
            System.out.println("⚠️ Department seeding skipped (safe fail): " + e.getMessage());
        }
    }
}