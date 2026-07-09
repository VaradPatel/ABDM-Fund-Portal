package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantUserAlreadyExistsException;
import abdm_nha_grant_access.example.nha_grant.Interface.IUserService;
import abdm_nha_grant_access.example.nha_grant.dto.ChangePassword;
import abdm_nha_grant_access.example.nha_grant.dto.Signup;
import abdm_nha_grant_access.example.nha_grant.dto.UserApprovalRequest;
import abdm_nha_grant_access.example.nha_grant.entity.States;
import abdm_nha_grant_access.example.nha_grant.entity.User;
import abdm_nha_grant_access.example.nha_grant.entity.UserStateRole;
import abdm_nha_grant_access.example.nha_grant.redis.hash.BlacklistToken;
import abdm_nha_grant_access.example.nha_grant.redis.hash.Otp;
import abdm_nha_grant_access.example.nha_grant.redis.repository.IBlacklistTokenRepository;
import abdm_nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import abdm_nha_grant_access.example.nha_grant.repository.IUserStateRoleRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IstatesRepository;
import abdm_nha_grant_access.example.nha_grant.repository.UserRepo;
import abdm_nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserService {

@Autowired
    UserRepo userRepo;
@Autowired
    IstatesRepository istatesRepository;
@Autowired
    IUserStateRoleRepo iUserStateRoleRepo;
@Autowired
    IBlacklistTokenRepository blacklistTokenRepository;

@Autowired
PasswordEncoder bCryptPasswordEncoder;
@Autowired
    IOtpRepository iOtpRepository;
@Autowired
    RSAUtil rsaUtil;
    @Override
    @Transactional
    public void signup(Signup signup) throws RuntimeException, GrantUserAlreadyExistsException {
        if (userRepo.findByEmail(signup.getEmail()).isPresent() || userRepo.findByMobileNumber(signup.getMobile()).isPresent()) {
            throw new GrantUserAlreadyExistsException("Email or mobile already registered!");
        }
        if(signup.getTransactionId()==null)
        {
            throw new GrantUserAlreadyExistsException("Transcation Id is missing");

        }
        Optional<Otp> otp=iOtpRepository.findById(signup.getTransactionId());
        if(otp.isEmpty() || !(otp.get().getContact().equals(signup.getMobile())) || !otp.get().isVerified())
        {
            throw new GrantUserAlreadyExistsException("Otp is not verified ");
        }
        List<UserStateRole> userStateRoles1;
//if(signup.getRoles().getId()>1) {
//    userStateRoles1 = iUserStateRoleRepo.getUserByStateAndRole(signup.getRoles().getId(), signup.getStateId());
//    if(userStateRoles1.size()>0)
//    {
//        throw new GrantUserAlreadyExistsException("There is a User corresponding to the state and role ");
//
//    }
//}

        try
        {
            boolean isActiveAndVerified = signup.getRoles().getId() > 2;
            User user=User.builder().

                    email(signup.getEmail()).
                    name(signup.getName())
                    .roleId(signup.getRoles().getId())
                    .mobileNumber(signup.getMobile())
                    .isNew(true)
                    .designation(signup.getDesignation())
                    .gender(signup.getGender())
                    .dob(signup.getDob())
                    .roleId(signup.getRoles().getId())
                    .isActivated(isActiveAndVerified)
                    .isVerified(isActiveAndVerified)
                    .password(bCryptPasswordEncoder.encode("Nha@123"))
                                            .
                    build();
            user=userRepo.save(user);
            Set<UserStateRole> userStateRoles = new HashSet<>();

            for (Integer stateId : signup.getStateId()) {
                States state = istatesRepository.findById(stateId)
                        .orElseThrow(() -> new RuntimeException("State not found: " + stateId));

                UserStateRole userStateRole = UserStateRole.builder()

                        .user(user)
                        .state(state)
                        .role(signup.getRoles())
                        .designation(signup.getDesignation())
                        .build();

                userStateRoles.add(userStateRole);
            }

           iUserStateRoleRepo.saveAll(userStateRoles);

        }
        catch (Exception e)
        {
            throw new RuntimeException(e.toString());
        }

    }
    @Transactional
    public Integer approveUser(UserApprovalRequest request) {
        if(request.getIsApproved()) {
            String hashedPassword = bCryptPasswordEncoder.encode("Nha@123");
            int updated = userRepo.updateUserVerificationStatus(request.getUserId(), request.getIsApproved(),hashedPassword);
            return updated;
        }
        else {
            if (userRepo.existsById(request.getUserId())) {
                userRepo.deleteById(request.getUserId());
                return 1;
            } else {
                return 0;
            }
        }
    }
    @Override
    public Integer changePassword(ChangePassword changePassword) throws Exception {
        String decryptedPassword = rsaUtil.decrypt(changePassword.getPassword());
        String hashedPassword=bCryptPasswordEncoder.encode(decryptedPassword);
      // System.out.println("password is "+ decryptedPassword + " " + hashedPassword);
        return userRepo.updateUserVerificationStatusByMobile(changePassword.getMobile(),true,hashedPassword );
    }

    @Override
    public String logOut(String token) {
        String token1 = token.substring(7);
        BlacklistToken blacklistToken =new BlacklistToken();
        blacklistToken.setToken(token1);
        blacklistToken.setExpired(true);
        blacklistToken.setTimeToLive(30);
        blacklistTokenRepository.save(blacklistToken);
        return "logout Successfully";


    }

}
