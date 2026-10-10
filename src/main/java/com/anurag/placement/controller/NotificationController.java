package com.anurag.placement.controller;

import com.anurag.placement.entity.Notification;
import com.anurag.placement.repository.NotificationRepository;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationRepository repository;

    public NotificationController(
            NotificationRepository repository) {

        this.repository = repository;
    }

    @PostMapping
    public Notification create(
            @RequestBody Notification notification) {

        notification.setReadStatus(false);

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        return repository.save(notification);
    }

    @GetMapping("/{email}")
    public List<Notification> getNotifications(
            @PathVariable String email) {

        return repository
                .findByStudentEmailOrderByCreatedAtDesc(
                        email
                );
    }

    @PutMapping("/{id}/read")
    public Notification markRead(
            @PathVariable Long id) {

        Notification notification =
                repository.findById(id)
                        .orElseThrow();

        notification.setReadStatus(true);

        return repository.save(notification);
    }
}
