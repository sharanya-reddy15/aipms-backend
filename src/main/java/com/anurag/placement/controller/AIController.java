package com.anurag.placement.controller;

import com.anurag.placement.entity.Job;
import com.anurag.placement.entity.Student;
import com.anurag.placement.repository.JobRepository;
import com.anurag.placement.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AIController {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public AIController(StudentRepository studentRepository, JobRepository jobRepository) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/match/{studentId}/{jobId}")
    public Map<String, Object> match(@PathVariable Long studentId, @PathVariable Long jobId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Job job = jobRepository.findById(jobId).orElseThrow();

        Set<String> studentSkills = toSkillSet(student.getSkills());
        Set<String> requiredSkills = toSkillSet(job.getRequiredSkills());

        List<String> matched = new ArrayList<>();
        List<String> gaps = new ArrayList<>();
        for (String required : requiredSkills) {
            boolean found = studentSkills.stream().anyMatch(s -> s.equals(required)
                    || s.contains(required) || required.contains(s));
            if (found) matched.add(required); else gaps.add(required);
        }

        double cosine = cosineSimilarity(
                student.getSkills() == null ? "" : student.getSkills(),
                job.getRequiredSkills() == null ? "" : job.getRequiredSkills());
        int skillScore = requiredSkills.isEmpty()
                ? 0
                : (int) Math.round((matched.size() * 100.0 / requiredSkills.size()));
        int educationScore = calculateEducation(student, job);
        int experienceScore = calculateExperience(student, job);
        int projectScore = hasText(student.getProjects()) ? 100 : 0;
        int certificationScore = hasText(student.getCertifications()) ? 100 : 0;
        int eligibilityScore = calculateEligibility(student, job);

        int finalScore = (int) Math.round(
                skillScore * 0.40 +
                educationScore * 0.20 +
                experienceScore * 0.15 +
                projectScore * 0.15 +
                certificationScore * 0.10);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studentId", studentId);
        result.put("jobId", jobId);
        result.put("matchScore", Math.min(100, finalScore));
        result.put("cosineSimilarity", Math.round(cosine * 1000.0) / 1000.0);
        result.put("skillScore", skillScore);
        result.put("educationScore", educationScore);
        result.put("experienceScore", experienceScore);
        result.put("projectScore", projectScore);
        result.put("certificationScore", certificationScore);
        result.put("eligibilityScore", eligibilityScore);
        result.put("eligible", eligibilityScore >= 75);
        result.put("matchedSkills", matched);
        result.put("skillGaps", gaps);
        result.put("message", getMessage(finalScore));
        return result;
    }

    @GetMapping("/recommendations/{studentId}")
    public List<Map<String, Object>> recommendations(@PathVariable Long studentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Job job : jobRepository.findAll()) {
            Map<String, Object> match = match(studentId, job.getId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("job", job);
            item.put("matchScore", match.get("matchScore"));
            item.put("skillGaps", match.get("skillGaps"));
            item.put("eligible", match.get("eligible"));
            result.add(item);
        }
        result.sort((a, b) -> Integer.compare(
                (Integer) b.get("matchScore"), (Integer) a.get("matchScore")));
        return result;
    }

    private int calculateEducation(Student student, Job job) {
        if (job.getGraduationYear() == null || student.getGraduationYear() == null) return 50;
        return Objects.equals(job.getGraduationYear(), student.getGraduationYear()) ? 100 : 0;
    }

    private int calculateExperience(Student student, Job job) {
        if (!hasText(job.getExperience()) || !hasText(student.getExperience())) return 50;
        if (student.getExperience().toLowerCase().contains("fresher")
                && job.getExperience().toLowerCase().contains("fresher")) return 100;
        return student.getExperience().toLowerCase().contains(job.getExperience().toLowerCase()) ? 100 : 50;
    }

    private int calculateEligibility(Student student, Job job) {
        int score = 0;
        if (student.getCgpa() == null || job.getMinCgpa() == null || student.getCgpa() >= job.getMinCgpa()) score += 50;
        if (!hasText(job.getBranch()) || !hasText(student.getBranch())
                || job.getBranch().equalsIgnoreCase(student.getBranch())) score += 25;
        if (job.getGraduationYear() == null || student.getGraduationYear() == null
                || Objects.equals(job.getGraduationYear(), student.getGraduationYear())) score += 25;
        return score;
    }

    private Set<String> toSkillSet(String value) {
        Set<String> result = new LinkedHashSet<>();
        if (!hasText(value)) return result;
        for (String item : value.toLowerCase().split(",")) {
            String skill = item.trim();
            if (!skill.isEmpty()) result.add(skill);
        }
        return result;
    }

    private double cosineSimilarity(String first, String second) {
        Map<String, Integer> a = termFrequency(first);
        Map<String, Integer> b = termFrequency(second);
        Set<String> vocabulary = new HashSet<>(a.keySet());
        vocabulary.addAll(b.keySet());
        double dot = 0, normA = 0, normB = 0;
        for (String word : vocabulary) {
            double x = a.getOrDefault(word, 0);
            double y = b.getOrDefault(word, 0);
            dot += x * y;
            normA += x * x;
            normB += y * y;
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private Map<String, Integer> termFrequency(String text) {
        Map<String, Integer> tf = new HashMap<>();
        if (!hasText(text)) return tf;
        for (String token : text.toLowerCase().replaceAll("[^a-z0-9+#. ]", " ").split("\\s+")) {
            if (token.length() >= 2) tf.merge(token, 1, Integer::sum);
        }
        return tf;
    }

    private boolean hasText(String value) { return value != null && !value.isBlank(); }

    private String getMessage(int score) {
        if (score >= 80) return "Strong job match";
        if (score >= 60) return "Good job match";
        if (score >= 40) return "Moderate job match";
        return "Low job match";
    }
}
