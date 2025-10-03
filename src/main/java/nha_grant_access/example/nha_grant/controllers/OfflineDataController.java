package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.OfflineProposalRequest;
import nha_grant_access.example.nha_grant.dto.SuccessResponse;
import nha_grant_access.example.nha_grant.entity.OfflineData;
import nha_grant_access.example.nha_grant.repository.IOfflineDataRepo;
import nha_grant_access.example.nha_grant.service.OfflineDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offline-data")
@RequiredArgsConstructor
public class OfflineDataController {

    private final OfflineDataService offlineDataService;
    @Autowired
    private IOfflineDataRepo iOfflineDataRepo;

    @PostMapping
    public ResponseEntity<?> createOfflineData(
            @Valid @RequestBody List<OfflineProposalRequest> request) {

        try {

            List<OfflineData> savedDataList = new ArrayList<>();

            for (OfflineProposalRequest req : request) {
                OfflineData saved = offlineDataService.saveOfflineData(req);
                savedDataList.add(saved);
            }

            return ResponseEntity.ok(savedDataList);
        }
        catch (Exception e)
        {
            {

                return  ResponseEntity.internalServerError().body(new Error("Error while submitting offline data", e.toString()));
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfflineDataById(@PathVariable Integer id) {
        try {
            OfflineData data = offlineDataService.getOfflineDataById(id);
            return ResponseEntity.ok(data);
        }
        catch (Exception e)
        {
            return  ResponseEntity.internalServerError().body(new Error("Error while fetching offline proposals " , e.toString()));
        }
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getOfflineDataAll() {
        try {
            List<OfflineData> data = iOfflineDataRepo.findAll();
            return ResponseEntity.ok(data);
        }
        catch (Exception e)
        {
            return  ResponseEntity.internalServerError().body(new Error("Error while fetching offline proposals " , e.toString()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOfflineData(
            @PathVariable Integer id,
            @Valid @RequestBody OfflineProposalRequest request) {

        OfflineData updatedData = offlineDataService.updateOfflineData(id, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Offline data updated successfully");
        response.put("data", updatedData);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOfflineData(@PathVariable Integer id) {
        offlineDataService.deleteOfflineData(id);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Offline data deleted successfully");

        return ResponseEntity.ok(response);
    }
}