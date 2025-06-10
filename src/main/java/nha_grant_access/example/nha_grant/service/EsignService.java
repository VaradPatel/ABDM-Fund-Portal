package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.dto.Esign.Document;
import nha_grant_access.example.nha_grant.dto.Esign.DocumentRequest;
import nha_grant_access.example.nha_grant.dto.Esign.EspResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;




@Service
public class EsignService {

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

    public EspResponse sendToDigiSignApi(DocumentRequest documentRequest) throws Exception {
        RestTemplate restTemplate=getRestTemplateWithoutSSL();
        String apiUrl = "https://digisignbeta.abdm.gov.in/digiSign/genEspRequest";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Cookie", "JSESSIONID=F7BF4015292D282458F64727102196E3");


        HttpEntity<DocumentRequest> request = new HttpEntity<>(documentRequest, headers);
        System.out.println("request is " + request.toString());

        ResponseEntity<EspResponse> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, EspResponse.class);
        EspResponse espResponse = response.getBody();
        return  espResponse;
    }
}
