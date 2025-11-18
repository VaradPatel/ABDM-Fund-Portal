package nha_grant_access.example.nha_grant.service;



import lombok.RequiredArgsConstructor;

import nha_grant_access.example.nha_grant.entity.ExcelFile;
import nha_grant_access.example.nha_grant.repository.ExcelFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExcelService {
    @Autowired

    ExcelFileRepository excelFileRepository;

    public ExcelFile storeFile(String requestId, MultipartFile file) throws IOException {

        Optional<ExcelFile> existingFile = excelFileRepository.findByRequestId(requestId);

        ExcelFile excelFile;

        if (existingFile.isPresent()) {
            // Update the existing record
            excelFile = existingFile.get();
            excelFile.setFileName(file.getOriginalFilename());
            excelFile.setContentType(file.getContentType());
            excelFile.setData(file.getBytes());
        } else {
            // Create a new record
            excelFile = ExcelFile.builder()
                    .requestId(requestId)
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        }

        // Save or update (JPA handles both)
        return excelFileRepository.save(excelFile);
    }

    public Optional<ExcelFile> getFile(Long id) {
        return excelFileRepository.findById(id);
    }

    public Optional<ExcelFile> getFileByRequestId(String requestId) {
        return excelFileRepository.findByRequestId(requestId);
    }
}
