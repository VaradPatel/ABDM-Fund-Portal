package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/grant-requests")
@RequiredArgsConstructor
public class GrantRequestController {
    @Autowired
    IGrantRequests iGrantRequests;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepo userRepo;
    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('SHA Finance Division Individual')")
    public ResponseEntity<?> createGrantRequest(@RequestBody @Valid GrantRequestInputDto grantRequestInputDTO) {
        try {
//
//            if (token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            }
//
//            // Extract email from token
//            String email = jwtUtil.extractUsername(token);
//            String role= jwtUtil.extractRoleId(token);

//            User user = userRepo.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
            System.out.println("positive balance is" +grantRequestInputDTO.getPositiveBalance());

            GrantRequestInputDto savedGrantRequest = iGrantRequests.saveGrantRequest(grantRequestInputDTO,false);
            return ResponseEntity.ok(new GrantResponse(savedGrantRequest.getRequestId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error("Failed to create Grant Request ",e.toString()));
        }
    }

    @GetMapping("/get-all/{stateId}")

   //@PreAuthorize("hasAuthority('SHA Finance Division Individual')")
    public ResponseEntity<?> getAllGrantRequest(@PathVariable("stateId") Integer stateId , @RequestParam(value = "format", defaultValue = "json") String format) {
        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                System.out.println("Authenticated User: " + authentication.getName());
//                System.out.println("Roles: " + authentication.getAuthorities());
//            }
//            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequest(stateId);
//            return ResponseEntity.ok(allGrantRequests);
            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequest(stateId);

            if(format.equals("csv"))
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
                    // Write CSV header
                    writer.write("Request ID,Requested Amount,Release Amount,Date Requested,Released Date,Request Status,Proposal Type,Implementation Type,Tranche,Policy Start Date,Policy End Date,Financial Year,Sanction Date");
                    writer.newLine();

                    // Write data rows
                    for (AllGrantRequest row : allGrantRequests) {
                        writer.write(String.join(",",
                                sanitizeValue(row.getRequestId()),
                                sanitizeValue(row.getRequestedAmount()),
                                sanitizeValue(row.getReleaseAmount()),
                                sanitizeValue(row.getDateRequested()),
                                sanitizeValue(row.getReleasedDate()),
                                sanitizeValue(row.getRequestStatus()),
                                sanitizeValue(row.getProposalType() != null ? row.getProposalType().getName() : null),
                                sanitizeValue(row.getImplementationTypes() != null ? row.getImplementationTypes().getName() : null),
                                sanitizeValue(row.getTranche() != null ? row.getTranche().toString() : null),
                                sanitizeValue(row.getPolicyStartDate()),
                                sanitizeValue(row.getPolicyEndDate()),
                                sanitizeValue(row.getFinancialYear()),
                                sanitizeValue(row.getSanctionDate())
                        ));
                        writer.newLine();
                    }

                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException("Error generating CSV", e);
                }

                // Convert to InputStream for ResponseEntity
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AllGrantRequests.csv");
                headers.setContentType(MediaType.TEXT_PLAIN);

                return new ResponseEntity<>(new InputStreamResource(bais), headers, HttpStatus.OK);
            }
//            else if (format.equals("pdf"))
//            {
//                try (PDDocument document = new PDDocument();
//                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//
//                    PDPage page = new PDPage(PDRectangle.A4);
//                    document.addPage(page);
//
//                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
//                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
//                        contentStream.beginText();
//                        contentStream.setLeading(16);
//                        contentStream.newLineAtOffset(50, 750);
//                        contentStream.showText("All Grant Requests Report");
//                        contentStream.newLine();
//                        contentStream.endText();
//
//                        contentStream.setFont(PDType1Font.HELVETICA, 10);
//                        float y = 730; // Start position
//
//                        for (AllGrantRequest row : data) {
//                            if (y < 50) { // Create a new page if content exceeds limit
//                                contentStream.close();
//                                PDPage newPage = new PDPage(PDRectangle.A4);
//                                document.addPage(newPage);
//                                contentStream = new PDPageContentStream(document, newPage);
//                                y = 750;
//                            }
//
//                            contentStream.beginText();
//                            contentStream.newLineAtOffset(50, y);
//                            contentStream.showText("Request ID: " + sanitizeValue(row.getRequestId()));
//                            contentStream.newLine();
//                            contentStream.showText("Requested Amount: " + sanitizeValue(row.getRequestedAmount()));
//                            contentStream.newLine();
//                            contentStream.showText("Release Amount: " + sanitizeValue(row.getReleaseAmount()));
//                            contentStream.newLine();
//                            contentStream.showText("Date Requested: " + sanitizeValue(row.getDateRequested()));
//                            contentStream.newLine();
//                            contentStream.showText("Released Date: " + sanitizeValue(row.getReleasedDate()));
//                            contentStream.newLine();
//                            contentStream.showText("Request Status: " + sanitizeValue(row.getRequestStatus()));
//                            contentStream.newLine();
//                            contentStream.showText("Proposal Type: " + sanitizeValue(row.getProposalType()));
//                            contentStream.newLine();
//                            contentStream.showText("Implementation Types: " + sanitizeValue(row.getImplementationTypes()));
//                            contentStream.newLine();
//                            contentStream.showText("Tranche: " + sanitizeValue(row.getTranche()));
//                            contentStream.newLine();
//                            contentStream.showText("Policy Start Date: " + sanitizeValue(row.getPolicyStartDate()));
//                            contentStream.newLine();
//                            contentStream.showText("Policy End Date: " + sanitizeValue(row.getPolicyEndDate()));
//                            contentStream.newLine();
//                            contentStream.showText("Financial Year: " + sanitizeValue(row.getFinancialYear()));
//                            contentStream.newLine();
//                            contentStream.showText("Sanction Date: " + sanitizeValue(row.getSanctionDate()));
//                            contentStream.newLine();
//
//                            y -= 220; // Adjust spacing
//                            contentStream.endText();
//                        }
//                        contentStream.close();
//                    }
//
//                    document.save(baos);
//                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AllGrantRequests.pdf");
//                    headers.setContentType(MediaType.APPLICATION_PDF);
//
//                    return new ResponseEntity<>(new InputStreamResource(bais), headers, HttpStatus.OK);
//                } catch (IOException e) {
//                    throw new RuntimeException("Error generating PDF", e);
//                }
//            }
            else {
                return ResponseEntity.ok().body(allGrantRequests);
            }
        }
        catch(Exception e){
                return ResponseEntity.internalServerError().body(new Error("failed to fetch details ", e.toString()));
            }

    }
    @GetMapping("/get/{requestId}")
    public ResponseEntity<?> getGrantRequestByRequestId(@PathVariable("requestId") String requestId) {
        try {
            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            return ResponseEntity.ok(grantRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch grant request details", e.toString()));
        }

    }




    // Helper method to generate PDF
    private String sanitizeValue(Object value) {
        if (value == null) return "";
        if (value instanceof LocalDateTime) return value.toString().replace("T", " ");
        if (value instanceof LocalDate) return value.toString();
        if (value instanceof BigDecimal) return value.toString();
        return value.toString().replace(",", " "); // Prevent CSV corruption
    }
}
