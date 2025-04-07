package nha_grant_access.example.nha_grant.Interface;

import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;

import java.security.GeneralSecurityException;

public interface IOtp {

   OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo);

  //  OtpValidateResponseTo validateOtp(OtpValidateRequestTo otpValidateRequestTo, boolean callInternal) throws OtpException, GeneralSecurityException;

}
