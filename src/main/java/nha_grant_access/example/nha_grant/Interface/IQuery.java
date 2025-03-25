package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;

import java.util.List;

public interface IQuery {
    List<GetActiveQuery> getActiveQueryByUserId(Integer userID);
    public void respondToQueryBysha(GrantRequestInputDto grantRequestInputDto);
}
