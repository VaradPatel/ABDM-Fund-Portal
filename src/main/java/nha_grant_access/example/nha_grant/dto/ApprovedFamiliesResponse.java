package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POJO for the approved families response from NHA API
 * Wraps the list of approved family data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedFamiliesResponse {

    @JsonProperty("list")
    private List<ApprovedFamilyData> list;
}

