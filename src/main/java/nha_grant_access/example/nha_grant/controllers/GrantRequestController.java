package nha_grant_access.example.nha_grant.controllers;


import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IQueries;
import nha_grant_access.example.nha_grant.repository.IstatesRepository;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.service.GrantRequestService;
import nha_grant_access.example.nha_grant.service.OtpService;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.lowagie.text.Document;
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



import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/grant-requests")
@RequiredArgsConstructor
@Slf4j
public class GrantRequestController {
    @Autowired
    IGrantRequests iGrantRequests;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    IstatesRepository istatesRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private IQueries iQueries;
    @Autowired
    GrantRequestService grantRequestService;
    @Autowired
    OtpService otpService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SHA Finance Division Individual')")
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


            GrantRequestInputDto savedGrantRequest = iGrantRequests.saveGrantRequest(grantRequestInputDTO, false);
            //sendnotification
            try {
                otpService.ApplicationSendTOCEO(savedGrantRequest.getRequestId(), savedGrantRequest.getProposalTypeId(), savedGrantRequest.getStateId(), savedGrantRequest.getImplementationModeId(), savedGrantRequest.getUserId());

                otpService.ApplicationSendMsgToSha(savedGrantRequest.getRequestId(), savedGrantRequest.getProposalTypeId(), savedGrantRequest.getStateId(), savedGrantRequest.getImplementationModeId(), savedGrantRequest.getUserId());
            }
            catch (Exception e)
            {
                ;
            }
            return ResponseEntity.ok(new GrantResponse(savedGrantRequest.getRequestId()));
        }
        catch (Exception e) {
            log.error("exception " + e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(new Error("Failed to create Grant Request ", e.toString()));
        }
    }

    @GetMapping("/get-all/{stateId}")
    @PreAuthorize("hasAuthority('SHA Finance Division Individual') or hasAuthority('State CEO')")


    public ResponseEntity<?> getAllGrantRequest(@PathVariable("stateId") Integer stateId, @RequestParam(value = "userId", required = false) Integer userId, @RequestParam(value = "format", defaultValue = "json") String format) {
        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                System.out.println("Authenticated User: " + authentication.getName());
//                System.out.println("Roles: " + authentication.getAuthorities());
//            }
//            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequest(stateId);
//            return ResponseEntity.ok(allGrantRequests);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();
            System.out.println("userId" + user.getId());
            if(!(user.getId().equals(userId)))
            {
                System.out.println("invalid");
                return  ResponseEntity.badRequest().body(new Error("You are not Authorized for this Endpoint","You are not Authorized for this Endpoint"));
            }
            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequestByState(Collections.singletonList(stateId), userId);
            for (AllGrantRequest request : allGrantRequests) {
                if (request.getStatusId() >= 4 && request.getStatusId() <= 8) {
                    request.setRequestStatus("NHA Review");
                } else if (request.getStatusId() == 10) {
                    request.setRequestStatus("Query by NHA");
                }
            }

            if (format.equals("csv")) {
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
            } else if (format.equals("pdf")) {
                try {
                  com.lowagie.text.Document document = new Document(PageSize.A4.rotate()); // rotate for wide table
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    PdfWriter.getInstance(document, baos);
                    document.open();

                    // Title
                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                    Paragraph title = new Paragraph("All Grant Requests Report", titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(20f);
                    document.add(title);

                    // Define table with number of columns (adjust as per fields)
                    PdfPTable table = new PdfPTable(13);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f}); // column widths

                    // Header font
                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

                    // Add table headers
                    String[] headers = {
                            "Request ID", "Requested Amount", "Release Amount", "Date Requested", "Released Date",
                            "Request Status", "Proposal Type", "Implementation Types", "Tranche", "Policy Start Date",
                            "Policy End Date", "Financial Year", "Sanction Date"
                    };

                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(new Color(220, 220, 220));
                        table.addCell(cell);
                    }

                    // Content font
                    Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

                    // Add rows for each AllGrantRequest
                    for (AllGrantRequest row : allGrantRequests) {
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getRequestId()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getRequestedAmount()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getReleaseAmount()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getDateRequested()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getReleasedDate()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getRequestStatus()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getProposalType().getName()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getImplementationTypes().getName()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getTranche()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getPolicyStartDate()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getPolicyEndDate()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getFinancialYear()), contentFont)));
                        table.addCell(new PdfPCell(new Phrase(sanitizeValue(row.getSanctionDate()), contentFont)));
                    }

                    // Add table to document
                    document.add(table);

                    document.close();

                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                    HttpHeaders headersHttp = new HttpHeaders();
                    headersHttp.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AllGrantRequests.pdf");
                    headersHttp.setContentType(MediaType.APPLICATION_PDF);

                    return ResponseEntity.ok()
                            .headers(headersHttp)
                            .body(new InputStreamResource(bais));

                } catch (Exception e) {
                    throw new RuntimeException("Error generating PDF", e);
                }
            }
        else {
                return ResponseEntity.ok().body(allGrantRequests);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error("failed to fetch details ", e.toString()));
        }

    }


    @GetMapping("/get/{requestId}")
    public ResponseEntity<?> getGrantRequestByRequestId(@PathVariable("requestId") String requestId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();

            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            if(user.getId().equals(grantRequests.getUser().getId()))
            {
                return ResponseEntity.badRequest().body(new Error("Not Authorized","Not Authorized"));
            }
            List<Object[]> rawResults = iQueries.getWorkflowRemarksByRequestId(requestId);

            List<WorkFlowRemarkResponse> remarks = rawResults.stream().map(obj -> {
                WorkFlowRemarkResponse response = new WorkFlowRemarkResponse();
                response.setRemarks((String) obj[0]);
                response.setRoleName((String) obj[1]);
                response.setCreatedAt(obj[2] != null ? ((java.sql.Timestamp) obj[2]).toLocalDateTime() : null);
                return response;
            }).toList();

            return ResponseEntity.ok(new WorkFlowHistory(grantRequests,remarks));
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

@PostMapping("upload-sanction")
@PreAuthorize("hasAuthority('NHA State Co-ordinator') ")
        public ResponseEntity<?>UploadSanction(@Valid @RequestBody UploadSanction uploadSanction)
{

    try
    {
        Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(uploadSanction.getRequestId());

        if(grantRequests.isEmpty() || grantRequests.get().getStatusDescription().getId()!=8)
        {
            return ResponseEntity.badRequest().body(new Error("Cant perform this action as application is not at this stage",""));
        }
        BigDecimal AmountReleased=iGrantRequestsRepo.getTotalReleasedAmountByStateId(grantRequests.get().getState().getId());
        Optional<States> state=istatesRepository.findById(grantRequests.get().getState().getId());



        BigDecimal totalAmount = uploadSanction.getAmountSC()
                .add(uploadSanction.getAmountGC())
                .add(uploadSanction.getAmountST());
        if(totalAmount.compareTo(grantRequests.get().getMaxEligibleGrant())>0)
        {
            return ResponseEntity.badRequest().body(new Error("Total amount cannot be more than the Max Eligible Grants ","Total amount cannot be more than the Max Eligible Grants"));

        }
        if(totalAmount.compareTo(grantRequests.get().getRequestedAmount()) > 0)
        {
               return ResponseEntity.badRequest().body(new Error("Total amount cannot be more than the requested Amount","Total amount cannot be more than the requested Amount"));

        }




        int op=iGrantRequestsRepo.updateGrantSanctionDetailsByRequestId(uploadSanction.getAmountSC(),uploadSanction.getAmountST(),uploadSanction.getAmountGC(),uploadSanction.getSanctionDate(),uploadSanction.getRequestId(),uploadSanction.getSanctionLetterBytes());
if(op>0)
{
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    User user = userRepo.findByEmail(name).get();
    //save to workFlow
    grantRequestService.saveToWorkFlow(uploadSanction.getRequestId(),user.getId(), 8,1, "");
   // Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(uploadSanction.getRequestId());
    try {
        otpService.ApplicationFinalApprovalToShaAndCeo(uploadSanction.getRequestId(), grantRequests.get().getProposalType().getId(), grantRequests.get().getState().getId(), Math.toIntExact(grantRequests.get().getImplementationMode().getId()), grantRequests.get().getUser().getId());
    }
    catch (Exception e)
    {
        ;
    }
  return ResponseEntity.ok().body(new SuccessResponse("Successfully Uploaded sanction"));
}
else
{
    return ResponseEntity.internalServerError().body(new Error("Failed to  Uploaded sanction","Failed to  Uploaded sanction"));
}

    }
    catch(Exception e)
    {
        return ResponseEntity.internalServerError().body(new Error("Failed to upload Sanction ", e.toString()));

    }
}

}
