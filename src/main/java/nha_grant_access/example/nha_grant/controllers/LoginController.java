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
import nha_grant_access.example.nha_grant.redis.hash.Otp;
import nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import nha_grant_access.example.nha_grant.repository.IRolesRepository;
import nha_grant_access.example.nha_grant.repository.IUserStateRoleRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    IOtpRepository iOtpRepository;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new Error("Invalid email or password! ", "Invalid email or password  !"));
        }


        User user = userOpt.get();
        if(!user.getIsActivated())
        {
            return ResponseEntity.status(401).body(new Error("User is Not activated ", "User is Not activated  !"));

        }

        try {
            String decryptedPassword = rsaUtil.decrypt(request.getPassword());

            if (!PasswordEncoder.matches(decryptedPassword, user.getPassword())) {

                return ResponseEntity.status(401).body(new Error("Invalid email or password! ", "Invalid email or password!" + decryptedPassword));

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
       String data= rsaUtil.encrypt(test.getEncrypt());

        return ResponseEntity.ok().body(new SuccessResponse(data));
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
            else {
                Optional<Otp> otpDetails = iOtpRepository.findById(changePassword.getTransactionId());
                if(  (otpDetails.isEmpty() || otpDetails.get().isExpired()) || !otpDetails.get().isVerified() || !(otpDetails.get().getContact().equals(changePassword.getMobile()))) {
                    return ResponseEntity.badRequest().body(new Error("Invalid Request", "Invalid Request"));
                }
                if (iUserService.changePassword(changePassword) > 0) {
                    return ResponseEntity.ok().body(new SuccessResponse("Password changed Successfully"));
                }
                log.info("change password is zero");
            }

            return ResponseEntity.internalServerError().body(new Error("change password failed ","change password failed"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while changing password", "Error occured while chnaging password"));
        }

    }
    @PostMapping("/user-activate/{userId}/{status}")
    @PreAuthorize("hasAuthority('NHA Admin')")

    public ResponseEntity<?> UserActivation(@PathVariable("userId") Integer userId , @PathVariable("status") Integer status)
    {
 try
 {
     if(status!=0)
     {
         userRepo.updateUserActivationStatus(userId,true);
         return ResponseEntity.ok().body(new SuccessResponse("Successfully activated user"));
     }
     else {
         userRepo.updateUserActivationStatus(userId,false);
         return ResponseEntity.ok().body(new SuccessResponse("Successfully deactivated user"));
     }
 }
       catch (Exception e) {
        log.error(e.toString());
        return ResponseEntity.internalServerError().body(new Error("Error occured while activate/deactivate user", "Error occured while  activate/deactivate user"));
    }

    }

    @PostMapping(path = "/user/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
       try {
           String msg = iUserService.logOut(token);
           return ResponseEntity.ok().body(new SuccessResponse(msg));
       }
       catch (Exception e)
       {
           log.error(e.toString());
           return ResponseEntity.internalServerError().body(new Error("Error occured while logging out user", "Error occured while logging out user"));

       }
    }


}
