package abdm_nha_grant_access.example.nha_grant.Interface;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantUserAlreadyExistsException;
import abdm_nha_grant_access.example.nha_grant.dto.ChangePassword;
import abdm_nha_grant_access.example.nha_grant.dto.Signup;
import abdm_nha_grant_access.example.nha_grant.dto.UserApprovalRequest;

public interface IUserService {
    public void signup(Signup signup) throws RuntimeException, GrantUserAlreadyExistsException;

    public Integer approveUser(UserApprovalRequest request);
    public Integer changePassword(ChangePassword changePassword) throws Exception;
    public String logOut(String token);

}
