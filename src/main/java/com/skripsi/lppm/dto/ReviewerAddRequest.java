package com.skripsi.lppm.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewerAddRequest {
    private List<Long> reviewerIds;
}
