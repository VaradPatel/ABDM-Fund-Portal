package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import nha_grant_access.example.nha_grant.dto.VerifyOtpRequest;

import java.security.GeneralSecurityException;

public interface IOtp {

   OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo);

  Boolean validateOtp(VerifyOtpRequest otpValidateRequestTo);

}
