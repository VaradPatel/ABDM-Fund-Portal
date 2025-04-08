package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IOtp;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import nha_grant_access.example.nha_grant.dto.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OtpController {
    @Autowired
    IOtp iOtp;
    @PostMapping(path ="/send-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOtp(@Valid @RequestBody OtpGenerateRequest otpGenerateRequestTo)
             {
try {
    OtpResponseTo otpResponseTo=iOtp.generateOtp(otpGenerateRequestTo);

    return ResponseEntity.ok().body(otpResponseTo);
}
catch (Exception e)
{
    log.error(e.toString());
    return ResponseEntity.internalServerError().body(new Error("Send Otp Failed",e.toString()));
}
    }
}
