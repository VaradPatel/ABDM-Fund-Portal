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
import nha_grant_access.example.nha_grant.repository.IstatesRepository;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    IstatesRepository istatesRepository;
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

        System.out.println("message is " + message);
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
        System.out.println("response is " + response.getBody());
        return response.getBody();
    }
public String ApplicationSendTOCEO(String requestId, Integer proposalType, Integer State, Integer ImplemetationType, Integer userId)
{
    ProposalType proposal= grantRequestService.getProposalType(proposalType);
    ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
Optional<States> states=istatesRepository.findById(State);
User user=userRepo.findUserByStateAndRole(2,State);
   // System.out.println("user is " + user.toString() );
    String message = String.format(
            "Dear User ,  \n" +
                    "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been received from the SHA %s for your review. \n" +
                    "National Health Authority",
            requestId,
            implementationTypes.getName(),
            proposal.getName(),
            states.get().getName()

    );


    System.out.println("sms message " + message);
     MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("userid", "abhaotp");
    body.add("password", "f9F3r]{S");
    body.add("mobile", user.getMobileNumber());
    body.add("senderid", "NHASMS");
    body.add("dltEntityId", "1001548700000010184");
    body.add("msg", message);
    body.add("sendMethod", "quick");
    body.add("msgType", "text");
    body.add("dltTemplateId", "1007855346570995386");
    body.add("output", "json");
    body.add("duplicatecheck", "true");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    System.out.println("Response is "+ response.getBody());
    SendEmail(message,user.getEmail());

    return "";
}
    public String ApplicationSendMsgToSha(String requestId, Integer proposalType, Integer State, Integer ImplemetationType , Integer userId)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        Optional<User> user=userRepo.findById(userId);
        // System.out.println("user is " + user.toString() );
        String message = String.format(
                "Dear User , \n" +
                        "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been submitted to CEO for verification. \n" +
                        "National Health Authority",

                requestId,
                implementationTypes.getName(),
                proposal.getName()
        );


        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", user.get().getMobileNumber());
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007139939459847170");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
SendEmail(message,user.get().getEmail());
        return "";
    }
    public String ApplicationApprovedMsgToCEO(String requestId, Integer proposalType, Integer State, Integer ImplemetationType)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        User user=userRepo.findUserByStateAndRole(2,State);
        // System.out.println("user is " + user.toString() );
        String message = String.format(
                "Dear User , \n"+
                        "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been verified by you and submitted for verification to NHA. \n" +
                        "National Health Authority",

                requestId,
                implementationTypes.getName(),
                proposal.getName()
        );


        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", user.getMobileNumber());
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007888067112951344");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
SendEmail(message, user.getEmail());
        return "";
    }
    public String ApplicationApprovedMsgToStateCordAndSha(String requestId, Integer proposalType, Integer State, Integer ImplemetationType)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        User user=userRepo.findUserByStateAndRole(3,State);
        // System.out.println("user is " + user.toString() );
        String message = String.format(
                "Dear User , \n" +
                        "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been verified by CEO and is submitted for verification to NHA. \n" +
                        "National Health Authority",
                       // e.g. "Dr. Sharma"
               requestId,   // e.g. "NHA100937363"
                implementationTypes.getName(),   // e.g. "hybrid"
                proposal.getName()  // e.g. "implementation"
        );


        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", user.getMobileNumber());
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007630246614976979");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
SendEmail(message,user.getEmail());
        return "";
    }
    public String LoginCredentials(String Username, String password, String mobile )
    {

        // System.out.println("user is " + user.toString() );
        String message = String.format(
                "Dear User , \n" +
                        "Your credentials for NHA Grants Portal are as below: \n" +
                        "Username: %s \n" +
                        "Temporary password: %s \n" +
                        "Kindly login to the application and reset your password. \n" +
                        "National Health Authority",
                 Username, password
        );

        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", mobile);
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007694322530113221");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());


        return "";
    }
    public String ApplicationFinalApprovalToShaAndCeo(String requestId, Integer proposalType, Integer State, Integer ImplemetationType, Integer userId)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        User user=userRepo.findUserByStateAndRole(3,State);
        Optional<User> user1=userRepo.findById(userId);
        // System.out.println("user is " + user.toString() );
        String message = String.format(
                "Dear User , \n" +
                        "GIA Request No. %s for the %s scheme (Proposal Type: %s) has been approved by NHA. \n" +
                        "National Health Authority",
                requestId, implementationTypes.getName(), proposal.getName()
        );


        String mobileNumbers = user.getMobileNumber() + "," + user1.get().getMobileNumber();

        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", mobileNumbers);
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007067309724223338");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
SendEmail(message,user.getEmail());
SendEmail(message,user1.get().getEmail());
        return "";
    }
    public String ApplicationQueryRaiseMsgToSha(String requestId, Integer proposalType, Integer State, Integer ImplemetationType, Integer userId)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        //User user=userRepo.findUserByStateAndRole(3,State);
        Optional<User> user1=userRepo.findById(userId);
        // System.out.println("user is " + user.toString() );

        String message = String.format(
                "Dear User , \n" +
                        "A query has been raised on GIA Request No. %s for the %s scheme (Proposal Type: %s) by CEO. \n" +
                        "National Health Authority",
                // e.g. "Dr. Sharma"
                requestId   ,  // e.g. "NHA100937363"
                implementationTypes.getName(),    // e.g. "hybrid"
                proposal.getName()  // e.g. "implementation"
        );



        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", user1.get().getMobileNumber());
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007584520273066099");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
      SendEmail(message,user1.get().getEmail());
        return "";
    }
    public String ApplicationQueryRaiseMsgToCeo(String requestId, Integer proposalType, Integer State, Integer ImplemetationType, Integer userId)
    {
        ProposalType proposal= grantRequestService.getProposalType(proposalType);
        ImplementationTypes implementationTypes=grantRequestService.getImplementationType(ImplemetationType);
        Optional<States> states=istatesRepository.findById(State);
        User user=userRepo.findUserByStateAndRole(2,State);

        // System.out.println("user is " + user.toString() );

        String message = String.format(
                "Dear User , \n" +
                        "A query has been raised regarding GIA Request No. %s under the %s scheme (Proposal Type: %s) by the National Health Authority (NHA). \n" +
                        "You are requested to review and respond to the query at the earliest. \n" +
                        "Best regards, \n" +
                        "National Health Authority",
                 requestId, implementationTypes.getName() , proposal.getName()
        );



        System.out.println("sms message " + message);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("userid", "abhaotp");
        body.add("password", "f9F3r]{S");
        body.add("mobile", user.getMobileNumber());
        body.add("senderid", "NHASMS");
        body.add("dltEntityId", "1001548700000010184");
        body.add("msg", message);
        body.add("sendMethod", "quick");
        body.add("msgType", "text");
        body.add("dltTemplateId", "1007116399277243301");
        body.add("output", "json");
        body.add("duplicatecheck", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response is "+ response.getBody());
        SendEmail(message,user.getEmail());
        return "";
    }
    public String SendEmail(String message, String email)
    {
        RestTemplate restTemplate = new RestTemplate();

        // URL
        String url = "http://global2sbx.abdm.gov.internal/internal/v3/notification/message";

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("REQUEST-ID", UUID.randomUUID().toString());
        headers.set("TIMESTAMP", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        // Receiver
        Map<String, String> receiverEntry = new HashMap<>();
        receiverEntry.put("key", "emailId");
        receiverEntry.put("value", email);
        List<Map<String, String>> receiverList = List.of(receiverEntry);

        // Notification entries
        List<Map<String, String>> notificationList = new ArrayList<>();

        notificationList.add(Map.of("key", "templateId", "value", "123456789"));
        notificationList.add(Map.of("key", "subject", "value", "NHA Grant Request Portal"));
        notificationList.add(Map.of(
                "key", "content",
                "value", message
        ));

        // Full request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("origin", "abha");
        requestBody.put("type", List.of("email"));
        requestBody.put("contentType", "otp");
        requestBody.put("sender", "NHASMS");
        requestBody.put("receiver", receiverList);
        requestBody.put("notification", notificationList);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("email response " + response.toString());
   return "";
    }

}



