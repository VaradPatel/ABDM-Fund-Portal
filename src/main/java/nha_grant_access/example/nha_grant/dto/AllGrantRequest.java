package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.entity.ImplementationTypes;
import nha_grant_access.example.nha_grant.entity.ProposalType;
import nha_grant_access.example.nha_grant.entity.States;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AllGrantRequest {

    private String requestId;
    private BigDecimal requestedAmount;
    private BigDecimal releaseAmount;
    private LocalDateTime dateRequested;
    private LocalDateTime releasedDate;
    private String requestStatus;
    private ProposalType proposalType;
    private ImplementationTypes implementationTypes;
    private List<Integer> Tranche;
    private LocalDate policyStartDate;
    private LocalDate policyEndDate;
    private String financialYear;
    private LocalDateTime sanctionDate;
    private States states;
    @JsonIgnore
    private Integer statusId;
    private byte[] sanction_letter;
    private BigDecimal amountSc;
    private BigDecimal amountSt;
    private BigDecimal amountGc;
    private Integer schemeId;
    private String schemeName;
}
