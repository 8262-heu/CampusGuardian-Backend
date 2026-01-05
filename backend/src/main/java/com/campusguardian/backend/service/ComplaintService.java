package com.campusguardian.backend.service;

import com.campusguardian.backend.model.Complaint;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ComplaintService {

    private static final String COLLECTION_NAME = "complaints";
    private static final String DEPT_COLLECTION = "departments"; // 🔥 NEW

    // ---------- CREATE ----------
    public Complaint createComplaint(Complaint c)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        c.setId(UUID.randomUUID().toString());

        if (c.getStatus() == null || c.getStatus().isEmpty()) {
            c.setStatus("Pending");
        }

        if (c.getPriority() == null || c.getPriority().isEmpty()) {
            c.setPriority("Normal");
        }

        String now = LocalDateTime.now().toString();
        c.setCreatedAt(now);
        c.setUpdatedAt(now);

        // 🔥 FIX: Resolve departmentName from departmentId
        if (c.getDepartmentId() != null && !c.getDepartmentId().isEmpty()) {

            DocumentSnapshot deptDoc =
                    db.collection(DEPT_COLLECTION)
                            .document(c.getDepartmentId())
                            .get()
                            .get();

            if (deptDoc.exists()) {
                c.setDepartmentName(deptDoc.getString("name"));
            }
        }

        db.collection(COLLECTION_NAME)
                .document(c.getId())
                .set(c)
                .get();

        return c;
    }

    // ---------- GET ALL ----------
    public List<Complaint> getAllComplaints()
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future =
                db.collection(COLLECTION_NAME).get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<Complaint> list = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            list.add(doc.toObject(Complaint.class));
        }

        return list;
    }

    // ---------- GET BY EMAIL ----------
    public List<Complaint> getComplaintsByEmail(String email)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future =
                db.collection(COLLECTION_NAME)
                        .whereEqualTo("email", email)
                        .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<Complaint> list = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            list.add(doc.toObject(Complaint.class));
        }

        return list;
    }

    // ---------- GET BY DEPARTMENT ----------
    public List<Complaint> getComplaintsByDepartment(String departmentId)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future =
                db.collection(COLLECTION_NAME)
                        .whereEqualTo("departmentId", departmentId)
                        .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<Complaint> list = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            list.add(doc.toObject(Complaint.class));
        }

        return list;
    }

    // ---------- UPDATE STATUS ----------
    public boolean updateStatus(String id, String status)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef =
                db.collection(COLLECTION_NAME).document(id);

        DocumentSnapshot doc = docRef.get().get();
        if (!doc.exists()) return false;

        docRef.update(
                "status", status,
                "updatedAt", LocalDateTime.now().toString()
        ).get();

        return true;
    }

    // ---------- UPDATE PRIORITY ----------
    public boolean updatePriority(String id, String priority)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef =
                db.collection(COLLECTION_NAME).document(id);

        DocumentSnapshot doc = docRef.get().get();
        if (!doc.exists()) return false;

        docRef.update(
                "priority", priority,
                "updatedAt", LocalDateTime.now().toString()
        ).get();

        return true;
    }

    // ---------- DELETE ----------
    public boolean deleteComplaint(String id)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef =
                db.collection(COLLECTION_NAME).document(id);

        DocumentSnapshot doc = docRef.get().get();
        if (!doc.exists()) return false;

        docRef.delete().get();
        return true;
    }
}