package com.anurag.placement.controller;

import com.anurag.placement.entity.Student;
import com.anurag.placement.repository.StudentRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(
            StudentRepository studentRepository) {

        this.studentRepository = studentRepository;
    }

    @PostMapping
    public Student createStudent(
            @RequestBody Student student) {

        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> getStudents() {

        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudent(
            @PathVariable Long id) {

        return studentRepository
                .findById(id)
                .orElseThrow();
    }

    @GetMapping("/email/{email}")
    public Student getStudentByEmail(
            @PathVariable String email) {

        return studentRepository
                .findByEmail(email)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Long id,
            @RequestBody Student updatedStudent) {

        Student student =
                studentRepository
                        .findById(id)
                        .orElseThrow();

        student.setName(
                updatedStudent.getName()
        );

        student.setEmail(
                updatedStudent.getEmail()
        );

        student.setPhone(
                updatedStudent.getPhone()
        );

        student.setCollege(
                updatedStudent.getCollege()
        );

        student.setBranch(
                updatedStudent.getBranch()
        );

        student.setCgpa(
                updatedStudent.getCgpa()
        );

        student.setGraduationYear(
                updatedStudent.getGraduationYear()
        );

        student.setSkills(
                updatedStudent.getSkills()
        );

        student.setProjects(
                updatedStudent.getProjects()
        );

        student.setCertifications(
                updatedStudent.getCertifications()
        );

        student.setExperience(
                updatedStudent.getExperience()
        );

        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(
            @PathVariable Long id) {

        studentRepository.deleteById(id);

        return "Student deleted successfully";
    }
}
