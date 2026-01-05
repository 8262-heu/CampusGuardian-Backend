package com.campusguardian.backend.controller;

import com.campusguardian.backend.model.Complaint;
import com.campusguardian.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // ---------- CREATE ----------
    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint)
            throws ExecutionException, InterruptedException {
        return complaintService.createComplaint(complaint);
    }

    // ---------- GET ALL ----------
    @GetMapping
    public List<Complaint> getAllComplaints()
            throws ExecutionException, InterruptedException {
        return complaintService.getAllComplaints();
    }

    // ---------- GET BY EMAIL ----------
    @GetMapping("/by-email")
    public List<Complaint> getComplaintsByEmail(@RequestParam String email)
            throws ExecutionException, InterruptedException {
        return complaintService.getComplaintsByEmail(email);
    }

    // 🔥 ---------- GET BY DEPARTMENT (NEW) ----------
    @GetMapping("/by-department")
    public List<Complaint> getComplaintsByDepartment(
            @RequestParam String departmentId)
            throws ExecutionException, InterruptedException {

        return complaintService.getComplaintsByDepartment(departmentId);
    }

    // ---------- UPDATE STATUS ----------
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStatus(
            @PathVariable String id,
            @RequestParam String status)
            throws ExecutionException, InterruptedException {

        boolean updated = complaintService.updateStatus(id, status);

        if (updated)
            return ResponseEntity.ok("Status Updated Successfully");
        else
            return ResponseEntity.status(404).body("Complaint Not Found");
    }

    // ---------- UPDATE PRIORITY ----------
    @PutMapping("/{id}/priority")
    public ResponseEntity<String> updatePriority(
            @PathVariable String id,
            @RequestParam String priority)
            throws ExecutionException, InterruptedException {

        boolean updated = complaintService.updatePriority(id, priority);

        if (updated)
            return ResponseEntity.ok("Priority Updated Successfully");
        else
            return ResponseEntity.status(404).body("Complaint Not Found");
    }

    // ---------- DELETE ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(@PathVariable String id) {

        try {
            boolean deleted = complaintService.deleteComplaint(id);

            if (deleted)
                return ResponseEntity.ok("Deleted Successfully");
            else
                return ResponseEntity.status(404).body("Complaint Not Found");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting complaint");
        }
    }
}
