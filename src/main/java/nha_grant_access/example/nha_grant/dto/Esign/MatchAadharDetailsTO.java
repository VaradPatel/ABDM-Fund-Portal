package nha_grant_access.example.nha_grant.dto.Esign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

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
