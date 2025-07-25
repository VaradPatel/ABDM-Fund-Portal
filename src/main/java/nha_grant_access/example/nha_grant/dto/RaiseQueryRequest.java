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

    @NotNull(message = "Query can't be null")
    @Pattern(
            regexp = "^(?!.*<[^>]+>)[a-zA-Z0-9()\\-_,.?\\s]*$",
            message = "Input must not contain HTML tags and may only include letters, numbers, spaces, parentheses, hyphen, underscore, period, comma, and question mark"
    )
    private String query;

    @NotNull(message="Proposal Type Id is compulsory")
    private Integer proposalTypeId;
    @NotNull(message="stateId cannot be null")
    private Integer stateId;
    private Integer isStateCeo;


}
