package com.anurag.placement.controller;

import com.anurag.placement.entity.Job;
import com.anurag.placement.repository.JobRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {

        this.jobRepository = jobRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Job createJob(
            @RequestBody Job job) {

        return jobRepository.save(job);
    }

    @GetMapping
    public List<Job> getJobs() {

        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public Job getJob(
            @PathVariable Long id) {

        return jobRepository
                .findById(id)
                .orElseThrow();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Job updateJob(
            @PathVariable Long id,
            @RequestBody Job updatedJob) {

        Job job =
                jobRepository
                        .findById(id)
                        .orElseThrow();

        job.setTitle(updatedJob.getTitle());
        job.setCompany(updatedJob.getCompany());
        job.setDescription(updatedJob.getDescription());
        job.setRequiredSkills(
                updatedJob.getRequiredSkills()
        );
        job.setMinCgpa(updatedJob.getMinCgpa());
        job.setBranch(updatedJob.getBranch());
        job.setGraduationYear(
                updatedJob.getGraduationYear()
        );
        job.setExperience(updatedJob.getExperience());
        job.setSalary(updatedJob.getSalary());
        job.setLocation(updatedJob.getLocation());
        job.setJobType(updatedJob.getJobType());

        return jobRepository.save(job);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobRepository.deleteById(id);

        return "Job deleted successfully";
    }
}
