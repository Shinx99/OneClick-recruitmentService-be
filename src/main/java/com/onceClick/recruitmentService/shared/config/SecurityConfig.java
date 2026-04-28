package com.onceClick.recruitmentService.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(authz -> authz

                        // Actuator & Health
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // API Documentation
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ========== JOB APPLICATION APIs ==========
                        // Cần authenticated cho tất cả API job application
                        .requestMatchers(
                                        "/api/jobs/apply",
                                        "/api/jobs/*/check-applied",
                                        "/api/applications/my-applications",
                                        "/api/applications/*",
                                        "/api/applications/*/*",
                                        "/api/employer/jobs/*/applications",
                                        "/api/employer/applications/*/*/status"
                        ).authenticated()

                                // ========== RECRUITER APPLICATION APIs (MỚI) ==========
                        .requestMatchers(
                                        // Candidate APIs
                                        "/api/jobs/apply",
                                        "/api/jobs/*/check-applied",
                                        "/api/applications/my-applications",
                                        "/api/applications/*",
                                        "/api/applications/*/*",

                                        // Recruiter APIs
                                        "/api/employer/jobs/*/applications",
                                        "/api/employer/applications/*/*/status",

                                        // ========== RECRUITER APIs (MỚI - THÊM CÁC DÒNG NÀY) ==========
                                        "/api/employer/jobs/*/applications/stats",           // Thống kê
                                        "/api/employer/applications/*",                      // Chi tiết đơn
                                        "/api/employer/applications/*/history",              // Lịch sử
                                        "/api/employer/applications/*/status",               // Cập nhật status (API mới)
                                        "/api/employer/applications/*/schedule-interview",   // Lên lịch PV
                                        "/api/employer/schedule-interview/*",                // Hủy lịch PV
                                        "/api/employer/jobs",                                // Danh sách job của employer

                                        // Notification APIs
                                        "/api/notifications/my",
                                        "/api/notifications/unread-count",
                                        "/api/notifications/*/read",
                                        "/api/employer/notifications",
                                        "/api/employer/notifications/unread-count",

                                // ========== THÊM API XEM CV ==========
                                "/api/profile/cv/stream/*/*"  // Cho phép recruiter xem CV của candidate
                        ).authenticated()

                                // ========== AI CV MATCH APIs ==========
                                .requestMatchers("/api/ai-cv-match/**").permitAll()
                                .requestMatchers("/api/ai-cv-match/resume/**").permitAll()
                                .requestMatchers("/api/ai-cv-match/new/**").permitAll()
                                .requestMatchers("/api/ai-cv-match/s3/**").permitAll()
                                .requestMatchers("/api/ai-cv-match/cache/**").permitAll()

                                // ========== RECRUITMENT APIs ==========
                        .requestMatchers("/api/recruitment/job/all").permitAll()
                        .requestMatchers("/api/recruitment/candidate/**").authenticated()
                        .requestMatchers("/api/recruitment/employer/**").authenticated()
//                        .requestMatchers("/api/recruitment/job/**").authenticated()
                        .requestMatchers("/api/recruitment/**").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_admin")

                        // Internal path
                        .requestMatchers("/api/internal/**").permitAll()

                        // Profile APIs
                        .requestMatchers("/api/profile/**").permitAll()

                        // Chatbot
                        .requestMatchers(
                                "/api/chatbot/ws/**",
                                "/api/chatbot/webhook/**"
                        ).permitAll()

                        // User endpoints (cần authenticated)
                        .requestMatchers(
                                "/api/chatbot/me/**",
                                "/api/chatbot/conversations/**"
                        ).authenticated()

                        // Admin endpoints (chỉ admin)
                        //.requestMatchers("/api/chatbot/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/chatbot/admin/**").permitAll()

                        //ws
                        // WebSocket & SockJS - Tất cả đều permitAll (authentication qua interceptor)
                        .requestMatchers("/ws/**").permitAll()
                        /*.requestMatchers("/ws").permitAll()
                        .requestMatchers("/ws/info").permitAll()*/
                        //.requestMatchers("/ws/**").permitAll() // Auth qua WebSocket interceptor


                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())

                ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        converter.setPrincipalClaimName("sub");
        return converter;
    }
}
