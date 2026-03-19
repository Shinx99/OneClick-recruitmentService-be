package com.onceClick.recruitmentService.features.candidate.handler;

import com.onceClick.recruitmentService.features.candidate.dto.CandidateDto;
import com.onceClick.recruitmentService.infrastructure.feign.AuthAccountDto;
import com.onceClick.recruitmentService.infrastructure.feign.AuthServiceClient;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRoleValueException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateProfileHandler {

    private final CandidateRepository candidateRepository;



//    @Transactional
//    public CandidateDto createProfile(UUID accountId){
//
//        // 1. Check exist
//        if(candidateRepository.existsById(accountId)) {
//            throw new FeignException.Conflict("Candidate profile already exists");
//        }
//
//        // 2. Sync from Auth Service
//        AuthAccountDto accountDto = syncAccountData(accountId);
//
//        // 3. Check valid role
//        if(!"CANDIDATE".equalsIgnoreCase(accountDto.getRole())){
//            throw new InvalidRoleValueException("Account is not CANDIDATE: " + accountDto.getRole());
//        }
//
//
//
//    }





}



