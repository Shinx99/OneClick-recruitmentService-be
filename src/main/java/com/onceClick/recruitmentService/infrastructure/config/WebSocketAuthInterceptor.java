package com.onceClick.recruitmentService.infrastructure.config;

import com.onceClick.recruitmentService.shared.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("🔵 WebSocketAuthInterceptor - Command: {}", accessor != null ? accessor.getCommand() : "null");
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("🔵 CONNECT frame detected");
            log.info("🔵 Authorization header: {}", authHeader != null ? "Present" : "Missing");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    Jwt jwt = jwtDecoder.decode(token);

                    String userIdStr = jwt.getClaimAsString("sub");
                    UUID userId = UUID.fromString(userIdStr);
                    String userType = extractUserTypeFromJwt(jwt);
                    List<String> roles = jwt.getClaimAsStringList("roles");

                    // Tạo CustomUserPrincipal thay vì anonymous Principal
                    CustomUserPrincipal principal = new CustomUserPrincipal(userId, userType);

                    // Tạo Authentication với CustomUserPrincipal
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            principal,  // Dùng principal thay vì userIdStr
                            null,
                            authorities
                    );

                    // Set vào SecurityContext
                    // Lưu ý: SecurityContextHolder không reliable trong WebSocket
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Set user vào STOMP session
                    accessor.setUser(principal);  // Set principal

                    // Lưu vào session attributes để dùng cho các messages sau
                    /*accessor.getSessionAttributes().put("SPRING_SECURITY_CONTEXT",
                            SecurityContextHolder.getContext());*/

                    // Lưu session attributes
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("userType", userType);

                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                }
            }
        }
        
        return message;
    }
    
    private String extractUserTypeFromJwt(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            if (roles.contains("admin")) return "admin";
            if (roles.contains("recruiter")) return "recruiter";
            if (roles.contains("candidate")) return "candidate";
        }
        
        // Fallback to user_type claim
        String userType = jwt.getClaimAsString("user_type");
        if (userType != null) return userType;
        
        return "anonymous";
    }
}