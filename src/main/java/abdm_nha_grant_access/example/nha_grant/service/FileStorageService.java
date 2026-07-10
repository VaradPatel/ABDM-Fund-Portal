package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.enums.UploadHeading;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Set;

@Slf4j
@Service
public class FileStorageService {

    public static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "xls", "xlsx", "doc", "docx");
    private static final String RAND_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${proposal.upload-dir}")
    private String uploadDir;

    public record StoredFile(String originalFileName, String storedFileName,
                              String relativePath, long size) {
    }

    public StoredFile store(String requestId, UploadHeading heading, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new GrantException("An uploaded file for heading " + heading.getFolderName() + " is empty");
        }

        String sanitizedName = sanitizeFileName(file.getOriginalFilename());
        validateExtension(sanitizedName);

        Path headingDir = Paths.get(uploadDir, requestId, heading.getFolderName());
        Files.createDirectories(headingDir);

        String finalName = uniqueNameWithin(headingDir, sanitizedName);
        Path target = headingDir.resolve(finalName);
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = Paths.get(requestId, heading.getFolderName(), finalName).toString();
        return new StoredFile(sanitizedName, finalName, relativePath, file.getSize());
    }

    // relativePath comes from a ProposalFile row we wrote ourselves during store(), but
    // re-derive and re-check it against uploadDir here anyway rather than trusting the
    // DB value blindly - defense in depth against a path ever getting corrupted upstream.
    public Resource loadAsResource(String relativePath) {
        Path base = Paths.get(uploadDir).normalize();
        Path file = base.resolve(relativePath).normalize();
        if (!file.startsWith(base)) {
            throw new GrantException("Invalid file path: " + relativePath);
        }
        if (!Files.exists(file) || !Files.isReadable(file)) {
            throw new GrantException("File not found: " + relativePath);
        }
        return new FileSystemResource(file);
    }

    // Best-effort cleanup used when a proposal submission fails partway through
    // (e.g. an IOException after some files were already written). Not full
    // two-phase-commit atomicity - just avoids leaving orphaned files on disk for
    // a request_id whose DB row got rolled back.
    public void deleteRequestFolder(String requestId) {
        Path requestDir = Paths.get(uploadDir, requestId);
        if (!Files.exists(requestDir)) {
            return;
        }
        try (var walk = Files.walk(requestDir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    log.error("Failed to clean up {} after a failed proposal submission: {}", p, e.toString());
                }
            });
        } catch (IOException e) {
            log.error("Failed to walk {} for cleanup: {}", requestDir, e.toString());
        }
    }

    // Path-traversal defense: take only the last path segment (strips any directory
    // components an attacker embeds in the multipart filename, whether relative "../"
    // or absolute "/etc/passwd"-style), then reject anything that still isn't a plain name.
    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new GrantException("Uploaded file must have a file name");
        }
        // getFileName() already stripped any directory components, so the traversal
        // segments themselves ("..", ".") are the only unsafe values left to reject -
        // a flat filename that merely contains ".." (e.g. "FY24-25..Q1.pdf") is safe.
        String cleaned = Paths.get(originalFilename).getFileName().toString();
        if (cleaned.equals("..") || cleaned.equals(".") || cleaned.isBlank()) {
            throw new GrantException("Invalid file name: " + originalFilename);
        }
        return cleaned;
    }

    private void validateExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            throw new GrantException("File '" + fileName + "' has no file extension");
        }
        String extension = fileName.substring(dot + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new GrantException("File '" + fileName + "' has an unsupported type. Allowed: " + ALLOWED_EXTENSIONS);
        }
    }

    // Collision handling: if a file with the same name already exists in this heading's
    // folder, suffix with a short random token before the extension rather than silently
    // overwriting it.
    private String uniqueNameWithin(Path dir, String fileName) {
        Path candidate = dir.resolve(fileName);
        if (!Files.exists(candidate)) {
            return fileName;
        }
        int dot = fileName.lastIndexOf('.');
        String base = fileName.substring(0, dot);
        String ext = fileName.substring(dot);
        String result;
        do {
            result = base + "_" + randomToken(6) + ext;
        } while (Files.exists(dir.resolve(result)));
        return result;
    }

    private String randomToken(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RAND_CHARS.charAt(RANDOM.nextInt(RAND_CHARS.length())));
        }
        return sb.toString();
    }
}
