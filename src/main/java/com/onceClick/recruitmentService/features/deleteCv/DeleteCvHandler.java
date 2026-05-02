package com.onceClick.recruitmentService.features.deleteCv;

import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCvHandler {

    private final DeleteCvService deleteCvService;


    @CacheEvict(value = {
            "resume:by-resume-id",
            "resume:by-candidate-id",
            "resumes"
    }, allEntries = true)
    public void softDeleteCv(UUID candidateId, UUID resumeId) {
        deleteCvService.softDeleteCv(candidateId, resumeId);
    }


    @CacheEvict(value = {
            "resume:by-resume-id",
            "resume:by-candidate-id",
            "resumes"
    }, allEntries = true)
    public void softDeleteAllCvs(UUID candidateId) {
        deleteCvService.softDeleteAllCvs(candidateId);
    }


}