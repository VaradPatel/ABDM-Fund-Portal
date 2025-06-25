package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.RequestId;

import java.util.List;

public interface IGrantRequests {
    GrantRequestInputDto saveGrantRequest(GrantRequestInputDto grantRequestInputDTO, Boolean isQueryResponse) ;
    List<AllGrantRequest> getAllGrantRequestByState(List<Integer>stateId, Integer userId );
    List<AllGrantRequest> getAllGrantRequest();
    String stateCeoApprove(RequestId requestId);

}
