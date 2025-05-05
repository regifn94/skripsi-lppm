package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Notification;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Notification> getAllNotifications(){
        try {
            return notificationRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    public void sendNotification(User user, String message, String proposal, Long proposalId) {
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .isRead(false)
                .relatedModel(proposal)
                .relatedId(proposalId)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
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