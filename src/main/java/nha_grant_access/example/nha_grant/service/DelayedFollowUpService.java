package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DelayedFollowUpService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;

    @Async
    public void fetchSignedPdfOnce(String txnId, String requestId) {
        try {
            // ⏳ Wait 3 minutes
            Thread.sleep(180_000);

            String pdfUrl = "https://digisignbeta.abdm.gov.in/digiSign/pdf/" + txnId;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_PDF, MediaType.TEXT_HTML));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(pdfUrl, HttpMethod.GET, entity, byte[].class);

            MediaType contentType = response.getHeaders().getContentType();

            if (MediaType.APPLICATION_PDF.equals(contentType)) {
                byte[] pdfBytes = response.getBody();

                iGrantRequestsRepo.updateEsignStatusByRequestID(requestId,pdfBytes,txnId,true);

              //  System.out.println("✅ PDF received (size = " + pdfBytes.length + " bytes)");
                // You can store the PDF here if needed
            } else if (MediaType.TEXT_HTML.equals(contentType)) {
                String html = new String(response.getBody(), StandardCharsets.UTF_8);
                System.err.println("❌ User hasn't signed yet. HTML response: " + html);
            } else {
                System.err.println("❗ Unexpected response content type: " + contentType);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("⛔ Delayed task interrupted.");
        } catch (Exception e) {
            System.err.println("❗ Error while fetching signed PDF: " + e.getMessage());
        }
    }
}

