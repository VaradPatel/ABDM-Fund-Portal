package nha_grant_access.example.nha_grant.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApprovalRequest {

        private Integer userId;
        private Boolean isApproved;
        private String remarks;
    }

