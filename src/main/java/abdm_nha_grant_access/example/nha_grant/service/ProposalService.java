package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalFileInfo;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalResponse;
import abdm_nha_grant_access.example.nha_grant.dto.ProposalUploadResponse;
import abdm_nha_grant_access.example.nha_grant.dto.WorkflowHistoryEntry;
import abdm_nha_grant_access.example.nha_grant.entity.Proposal;
import abdm_nha_grant_access.example.nha_grant.entity.ProposalFile;
import abdm_nha_grant_access.example.nha_grant.entity.ProposalWorkflowHistory;
import abdm_nha_grant_access.example.nha_grant.entity.User;
import abdm_nha_grant_access.example.nha_grant.enums.ProposalAction;
import abdm_nha_grant_access.example.nha_grant.enums.ProposalCategory;
import abdm_nha_grant_access.example.nha_grant.enums.ProposalStatus;
import abdm_nha_grant_access.example.nha_grant.enums.UploadHeading;
import abdm_nha_grant_access.example.nha_grant.enums.WorkflowAction;
import abdm_nha_grant_access.example.nha_grant.repository.IProposalFileRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IProposalRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IProposalWorkflowHistoryRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IstatesRepository;
import abdm_nha_grant_access.example.nha_grant.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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

    @Autowired
    private IProposalWorkflowHistoryRepo workflowHistoryRepo;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Transactional
    public ProposalUploadResponse submitProposal(Integer stateId, String financialYear, String quarter,
                                                  Integer categoryId, BigDecimal amountRequested,
                                                  List<MultipartFile> ucFiles,
                                                  List<MultipartFile> reconcileFiles,
                                                  List<MultipartFile> fundAllocationFiles,
                                                  List<MultipartFile> unspendBalanceFiles,
                                                  List<MultipartFile> othersFiles,
                                                  String remarks,
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

        if (amountRequested == null) {
            throw new GrantException("amount_requested is required");
        }
        if (amountRequested.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GrantException("amount_requested must be greater than 0");
        }

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

        String normalizedRemarks = (remarks == null || remarks.isBlank()) ? null : remarks.trim();

        // A freshly submitted proposal starts out awaiting the NHA state coordinator's
        // review; PENDING_AT_STATE only applies later, once a query is raised back to the state.
        Proposal proposal = Proposal.builder()
                .requestId(requestId)
                .stateId(stateId)
                .financialYear(financialYear.trim())
                .quarter(normalizedQuarter)
                .categoryId(category.getId())
                .amountRequested(amountRequested)
                .userId(user.getId())
                .status(ProposalStatus.PENDING_AT_NHA_STATE_COORD.getId())
                .remarks(normalizedRemarks)
                .build();
        try {
            proposal = proposalRepo.save(proposal);
        } catch (DataIntegrityViolationException e) {
            // The findByRequestId check in generateRequestId() and this insert are not
            // atomic, so two concurrent submissions can rarely pick the same request_id;
            // the DB UNIQUE constraint is the real backstop - translate its violation into
            // a clean, retryable error instead of a raw 500.
            throw new GrantException("Could not generate a unique request_id - please retry the submission");
        }
        logHistory(proposal.getId(), user.getId(), WorkflowAction.SUBMITTED, normalizedRemarks, proposal.getStatus());

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
                                                   Integer categoryId, Integer status) {
        String normalizedQuarter = (quarter == null || quarter.isBlank()) ? null : quarter.trim().toUpperCase();
        if (normalizedQuarter != null && !VALID_QUARTERS.contains(normalizedQuarter)) {
            throw new GrantException("quarter must be one of Q1, Q2, Q3, Q4");
        }
        String normalizedFinancialYear = (financialYear == null || financialYear.isBlank())
                ? null : financialYear.trim();
        if (status != null) {
            ProposalStatus.fromId(status);
        }
        // state_id=0 is the "all states" sentinel - treat it the same as omitting the filter.
        Integer normalizedStateId = (stateId != null && stateId == 0) ? null : stateId;

        List<Proposal> proposals =
                proposalRepo.search(normalizedStateId, normalizedFinancialYear, normalizedQuarter, categoryId, status);
        if (proposals.isEmpty()) {
            return List.of();
        }
        List<Integer> proposalIds = proposals.stream().map(Proposal::getId).toList();

        Map<Integer, List<ProposalFile>> filesByProposal = proposalFileRepo.findByProposalIdIn(proposalIds)
                .stream()
                .collect(Collectors.groupingBy(ProposalFile::getProposalId));

        return proposals.stream()
                .map(p -> toProposalResponse(p, filesByProposal.getOrDefault(p.getId(), List.of())))
                .toList();
    }

    // The NHA State Coord's response to a proposal that is pending their review: either
    // accept it outright, or raise a query (with remarks) that bounces it back to the
    // state bucket for edit-and-resubmit via editProposal(...) below.
    @Transactional
    public ProposalResponse actionOnProposal(String requestId, String actionStr, String remarks,
                                              String actingUserEmail) {
        if (requestId == null || requestId.isBlank()) {
            throw new GrantException("requestId is required");
        }
        Proposal proposal = proposalRepo.findByRequestId(requestId.trim())
                .orElseThrow(() -> new GrantException("Proposal not found for request_id " + requestId));

        if (!proposal.getStatus().equals(ProposalStatus.PENDING_AT_NHA_STATE_COORD.getId())) {
            throw new GrantException("Action can only be taken while the proposal is pending at NHA State Coord");
        }

        ProposalAction action = ProposalAction.fromString(actionStr);
        if (action.isRemarksRequired() && (remarks == null || remarks.isBlank())) {
            throw new GrantException("remarks is required when raising a query");
        }

        User actingUser = userRepo.findByEmail(actingUserEmail)
                .orElseThrow(() -> new GrantException("Authenticated user not found for email " + actingUserEmail));

        proposal.setStatus(action.getResultingStatus().getId());
        proposal.setRemarks(action.isRemarksRequired() ? remarks.trim() : null);
        proposal = proposalRepo.save(proposal);

        WorkflowAction historyAction = action == ProposalAction.ACCEPT
                ? WorkflowAction.ACCEPTED : WorkflowAction.QUERY_RAISED;
        logHistory(proposal.getId(), actingUser.getId(), historyAction, proposal.getRemarks(), proposal.getStatus());

        return toProposalResponse(proposal, proposalFileRepo.findByProposalId(proposal.getId()));
    }

    // Lets the state edit a proposal that a query was raised against (status=PENDING_AT_STATE),
    // optionally updating quarter/category. All previously uploaded files for the proposal are
    // discarded and replaced with whatever files are attached to this call - this is a full
    // replace, not an append - then the proposal is resubmitted by moving it back to
    // PENDING_AT_NHA_STATE_COORD, where the coord can accept it or raise another query.
    @Transactional
    public ProposalResponse editProposal(String requestId, String quarter, Integer categoryId,
                                          BigDecimal amountRequested,
                                          List<MultipartFile> ucFiles,
                                          List<MultipartFile> reconcileFiles,
                                          List<MultipartFile> fundAllocationFiles,
                                          List<MultipartFile> unspendBalanceFiles,
                                          List<MultipartFile> othersFiles,
                                          String remarks,
                                          String actingUserEmail) throws IOException {
        if (requestId == null || requestId.isBlank()) {
            throw new GrantException("requestId is required");
        }
        Proposal proposal = proposalRepo.findByRequestId(requestId.trim())
                .orElseThrow(() -> new GrantException("Proposal not found for request_id " + requestId));

        if (!proposal.getStatus().equals(ProposalStatus.PENDING_AT_STATE.getId())) {
            throw new GrantException("Proposal can only be edited while a query is pending at state");
        }

        User actingUser = userRepo.findByEmail(actingUserEmail)
                .orElseThrow(() -> new GrantException("Authenticated user not found for email " + actingUserEmail));

        if (quarter != null && !quarter.isBlank()) {
            String normalizedQuarter = quarter.trim().toUpperCase();
            if (!VALID_QUARTERS.contains(normalizedQuarter)) {
                throw new GrantException("quarter must be one of Q1, Q2, Q3, Q4");
            }
            proposal.setQuarter(normalizedQuarter);
        }
        if (categoryId != null) {
            proposal.setCategoryId(ProposalCategory.fromId(categoryId).getId());
        }
        if (amountRequested != null) {
            if (amountRequested.compareTo(BigDecimal.ZERO) <= 0) {
                throw new GrantException("amount_requested must be greater than 0");
            }
            proposal.setAmountRequested(amountRequested);
        }

        // The state's reply to the coord's query, if they choose to leave one; replaces the
        // coord's query text since that query is now being addressed by this resubmission.
        String normalizedRemarks = (remarks == null || remarks.isBlank()) ? null : remarks.trim();
        proposal.setRemarks(normalizedRemarks);

        Map<UploadHeading, List<MultipartFile>> filesByHeading = new LinkedHashMap<>();
        filesByHeading.put(UploadHeading.UC, ucFiles);
        filesByHeading.put(UploadHeading.RECONCILE, reconcileFiles);
        filesByHeading.put(UploadHeading.FUND_ALLOCATION, fundAllocationFiles);
        filesByHeading.put(UploadHeading.UNSPEND_BALANCE, unspendBalanceFiles);
        filesByHeading.put(UploadHeading.OTHERS, othersFiles);

        // Fail fast on any bad new file before deleting the old ones or touching the DB.
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

        // Full replace: wipe every previously uploaded file for this proposal (disk + DB rows)
        // before storing whatever was attached to this edit call.
        fileStorageService.deleteRequestFolder(proposal.getRequestId());
        proposalFileRepo.deleteByProposalId(proposal.getId());

        proposal.setStatus(ProposalStatus.PENDING_AT_NHA_STATE_COORD.getId());
        proposal = proposalRepo.save(proposal);

        List<ProposalFile> newFiles = new ArrayList<>();
        for (Map.Entry<UploadHeading, List<MultipartFile>> entry : filesByHeading.entrySet()) {
            List<MultipartFile> files = entry.getValue();
            if (files == null) {
                continue;
            }
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                FileStorageService.StoredFile stored =
                        fileStorageService.store(proposal.getRequestId(), entry.getKey(), file);
                newFiles.add(ProposalFile.builder()
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
        proposalFileRepo.saveAll(newFiles);

        logHistory(proposal.getId(), actingUser.getId(), WorkflowAction.EDITED_RESUBMITTED, normalizedRemarks,
                proposal.getStatus());

        return toProposalResponse(proposal, proposalFileRepo.findByProposalId(proposal.getId()));
    }

    public ProposalFile getProposalFile(Integer fileId) {
        return proposalFileRepo.findById(fileId)
                .orElseThrow(() -> new GrantException("File not found: " + fileId));
    }

    public List<WorkflowHistoryEntry> getWorkflowHistory(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new GrantException("requestId is required");
        }
        Proposal proposal = proposalRepo.findByRequestId(requestId.trim())
                .orElseThrow(() -> new GrantException("Proposal not found for request_id " + requestId));

        List<ProposalWorkflowHistory> history =
                workflowHistoryRepo.findByProposalIdOrderByCreatedAtAsc(proposal.getId());
        if (history.isEmpty()) {
            return List.of();
        }

        List<Integer> userIds = history.stream().map(ProposalWorkflowHistory::getUserId).distinct().toList();
        Map<Integer, String> nameByUserId = userRepo.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        return history.stream().map(h -> {
            WorkflowAction action = WorkflowAction.valueOf(h.getAction());
            ProposalStatus status = ProposalStatus.fromId(h.getStatusAfter());
            return WorkflowHistoryEntry.builder()
                    .action(action.name())
                    .actionLabel(action.getLabel())
                    .userId(h.getUserId())
                    .userName(nameByUserId.get(h.getUserId()))
                    .remarks(h.getRemarks())
                    .statusAfter(status.getId())
                    .statusAfterLabel(status.getLabel())
                    .createdAt(h.getCreatedAt())
                    .build();
        }).toList();
    }

    private void logHistory(Integer proposalId, Integer userId, WorkflowAction action, String remarks,
                             Integer statusAfter) {
        workflowHistoryRepo.save(ProposalWorkflowHistory.builder()
                .proposalId(proposalId)
                .userId(userId)
                .action(action.name())
                .remarks(remarks)
                .statusAfter(statusAfter)
                .build());
    }

    private ProposalResponse toProposalResponse(Proposal p, List<ProposalFile> files) {
        Map<String, List<ProposalFileInfo>> filesByHeading = new LinkedHashMap<>();
        for (UploadHeading heading : UploadHeading.values()) {
            filesByHeading.put(heading.getJsonKey(), new ArrayList<>());
        }
        for (ProposalFile pf : files) {
            UploadHeading heading = UploadHeading.valueOf(pf.getHeading());
            filesByHeading.get(heading.getJsonKey()).add(ProposalFileInfo.builder()
                    .id(pf.getId())
                    .fileName(pf.getOriginalFileName())
                    .downloadUrl(contextPath + "/proposal/download/" + pf.getId())
                    .build());
        }
        ProposalStatus status = ProposalStatus.fromId(p.getStatus());
        return ProposalResponse.builder()
                .requestId(p.getRequestId())
                .stateId(p.getStateId())
                .financialYear(p.getFinancialYear())
                .quarter(p.getQuarter())
                .categoryId(p.getCategoryId())
                .amountRequested(p.getAmountRequested())
                .statusId(status.getId())
                .statusLabel(status.getLabel())
                .remarks(p.getRemarks())
                .files(filesByHeading)
                .build();
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
