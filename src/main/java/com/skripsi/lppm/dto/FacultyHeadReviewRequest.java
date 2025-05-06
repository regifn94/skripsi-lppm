package com.skripsi.lppm.dto;

import lombok.Data;

@Data
public class FacultyHeadReviewRequest {
    private Long proposalId;
    private Long reviewedById;
    private String status;
    private String notes;
}
