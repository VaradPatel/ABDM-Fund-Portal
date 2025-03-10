package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "request_id", nullable = false, length = 256)
    private String requestId;

    @ManyToOne
    @JoinColumn(name = "final_flow_status")
    private Action finalFlowStatus;

    @ManyToOne
    @JoinColumn(name = "state_ceo_status")
    private Action stateCeoStatus;

    @ManyToOne
    @JoinColumn(name = "nha_state_coordinator_status")
    private Action nhaStateCoordinatorStatus;

    @ManyToOne
    @JoinColumn(name = "nha_reviewer_status")
    private Action nhaReviewerStatus;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private States state;

    @ManyToOne
    @JoinColumn(name = "proposal_type", nullable = false)
    private ProposalType proposalType;

    @ManyToOne
    @JoinColumn(name = "previous_role_id")
    private Roles previousRole;

    @ManyToOne
    @JoinColumn(name = "current_role_id")
    private Roles currentRole;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Only updated_at changes
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Set only once
        this.updatedAt = LocalDateTime.now();
    }
}


