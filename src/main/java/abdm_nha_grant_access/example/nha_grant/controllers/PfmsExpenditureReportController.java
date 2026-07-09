package abdm_nha_grant_access.example.nha_grant.controllers;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.Error;
import abdm_nha_grant_access.example.nha_grant.dto.StateExpenditureSummary;
import abdm_nha_grant_access.example.nha_grant.dto.SuccessResponse;
import abdm_nha_grant_access.example.nha_grant.entity.PfmsExpenditureReportFile;
import abdm_nha_grant_access.example.nha_grant.service.PfmsExpenditureReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pfms-expenditure")
@Slf4j
public class PfmsExpenditureReportController {

    @Autowired
    private PfmsExpenditureReportService pfmsExpenditureReportService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("financialYear") String financialYear,
            @RequestParam("quarter") String quarter) {
        try {
            PfmsExpenditureReportService.UploadResult result =
                    pfmsExpenditureReportService.uploadReport(file, date, financialYear, quarter);

            String message = "Uploaded successfully. "
                    + " state record(s) saved for date " + date;

            return ResponseEntity.ok(new SuccessResponse(message));
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Upload failed", e.getMessage()));
        }
    }

    @GetMapping("/download-latest")
    public ResponseEntity<?> downloadLatest() {
        try {
            PfmsExpenditureReportFile file = pfmsExpenditureReportService.getLatestFile();
            MediaType mediaType = file.getContentType() != null
                    ? MediaType.parseMediaType(file.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFileName()).build());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(file.getFileData());
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Download failed", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(
            @RequestParam("states") List<Integer> states,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "financialYear", required = false) String financialYear,
            @RequestParam(value = "quarter", required = false) String quarter) {
        try {
            List<StateExpenditureSummary> summary = pfmsExpenditureReportService
                    .getSummary(states, startDate, endDate, financialYear, quarter);
            return ResponseEntity.ok(summary);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch summary", e.getMessage()));
        }
    }

    @GetMapping("/latest-two-days")
    public ResponseEntity<?> getLatestTwoDays(
            @RequestParam("states") List<Integer> states,
            @RequestParam(value = "sumStates", required = false, defaultValue = "false") boolean sumStates,
            @RequestParam(value = "financialYear", required = false) String financialYear,
            @RequestParam(value = "quarter", required = false) String quarter) {
        try {
            List<StateExpenditureSummary> summary = pfmsExpenditureReportService
                    .getLatestTwoDaysSummary(states, sumStates, financialYear, quarter);
            return ResponseEntity.ok(summary);
        } catch (GrantException e) {
            return ResponseEntity.badRequest().body(new Error("Validation failed", e.getMessage()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch latest two days summary", e.getMessage()));
        }
    }
}
