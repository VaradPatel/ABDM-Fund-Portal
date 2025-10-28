package nha_grant_access.example.nha_grant.controllers;


import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.SuccessResponse;
import nha_grant_access.example.nha_grant.entity.ExcelFile;
import nha_grant_access.example.nha_grant.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/excel")

public class ExcelUpload {
    @Autowired
    ExcelService excelService;
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("request_id") String requestId) {
        try {
           nha_grant_access.example.nha_grant.entity.ExcelFile savedFile = excelService.storeFile(requestId, file);
            return ResponseEntity.ok(new SuccessResponse("File uploaded successfully with Request Id: "
                   + requestId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Error("Upload failed: " , e.getMessage()));
        }
    }
    @GetMapping("/download/{requestId}")
    public ResponseEntity<byte[]> downloadExcelByRequestId(@PathVariable String requestId) {

            return excelService.getFileByRequestId(requestId)
                    .map(file -> {
                        // --- Ensure Excel MIME type ---
                        String contentType = file.getContentType();
                        if (contentType == null || contentType.isBlank()) {
                            // default to .xlsx MIME type
                            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                        }

                        // --- Ensure filename ends with .xlsx ---
                        String fileName = file.getFileName();
                        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
                            fileName = (fileName != null ? fileName : "download") + ".xlsx";
                        }

                        // --- Build the response ---
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                        "attachment; filename=\"" + fileName + "\"")
                                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                                .header(HttpHeaders.PRAGMA, "no-cache")
                                .header(HttpHeaders.EXPIRES, "0")
                                .body(file.getData());
                    })
                    .orElse(ResponseEntity.notFound().build());
        }

    }



