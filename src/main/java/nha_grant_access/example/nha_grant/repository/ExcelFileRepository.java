package nha_grant_access.example.nha_grant.repository;


import nha_grant_access.example.nha_grant.entity.ExcelFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExcelFileRepository extends JpaRepository<ExcelFile, Long> {
    Optional<ExcelFile> findByRequestId(String requestId);
}
