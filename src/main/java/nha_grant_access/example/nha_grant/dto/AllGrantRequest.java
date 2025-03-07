package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}
