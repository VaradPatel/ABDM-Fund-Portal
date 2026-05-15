package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO for individual approved family data from NHA API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedFamilyData {

    @JsonProperty("total_family_count")
    private String totalFamilyCount;

    @JsonProperty("fresh_family_count")
    private String freshFamilyCount;

    @JsonProperty("old_family_count")
    private String oldFamilyCount;

    @JsonProperty("state_name")
    private String stateName;

    @JsonProperty("state_code")
    private String stateCode;
}

