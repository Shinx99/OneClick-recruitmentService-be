package com.onceClick.recruitmentService.infrastructure.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onceClick.recruitmentService.shared.security.CustomUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS()
                .setWebSocketEnabled(true)
                .setHeartbeatTime(25000);

        registry.addEndpoint("/ws/secure")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }

    // MessageConverters để fix lỗi application/javascript
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Cấu hình ObjectMapper hỗ trợ Java 8 Time
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Converter cho JSON
        MappingJackson2MessageConverter jsonConverter = new MappingJackson2MessageConverter();
        jsonConverter.setObjectMapper(objectMapper);

        // Converter cho String
        StringMessageConverter stringConverter = new StringMessageConverter();

        // Thêm JSON converter trước
        messageConverters.add(jsonConverter);
        messageConverters.add(stringConverter);

        return false;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal user = accessor.getUser();

        log.info("WebSocket CONNECTED - SessionId: {}, User: {}",
                sessionId, user != null ? user.getName() : "anonymous");

        if (user instanceof CustomUserPrincipal) {
            CustomUserPrincipal principal = (CustomUserPrincipal) user;
            log.info("User details - ID: {}, Type: {}",
                    principal.getUserId(), principal.getUserType());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal user = accessor.getUser();

        log.info("WebSocket DISCONNECTED - SessionId: {}, User: {}",
                sessionId, user != null ? user.getName() : "anonymous");
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal user = accessor.getUser();

        log.debug("WebSocket SUBSCRIBE - User: {}, Destination: {}",
                user != null ? user.getName() : "anonymous", destination);
    }
}