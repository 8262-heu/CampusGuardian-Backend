package com.campusguardian.backend.controller;

import com.campusguardian.backend.model.Department;
import com.campusguardian.backend.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    // GET all departments (for dropdowns)
    @GetMapping
    public List<Department> getDepartments() throws Exception {
        return service.getAll();
    }
}
