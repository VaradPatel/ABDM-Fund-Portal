package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO to store token refresh and data fetch logs
 * Can be used for auditing and monitoring purposes
 */
public class TokenRefreshLogDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("token_fetch_status")
    private String tokenFetchStatus; // "SUCCESS" or "FAILED"

    @JsonProperty("token_fetch_error")
    private String tokenFetchError; // null if success

    @JsonProperty("data_fetch_status")
    private String dataFetchStatus; // "SUCCESS" or "FAILED"

    @JsonProperty("data_fetch_error")
    private String dataFetchError; // null if success

    @JsonProperty("response_data")
    private String responseData; // Raw response from second API

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    // Constructors
    public TokenRefreshLogDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public TokenRefreshLogDTO(String tokenFetchStatus, String dataFetchStatus) {
        this.tokenFetchStatus = tokenFetchStatus;
        this.dataFetchStatus = dataFetchStatus;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenFetchStatus() {
        return tokenFetchStatus;
    }

    public void setTokenFetchStatus(String tokenFetchStatus) {
        this.tokenFetchStatus = tokenFetchStatus;
    }

    public String getTokenFetchError() {
        return tokenFetchError;
    }

    public void setTokenFetchError(String tokenFetchError) {
        this.tokenFetchError = tokenFetchError;
    }

    public String getDataFetchStatus() {
        return dataFetchStatus;
    }

    public void setDataFetchStatus(String dataFetchStatus) {
        this.dataFetchStatus = dataFetchStatus;
    }

    public String getDataFetchError() {
        return dataFetchError;
    }

    public void setDataFetchError(String dataFetchError) {
        this.dataFetchError = dataFetchError;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TokenRefreshLogDTO{" +
                "id=" + id +
                ", tokenFetchStatus='" + tokenFetchStatus + '\'' +
                ", tokenFetchError='" + tokenFetchError + '\'' +
                ", dataFetchStatus='" + dataFetchStatus + '\'' +
                ", dataFetchError='" + dataFetchError + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

