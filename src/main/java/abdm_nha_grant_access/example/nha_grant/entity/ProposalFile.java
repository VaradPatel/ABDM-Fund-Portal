package abdm_nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proposal_id", nullable = false)
    private Integer proposalId;

    // Stores the UploadHeading enum's .name(), e.g. "FUND_ALLOCATION" - not the display
    // label, so a future rename of the display label doesn't require a data migration.
    @Column(name = "heading", nullable = false)
    private String heading;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    // Relative to proposal.upload-dir, e.g. "REQ-.../UC/report.pdf" - not the absolute
    // path, so the base dir can be moved/reconfigured without invalidating stored rows.
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
