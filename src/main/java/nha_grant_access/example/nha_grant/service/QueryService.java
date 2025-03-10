package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService implements IQuery {
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Override
    public List<GetActiveQuery> getActiveQueryByUserId(Integer userID) {

        List<Object[]> results = iGrantRequestsRepo.findActiveQueryFromUserID(userID);
        return results.stream()
                .map(obj -> new GetActiveQuery((String) obj[0]))
                .collect(Collectors.toList());

    }
}
