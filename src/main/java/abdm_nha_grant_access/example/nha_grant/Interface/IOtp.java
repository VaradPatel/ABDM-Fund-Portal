package abdm_nha_grant_access.example.nha_grant.Interface;

import abdm_nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import abdm_nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import abdm_nha_grant_access.example.nha_grant.dto.VerifyOtpRequest;

public interface IOtp {

   OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo);

  Boolean validateOtp(VerifyOtpRequest otpValidateRequestTo);

}
