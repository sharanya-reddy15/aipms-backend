package com.anurag.placement.controller;

import com.anurag.placement.entity.Resume;
import com.anurag.placement.entity.Student;
import com.anurag.placement.repository.ResumeRepository;
import com.anurag.placement.repository.StudentRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final StudentRepository studentRepository;

    public ResumeController(ResumeRepository resumeRepository,
                            StudentRepository studentRepository) {
        this.resumeRepository = resumeRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/upload/{studentId}")
    public Map<String, Object> uploadResume(
            @PathVariable Long studentId,
            @RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) throw new RuntimeException("Please select a PDF file");
        if (file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            String text = new PDFTextStripper().getText(document);
            String detectedSkills = String.join(", ", extractSkills(text));

            Resume resume = resumeRepository.findByStudentId(studentId)
                    .orElse(new Resume());
            resume.setStudentId(studentId);
            resume.setFileName(file.getOriginalFilename());
            resume.setExtractedText(text);
            resume.setSkills(detectedSkills);
            resume.setEducation(extractSection(text, "education"));
            resume.setProjects(extractSection(text, "projects|project"));
            resume.setCertifications(extractSection(text, "certifications|certificates"));
            resume.setExperience(extractSection(text, "experience|internship"));
            resumeRepository.save(resume);

            student.setResumeFileName(file.getOriginalFilename());
            student.setSkills(detectedSkills);
            studentRepository.save(student);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Resume uploaded and analyzed successfully");
            response.put("resumeId", resume.getId());
            response.put("fileName", resume.getFileName());
            response.put("skills", extractSkills(text));
            response.put("education", resume.getEducation());
            response.put("projects", resume.getProjects());
            response.put("certifications", resume.getCertifications());
            response.put("experience", resume.getExperience());
            return response;
        }
    }

    @GetMapping("/{studentId}")
    public Resume getResume(@PathVariable Long studentId) {
        return resumeRepository.findByStudentId(studentId).orElse(null);
    }

    private Set<String> extractSkills(String text) {
        String lower = text.toLowerCase();
        String[] knownSkills = {
                "java", "python", "c++", "javascript", "typescript", "react",
                "html", "css", "spring boot", "spring", "mysql", "sql",
                "mongodb", "node.js", "git", "github", "machine learning",
                "deep learning", "artificial intelligence", "tensorflow", "pytorch",
                "pandas", "numpy", "aws", "google cloud", "docker", "rest api"
        };

        Set<String> result = new LinkedHashSet<>();
        for (String skill : knownSkills) {
            if (lower.contains(skill)) result.add(skill);
        }
        return result;
    }

    private String extractSection(String text, String headings) {
        String[] lines = text.split("\\R");
        StringBuilder result = new StringBuilder();
        boolean capture = false;

        for (String line : lines) {
            String clean = line.trim();
            String lower = clean.toLowerCase();

            if (lower.matches("(?i).*\\b(" + headings + ")\\b.*")) {
                capture = true;
                continue;
            }

            if (capture && lower.matches(
                    "(?i)^(education|projects?|certifications?|certificates?|experience|internship|skills|technical skills|achievements|languages|summary|objective)\\s*[:\\-]?$")) {
                break;
            }

            if (capture && !clean.isBlank()) {
                if (result.length() > 0) result.append(" ");
                result.append(clean);
            }
        }
        return result.toString();
    }
}
