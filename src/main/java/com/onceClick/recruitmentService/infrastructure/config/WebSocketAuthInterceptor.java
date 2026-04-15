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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔵 WebSocket CONNECT frame detected");

            String token = extractToken(accessor);

            if (token != null) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);

                    String userIdStr = jwt.getClaimAsString("sub");
                    UUID userId = UUID.fromString(userIdStr);
                    String userType = extractUserTypeFromJwt(jwt);
                    List<String> roles = jwt.getClaimAsStringList("roles");

                    CustomUserPrincipal principal = new CustomUserPrincipal(userId, userType);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            jwt,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(principal);

                    // Lưu vào session attributes
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("userType", userType);

                    log.info("WebSocket authenticated: userId={}, userType={}", userId, userType);

                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage(), e);
                }
            } else {
                log.warn("No token found for WebSocket CONNECT");
                // Log available header names (không log giá trị)
                if (accessor.toNativeHeaderMap() != null) {
                    log.debug("Available native header names: {}", accessor.toNativeHeaderMap().keySet());
                }
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // Method 1: Từ Authorization header (dùng getFirstNativeHeader - public method)
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Token found in Authorization header");
            return authHeader.substring(7);
        }

        // Method 2: Từ native header "access_token"
        String accessTokenHeader = accessor.getFirstNativeHeader("access_token");
        if (accessTokenHeader != null && !accessTokenHeader.isEmpty()) {
            log.info("Token found in access_token header");
            return accessTokenHeader;
        }

        // Method 3: Từ native header "token"
        String tokenHeader = accessor.getFirstNativeHeader("token");
        if (tokenHeader != null && !tokenHeader.isEmpty()) {
            log.info("Token found in token header");
            return tokenHeader;
        }

        // Method 4: Từ query string (SockJS thường gửi token trong URL)
        // Query string có thể ở trong native header "query_string" hoặc trong destination
        String queryString = accessor.getFirstNativeHeader("query_string");
        if (queryString != null && queryString.contains("access_token=")) {
            log.info("Token found in query_string header");
            String token = extractTokenFromQueryString(queryString);
            if (token != null) return token;
        }

        // Method 5: Từ native header "native_headers" (đôi khi SockJS gửi ở đây)
        String nativeHeaders = accessor.getFirstNativeHeader("native_headers");
        if (nativeHeaders != null && nativeHeaders.contains("access_token=")) {
            log.info("Token found in native_headers header");
            String token = extractTokenFromQueryString(nativeHeaders);
            if (token != null) return token;
        }

        // Method 6: Lấy từ session attributes (nếu đã được lưu từ trước)
        if (accessor.getSessionAttributes() != null) {
            String cachedToken = (String) accessor.getSessionAttributes().get("access_token");
            if (cachedToken != null) {
                log.info("Token found in session attributes");
                return cachedToken;
            }
        }

        // Method 7: Log tất cả native header names để debug
        log.debug("Checking all native headers for token...");
        if (accessor.toNativeHeaderMap() != null) {
            accessor.toNativeHeaderMap().forEach((key, value) -> {
                if (key.toLowerCase().contains("token") || key.toLowerCase().contains("auth")) {
                    log.debug("Header '{}' = {}", key, value);
                }
            });
        }

        return null;
    }

    private String extractTokenFromQueryString(String queryString) {
        try {
            // Query string có thể là URL encoded
            String decoded = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
            String[] pairs = decoded.split("&");
            for (String pair : pairs) {
                if (pair.startsWith("access_token=")) {
                    return pair.substring("access_token=".length());
                }
                // Cũng có thể là param "token"
                if (pair.startsWith("token=")) {
                    return pair.substring("token=".length());
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse query string: {}", e.getMessage());
        }
        return null;
    }

    private String extractUserTypeFromJwt(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            if (roles.contains("admin")) return "admin";
            if (roles.contains("recruiter")) return "recruiter";
            if (roles.contains("candidate")) return "candidate";
        }

        String userType = jwt.getClaimAsString("user_type");
        if (userType != null) return userType;

        return "candidate";
    }
}