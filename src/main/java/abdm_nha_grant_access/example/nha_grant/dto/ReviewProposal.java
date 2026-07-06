package abdm_nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewProposal {
    private String requestId;
    private String remarks;
    private BigDecimal requestedAmount;
    private String userName;
    private Date createdAt;
    private String stateName;

}
