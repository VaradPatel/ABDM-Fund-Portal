package abdm_nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalActionRequest {

    @NotBlank(message = "requestId is mandatory")
    private String requestId;

    @NotBlank(message = "action is mandatory")
    private String action;

    private String remarks;
}
