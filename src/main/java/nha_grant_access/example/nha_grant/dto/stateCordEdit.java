package nha_grant_access.example.nha_grant.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class stateCordEdit {

    private ImplementationTrustNhaPayementDetails implementationTrustNhaPayementDetails;
    private AdminNhaPaymentDetails adminNhaPaymentDetails;
    private ImplementationInsurancePayementDetails implementationInsurancePayementDetails;
    private VVSImplementationNewBenef vvsImplementationNewBenef;
    private AashaImplementationTrust aashaImplementationTrust;
    private AashaHybrid aashaHybrid;
    private AashaAdmin aashaAdmin;
    private VvsHybrid vvsHybrid;
    private VvsAdmin vvsAdmin;
}