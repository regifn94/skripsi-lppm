package com.skripsi.lppm.controller;

import com.skripsi.lppm.handler.MyWebSocketHandler;
import com.skripsi.lppm.model.Notification;
import com.skripsi.lppm.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", allowCredentials = "false")
@Tag(name = "Notification API", description = "API untuk mengelola notifikasi user")
public class NotificationController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MyWebSocketHandler myWebSocketHandler;

    // NotificationController.java
    @PostMapping("/test/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) throws Exception {
        Notification notif = Notification.builder()
                .message("Ini notifikasi test untuk user " + userId)
                .relatedModel("Proposal")
                .relatedId(123L)
                .createdAt(LocalDateTime.now()) // tambahkan timestamp
                .build();

        myWebSocketHandler.sendMessageToUser(userId.toString(), "");
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notif);
        return ResponseEntity.ok("Notifikasi dikirim ke /topic/notifications/" + userId);
    }

    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestBody Notification request) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + request.getId(),
                "Notifikasi untuk user " + request.getId() + ": " + request.getMessage()
        );
        return ResponseEntity.ok("Notifikasi dikirim ke /topic/notifications/" + request.getId());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Ambil semua notifikasi belum dibaca untuk user tertentu",
            description = "Mengambil list notifikasi yang belum dibaca berdasarkan ID user."
    )
     public ResponseEntity<?> getNotificationsByUserId(@PathVariable("id") Long userId){
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);

        // Hitung notifikasi read & unread
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        long readCount = notifications.stream().filter(Notification::isRead).count();

        // Sederhanakan notifikasi: ambil hanya field penting
        List<Map<String, Object>> simplifiedNotifications = notifications.stream().map(notification -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", notification.getId());
            map.put("message", notification.getMessage());
            map.put("createdAt", notification.getCreatedAt());
            map.put("relatedModel", notification.getRelatedModel());
            map.put("relatedId", notification.getRelatedId());
            map.put("read", notification.isRead());
            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", simplifiedNotifications);
        response.put("notificationSummary", Map.of(
                "totalRead", readCount,
                "totalUnread", unreadCount
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> notifications(){
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

//    @GetMapping("/{id}")
//    @Operation(
//            summary = "Ambil semua notifikasi belum dibaca untuk user tertentu",
//            description = "Mengambil list notifikasi yang belum dibaca berdasarkan ID user."
//    )
//    public ResponseEntity<?> notification(@PathVariable("id") Long id){
//        return ResponseEntity.ok(notificationService.getUnreadNotifications(id));
//    }

    @GetMapping("/mark-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
