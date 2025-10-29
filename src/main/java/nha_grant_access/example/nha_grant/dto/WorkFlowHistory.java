package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.entity.GrantRequests;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkFlowHistory {
    private GrantRequests grantRequest;
    private List<WorkFlowRemarkResponse> workflowRemarks;
    private Boolean IsCalcFileUploaded;
}
