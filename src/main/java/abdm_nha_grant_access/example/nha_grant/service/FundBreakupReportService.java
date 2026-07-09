package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.FundBreakupSnapshot;
import abdm_nha_grant_access.example.nha_grant.dto.StateFundBreakupSummary;
import abdm_nha_grant_access.example.nha_grant.entity.FundBreakupReport;
import abdm_nha_grant_access.example.nha_grant.entity.FundBreakupReportFile;
import abdm_nha_grant_access.example.nha_grant.entity.States;
import abdm_nha_grant_access.example.nha_grant.repository.IFundBreakupReportFileRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IFundBreakupReportRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IstatesRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FundBreakupReportService {

    private static final Set<String> VALID_QUARTERS = Set.of("Q1", "Q2", "Q3", "Q4");

    // Known state-name spellings/aliases that show up in ABDM fund breakup exports but don't
    // match the canonical name stored in the states master table. Keys and values are
    // pre-normalized (see normalize()). Extend this as new mismatches are discovered.
    private static final Map<String, String> STATE_NAME_ALIASES = Map.ofEntries(
            Map.entry("harayana", "haryana"),
            Map.entry("orissa", "odisha"),
            Map.entry("pondicherry", "puducherry"),
            Map.entry("uttaranchal", "uttarakhand"),
            Map.entry("nct of delhi", "delhi"),
            Map.entry("new delhi", "delhi"),
            Map.entry("lakshwadweep", "lakshadweep"),
            Map.entry("jammu & kashmir", "jammu and kashmir"),
            Map.entry("andaman & nicobar islands", "andaman and nicobar"),
            Map.entry("andaman and nicobar islands", "andaman and nicobar"),
            Map.entry("dadra nagar haveli & daman & diu", "dnh & dd")
    );

    @Autowired
    private IFundBreakupReportRepo fundBreakupReportRepo;

    @Autowired
    private IFundBreakupReportFileRepo fundBreakupReportFileRepo;

    @Autowired
    private IstatesRepository statesRepository;

    @Transactional
    public UploadResult uploadReport(MultipartFile file, LocalDate reportDate,
                                      String financialYear, String quarter) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new GrantException("file is required");
        }
        if (reportDate == null) {
            throw new GrantException("date is required");
        }
        if (financialYear == null || financialYear.isBlank()) {
            throw new GrantException("financialYear is required");
        }
        String normalizedQuarter = quarter == null ? "" : quarter.trim().toUpperCase();
        if (!VALID_QUARTERS.contains(normalizedQuarter)) {
            throw new GrantException("quarter is required and must be one of Q1, Q2, Q3, Q4");
        }

        List<States> masterStates = statesRepository.findAll();
        Map<String, States> statesByNormalizedName = masterStates.stream()
                .filter(s -> s.getName() != null)
                .collect(Collectors.toMap(s -> normalize(s.getName()), s -> s, (a, b) -> a));

        byte[] fileBytes = file.getBytes();

        List<ParsedRow> parsedRows;
        List<String> fuzzyMatchNotes = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(fileBytes); Workbook workbook = WorkbookFactory.create(is)) {
            parsedRows = parseAndValidate(workbook, statesByNormalizedName, masterStates, fuzzyMatchNotes);
        }

        FundBreakupReportFile fileEntity = fundBreakupReportFileRepo
                .findByReportDateAndFinancialYearAndQuarter(reportDate, financialYear.trim(), normalizedQuarter)
                .orElseGet(FundBreakupReportFile::new);
        fileEntity.setReportDate(reportDate);
        fileEntity.setFinancialYear(financialYear.trim());
        fileEntity.setQuarter(normalizedQuarter);
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setFileData(fileBytes);
        fileEntity.setUpdatedAt(LocalDateTime.now());
        fundBreakupReportFileRepo.save(fileEntity);

        List<FundBreakupReport> saved = new ArrayList<>();
        for (ParsedRow pr : parsedRows) {
            FundBreakupReport entity = fundBreakupReportRepo
                    .findByReportDateAndStateId(reportDate, pr.state.getId())
                    .orElseGet(FundBreakupReport::new);
            entity.setReportDate(reportDate);
            entity.setFinancialYear(financialYear.trim());
            entity.setQuarter(normalizedQuarter);
            entity.setStateId(pr.state.getId());
            entity.setStateName(pr.state.getName());
            entity.setTotalFundReleased(pr.totalFundReleased);
            entity.setTotalExpenditure(pr.totalExpenditure);
            entity.setTotalBalance(pr.totalBalance);
            entity.setHrFundReleased(pr.hrFundReleased);
            entity.setHrExpenditure(pr.hrExpenditure);
            entity.setHrBalance(pr.hrBalance);
            entity.setIecCbFundReleased(pr.iecCbFundReleased);
            entity.setIecCbExpenditure(pr.iecCbExpenditure);
            entity.setIecCbBalance(pr.iecCbBalance);
            entity.setAdminFundReleased(pr.adminFundReleased);
            entity.setAdminExpenditure(pr.adminExpenditure);
            entity.setAdminBalance(pr.adminBalance);
            saved.add(fundBreakupReportRepo.save(entity));
        }
        return new UploadResult(saved, fuzzyMatchNotes);
    }

    public List<StateFundBreakupSummary> getSummary(List<Integer> stateIds, LocalDate startDate, LocalDate endDate,
                                                      String financialYear, String quarter) {
        if (stateIds == null || stateIds.isEmpty()) {
            throw new GrantException("states is required");
        }
        if (startDate == null) {
            throw new GrantException("start_date is required");
        }
        LocalDate effectiveEndDate = endDate != null ? endDate : startDate;
        if (effectiveEndDate.isBefore(startDate)) {
            throw new GrantException("end_date must not be before start_date");
        }

        stateIds = resolveStateIds(stateIds);

        List<FundBreakupReport> reports = filterByFinancialYearAndQuarter(
                fundBreakupReportRepo.findByStateIdInAndReportDateBetween(stateIds, startDate, effectiveEndDate),
                financialYear, quarter);

        Map<Integer, Map<LocalDate, FundBreakupReport>> byStateAndDate = groupByStateAndDate(reports);
        Map<Integer, States> statesById = statesRepository.findAllById(stateIds).stream()
                .collect(Collectors.toMap(States::getId, s -> s));

        List<LocalDate> dateRange = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(effectiveEndDate); date = date.plusDays(1)) {
            dateRange.add(date);
        }

        List<StateFundBreakupSummary> summaries = new ArrayList<>();
        for (Integer stateId : stateIds) {
            Map<LocalDate, FundBreakupReport> dateMap = byStateAndDate.getOrDefault(stateId, Map.of());
            summaries.add(StateFundBreakupSummary.builder()
                    .stateId(stateId)
                    .stateName(resolveStateName(stateId, statesById, dateMap))
                    .data(buildDailyData(dateRange, dateMap))
                    .build());
        }
        return summaries;
    }

    public List<StateFundBreakupSummary> getLatestTwoDaysSummary(List<Integer> stateIds, boolean sumStates,
                                                                   String financialYear, String quarter) {
        if (stateIds == null || stateIds.isEmpty()) {
            throw new GrantException("states is required");
        }

        stateIds = resolveStateIds(stateIds);

        List<LocalDate> latestDates = fundBreakupReportRepo
                .findDistinctReportDatesByStateIdInOrderByReportDateDesc(stateIds)
                .stream()
                .limit(2)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (latestDates.isEmpty()) {
            return List.of();
        }

        List<FundBreakupReport> reports = filterByFinancialYearAndQuarter(
                fundBreakupReportRepo.findByStateIdInAndReportDateIn(stateIds, latestDates),
                financialYear, quarter);

        Map<Integer, Map<LocalDate, FundBreakupReport>> byStateAndDate = groupByStateAndDate(reports);

        if (sumStates) {
            List<FundBreakupSnapshot> summed = new ArrayList<>();
            for (LocalDate date : latestDates) {
                BigDecimal totalFundReleased = BigDecimal.ZERO;
                BigDecimal totalExpenditure = BigDecimal.ZERO;
                BigDecimal totalBalance = BigDecimal.ZERO;
                BigDecimal hrFundReleased = BigDecimal.ZERO;
                BigDecimal hrExpenditure = BigDecimal.ZERO;
                BigDecimal hrBalance = BigDecimal.ZERO;
                BigDecimal iecCbFundReleased = BigDecimal.ZERO;
                BigDecimal iecCbExpenditure = BigDecimal.ZERO;
                BigDecimal iecCbBalance = BigDecimal.ZERO;
                BigDecimal adminFundReleased = BigDecimal.ZERO;
                BigDecimal adminExpenditure = BigDecimal.ZERO;
                BigDecimal adminBalance = BigDecimal.ZERO;
                for (Integer stateId : stateIds) {
                    FundBreakupReport report = byStateAndDate.getOrDefault(stateId, Map.of()).get(date);
                    if (report != null) {
                        totalFundReleased = totalFundReleased.add(report.getTotalFundReleased());
                        totalExpenditure = totalExpenditure.add(report.getTotalExpenditure());
                        totalBalance = totalBalance.add(report.getTotalBalance());
                        hrFundReleased = hrFundReleased.add(report.getHrFundReleased());
                        hrExpenditure = hrExpenditure.add(report.getHrExpenditure());
                        hrBalance = hrBalance.add(report.getHrBalance());
                        iecCbFundReleased = iecCbFundReleased.add(report.getIecCbFundReleased());
                        iecCbExpenditure = iecCbExpenditure.add(report.getIecCbExpenditure());
                        iecCbBalance = iecCbBalance.add(report.getIecCbBalance());
                        adminFundReleased = adminFundReleased.add(report.getAdminFundReleased());
                        adminExpenditure = adminExpenditure.add(report.getAdminExpenditure());
                        adminBalance = adminBalance.add(report.getAdminBalance());
                    }
                }
                summed.add(FundBreakupSnapshot.builder()
                        .reportDate(date)
                        .totalFundReleased(totalFundReleased)
                        .totalExpenditure(totalExpenditure)
                        .totalBalance(totalBalance)
                        .hrFundReleased(hrFundReleased)
                        .hrExpenditure(hrExpenditure)
                        .hrBalance(hrBalance)
                        .iecCbFundReleased(iecCbFundReleased)
                        .iecCbExpenditure(iecCbExpenditure)
                        .iecCbBalance(iecCbBalance)
                        .adminFundReleased(adminFundReleased)
                        .adminExpenditure(adminExpenditure)
                        .adminBalance(adminBalance)
                        .build());
            }
            return List.of(StateFundBreakupSummary.builder()
                    .stateId(null)
                    .stateName("ALL")
                    .data(summed)
                    .build());
        }

        Map<Integer, States> statesById = statesRepository.findAllById(stateIds).stream()
                .collect(Collectors.toMap(States::getId, s -> s));

        List<StateFundBreakupSummary> summaries = new ArrayList<>();
        for (Integer stateId : stateIds) {
            Map<LocalDate, FundBreakupReport> dateMap = byStateAndDate.getOrDefault(stateId, Map.of());
            summaries.add(StateFundBreakupSummary.builder()
                    .stateId(stateId)
                    .stateName(resolveStateName(stateId, statesById, dateMap))
                    .data(buildDailyData(latestDates, dateMap))
                    .build());
        }
        return summaries;
    }

    // A states list containing 0 is a sentinel meaning "all states" (used by callers who
    // don't want to enumerate every state id, e.g. a UI-level "select all" option).
    private List<Integer> resolveStateIds(List<Integer> stateIds) {
        if (stateIds.contains(0)) {
            return statesRepository.findAll().stream()
                    .map(States::getId)
                    .collect(Collectors.toList());
        }
        return stateIds;
    }

    private List<FundBreakupReport> filterByFinancialYearAndQuarter(List<FundBreakupReport> reports,
                                                                       String financialYear, String quarter) {
        if (financialYear != null && !financialYear.isBlank()) {
            String normalizedFy = financialYear.trim();
            reports = reports.stream()
                    .filter(r -> normalizedFy.equalsIgnoreCase(r.getFinancialYear()))
                    .collect(Collectors.toList());
        }
        if (quarter != null && !quarter.isBlank()) {
            String normalizedQuarter = quarter.trim();
            reports = reports.stream()
                    .filter(r -> normalizedQuarter.equalsIgnoreCase(r.getQuarter()))
                    .collect(Collectors.toList());
        }
        return reports;
    }

    private Map<Integer, Map<LocalDate, FundBreakupReport>> groupByStateAndDate(List<FundBreakupReport> reports) {
        return reports.stream()
                .collect(Collectors.groupingBy(FundBreakupReport::getStateId,
                        Collectors.toMap(FundBreakupReport::getReportDate, r -> r, (a, b) -> a)));
    }

    private String resolveStateName(Integer stateId, Map<Integer, States> statesById,
                                     Map<LocalDate, FundBreakupReport> dateMap) {
        States state = statesById.get(stateId);
        if (state != null) {
            return state.getName();
        }
        return dateMap.values().stream()
                .filter(Objects::nonNull)
                .map(FundBreakupReport::getStateName)
                .findFirst()
                .orElse(null);
    }

    private List<FundBreakupSnapshot> buildDailyData(List<LocalDate> dates,
                                                        Map<LocalDate, FundBreakupReport> dateMap) {
        List<FundBreakupSnapshot> data = new ArrayList<>();
        for (LocalDate date : dates) {
            FundBreakupReport report = dateMap.get(date);
            data.add(report != null ? toSnapshot(report) : FundBreakupSnapshot.builder().reportDate(date).build());
        }
        return data;
    }

    private FundBreakupSnapshot toSnapshot(FundBreakupReport report) {
        if (report == null) {
            return null;
        }
        return FundBreakupSnapshot.builder()
                .reportDate(report.getReportDate())
                .totalFundReleased(report.getTotalFundReleased())
                .totalExpenditure(report.getTotalExpenditure())
                .totalBalance(report.getTotalBalance())
                .hrFundReleased(report.getHrFundReleased())
                .hrExpenditure(report.getHrExpenditure())
                .hrBalance(report.getHrBalance())
                .iecCbFundReleased(report.getIecCbFundReleased())
                .iecCbExpenditure(report.getIecCbExpenditure())
                .iecCbBalance(report.getIecCbBalance())
                .adminFundReleased(report.getAdminFundReleased())
                .adminExpenditure(report.getAdminExpenditure())
                .adminBalance(report.getAdminBalance())
                .build();
    }

    private List<ParsedRow> parseAndValidate(Workbook workbook, Map<String, States> statesByNormalizedName,
                                              List<States> masterStates, List<String> fuzzyMatchNotes) {
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();

        int headerRow = -1;
        int colState = -1;
        int colTotalReleased = -1, colTotalExpenditure = -1, colTotalBalance = -1;
        int colHrReleased = -1, colHrExpenditure = -1, colHrBalance = -1;
        int colIecReleased = -1, colIecExpenditure = -1, colIecBalance = -1;
        int colAdminReleased = -1, colAdminExpenditure = -1, colAdminBalance = -1;

        int maxScanRows = Math.min(sheet.getLastRowNum(), 30);
        for (int r = 0; r <= maxScanRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            int cState = -1;
            int cTotalReleased = -1, cTotalExpenditure = -1, cTotalBalance = -1;
            int cHrReleased = -1, cHrExpenditure = -1, cHrBalance = -1;
            int cIecReleased = -1, cIecExpenditure = -1, cIecBalance = -1;
            int cAdminReleased = -1, cAdminExpenditure = -1, cAdminBalance = -1;

            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                if (c < 0) continue;
                String norm = normalize(readText(row, c, formatter));
                if (norm.isEmpty()) continue;

                if (norm.equals("state")) {
                    cState = c;
                } else if (norm.contains("hr") && norm.contains("releas")) {
                    cHrReleased = c;
                } else if (norm.contains("hr") && norm.contains("expenditure")) {
                    cHrExpenditure = c;
                } else if (norm.contains("hr") && norm.contains("balance")) {
                    cHrBalance = c;
                } else if (norm.contains("iec") && norm.contains("releas")) {
                    cIecReleased = c;
                } else if (norm.contains("iec") && norm.contains("expenditure")) {
                    cIecExpenditure = c;
                } else if (norm.contains("iec") && norm.contains("balance")) {
                    cIecBalance = c;
                } else if (norm.contains("admin") && norm.contains("releas")) {
                    cAdminReleased = c;
                } else if (norm.contains("admin") && norm.contains("expenditure")) {
                    cAdminExpenditure = c;
                } else if (norm.contains("admin") && norm.contains("balance")) {
                    cAdminBalance = c;
                } else if (norm.contains("fund") && norm.contains("releas")) {
                    cTotalReleased = c;
                } else if (norm.contains("expenditure")) {
                    cTotalExpenditure = c;
                } else if (norm.equals("balance")) {
                    cTotalBalance = c;
                }
            }

            if (cState >= 0 && cTotalReleased >= 0 && cTotalExpenditure >= 0 && cTotalBalance >= 0
                    && cHrReleased >= 0 && cHrExpenditure >= 0 && cHrBalance >= 0
                    && cIecReleased >= 0 && cIecExpenditure >= 0 && cIecBalance >= 0
                    && cAdminReleased >= 0 && cAdminExpenditure >= 0 && cAdminBalance >= 0) {
                headerRow = r;
                colState = cState;
                colTotalReleased = cTotalReleased;
                colTotalExpenditure = cTotalExpenditure;
                colTotalBalance = cTotalBalance;
                colHrReleased = cHrReleased;
                colHrExpenditure = cHrExpenditure;
                colHrBalance = cHrBalance;
                colIecReleased = cIecReleased;
                colIecExpenditure = cIecExpenditure;
                colIecBalance = cIecBalance;
                colAdminReleased = cAdminReleased;
                colAdminExpenditure = cAdminExpenditure;
                colAdminBalance = cAdminBalance;
                break;
            }
        }

        if (headerRow == -1) {
            throw new GrantException(missingColumnsMessage(sheet, formatter));
        }

        List<String> missingValueIssues = new ArrayList<>();
        List<String> stateNotFoundIssues = new ArrayList<>();
        List<ParsedRow> parsedRows = new ArrayList<>();

        for (int r = headerRow + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            String stateName = readText(row, colState, formatter);
            if (stateName.isEmpty() || normalize(stateName).contains("total")) {
                continue;
            }

            BigDecimal totalFundReleased = readAmount(row, colTotalReleased);
            BigDecimal totalExpenditure = readAmount(row, colTotalExpenditure);
            BigDecimal totalBalance = readAmount(row, colTotalBalance);
            BigDecimal hrFundReleased = readAmount(row, colHrReleased);
            BigDecimal hrExpenditure = readAmount(row, colHrExpenditure);
            BigDecimal hrBalance = readAmount(row, colHrBalance);
            BigDecimal iecCbFundReleased = readAmount(row, colIecReleased);
            BigDecimal iecCbExpenditure = readAmount(row, colIecExpenditure);
            BigDecimal iecCbBalance = readAmount(row, colIecBalance);
            BigDecimal adminFundReleased = readAmount(row, colAdminReleased);
            BigDecimal adminExpenditure = readAmount(row, colAdminExpenditure);
            BigDecimal adminBalance = readAmount(row, colAdminBalance);

            List<String> rowMissing = new ArrayList<>();
            if (totalFundReleased == null) rowMissing.add("Total Fund- Released");
            if (totalExpenditure == null) rowMissing.add("Total Expenditure by State");
            if (totalBalance == null) rowMissing.add("Balance");
            if (hrFundReleased == null) rowMissing.add("HR Released");
            if (hrExpenditure == null) rowMissing.add("HR Expenditure");
            if (hrBalance == null) rowMissing.add("HR Balance");
            if (iecCbFundReleased == null) rowMissing.add("IEC-CB Released");
            if (iecCbExpenditure == null) rowMissing.add("IEC-CB Expenditure");
            if (iecCbBalance == null) rowMissing.add("IEC-CB Balance");
            if (adminFundReleased == null) rowMissing.add("Admin Released");
            if (adminExpenditure == null) rowMissing.add("Admin Expenditure");
            if (adminBalance == null) rowMissing.add("Admin Balance");
            if (!rowMissing.isEmpty()) {
                missingValueIssues.add("Row " + (r + 1) + " (" + stateName + "): " + summarize(rowMissing, 3));
                continue;
            }

            States matched = resolveState(stateName, statesByNormalizedName, masterStates, r + 1, fuzzyMatchNotes);
            if (matched == null) {
                stateNotFoundIssues.add("Row " + (r + 1) + ": '" + stateName + "'");
                continue;
            }

            parsedRows.add(new ParsedRow(matched, totalFundReleased, totalExpenditure, totalBalance,
                    hrFundReleased, hrExpenditure, hrBalance,
                    iecCbFundReleased, iecCbExpenditure, iecCbBalance,
                    adminFundReleased, adminExpenditure, adminBalance));
        }

        if (parsedRows.isEmpty() && missingValueIssues.isEmpty() && stateNotFoundIssues.isEmpty()) {
            throw new GrantException("No valid state data rows found in the uploaded excel");
        }

        if (!missingValueIssues.isEmpty() || !stateNotFoundIssues.isEmpty()) {
            throw new GrantException(buildValidationMessage(missingValueIssues, stateNotFoundIssues));
        }

        return parsedRows;
    }

    // Keeps the thrown error short: instead of one line per bad row (which can balloon to an
    // unreadable wall of text on a large file), groups issues by kind and shows only a few
    // examples with a count of the rest.
    private String buildValidationMessage(List<String> missingValueIssues, List<String> stateNotFoundIssues) {
        List<String> parts = new ArrayList<>();
        if (!missingValueIssues.isEmpty()) {
            parts.add(missingValueIssues.size() + " row(s) have missing/invalid values, e.g. "
                    + summarize(missingValueIssues, 3));
        }
        if (!stateNotFoundIssues.isEmpty()) {
            parts.add(stateNotFoundIssues.size() + " row(s) have a state not found in the states list, e.g. "
                    + summarize(stateNotFoundIssues, 3));
        }
        return String.join(". ", parts);
    }

    private String summarize(List<String> items, int limit) {
        if (items.size() <= limit) {
            return String.join("; ", items);
        }
        return String.join("; ", items.subList(0, limit)) + " and " + (items.size() - limit) + " more";
    }

    /**
     * Resolves an excel state name against the master states table, tolerating common
     * typos/aliases (e.g. "Harayana" -> "Haryana"). Tries, in order: exact normalized match,
     * known alias match, then a conservative edit-distance fuzzy match. Any non-exact match
     * is recorded in fuzzyMatchNotes so it stays visible to the caller instead of being silent.
     */
    private States resolveState(String excelStateName, Map<String, States> statesByNormalizedName,
                                 List<States> masterStates, int rowNumber, List<String> fuzzyMatchNotes) {
        String normalizedExcelName = normalize(excelStateName);

        States exact = statesByNormalizedName.get(normalizedExcelName);
        if (exact != null) {
            return exact;
        }

        String alias = STATE_NAME_ALIASES.get(normalizedExcelName);
        if (alias != null) {
            States aliased = statesByNormalizedName.get(alias);
            if (aliased != null) {
                fuzzyMatchNotes.add("Row " + rowNumber + ": matched '" + excelStateName + "' to '"
                        + aliased.getName() + "' (known alias)");
                return aliased;
            }
        }

        States best = null;
        int bestDistance = Integer.MAX_VALUE;
        boolean ambiguous = false;
        for (States candidate : masterStates) {
            if (candidate.getName() == null) continue;
            int distance = levenshteinDistance(normalizedExcelName, normalize(candidate.getName()));
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
                ambiguous = false;
            } else if (distance == bestDistance) {
                ambiguous = true;
            }
        }

        if (best != null && !ambiguous && bestDistance <= maxAllowedDistance(normalizedExcelName)) {
            fuzzyMatchNotes.add("Row " + rowNumber + ": matched '" + excelStateName + "' to '"
                    + best.getName() + "' (fuzzy match, edit distance " + bestDistance + ")");
            return best;
        }

        return null;
    }

    // Short names are more prone to accidental fuzzy collisions (e.g. "Goa"), so only
    // longer names are allowed a fuzzy match, and only within a small edit distance.
    private int maxAllowedDistance(String normalizedName) {
        int len = normalizedName.length();
        if (len <= 4) return 0;
        if (len <= 7) return 1;
        return 2;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private String missingColumnsMessage(Sheet sheet, DataFormatter formatter) {
        boolean foundState = false;
        boolean foundTotalReleased = false, foundTotalExpenditure = false, foundTotalBalance = false;
        boolean foundHrReleased = false, foundHrExpenditure = false, foundHrBalance = false;
        boolean foundIecReleased = false, foundIecExpenditure = false, foundIecBalance = false;
        boolean foundAdminReleased = false, foundAdminExpenditure = false, foundAdminBalance = false;

        for (Row row : sheet) {
            if (row == null) continue;
            for (Cell cell : row) {
                String norm = normalize(readText(row, cell.getColumnIndex(), formatter));
                if (norm.isEmpty()) continue;
                if (norm.equals("state")) foundState = true;
                else if (norm.contains("hr") && norm.contains("releas")) foundHrReleased = true;
                else if (norm.contains("hr") && norm.contains("expenditure")) foundHrExpenditure = true;
                else if (norm.contains("hr") && norm.contains("balance")) foundHrBalance = true;
                else if (norm.contains("iec") && norm.contains("releas")) foundIecReleased = true;
                else if (norm.contains("iec") && norm.contains("expenditure")) foundIecExpenditure = true;
                else if (norm.contains("iec") && norm.contains("balance")) foundIecBalance = true;
                else if (norm.contains("admin") && norm.contains("releas")) foundAdminReleased = true;
                else if (norm.contains("admin") && norm.contains("expenditure")) foundAdminExpenditure = true;
                else if (norm.contains("admin") && norm.contains("balance")) foundAdminBalance = true;
                else if (norm.contains("fund") && norm.contains("releas")) foundTotalReleased = true;
                else if (norm.contains("expenditure")) foundTotalExpenditure = true;
                else if (norm.equals("balance")) foundTotalBalance = true;
            }
        }
        List<String> missingCols = new ArrayList<>();
        if (!foundState) missingCols.add("State");
        if (!foundTotalReleased) missingCols.add("Total Fund- Released");
        if (!foundTotalExpenditure) missingCols.add("Total Expenditure by State");
        if (!foundTotalBalance) missingCols.add("Balance");
        if (!foundHrReleased) missingCols.add("HR Released");
        if (!foundHrExpenditure) missingCols.add("HR Expenditure");
        if (!foundHrBalance) missingCols.add("HR Balance");
        if (!foundIecReleased) missingCols.add("IEC-CB Released");
        if (!foundIecExpenditure) missingCols.add("IEC-CB Expenditure");
        if (!foundIecBalance) missingCols.add("IEC-CB Balance");
        if (!foundAdminReleased) missingCols.add("Admin Released");
        if (!foundAdminExpenditure) missingCols.add("Admin Expenditure");
        if (!foundAdminBalance) missingCols.add("Admin Balance");
        if (missingCols.isEmpty()) {
            return "Could not locate a single header row containing all required columns: State, Total Fund- Released, "
                    + "Total Expenditure by State, Balance, HR Released, HR Expenditure, HR Balance, IEC-CB Released, "
                    + "IEC-CB Expenditure, IEC-CB Balance, Admin Released, Admin Expenditure, Admin Balance";
        }
        return "Uploaded excel is missing required column(s): " + String.join(", ", missingCols);
    }

    private String readText(Row row, int colIndex, DataFormatter formatter) {
        if (row == null || colIndex < 0) return "";
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    // A blank cell (no cell, BLANK type, or empty string) means "nothing to report yet" and
    // is treated as 0, not a validation error - states routinely leave a head's expenditure
    // cell empty when nothing has been spent against it. Only genuinely unparseable content
    // (garbled text, a broken formula) is treated as invalid/missing.
    private BigDecimal readAmount(Row row, int colIndex) {
        if (row == null || colIndex < 0) return BigDecimal.ZERO;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case BLANK:
                return BigDecimal.ZERO;
            case FORMULA:
                try {
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return null;
                }
            case STRING:
                String raw = cell.getStringCellValue().trim().replace(",", "");
                if (raw.isEmpty()) return BigDecimal.ZERO;
                try {
                    return new BigDecimal(raw);
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String normalize(String s) {
        return s == null ? "" : s.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    private static class ParsedRow {
        final States state;
        final BigDecimal totalFundReleased;
        final BigDecimal totalExpenditure;
        final BigDecimal totalBalance;
        final BigDecimal hrFundReleased;
        final BigDecimal hrExpenditure;
        final BigDecimal hrBalance;
        final BigDecimal iecCbFundReleased;
        final BigDecimal iecCbExpenditure;
        final BigDecimal iecCbBalance;
        final BigDecimal adminFundReleased;
        final BigDecimal adminExpenditure;
        final BigDecimal adminBalance;

        ParsedRow(States state, BigDecimal totalFundReleased, BigDecimal totalExpenditure, BigDecimal totalBalance,
                  BigDecimal hrFundReleased, BigDecimal hrExpenditure, BigDecimal hrBalance,
                  BigDecimal iecCbFundReleased, BigDecimal iecCbExpenditure, BigDecimal iecCbBalance,
                  BigDecimal adminFundReleased, BigDecimal adminExpenditure, BigDecimal adminBalance) {
            this.state = state;
            this.totalFundReleased = totalFundReleased;
            this.totalExpenditure = totalExpenditure;
            this.totalBalance = totalBalance;
            this.hrFundReleased = hrFundReleased;
            this.hrExpenditure = hrExpenditure;
            this.hrBalance = hrBalance;
            this.iecCbFundReleased = iecCbFundReleased;
            this.iecCbExpenditure = iecCbExpenditure;
            this.iecCbBalance = iecCbBalance;
            this.adminFundReleased = adminFundReleased;
            this.adminExpenditure = adminExpenditure;
            this.adminBalance = adminBalance;
        }
    }

    public static class UploadResult {
        private final List<FundBreakupReport> saved;
        private final List<String> fuzzyMatchNotes;

        public UploadResult(List<FundBreakupReport> saved, List<String> fuzzyMatchNotes) {
            this.saved = saved;
            this.fuzzyMatchNotes = fuzzyMatchNotes;
        }

        public List<FundBreakupReport> getSaved() {
            return saved;
        }

        public List<String> getFuzzyMatchNotes() {
            return fuzzyMatchNotes;
        }
    }
}
