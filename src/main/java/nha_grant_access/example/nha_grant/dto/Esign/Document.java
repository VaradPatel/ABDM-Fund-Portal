package nha_grant_access.example.nha_grant.dto.Esign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private String integratorName;
    private String templateId;
    private String stateCEOName;
    private String signingPlace;
    private String mobileNumber;
    private String emailId;
    private String state;
    private String proposalRaisedDate;
    private GIARequestDetails giaRequestDetails;
    private MatchAadharDetailsTO matchAadharDetailsTO;

}
