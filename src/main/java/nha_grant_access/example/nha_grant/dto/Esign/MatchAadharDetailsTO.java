package nha_grant_access.example.nha_grant.dto.Esign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchAadharDetailsTO {
    private String dobY;
    private String adharsignername;
    private String gender;
    private String appintName;
}
