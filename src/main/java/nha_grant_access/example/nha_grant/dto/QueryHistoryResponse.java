package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryHistoryResponse {
    private String requestId;
    private BigDecimal requestedAmount;
    private Date createdAt;
    private String queryComment;
    private String queryResponseComment;

}
