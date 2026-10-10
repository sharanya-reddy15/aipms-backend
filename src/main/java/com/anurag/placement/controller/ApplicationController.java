package com.anurag.placement.controller;

import com.anurag.placement.entity.Application;
import com.anurag.placement.repository.ApplicationRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationRepository repository;

    public ApplicationController(
            ApplicationRepository repository) {

        this.repository = repository;
    }

    @PostMapping
    public Application apply(
            @RequestBody Application application) {

        boolean exists =
                repository.existsByStudentEmailAndJobId(
                        application.getStudentEmail(),
                        application.getJobId()
                );

        if (exists) {

            throw new RuntimeException(
                    "Already applied for this job"
            );
        }

        application.setStatus("Applied");

        application.setAppliedDate(
                LocalDateTime.now()
        );

        return repository.save(application);
    }

    @GetMapping
    public List<Application> getApplications() {

        return repository.findAll();
    }

    @GetMapping("/student/{email}")
    public List<Application> getStudentApplications(
            @PathVariable String email) {

        return repository.findByStudentEmail(email);
    }

    @PutMapping("/{id}/status")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Application application =
                repository
                        .findById(id)
                        .orElseThrow();

        application.setStatus(status);

        return repository.save(application);
    }

    @DeleteMapping("/{id}")
    public String deleteApplication(
            @PathVariable Long id) {

        repository.deleteById(id);

        return "Application deleted successfully";
    }
}
