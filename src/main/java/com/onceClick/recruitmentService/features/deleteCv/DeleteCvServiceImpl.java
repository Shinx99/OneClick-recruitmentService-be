package com.onceClick.recruitmentService.features.deleteCv;

import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DeleteCvServiceImpl implements DeleteCvService {

    private final ResumeRepository resumeRepo;

    @Override
    @Transactional
    public void softDeleteCv(UUID candidateId, UUID resumeId) {
        Resume resume = resumeRepo.findById(resumeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Resume not found: resumeId=" + resumeId + ", candidateId=" + candidateId
                ));

        if (!resume.getCandidateId().equals(candidateId)) {
            throw new IllegalArgumentException(
                    "Resume does not belong to this candidate: candidateId=" + candidateId
            );
        }

        List<Resume> activeResumes = resumeRepo.findByCandidateIdAndStatus(candidateId, "active");

        if (resume.getIsDefault()) {
            // Nếu còn CV active khác → không cho xóa default
            if (activeResumes.size() >= 2) {
                throw new IllegalStateException(
                        "Cannot delete default CV when there are other active CVs. Please change default CV first."
                );
            }
        }

        resume.setStatus("deleted");
        resume.setDeletedAt(Instant.now());
        resumeRepo.save(resume);

        // Sau khi xoá, nếu còn CV active → chọn 1 CV mới làm default
        List<Resume> stillActive = resumeRepo.findByCandidateIdAndStatus(candidateId, "active");
        if (!stillActive.isEmpty() && resume.getIsDefault()) {
            Resume newDefault = stillActive.get(0); // hoặc chọn theo createdAt
            resumeRepo.setDefault(newDefault.getResumeId(), candidateId);
        }
        // Nếu không còn CV active → candidate không có CV default
    }

    @Override
    @Transactional
    public void softDeleteAllCvs(UUID candidateId) {
        List<Resume> resumes = resumeRepo.findByCandidateId(candidateId);

        for (Resume resume : resumes) {
            resume.setStatus("deleted");
            resume.setDeletedAt(Instant.now());
        }

        resumeRepo.saveAll(resumes);
    }



}