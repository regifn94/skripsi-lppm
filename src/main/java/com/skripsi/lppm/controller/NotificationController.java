package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> notification(@PathVariable("id") Long id){
        return ResponseEntity.ok(notificationService.getUnreadNotifications(id));
    }

    @GetMapping("/mark-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
