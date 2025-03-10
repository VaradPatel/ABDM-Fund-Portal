package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "request_id", nullable = false, length = 256)
    private String requestId;

    @ManyToOne
    @JoinColumn(name = "query_user_id", nullable = false)
    private User queryUser;

    @ManyToOne
    @JoinColumn(name = "response_user_id")
    private User responseUser;

    @Column(name = "query_field_name")
    private String queryFieldName;

    @Column(name = "query_comment", nullable = false)
    private String queryComment;

    @Column(name = "query_response_comment")
    private String queryResponseComment;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt ;

    @Column(name = "query_doc")
    private String queryDoc;

    @Column(name = "query_response_doc")
    private String queryResponseDoc;



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

