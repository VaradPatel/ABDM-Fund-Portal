package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.Exception.GrantUserAlreadyExistsException;
import nha_grant_access.example.nha_grant.dto.Signup;
import nha_grant_access.example.nha_grant.dto.UserApprovalRequest;

public interface IUserService {
    public void signup(Signup signup) throws RuntimeException, GrantUserAlreadyExistsException;

    public Integer approveUser(UserApprovalRequest request);

}
