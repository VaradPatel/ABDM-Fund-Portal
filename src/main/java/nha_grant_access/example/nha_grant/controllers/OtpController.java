package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IOtp;
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
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class OtpController {
    @Autowired
    IOtp iOtp;
    @Autowired
    UserRepo userRepo;
    @Autowired
    IRolesRepository iRolesRepository;
    @Autowired
    IUserStateRoleRepo iUserStateRoleRepo;
    @Autowired
    JwtUtil jwtUtil;


    @PostMapping(path ="/send-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOtp(@Valid @RequestBody OtpGenerateRequest otpGenerateRequestTo)
             {
try {
//    if(userRepo.findActiveByMobile(otpGenerateRequestTo.getMobile()).isEmpty())
//    {
//        return  ResponseEntity.badRequest().body(new Error ("No User found for mobile number","No User found for mobile number"));
//    }

    OtpResponseTo otpResponseTo=iOtp.generateOtp(otpGenerateRequestTo);

    return ResponseEntity.ok().body(otpResponseTo);
}
catch (Exception e)
{
    log.error(e.toString());
    return ResponseEntity.internalServerError().body(new Error("Send Otp Failed",e.toString()));
}
    }
    @PostMapping(path="/verify-otp", produces=MediaType.APPLICATION_JSON_VALUE)

        public ResponseEntity<?>verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest)
    {
try
{
    if(!iOtp.validateOtp(verifyOtpRequest))
    {
        return ResponseEntity.badRequest().body(new Error("Kindly Enter the Correct Otp","Kindly Enter the Correct Otp"));
    }
    if(verifyOtpRequest.getIslogin())
    {
       Optional<User> userOpt= userRepo.findByMobileNumber(verifyOtpRequest.getContact());
       if(userOpt.isEmpty())
       {
           return ResponseEntity.badRequest().body(new Error("No User found ","No User found"));

       }
        User user = userOpt.get();
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

    }
  return ResponseEntity.ok().body(new SuccessResponse("Otp validated Successfully"));

}
catch (Exception e)
{
    return ResponseEntity.internalServerError().body(new Error("Error occured while verifying otp",e.toString()));
}
    }
    @GetMapping("/download/pmjay-implementation")
    public ResponseEntity<Resource> getPmjayImplementationPdf() throws IOException
    {
        Resource resource = new ClassPathResource("pdf/PMJAY-Implementation.pdf");


        // Check if the resource exists
        // Set the appropriate content type for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "PMJAY-Implementation.pdf");

        // Return the PDF file as ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
    @GetMapping("/download/pmjay-administrative")
    public ResponseEntity<Resource> getPmjayAdministrativePdf() throws IOException
    {
        Resource resource = new ClassPathResource("pdf/PMJAY-Administrative.pdf");


        // Check if the resource exists
        // Set the appropriate content type for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "PMJAY-Administrative.pdf");

        // Return the PDF file as ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/download/frs")
    public ResponseEntity<Resource> getPmjayFRS() throws IOException
    {
        Resource resource = new ClassPathResource("pdf/FRS.docx");


        // Check if the resource exists
        // Set the appropriate content type for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ));
        headers.setContentDispositionFormData("attachment", "frs.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }



}
