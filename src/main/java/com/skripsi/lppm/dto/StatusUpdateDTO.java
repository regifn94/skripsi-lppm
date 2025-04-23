package com.skripsi.lppm.dto;

import com.skripsi.lppm.model.enums.StatusPenelitian;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class StatusUpdateDTO {
    private StatusPenelitian status;
    private String reason;
}