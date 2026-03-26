// CvResponse.java
package com.onceClick.recruitmentService.features.downloadCv.dto;

import java.util.List;

public record CvResponse(
    List<CvDto> cvs,
    long totalCount
) {}