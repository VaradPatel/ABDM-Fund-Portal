package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IUserService;
import nha_grant_access.example.nha_grant.dto.Signup;
import nha_grant_access.example.nha_grant.dto.State;
import nha_grant_access.example.nha_grant.dto.UserApprovalRequest;
import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.entity.UserStateRole;
import nha_grant_access.example.nha_grant.repository.IUserStateRoleRepo;
import nha_grant_access.example.nha_grant.repository.IstatesRepository;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements IUserService {

@Autowired
    UserRepo userRepo;
@Autowired
    IstatesRepository istatesRepository;
@Autowired
    IUserStateRoleRepo iUserStateRoleRepo;

    @Override
    @Transactional
    public void signup(Signup signup) throws RuntimeException {
        if (userRepo.findByEmail(signup.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }


        try
        {
            User user=User.builder().

                    email(signup.getEmail()).
                    name(signup.getName())
                    .roleId(signup.getRoles().getId())
                    .mobileNumber(signup.getMobile())
                    .isNew(true)
                    .designation(signup.getDesignation())
                    .isVerified(false)

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
            int updated = userRepo.updateUserVerificationStatus(request.getUserId(), request.getIsApproved());
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
}
