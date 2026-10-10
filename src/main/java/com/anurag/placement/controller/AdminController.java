package com.anurag.placement.controller;

import com.anurag.placement.repository.ApplicationRepository;
import com.anurag.placement.repository.InterviewRepository;
import com.anurag.placement.repository.JobRepository;
import com.anurag.placement.repository.StudentRepository;
import com.anurag.placement.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public AdminController(
            UserRepository userRepository,
            StudentRepository studentRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("users", userRepository.count());
        result.put("students", studentRepository.count());
        result.put("jobs", jobRepository.count());
        result.put("applications", applicationRepository.count());
        result.put("interviews", interviewRepository.count());
        return result;
    }
}
