package com.mit.rma_web_application.dtos;

import com.mit.rma_web_application.models.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovelStatusDTO {
    private String username;
    private ApprovalStatus approvalStatus;

}
