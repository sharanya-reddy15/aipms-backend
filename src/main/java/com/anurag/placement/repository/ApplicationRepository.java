package com.anurag.placement.repository;

import com.anurag.placement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentEmail(String studentEmail);

    boolean existsByStudentEmailAndJobId(
            String studentEmail,
            Long jobId
    );
}
