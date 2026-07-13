package abdm_nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHistoryEntry {
    private String action;
    private String actionLabel;
    private Integer userId;
    private String userName;
    private String remarks;
    private Integer statusAfter;
    private String statusAfterLabel;
    private LocalDateTime createdAt;
}
