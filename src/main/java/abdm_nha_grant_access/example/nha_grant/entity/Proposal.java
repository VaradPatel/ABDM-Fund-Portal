package abdm_nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "state_id", nullable = false)
    private Integer stateId;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "quarter", nullable = false)
    private String quarter;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "amount_requested", nullable = false)
    private BigDecimal amountRequested;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Stores ProposalStatus.getId() - see abdm_nha_grant_access.example.nha_grant.enums.ProposalStatus
    @Column(name = "status", nullable = false)
    private Integer status;

    // Set when the NHA State Coord raises a query (status=PENDING_AT_STATE); cleared on accept.
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
