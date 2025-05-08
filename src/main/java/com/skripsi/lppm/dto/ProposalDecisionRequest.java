package com.skripsi.lppm.dto;

import com.skripsi.lppm.model.enums.DecisionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalDecisionRequest {

    private Long proposalId;

    private Long decidedByUserId;

    @NotNull(message = "Status must not be null")
    private DecisionStatus status;

    private String note;
}