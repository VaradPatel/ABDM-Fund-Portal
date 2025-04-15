package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class WorkFlowRemarkResponse {
    private String remarks;
    private String roleName;
    private LocalDateTime createdAt;
}
