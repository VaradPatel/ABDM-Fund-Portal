package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Interface.IOtp;
import abdm_nha_grant_access.example.nha_grant.dto.OtpGenerateRequest;
import abdm_nha_grant_access.example.nha_grant.dto.OtpResponseTo;
import abdm_nha_grant_access.example.nha_grant.dto.VerifyOtpRequest;
import abdm_nha_grant_access.example.nha_grant.entity.States;
import abdm_nha_grant_access.example.nha_grant.entity.User;
import abdm_nha_grant_access.example.nha_grant.redis.hash.Otp;
import abdm_nha_grant_access.example.nha_grant.redis.repository.IOtpRepository;
import abdm_nha_grant_access.example.nha_grant.repository.IstatesRepository;
import abdm_nha_grant_access.example.nha_grant.repository.UserRepo;
import abdm_nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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

    @Value("${sms.url}")
    private String url;
    @Value(("${email.url}"))
    private String emailUrl;

    @Override
    public OtpResponseTo generateOtp(OtpGenerateRequest otpGenerateRequestTo) {

        List<Otp> previousOtps = iOtpRepository.findAllByContact(otpGenerateRequestTo.getMobile());
        System.out.println("size " + previousOtps.size());
        if (previousOtps.size() >=5) {
            throw new RuntimeException("Too many Request for send otp");
        }

        String otp = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
        String contact = otpGenerateRequestTo.getMobile();
        Otp otpEntity = new Otp(UUID.randomUUID().toString(), otp, 0, contact, false, 1, false);
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
            otpDetails.get().setTimeToLive(1);
            iOtpRepository.save(otpDetails.get());
            return false;
        }
        otpDetails.get().setVerified(true);
        otpDetails.get().setTimeToLive(1);

        iOtpRepository.save(otpDetails.get());
        return true;


    }

    public String sendOtp(String mobileNumber, String otp)  {
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

    public RestTemplate getRestTemplateWithoutSSL() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        return new RestTemplate();
    }


        public String SendEmail(String message, String email) throws Exception {
        RestTemplate restTemplate = getRestTemplateWithoutSSL();


        // URL
        String url = emailUrl;

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



