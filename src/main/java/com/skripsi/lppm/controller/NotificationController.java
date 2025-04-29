package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification API", description = "API untuk mengelola notifikasi user")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> notifications(){
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Ambil semua notifikasi belum dibaca untuk user tertentu",
            description = "Mengambil list notifikasi yang belum dibaca berdasarkan ID user."
    )
    public ResponseEntity<?> notification(@PathVariable("id") Long id){
        return ResponseEntity.ok(notificationService.getUnreadNotifications(id));
    }

    @GetMapping("/mark-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
