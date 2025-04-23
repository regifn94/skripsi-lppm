package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Notification;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> getUnreadNotifications(Long userId) {
        try {
            return notificationRepository.findByUserIdAndIsReadFalse(userId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void sendNotification(Long userId, String message) {
        try {
            User user = new User();
            user.setId(userId);
            Notification notification = Notification.builder()
                    .user(user)
                    .message(message)
                    .isRead(false)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void markAsRead(Long notificationId) {
        try {
            Notification notif = notificationRepository.findById(notificationId).orElseThrow();
            notif.setRead(true);
            notificationRepository.save(notif);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}