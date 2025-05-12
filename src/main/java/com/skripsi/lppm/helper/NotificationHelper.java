package com.skripsi.lppm.helper;

import com.skripsi.lppm.component.NotificationWebSocketController;
import com.skripsi.lppm.model.Notification;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationHelper {
    private final NotificationWebSocketController notificationWebSocketController;
    private final NotificationRepository notificationRepository;

    public void sendNotification(User user, String message, String relatedModel, Long proposalId) {
        try {
            if (user == null) return;

            Notification notification = Notification.builder()
                    .user(user)
                    .message(message)
                    .isRead(false)
                    .relatedModel(relatedModel)
                    .relatedId(proposalId)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            notificationWebSocketController.sendNotificationToUser(user.getId(), notification);
        }catch (Exception e){
            throw new RuntimeException("error : " + e.getMessage());
        }
    }
}
