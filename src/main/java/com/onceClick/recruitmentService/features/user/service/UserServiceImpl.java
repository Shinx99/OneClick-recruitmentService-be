// features/user/service/impl/UserServiceImpl.java
package com.onceClick.recruitmentService.features.user.service;

import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CandidateRepository candidateRepository;
    private final EmployerRepository employerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_INFO_PREFIX = "user:info:";
    private static final long CACHE_TTL_MINUTES = 30;

    @Override
    public UserInfo getUserInfo(UUID userId, String userType) {
        String cacheKey = USER_INFO_PREFIX + userId;
        
        // 1. Check cache
        UserInfo cached = (UserInfo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("✅ Cache hit for userId: {}", userId);
            return cached;
        }
        
        // 2. Load from DB
        log.debug("❌ Cache miss for userId: {}, loading from DB", userId);
        UserInfo userInfo = null;
        
        if ("candidate".equals(userType)) {
            userInfo = candidateRepository.findById(userId)
                    .map(this::toUserInfo)
                    .orElse(null);
        } else if ("recruiter".equals(userType)) {
            userInfo = employerRepository.findById(userId)
                    .map(this::toUserInfo)
                    .orElse(null);
        }
        
        // 3. Cache it
        if (userInfo != null) {
            redisTemplate.opsForValue().set(cacheKey, userInfo, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        
        return userInfo;
    }
    
    @Override
    public void evictUserCache(UUID userId) {
        String cacheKey = USER_INFO_PREFIX + userId;
        redisTemplate.delete(cacheKey);
        log.info("🗑️ Evicted cache for userId: {}", userId);
    }
    
    private UserInfo toUserInfo(Candidate candidate) {
        return new UserInfo(
                candidate.getCandidateId(),
                getFullName(candidate.getSurname(), candidate.getName()),
                candidate.getEmail(),
                candidate.getAvatarUrl(),
                "candidate"
        );
    }
    
    private UserInfo toUserInfo(Employer employer) {
        return new UserInfo(
                employer.getEmployerId(),
                getFullName(employer.getSurname(), employer.getName()),
                employer.getEmail(),
                employer.getAvatarUrl(),
                "recruiter"
        );
    }
    
    private String getFullName(String surname, String name) {
        if (surname == null && name == null) return null;
        if (surname == null) return name;
        if (name == null) return surname;
        return surname + " " + name;
    }
}