package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IOtp;
import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import nha_grant_access.example.nha_grant.redis.hash.Otp;
import nha_grant_access.example.nha_grant.redis.repository.IBlacklistTokenRepository;
import nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class OtpService implements IOtp {
    @Autowired
    IOtpRepository iOtpRepository;

    @Override
    public OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo) {
        String otp = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
String contact=otpGenerateRequestTo.getMobile();
        Otp otpEntity = new Otp(UUID.randomUUID().toString(), otp, 0, contact, false, 10,false);
        iOtpRepository.save(otpEntity);
        return new OtpResponseTo(otpEntity.getId(),"OTP sent sucessfully",otpEntity.getContact());

    }
}



