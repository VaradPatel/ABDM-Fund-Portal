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
public class Document {
    private String integratorName="NHAGRANTS";
    private String templateId="TEMPLATE_1";
    @JsonProperty("StateCEOName")
    private String stateCEOName;
    private String signingPlace="Delhi";
    private String mobileNumber;
    private String emailId;
    private String state;
    @JsonProperty("ProposalRaisedDate")
    private String ProposalRaisedDate;
    @JsonProperty("GIARequestDetails")
    private GIARequestDetails giaRequestDetails;
    private MatchAadharDetailsTO matchAadharDetailsTO;

}
