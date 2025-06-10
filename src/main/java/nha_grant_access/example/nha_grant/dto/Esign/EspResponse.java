package nha_grant_access.example.nha_grant.dto.Esign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EspResponse {
    @JsonProperty("espRequest")
    private String espRequest;

    @JsonProperty("aspTxnId")
    private String aspTxnId;

    @JsonProperty("contentType")
    private String contentType;   // e.g. "application/xml"

    @JsonProperty("espUrl")
    private String espUrl;        // the URL you sent the user to

    @JsonProperty("status")
    private String status;        // "success" | "failure" | etc.

    @JsonProperty("message")
    private String message;
}
