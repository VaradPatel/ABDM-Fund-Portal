package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Exception.GrantUserAlreadyExistsException;
import nha_grant_access.example.nha_grant.Interface.IUserService;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.entity.UserStateRole;
import nha_grant_access.example.nha_grant.repository.IRolesRepository;
import nha_grant_access.example.nha_grant.repository.IUserStateRoleRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class LoginController {
    //    @PostMapping("/login")
//    public ResponseEntity<?> createGrantRequest(@RequestBody @Valid GrantRequestInputDto grantRequestInputDTO) {
//
//
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired

    JwtUtil jwtUtil;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    IUserStateRoleRepo iUserStateRoleRepo;
    @Autowired
    PasswordEncoder PasswordEncoder;
    @Autowired
    IRolesRepository iRolesRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new Error("Invalid email or password! ", "Invalid email or password!"));
        }

        User user = userOpt.get();

        try {
            String decryptedPassword = rsaUtil.decrypt(request.getPassword());

            if (!PasswordEncoder.matches(decryptedPassword, user.getPassword())) {
                return ResponseEntity.status(401).body(new Error("Invalid email or password! ", "Invalid email or password!"));

            }


            List<UserStateRole> userStateRoleList = iUserStateRoleRepo.getUserStateRoleByUserid(user.getId());
            Roles roles =

                    iRolesRepository.findById(user.getRoleId())
                            .orElseThrow(() -> new RuntimeException("Role not found"));
            List<States> stateList = userStateRoleList.stream()
                    .map(UserStateRole::getState)
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(user.getEmail(), roles.getName(), user.getMobileNumber(), user.getEmail());


            LoginResponse loginResponse = LoginResponse.builder()
                    .email(user.getEmail())
                    .isNew(user.getIsNew())
                    .name(user.getName())
                    .mobile(user.getMobileNumber())
                    .userId(user.getId())
                    .role(roles)
                    .state(stateList)
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error("Error occured while login ", e.toString()));
        }


    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Signup request) throws GrantUserAlreadyExistsException {
        try {
            iUserService.signup(request);
            return ResponseEntity.ok().body(new SuccessResponse("Successfully Registered"));
        } catch (GrantUserAlreadyExistsException e) {
            log.error("User registration error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error("User already exists", e.getMessage()));
        } catch (Exception e) {
            log.error("error while registering " + e.toString());
            return ResponseEntity.internalServerError().body(new Error("Unable to process request  ", e.toString()));
        }
    }

    @PostMapping("/encrypt")
    public ResponseEntity<?> encrypt(@RequestBody Test test) throws Exception {
        return ResponseEntity.ok().body(rsaUtil.encrypt(test.getEncrypt()));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<?> decrypt(@RequestBody Test test) throws Exception {
        return ResponseEntity.ok().body(rsaUtil.encrypt(test.getEncrypt()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePassword changePassword) {
        try {
            if (!userRepo.findByMobileNumber(changePassword.getMobile()).isPresent()) {
                return ResponseEntity.badRequest().body(new Error("No User found", "No user found"));
            }
            if (changePassword.getTransactionId() == null && !changePassword.getIsNew()) {
                return ResponseEntity.badRequest().body(new Error("Transaction Id is missing", "Transaction Id is missing"));
            }
            if (changePassword.getIsNew()) {

                if (iUserService.changePassword(changePassword) > 0) {
                    return ResponseEntity.ok().body(new SuccessResponse("Password changed Successfully"));
                }
                log.info("change password is zero");
            }

            return ResponseEntity.internalServerError().body("");
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while chnaging password", "Error occured while chnaging password"));
        }

    }




}
