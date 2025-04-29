package com.skripsi.lppm.controller;

import com.skripsi.lppm.component.JwtUtil;
import com.skripsi.lppm.dto.LoginRequest;
import com.skripsi.lppm.dto.RegisterRequest;
import com.skripsi.lppm.model.Notification;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.service.NotificationService;
import com.skripsi.lppm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request);
        if(Objects.isNull(user)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong username or password");
        }
        String token = jwtUtil.generateToken(user);

        List<Notification> notifications = notificationService.getUnreadNotifications(user.getId());

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
        response.put("user", user);
        response.put("token", token);
        response.put("notifications", simplifiedNotifications);
        response.put("notificationSummary", Map.of(
                "totalRead", readCount,
                "totalUnread", unreadCount
        ));

        return ResponseEntity.ok(response);
    }


//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
//        String refreshTokenStr = request.get("refreshToken");
//        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshTokenStr)
//                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
//
//        if (!refreshTokenService.isValid(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
//        }
//
//        String token = jwtUtil.generateToken(refreshToken.getUser());
//        return ResponseEntity.ok(Map.of("token", token));
//    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        List<String> roles = jwtUtil.extractRoles(jwt);
        return ResponseEntity.ok(Map.of("username", username, "roles", roles));
    }
}
