package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IOtp;
import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import nha_grant_access.example.nha_grant.redis.hash.Otp;
import nha_grant_access.example.nha_grant.redis.repository.IBlacklistTokenRepository;
import nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class OtpService implements IOtp {
    @Autowired
    IOtpRepository iOtpRepository;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo) {
        String otp = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
String contact=otpGenerateRequestTo.getMobile();
        Otp otpEntity = new Otp(UUID.randomUUID().toString(), otp, 0, contact, false, 10,false);
        iOtpRepository.save(otpEntity);
        sendOtp(otpGenerateRequestTo.getMobile(),otpEntity.getOtp());
        return new OtpResponseTo(otpEntity.getId(),"OTP sent sucessfully",otpEntity.getContact());

    }
    public String sendOtp(String mobileNumber, String otp) {
        String lastFourDigits = mobileNumber.length() > 4
                ? mobileNumber.substring(mobileNumber.length() - 4)
                : mobileNumber;

        String message = String.format("Dear User, %s is OTP for verification of your mobile number ending with %s. National Health Authority", otp, lastFourDigits);

        String url = "https://sbx.sms24hours.com/SMSApi/send";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", mobileNumber);
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007654642424465739");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return response.getBody();
    }
}



