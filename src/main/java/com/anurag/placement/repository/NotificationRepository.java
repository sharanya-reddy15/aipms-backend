package com.anurag.placement.repository;

import com.anurag.placement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentEmailOrderByCreatedAtDesc(
            String studentEmail
    );
}
