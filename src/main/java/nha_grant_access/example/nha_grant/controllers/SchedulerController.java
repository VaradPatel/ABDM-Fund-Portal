package nha_grant_access.example.nha_grant.controllers;

import nha_grant_access.example.nha_grant.service.TokenRefreshAndDataFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    @Autowired
    private TokenRefreshAndDataFetchService tokenRefreshAndDataFetchService;

    /**
     * Endpoint to manually trigger the token refresh and data fetch
     * Useful for testing without waiting for the scheduled task
     */
    @PostMapping("/trigger-refresh")
    public ResponseEntity<String> triggerRefresh() {
        try {
            tokenRefreshAndDataFetchService.manualRefresh();
            return ResponseEntity.ok("Token refresh and data fetch triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error triggering refresh: " + e.getMessage());
        }
    }

    /**
     * Endpoint to get the currently cached token
     */
    @GetMapping("/cached-token")
    public ResponseEntity<String> getCachedToken() {
        String token = tokenRefreshAndDataFetchService.getCachedToken();
        if (token != null && !token.isEmpty()) {
            return ResponseEntity.ok("Current cached token: " + token);
        } else {
            return ResponseEntity.ok("No cached token available");
        }
    }
}

