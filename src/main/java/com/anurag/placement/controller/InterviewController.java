package com.anurag.placement.controller;

import com.anurag.placement.entity.Interview;
import com.anurag.placement.repository.InterviewRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:5173")
public class InterviewController {

    private final InterviewRepository repository;

    public InterviewController(
            InterviewRepository repository) {

        this.repository = repository;
    }

    @PostMapping
    public Interview createInterview(
            @RequestBody Interview interview) {

        if (interview.getStatus() == null) {

            interview.setStatus("Scheduled");
        }

        return repository.save(interview);
    }

    @GetMapping
    public List<Interview> getAll() {

        return repository.findAll();
    }

    @GetMapping("/student/{email}")
    public List<Interview> getStudentInterviews(
            @PathVariable String email) {

        return repository.findByStudentEmail(email);
    }

    @PutMapping("/{id}")
    public Interview updateInterview(
            @PathVariable Long id,
            @RequestBody Interview updated) {

        Interview interview =
                repository.findById(id)
                        .orElseThrow();

        interview.setInterviewDate(
                updated.getInterviewDate()
        );

        interview.setMode(
                updated.getMode()
        );

        interview.setMeetingLink(
                updated.getMeetingLink()
        );

        interview.setStatus(
                updated.getStatus()
        );

        return repository.save(interview);
    }
}
