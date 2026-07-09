package abdm_nha_grant_access.example.nha_grant.controllers;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.Error;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalResponse;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalUploadResponse;
import abdm_nha_grant_access.example.nha_grant.service.ProposalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/proposal")
@Slf4j
public class ProposalController {

    @Autowired
    private ProposalService proposalService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProposal(
            @RequestParam("state_id") Integer stateId,
            @RequestParam("financial_year") String financialYear,
            @RequestParam("quarter") String quarter,
            @RequestParam("category_id") Integer categoryId,
            @RequestParam(value = "ucFiles", required = false) List<MultipartFile> ucFiles,
            @RequestParam(value = "reconcileFiles", required = false) List<MultipartFile> reconcileFiles,
            @RequestParam(value = "fundAllocationFiles", required = false) List<MultipartFile> fundAllocationFiles,
            @RequestParam(value = "unspendBalanceFiles", required = false) List<MultipartFile> unspendBalanceFiles,
            @RequestParam(value = "othersFiles", required = false) List<MultipartFile> othersFiles) {
        try {
            String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ProposalUploadResponse result = proposalService.submitProposal(
                    stateId, financialYear, quarter, categoryId,
                    ucFiles, reconcileFiles, fundAllocationFiles, unspendBalanceFiles, othersFiles,
                    requesterEmail);
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
            @RequestParam(value = "category_id", required = false) Integer categoryId) {
        try {
            List<ProposalResponse> proposals =
                    proposalService.searchProposals(stateId, financialYear, quarter, categoryId);
            return ResponseEntity.ok(proposals);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch proposals", e.getMessage()));
        }
    }
}
