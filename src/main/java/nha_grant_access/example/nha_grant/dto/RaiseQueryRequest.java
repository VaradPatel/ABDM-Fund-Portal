package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RaiseQueryRequest {
    @NotNull(message = "requestID cant be null")
    private String requestId;
    @NotNull(message = "roleId cant be null")
    private Integer roleId;
    @NotNull(message="userId cant be null")
    private Integer userId;
    @NotNull(message ="Query cant be null")
    private String query;
    @NotNull(message="Proposal Type Id is compulsory")
    private Integer proposalTypeId;
    @NotNull(message="stateId cannot be null")
    private Integer stateId;


}
