package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RequestId {
    private String requestId;
    private Integer userId;
    private Integer proposalTypeId;
}
