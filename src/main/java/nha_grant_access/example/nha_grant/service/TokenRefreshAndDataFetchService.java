package nha_grant_access.example.nha_grant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nha_grant_access.example.nha_grant.dto.ApprovedFamiliesResponse;
import nha_grant_access.example.nha_grant.dto.ApprovedFamilyData;
import nha_grant_access.example.nha_grant.repository.IstatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TokenRefreshAndDataFetchService {

    private static final Logger LOGGER = Logger.getLogger(TokenRefreshAndDataFetchService.class.getName());

    private static final String TOKEN_API_URL = "https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/awsidam/";
    private static final String DATA_API_URL = "https://apisprod.nha.gov.in/pmjay/prodbis/bisdashboard/bis/families/approved";

    @Autowired
    IstatesRepository statesRepository;
    @Autowired
    private RestTemplate restTemplate;

    private String cachedToken = null;

    /**
     * Scheduled task that runs every hour (3600000 milliseconds = 1 hour)
     * Fetches a new token and then calls the data API with that token
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void refreshTokenAndFetchData() {
        try {
            LOGGER.info("Starting hourly token refresh and data fetch process...");

            // Step 1: Get the token from the first API
            String token = fetchToken();

            if (token != null && !token.isEmpty()) {
                // Step 2: Use the token to fetch PMJAY data
                fetchApprovedFamiliesData(token, "PMJAY");
                
                // Step 3: Use the token to fetch VVS data
                fetchApprovedFamiliesData(token, "VVS");
                
                // Step 4: Use the token to fetch AAA data
                fetchApprovedFamiliesData(token, "AAA");
                
                LOGGER.info("Successfully completed hourly token refresh and data fetch");
            } else {
                LOGGER.log(Level.WARNING, "Failed to obtain token from first API");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in scheduled token refresh and data fetch: " + e.getMessage(), e);
        }
    }

    /**
     * Calls the first API to get a token
     * Returns the token as a string (the response body)
     */
    private String fetchToken() {
        try {
            LOGGER.info("Fetching token from API: " + TOKEN_API_URL);

            // Create headers for the first API call
            HttpHeaders headers = createTokenApiHeaders();
            HttpEntity<String> request = new HttpEntity<>(null, headers);

            // Make the POST request
            ResponseEntity<String> response = restTemplate.exchange(
                    TOKEN_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String token = response.getBody();
                cachedToken = token;
                LOGGER.info("Successfully obtained token from first API");
                return token;
            } else {
                LOGGER.log(Level.WARNING, "Failed to fetch token. Status: " + response.getStatusCode());
                return null;
            }
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Error calling token API: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calls the second API to fetch approved families data using the provided token
     */
    private void fetchApprovedFamiliesData(String token, String type) {
        try {
            LOGGER.info("Fetching approved families data for type: " + type + " with token...");

            // Create headers for the second API call
            HttpHeaders headers = createDataApiHeaders(token);

            // Create the request body with the type parameter
            String requestBody = createRequestBody(type);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Make the POST request and receive response as POJO
            ResponseEntity<ApprovedFamiliesResponse> response = restTemplate.exchange(
                    DATA_API_URL,
                    HttpMethod.POST,
                    request,
                    ApprovedFamiliesResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ApprovedFamiliesResponse familiesResponse = response.getBody();
                LOGGER.info("Successfully fetched approved families data for type: " + type);

                if (familiesResponse != null && familiesResponse.getList() != null) {
                    List<ApprovedFamilyData> familiesList = familiesResponse.getList();
                    LOGGER.info("Total states/families received for " + type + ": " + familiesList.size());

                    // Process each family data and update database
                    for (ApprovedFamilyData familyData : familiesList) {
                        try {
                            // Convert stateCode to Integer
                            Integer stateCode = Integer.parseInt(familyData.getStateCode());

                            // Find state by tcs_dashboard_state_id matching stateCode
                            java.util.Optional<nha_grant_access.example.nha_grant.entity.States> stateOptional = 
                                statesRepository.findByTcsDashboardStateId(stateCode);

                            if (stateOptional.isPresent()) {
                                nha_grant_access.example.nha_grant.entity.States state = stateOptional.get();
                                
                                if ("PMJAY".equalsIgnoreCase(type)) {
                                    // Update tcsPmjayFamily with total_family_count
                                    Integer totalFamilyCount = Integer.parseInt(familyData.getTotalFamilyCount());
                                    state.setTcsPmjayFamily(totalFamilyCount);
                                    LOGGER.info("Updated PMJAY data for state: " + familyData.getStateName() + 
                                               ", Total Family Count: " + totalFamilyCount);
                                } else if ("VVS".equalsIgnoreCase(type)) {
                                    // Update tcsOldFamily with old_family_count and tcsNewFamily with fresh_family_count
                                    Integer oldFamilyCount = Integer.parseInt(familyData.getOldFamilyCount());
                                    Integer freshFamilyCount = Integer.parseInt(familyData.getFreshFamilyCount());
                                    
                                    state.setTcsOldFamily(oldFamilyCount);
                                    state.setTcsNewFamily(freshFamilyCount);
                                    LOGGER.info("Updated VVS data for state: " + familyData.getStateName() + 
                                               ", Old Family Count: " + oldFamilyCount + 
                                               ", Fresh Family Count: " + freshFamilyCount);
                                } else if ("AAA".equalsIgnoreCase(type)) {
                                    // Update tcsAashaFamily with total_family_count
                                    Integer totalFamilyCount = Integer.parseInt(familyData.getTotalFamilyCount());
                                    state.setTcsAashaFamily(totalFamilyCount);
                                    LOGGER.info("Updated AAA data for state: " + familyData.getStateName() + 
                                               ", Total Family Count: " + totalFamilyCount);
                                }
                                
                                statesRepository.save(state);
                            } else {
                                LOGGER.log(Level.WARNING, "State with tcs_dashboard_state_id " + stateCode + 
                                           " not found in database");
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.SEVERE, "Error parsing counts for state: " + 
                                    familyData.getStateName() + ", Error: " + e.getMessage(), e);
                        }
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Response body is null or list is empty for type: " + type);
                }
            } else {
                LOGGER.log(Level.WARNING, "Failed to fetch data for type: " + type + ", Status: " + 
                           response.getStatusCode());
            }
        } catch (RestClientException e) {
            LOGGER.log(Level.SEVERE, "Error calling data API for type: " + type + ", Error: " + 
                       e.getMessage(), e);
        }
    }

    /**
     * Creates headers for the token API call
     */
    private HttpHeaders createTokenApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.set("Referer", "https://dashboard.nha.gov.in/");
        headers.set("Origin", "https://dashboard.nha.gov.in");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-site");
        headers.set("Pragma", "no-cache");
        headers.set("Cache-Control", "no-cache");
        headers.set("Content-Length", "0");
        headers.set("TE", "trailers");
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates headers for the data API call
     */
    private HttpHeaders createDataApiHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("Access-Control-Allow-Origin", "https://dashboard.nha.gov.in/public/");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Cache-Control", "no-cache");
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("Origin", "https://dashboard.nha.gov.in");
        headers.set("Pragma", "no-cache");
        headers.set("Priority", "u=1, i");
        headers.set("Referer", "https://dashboard.nha.gov.in/");
        headers.set("Sec-CH-UA", "\"Microsoft Edge\";v=\"147\", \"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"147\"");
        headers.set("Sec-CH-UA-Mobile", "?0");
        headers.set("Sec-CH-UA-Platform", "\"Windows\"");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-site");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0");
        return headers;
    }

    /**
     * Creates the JSON request body for the data API call
     */
    private String createRequestBody(String type) {
        return "{\n" +
                "    \"type\":\"" + type + "\",\n" +
                "    \"rpttype\":\"S\",\n" +
                "    \"state_code\":\"\"\n" +
                "}";
    }

    /**
     * Optional: Get the cached token (useful if you need it elsewhere)
     */
    public String getCachedToken() {
        return cachedToken;
    }

    /**
     * Optional: Manually trigger the refresh (useful for testing)
     */
    public void manualRefresh() {
        refreshTokenAndFetchData();
    }
}

