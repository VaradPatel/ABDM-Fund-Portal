package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
//    @Pattern(
//            regexp = "^(?!.*(<script|javascript:|on\\w+\\s*=|<svg)).*$",
//            flags = Pattern.Flag.CASE_INSENSITIVE,
//            message = "Query contains forbidden HTML or script content"
//    )
    private String query;
    @NotNull(message="Proposal Type Id is compulsory")
    private Integer proposalTypeId;
    @NotNull(message="stateId cannot be null")
    private Integer stateId;
    private Integer isStateCeo;


}
