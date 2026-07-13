package abdm_nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalResponse {
    private String requestId;
    private Integer stateId;
    private String financialYear;
    private String quarter;
    private Integer categoryId;
    private Integer statusId;
    private String statusLabel;
    private String remarks;
    private Map<String, List<ProposalFileInfo>> files;
}
