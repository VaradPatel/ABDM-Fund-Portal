package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.dto.GetActiveQuery;

import java.util.List;

public interface IQuery {
    List<GetActiveQuery> getActiveQueryByUserId(Integer userID);
}
