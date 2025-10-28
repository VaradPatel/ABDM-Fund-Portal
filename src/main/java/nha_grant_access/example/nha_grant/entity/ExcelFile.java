package nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Lob;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String fileName;
    private String contentType;

    @Lob
    @Column(name = "data", columnDefinition = "BYTEA")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] data;
}
