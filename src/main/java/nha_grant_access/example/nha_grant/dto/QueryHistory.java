package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.entity.ImplementationTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryHistory {
    private String requestId;
    private String state;
    private String query;
    private LocalDateTime createdAt;
    private LocalDate policyStartDate;
    private LocalDate policyEndDate;
    private String implementationTypes;

}
