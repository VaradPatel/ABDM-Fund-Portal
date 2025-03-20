package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grant_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "grant_requests_seq", sequenceName = "grant_requests_seq", allocationSize = 1)

public class GrantRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grant_requests_seq")
    private Integer id;

    @Column(name = "request_id", nullable = false, unique = true, length = 256)
    private String requestId;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private States state;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "proposal_type_id", nullable = false)
    private ProposalType proposalType;

    @ManyToOne
    @JoinColumn(name = "implementation_mode_id", nullable = false)
    private ImplementationTypes implementationMode;

    @Column(name = "insurance_company", length = 255)
    private String insuranceCompany;

    @Column(name = "policy_start_date", nullable = false)
    private LocalDate policyStartDate;

    @Column(name = "policy_end_date", nullable = false)
    private LocalDate policyEndDate;

    @Column(name = "financial_year", nullable = false, length = 9)
    private String financialYear;

    @Column(name = "tranche", nullable = false)
    private String tranche;

    @Column(name = "pmjay_beneficiary_count", nullable = false)
    private Long pmjayBeneficiaryCount;

    @Column(name = "total_beneficiary_count", nullable = false)
    private Long totalBeneficiaryCount;

    @Column(name = "premium", nullable = false)
    private BigDecimal premium;

    @Column(name = "nha_share", nullable = false)
    private BigDecimal nhaShare;

    @Column(name = "max_eligible_grant", nullable = false)
    private BigDecimal maxEligibleGrant;

    @Column(name = "release_till_date", nullable = false, columnDefinition = "numeric default 0")
    private BigDecimal releaseTillDate;

    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "released_amount")
    private BigDecimal releasedAmount;

    @Column(name = "state_share", nullable = false)
    private BigDecimal stateShare;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",  columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "e_sign_status_state_ceo")
    private Boolean eSignStatusStateCeo;

    @ManyToOne
    @JoinColumn(name = "flow_status", referencedColumnName = "id")
    private StatusDescription statusDescription;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Set only once
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Only updated_at changes
    }
}
