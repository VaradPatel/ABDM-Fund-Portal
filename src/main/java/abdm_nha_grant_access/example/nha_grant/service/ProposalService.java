package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalResponse;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalUploadResponse;
import abdm_nha_grant_access.example.nha_grant.entity.Proposal;
import abdm_nha_grant_access.example.nha_grant.entity.ProposalFile;
import abdm_nha_grant_access.example.nha_grant.entity.User;
import abdm_nha_grant_access.example.nha_grant.enums.ProposalCategory;
import abdm_nha_grant_access.example.nha_grant.enums.ProposalStatus;
import abdm_nha_grant_access.example.nha_grant.enums.UploadHeading;
import abdm_nha_grant_access.example.nha_grant.repository.IProposalFileRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IProposalRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IstatesRepository;
import abdm_nha_grant_access.example.nha_grant.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProposalService {

    private static final Set<String> VALID_QUARTERS = Set.of("Q1", "Q2", "Q3", "Q4");
    private static final int REQUEST_ID_DIGITS = 1_000_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private IProposalRepo proposalRepo;

    @Autowired
    private IProposalFileRepo proposalFileRepo;

    @Autowired
    private IstatesRepository statesRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public ProposalUploadResponse submitProposal(Integer stateId, String financialYear, String quarter,
                                                  Integer categoryId,
                                                  List<MultipartFile> ucFiles,
                                                  List<MultipartFile> reconcileFiles,
                                                  List<MultipartFile> fundAllocationFiles,
                                                  List<MultipartFile> unspendBalanceFiles,
                                                  List<MultipartFile> othersFiles,
                                                  String requesterEmail) throws IOException {

        if (stateId == null) {
            throw new GrantException("state_id is required");
        }
        statesRepository.findById(stateId)
                .orElseThrow(() -> new GrantException("state_id " + stateId + " does not exist"));

        if (financialYear == null || financialYear.isBlank()) {
            throw new GrantException("financial_year is required");
        }
        String normalizedQuarter = quarter == null ? "" : quarter.trim().toUpperCase();
        if (!VALID_QUARTERS.contains(normalizedQuarter)) {
            throw new GrantException("quarter is required and must be one of Q1, Q2, Q3, Q4");
        }
        ProposalCategory category = ProposalCategory.fromId(categoryId);

        User user = userRepo.findByEmail(requesterEmail)
                .orElseThrow(() -> new GrantException("Authenticated user not found for email " + requesterEmail));

        Map<UploadHeading, List<MultipartFile>> filesByHeading = new LinkedHashMap<>();
        filesByHeading.put(UploadHeading.UC, ucFiles);
        filesByHeading.put(UploadHeading.RECONCILE, reconcileFiles);
        filesByHeading.put(UploadHeading.FUND_ALLOCATION, fundAllocationFiles);
        filesByHeading.put(UploadHeading.UNSPEND_BALANCE, unspendBalanceFiles);
        filesByHeading.put(UploadHeading.OTHERS, othersFiles);

        // Fail fast on any bad file (empty, no extension, disallowed type) before any DB
        // write or disk I/O, rather than partway through the storage loop below.
        for (List<MultipartFile> files : filesByHeading.values()) {
            if (files == null) {
                continue;
            }
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    validateFileNameAndExtension(file);
                }
            }
        }

        String requestId = generateRequestId();

        // A freshly submitted proposal starts out awaiting the NHA state coordinator's
        // review; PENDING_AT_STATE only applies later, once a query is raised back to the state.
        Proposal proposal = Proposal.builder()
                .requestId(requestId)
                .stateId(stateId)
                .financialYear(financialYear.trim())
                .quarter(normalizedQuarter)
                .categoryId(category.getId())
                .userId(user.getId())
                .status(ProposalStatus.PENDING_AT_NHA_STATE_COORD.getId())
                .build();
        proposal = proposalRepo.save(proposal);

        try {
            List<ProposalFile> proposalFiles = new ArrayList<>();
            for (Map.Entry<UploadHeading, List<MultipartFile>> entry : filesByHeading.entrySet()) {
                List<MultipartFile> files = entry.getValue();
                if (files == null) {
                    continue;
                }
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    FileStorageService.StoredFile stored = fileStorageService.store(requestId, entry.getKey(), file);
                    proposalFiles.add(ProposalFile.builder()
                            .proposalId(proposal.getId())
                            .heading(entry.getKey().name())
                            .originalFileName(stored.originalFileName())
                            .storedFileName(stored.storedFileName())
                            .filePath(stored.relativePath())
                            .contentType(file.getContentType())
                            .fileSize(stored.size())
                            .build());
                }
            }
            proposalFileRepo.saveAll(proposalFiles);
        } catch (IOException | RuntimeException e) {
            // DB writes roll back automatically via @Transactional, but files already
            // written to disk for this request_id would not be - clean those up so a
            // failed submission doesn't leave orphaned files behind.
            fileStorageService.deleteRequestFolder(requestId);
            throw e;
        }

        return ProposalUploadResponse.builder()
                .requestId(requestId)
                .message("Proposal submitted successfully")
                .build();
    }

    public List<ProposalResponse> searchProposals(Integer stateId, String financialYear, String quarter,
                                                   Integer categoryId) {
        String normalizedQuarter = (quarter == null || quarter.isBlank()) ? null : quarter.trim().toUpperCase();
        if (normalizedQuarter != null && !VALID_QUARTERS.contains(normalizedQuarter)) {
            throw new GrantException("quarter must be one of Q1, Q2, Q3, Q4");
        }
        String normalizedFinancialYear = (financialYear == null || financialYear.isBlank())
                ? null : financialYear.trim();

        List<Proposal> proposals = proposalRepo.search(stateId, normalizedFinancialYear, normalizedQuarter, categoryId);
        if (proposals.isEmpty()) {
            return List.of();
        }
        List<Integer> proposalIds = proposals.stream().map(Proposal::getId).toList();

        Map<Integer, List<ProposalFile>> filesByProposal = proposalFileRepo.findByProposalIdIn(proposalIds)
                .stream()
                .collect(Collectors.groupingBy(ProposalFile::getProposalId));

        return proposals.stream().map(p -> {
            Map<String, List<String>> files = new LinkedHashMap<>();
            for (UploadHeading heading : UploadHeading.values()) {
                files.put(heading.getJsonKey(), new ArrayList<>());
            }
            for (ProposalFile pf : filesByProposal.getOrDefault(p.getId(), List.of())) {
                UploadHeading heading = UploadHeading.valueOf(pf.getHeading());
                files.get(heading.getJsonKey()).add(pf.getOriginalFileName());
            }
            ProposalStatus status = ProposalStatus.fromId(p.getStatus());
            return ProposalResponse.builder()
                    .requestId(p.getRequestId())
                    .stateId(p.getStateId())
                    .financialYear(p.getFinancialYear())
                    .quarter(p.getQuarter())
                    .categoryId(p.getCategoryId())
                    .statusId(status.getId())
                    .statusLabel(status.getLabel())
                    .files(files)
                    .build();
        }).toList();
    }

    private void validateFileNameAndExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            throw new GrantException("Uploaded file must have a file name");
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            throw new GrantException("File '" + name + "' has no file extension");
        }
        String extension = name.substring(dot + 1).toLowerCase();
        if (!FileStorageService.ALLOWED_EXTENSIONS.contains(extension)) {
            throw new GrantException("File '" + name + "' has an unsupported type. Allowed: "
                    + FileStorageService.ALLOWED_EXTENSIONS);
        }
    }

    // Format: NHA<6-digit number>, e.g. NHA042817. Retries on collision and re-checks
    // via findByRequestId before use; proposals.request_id also carries a DB UNIQUE
    // constraint as a backstop against a race between the check and the insert.
    // Bounded to avoid spinning forever as the keyspace (1,000,000 values) fills up.
    private String generateRequestId() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String requestId = String.format("NHA%06d", RANDOM.nextInt(REQUEST_ID_DIGITS));
            if (proposalRepo.findByRequestId(requestId).isEmpty()) {
                return requestId;
            }
        }
        throw new GrantException("Could not generate a unique request_id - please retry the submission");
    }
}
