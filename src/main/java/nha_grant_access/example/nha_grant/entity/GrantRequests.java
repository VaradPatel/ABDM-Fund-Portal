package nha_grant_access.example.nha_grant.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.minio.messages.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.dto.StateShare;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonIgnore
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

//    @Column(name = "tranche", nullable = false)
//    private String tranche;
@Column(name = "tranche", columnDefinition = "jsonb") // JSONB for PostgreSQL
@JdbcTypeCode(SqlTypes.JSON) // Hibernate 6+ annotation for JSON support
private List<Integer> tranche;

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

//    @Column(name = "state_share", nullable = false)
//    private BigDecimal stateShare;
@Column(name = "state_share", columnDefinition = "jsonb")
@JdbcTypeCode(SqlTypes.JSON)
private List<StateShare> stateShare;


    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "sanction_date", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime sanctionDate;


    @Column(name = "updated_at",  columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "e_sign_status_state_ceo")
    private Boolean eSignStatusStateCeo;

    @ManyToOne
    @JoinColumn(name = "flow_status", referencedColumnName = "id")
    private StatusDescription statusDescription;

    @Column(name="remarks")
    private String remarks;

    @Column(name="bank_mapped_with_pmfs")

    private boolean bankMappedWithPfms;
    @Column(name="positive_balance")
    private Boolean positiveBalance;

    @Column(name="total_state_share")

    private BigDecimal totalStateShare;
@Column(name="sc_amount")
private BigDecimal amountSc;

    @Column(name="st_amount")
    private BigDecimal amountSt;

    @Column(name="gc_amount")
    private BigDecimal amountGc;

    @Lob
    @Column(name = "sanction_letter")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] sanction_letter;

    @Lob
    @Column(name = "esign_pdf")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] esign_pdf;
    @Column (name="esign_txn_id")
    private String esignTxnId;




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
