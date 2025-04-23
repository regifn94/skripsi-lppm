package com.skripsi.lppm.controller;

import com.skripsi.lppm.component.JwtUtil;
import com.skripsi.lppm.dto.LoginRequest;
import com.skripsi.lppm.dto.RegisterRequest;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.service.NotificationService;
import com.skripsi.lppm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String token = jwtUtil.generateToken(user);
        var notifications = notificationService.getUnreadNotifications(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);
        response.put("notifications", notifications);
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
