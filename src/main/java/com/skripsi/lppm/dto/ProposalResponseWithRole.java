package com.skripsi.lppm.dto;

import com.skripsi.lppm.model.Proposal;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProposalResponseWithRole {
    private Proposal proposal;
    private List<String> roles;
}
