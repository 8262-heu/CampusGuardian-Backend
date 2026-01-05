package com.campusguardian.backend.service;

import com.campusguardian.backend.model.Department;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class DepartmentService {

    private static final String COLLECTION = "departments";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    // 🔥 Auto seed departments (runs once safely)
    @PostConstruct
    public void seedDepartments() {
        try {
            CollectionReference ref = db().collection(COLLECTION);
            ApiFuture<QuerySnapshot> future = ref.get();
            if (!future.get().isEmpty()) return;

            List<Department> defaults = List.of(
                    new Department("hostel", "Hostel", "Hostel & accommodation issues", true),
                    new Department("it", "IT", "Internet, portal & technical issues", true),
                    new Department("security", "Security", "Safety & security concerns", true),
                    new Department("academics", "Academics", "Classes, exams, faculty", true),
                    new Department("admin", "Administration", "Office & admin delays", true),
                    new Department("harassment", "Harassment Cell", "Sensitive complaints", true),
                    new Department("personal", "Personal Issues", "Confidential personal matters", true),
                    new Department("general", "General", "Other campus issues", true)
            );

            for (Department d : defaults) {
                ref.document(d.getId()).set(d);
            }

            System.out.println("✅ Departments seeded successfully");

        } catch (Exception e) {
            System.err.println("❌ Department seeding failed: " + e.getMessage());
        }
    }

    // Fetch all active departments
    public List<Department> getAll() throws Exception {
        List<Department> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db().collection(COLLECTION).whereEqualTo("active", true).get();
        for (QueryDocumentSnapshot doc : future.get()) {
            list.add(doc.toObject(Department.class));
        }
        return list;
    }
}
