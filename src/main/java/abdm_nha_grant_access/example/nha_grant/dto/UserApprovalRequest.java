package abdm_nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApprovalRequest {
    @NotNull(message = "userId is mandatory")
        private Integer userId;

        private Boolean isApproved;
        private String remarks;
    }

