package com.onceClick.recruitmentService.features.deleteCv;

import java.util.UUID;

public interface DeleteCvService {


    void softDeleteCv(UUID candidateId, UUID resumeId);
    void softDeleteAllCvs(UUID candidateId);

}
