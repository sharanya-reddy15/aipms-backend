package com.anurag.placement.repository;

import com.anurag.placement.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByStudentEmail(String studentEmail);
}
