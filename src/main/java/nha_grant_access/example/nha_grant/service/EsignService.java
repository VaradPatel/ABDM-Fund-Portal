package nha_grant_access.example.nha_grant.service;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import nha_grant_access.example.nha_grant.dto.Esign.Document;
import nha_grant_access.example.nha_grant.dto.Esign.DocumentRequest;
import nha_grant_access.example.nha_grant.dto.Esign.EspResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;


@Service
@Slf4j
public class EsignService {
    @Value("${esignapi.url}")
    private String esignapiurl;
    @Value("${esign.name}")
    private String esignname;

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
        String apiUrl = esignapiurl;


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Cookie", "JSESSIONID=F7BF4015292D282458F64727102196E3");


        HttpEntity<DocumentRequest> request = new HttpEntity<>(documentRequest, headers);
        System.out.println("request is " + request.toString());

        ResponseEntity<EspResponse> response = null;
        try {
            response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, EspResponse.class);
            log.info("EspResponse: " + response.toString());
        } catch (org.springframework.web.client.HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
            log.error("API Error - Status Code: {}, Response Body: {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new Exception("API Error from DigiSign: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling DigiSign API: {}", e.getMessage(), e);
            throw e;
        }

        String xml=response.getBody().getEspRequest();
        Pattern pattern = Pattern.compile("(<Esign.*?</Esign>)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (matcher.find()) {
            String esignBlock = matcher.group(1);

            // Clean the <Esign> block
            String cleaned = esignBlock
                    .replace("\r", "")
                    .replace("\n", "")

                    .trim();
            xml = xml.replace(esignBlock, cleaned);
        }

        EspResponse espResponse = response.getBody();
        espResponse.setEspRequest(xml);



        return  espResponse;
    }
}
