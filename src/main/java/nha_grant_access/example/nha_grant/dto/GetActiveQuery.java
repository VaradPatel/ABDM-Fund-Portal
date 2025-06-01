package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetActiveQuery {
    private Integer queryId;
    private String requestId;
    private String QueryComment;
    private String queryDoc;
    private Date createdAt;
    private String userName;
    private String stateName;
    private Integer proposalTypeId;
    private Integer stateId;
}
