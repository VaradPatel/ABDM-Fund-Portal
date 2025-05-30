package nha_grant_access.example.nha_grant.service;

import io.micrometer.common.util.StringUtils;
import nha_grant_access.example.nha_grant.Interface.IOtp;
import nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import nha_grant_access.example.nha_grant.dto.VerifyOtpRequest;
import nha_grant_access.example.nha_grant.entity.ImplementationTypes;
import nha_grant_access.example.nha_grant.entity.ProposalType;
import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.redis.hash.Otp;
import nha_grant_access.example.nha_grant.redis.repository.IBlacklistTokenRepository;
import nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
public class OtpService implements IOtp {
    @Autowired
    IOtpRepository iOtpRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    UserRepo userRepo;
@Autowired
GrantRequestService grantRequestService;
    @Value("${sms.url}")
    private String url;

    @Override
    public OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo) {
        String otp = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
        String contact = otpGenerateRequestTo.getMobile();
        Otp otpEntity = new Otp(UUID.randomUUID().toString(), otp, 0, contact, false, 10, false);
        iOtpRepository.save(otpEntity);
        sendOtp(otpGenerateRequestTo.getMobile(), otpEntity.getOtp());
        return new OtpResponseTo(otpEntity.getId(), "OTP sent sucessfully", otpEntity.getContact());

    }

    @Override
    public Boolean validateOtp(VerifyOtpRequest otpValidateRequestTo) {
        String transactionId = otpValidateRequestTo.getTransactionId();

        Optional<Otp> otpDetails = iOtpRepository.findById(transactionId);

        if (otpDetails.isEmpty() || otpDetails.get().isExpired()) {
            return false;
        }

        if (otpDetails.get().getAttempts() >= 6) {
            iOtpRepository.deleteById(otpDetails.get().getId());
            return false;

        }
        String decryptedOtp = null;
        try {
            decryptedOtp = rsaUtil.decrypt(otpValidateRequestTo.getOtp());
        } catch (Exception e) {
            ;
        }

        if (!decryptedOtp.equals(otpDetails.get().getOtp())) {
            otpDetails.get().setAttempts(otpDetails.get().getAttempts() + 1);
            iOtpRepository.save(otpDetails.get());
            return false;
        }
        otpDetails.get().setVerified(true);
        iOtpRepository.save(otpDetails.get());
        return true;


    }

    public String sendOtp(String mobileNumber, String otp) {
        String lastFourDigits = mobileNumber.length() > 4
                ? mobileNumber.substring(mobileNumber.length() - 4)
                : mobileNumber;

        String message = String.format("Dear User, %s is OTP for verification of your mobile number ending with %s. National Health Authority", otp, lastFourDigits);


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
public String ApplicationSend(String requestId, Integer proposalType, Integer State, Integer ImplemetationType)
{
    ProposalType proposal= grantRequestService.getProposalType(proposalType);
    ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);

User user=userRepo.findUserByStateAndRole(2,State);
   // System.out.println("user is " + user.toString() );
    String message = String.format(
            "Dear User ,\n\n" +
                    "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been received from the SHA %s for your review.\n\n" +
                    "National Health Authority",
             requestId, implementationTypes.getName(), proposal.getName(), user.getName()
    );
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("userid", "abhaotp");
    body.add("password", "f9F3r]{S");
    body.add("mobile", user.getMobileNumber());
    body.add("senderid", "NHASMS");
    body.add("dltEntityId", "1007855346570995386");
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
    System.out.println("Response is "+ response.getBody());

    return "";
}

}



