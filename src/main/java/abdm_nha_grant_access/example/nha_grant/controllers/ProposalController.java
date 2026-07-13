package abdm_nha_grant_access.example.nha_grant.controllers;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.Error;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalActionRequest;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalResponse;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalUploadResponse;
import abdm_nha_grant_access.example.nha_grant.dto.WorkflowHistoryEntry;
import abdm_nha_grant_access.example.nha_grant.entity.ProposalFile;
import abdm_nha_grant_access.example.nha_grant.service.FileStorageService;
import abdm_nha_grant_access.example.nha_grant.service.ProposalService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/proposal")
@Slf4j
public class ProposalController {

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProposal(
            @RequestParam("state_id") Integer stateId,
            @RequestParam("financial_year") String financialYear,
            @RequestParam("quarter") String quarter,
            @RequestParam("category_id") Integer categoryId,
            @RequestParam("amount_requested") BigDecimal amountRequested,
            @RequestParam(value = "ucFiles", required = false) List<MultipartFile> ucFiles,
            @RequestParam(value = "reconcileFiles", required = false) List<MultipartFile> reconcileFiles,
            @RequestParam(value = "fundAllocationFiles", required = false) List<MultipartFile> fundAllocationFiles,
            @RequestParam(value = "unspendBalanceFiles", required = false) List<MultipartFile> unspendBalanceFiles,
            @RequestParam(value = "othersFiles", required = false) List<MultipartFile> othersFiles,
            @RequestParam(value = "remarks", required = false) String remarks) {
        try {
            String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ProposalUploadResponse result = proposalService.submitProposal(
                    stateId, financialYear, quarter, categoryId, amountRequested,
                    ucFiles, reconcileFiles, fundAllocationFiles, unspendBalanceFiles, othersFiles,
                    remarks, requesterEmail);
            return ResponseEntity.ok(result);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Upload failed", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getProposals(
            @RequestParam(value = "state_id", required = false) Integer stateId,
            @RequestParam(value = "financial_year", required = false) String financialYear,
            @RequestParam(value = "quarter", required = false) String quarter,
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) Integer status) {
        try {
            List<ProposalResponse> proposals =
                    proposalService.searchProposals(stateId, financialYear, quarter, categoryId, status);
            return ResponseEntity.ok(proposals);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch proposals", e.getMessage()));
        }
    }

    // NHA State Coord's response to a proposal pending their review: ACCEPT it, or
    // RAISE_QUERY (with remarks) to bounce it back to the state bucket for editing.
    @PostMapping("/action")
    public ResponseEntity<?> actionOnProposal(@Valid @RequestBody ProposalActionRequest request) {
        try {
            String actingUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ProposalResponse result = proposalService.actionOnProposal(
                    request.getRequestId(), request.getAction(), request.getRemarks(), actingUserEmail);
            return ResponseEntity.ok(result);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Action failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Action failed", e.getMessage()));
        }
    }

    // Lets the state edit a proposal a query was raised against and resubmit it (moves status
    // back to PENDING_AT_NHA_STATE_COORD, from where the coord can accept or raise another
    // query). This is a full file replace: every previously uploaded file for the proposal is
    // deleted and only the files attached to this call are kept.
    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editProposal(
            @RequestParam("request_id") String requestId,
            @RequestParam(value = "quarter", required = false) String quarter,
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "amount_requested", required = false) BigDecimal amountRequested,
            @RequestParam(value = "ucFiles", required = false) List<MultipartFile> ucFiles,
            @RequestParam(value = "reconcileFiles", required = false) List<MultipartFile> reconcileFiles,
            @RequestParam(value = "fundAllocationFiles", required = false) List<MultipartFile> fundAllocationFiles,
            @RequestParam(value = "unspendBalanceFiles", required = false) List<MultipartFile> unspendBalanceFiles,
            @RequestParam(value = "othersFiles", required = false) List<MultipartFile> othersFiles,
            @RequestParam(value = "remarks", required = false) String remarks) {
        try {
            String actingUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ProposalResponse result = proposalService.editProposal(requestId, quarter, categoryId, amountRequested,
                    ucFiles, reconcileFiles, fundAllocationFiles, unspendBalanceFiles, othersFiles,
                    remarks, actingUserEmail);
            return ResponseEntity.ok(result);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Edit failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Edit failed", e.getMessage()));
        }
    }

    @GetMapping("/{requestId}/timeline")
    public ResponseEntity<?> getWorkflowHistory(@PathVariable String requestId) {
        try {
            List<WorkflowHistoryEntry> history = proposalService.getWorkflowHistory(requestId);
            return ResponseEntity.ok(history);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch timeline", e.getMessage()));
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer fileId) {
        try {
            ProposalFile proposalFile = proposalService.getProposalFile(fileId);
            Resource resource = fileStorageService.loadAsResource(proposalFile.getFilePath());
            MediaType mediaType = proposalFile.getContentType() != null
                    ? MediaType.parseMediaType(proposalFile.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(proposalFile.getOriginalFileName())
                                    .build().toString())
                    .body(resource);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Download failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Download failed", e.getMessage()));
        }
    }
}
