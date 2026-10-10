package com.anurag.placement.repository;

import com.anurag.placement.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByStudentId(Long studentId);
}
