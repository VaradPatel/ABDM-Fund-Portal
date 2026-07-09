package abdm_nha_grant_access.example.nha_grant.service;

import abdm_nha_grant_access.example.nha_grant.Exception.GrantException;
import abdm_nha_grant_access.example.nha_grant.dto.PfmsExpenditureSnapshot;
import abdm_nha_grant_access.example.nha_grant.dto.StateExpenditureSummary;
import abdm_nha_grant_access.example.nha_grant.entity.PfmsExpenditureReport;
import abdm_nha_grant_access.example.nha_grant.entity.PfmsExpenditureReportFile;
import abdm_nha_grant_access.example.nha_grant.entity.States;
import abdm_nha_grant_access.example.nha_grant.repository.IPfmsExpenditureReportFileRepo;
import abdm_nha_grant_access.example.nha_grant.repository.IPfmsExpenditureReportRepo;
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
public class PfmsExpenditureReportService {

    private static final Set<String> VALID_QUARTERS = Set.of("Q1", "Q2", "Q3", "Q4");

    // Known state-name spellings/aliases that show up in PFMS exports but don't match the
    // canonical name stored in the states master table. Keys and values are pre-normalized
    // (see normalize()). Extend this as new mismatches are discovered.
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

    // States/UTs this PFMS TSA Expenditure report is expected to cover, keyed by the
    // canonical spelling used in the states master table. The master table also holds
    // entries (e.g. "Chandigarh", "ALL") that this report type never reports on, so
    // completeness is checked against this explicit list rather than every master row.
    private static final List<String> PFMS_TRACKED_STATES = List.of(
            "Arunachal Pradesh", "Tripura", "Assam", "Manipur", "Nagaland", "Meghalaya", "Mizoram", "Sikkim",
            "Andhra Pradesh", "Telangana", "Himachal Pradesh", "Madhya Pradesh", "Tamil Nadu", "Goa", "Haryana",
            "Jammu And Kashmir", "Chhattisgarh", "Kerala", "Uttarakhand", "Maharashtra", "West Bengal", "Gujarat",
            "Puducherry", "Karnataka", "Punjab", "Jharkhand", "Rajasthan", "Bihar", "Uttar Pradesh", "Delhi",
            "Odisha", "Andaman and Nicobar", "DNH & DD", "Ladakh", "Lakshadweep"
    );

    @Autowired
    private IPfmsExpenditureReportRepo pfmsExpenditureReportRepo;

    @Autowired
    private IPfmsExpenditureReportFileRepo pfmsExpenditureReportFileRepo;

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

        PfmsExpenditureReportFile fileEntity = pfmsExpenditureReportFileRepo
                .findByReportDateAndFinancialYearAndQuarter(reportDate, financialYear.trim(), normalizedQuarter)
                .orElseGet(PfmsExpenditureReportFile::new);
        fileEntity.setReportDate(reportDate);
        fileEntity.setFinancialYear(financialYear.trim());
        fileEntity.setQuarter(normalizedQuarter);
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setFileData(fileBytes);
        fileEntity.setUpdatedAt(LocalDateTime.now());
        pfmsExpenditureReportFileRepo.save(fileEntity);

        List<PfmsExpenditureReport> saved = new ArrayList<>();
        for (ParsedRow pr : parsedRows) {
            PfmsExpenditureReport entity = pfmsExpenditureReportRepo
                    .findByReportDateAndStateId(reportDate, pr.state.getId())
                    .orElseGet(PfmsExpenditureReport::new);
            entity.setReportDate(reportDate);
            entity.setFinancialYear(financialYear.trim());
            entity.setQuarter(normalizedQuarter);
            entity.setStateId(pr.state.getId());
            entity.setStateName(pr.state.getName());
            entity.setTotalFundReleased(pr.fund);
            entity.setTotalSuccessExpenditure(pr.expenditure);
            entity.setBalance(pr.balance);
            saved.add(pfmsExpenditureReportRepo.save(entity));
        }
        return new UploadResult(saved, fuzzyMatchNotes);
    }

    public List<StateExpenditureSummary> getSummary(List<Integer> stateIds, LocalDate startDate, LocalDate endDate,
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

        List<PfmsExpenditureReport> reports = filterByFinancialYearAndQuarter(
                pfmsExpenditureReportRepo.findByStateIdInAndReportDateBetween(stateIds, startDate, effectiveEndDate),
                financialYear, quarter);

        Map<Integer, Map<LocalDate, PfmsExpenditureReport>> byStateAndDate = groupByStateAndDate(reports);
        Map<Integer, States> statesById = statesRepository.findAllById(stateIds).stream()
                .collect(Collectors.toMap(States::getId, s -> s));

        List<LocalDate> dateRange = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(effectiveEndDate); date = date.plusDays(1)) {
            dateRange.add(date);
        }

        List<StateExpenditureSummary> summaries = new ArrayList<>();
        for (Integer stateId : stateIds) {
            Map<LocalDate, PfmsExpenditureReport> dateMap = byStateAndDate.getOrDefault(stateId, Map.of());
            summaries.add(StateExpenditureSummary.builder()
                    .stateId(stateId)
                    .stateName(resolveStateName(stateId, statesById, dateMap))
                    .data(buildDailyData(dateRange, dateMap))
                    .build());
        }
        return summaries;
    }

    public List<StateExpenditureSummary> getLatestTwoDaysSummary(List<Integer> stateIds, boolean sumStates,
                                                                   String financialYear, String quarter) {
        if (stateIds == null || stateIds.isEmpty()) {
            throw new GrantException("states is required");
        }

        stateIds = resolveStateIds(stateIds);

        List<LocalDate> latestDates = pfmsExpenditureReportRepo
                .findDistinctReportDatesByStateIdInOrderByReportDateDesc(stateIds)
                .stream()
                .limit(2)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (latestDates.isEmpty()) {
            return List.of();
        }

        List<PfmsExpenditureReport> reports = filterByFinancialYearAndQuarter(
                pfmsExpenditureReportRepo.findByStateIdInAndReportDateIn(stateIds, latestDates),
                financialYear, quarter);

        Map<Integer, Map<LocalDate, PfmsExpenditureReport>> byStateAndDate = groupByStateAndDate(reports);

        if (sumStates) {
            List<PfmsExpenditureSnapshot> summed = new ArrayList<>();
            for (LocalDate date : latestDates) {
                BigDecimal fund = BigDecimal.ZERO;
                BigDecimal expenditure = BigDecimal.ZERO;
                BigDecimal balance = BigDecimal.ZERO;
                for (Integer stateId : stateIds) {
                    PfmsExpenditureReport report = byStateAndDate.getOrDefault(stateId, Map.of()).get(date);
                    if (report != null) {
                        fund = fund.add(report.getTotalFundReleased());
                        expenditure = expenditure.add(report.getTotalSuccessExpenditure());
                        balance = balance.add(report.getBalance());
                    }
                }
                summed.add(PfmsExpenditureSnapshot.builder()
                        .reportDate(date)
                        .totalFundReleased(fund)
                        .totalSuccessExpenditure(expenditure)
                        .balance(balance)
                        .build());
            }
            return List.of(StateExpenditureSummary.builder()
                    .stateId(null)
                    .stateName("ALL")
                    .data(summed)
                    .build());
        }

        Map<Integer, States> statesById = statesRepository.findAllById(stateIds).stream()
                .collect(Collectors.toMap(States::getId, s -> s));

        List<StateExpenditureSummary> summaries = new ArrayList<>();
        for (Integer stateId : stateIds) {
            Map<LocalDate, PfmsExpenditureReport> dateMap = byStateAndDate.getOrDefault(stateId, Map.of());
            summaries.add(StateExpenditureSummary.builder()
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

    private List<PfmsExpenditureReport> filterByFinancialYearAndQuarter(List<PfmsExpenditureReport> reports,
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

    private Map<Integer, Map<LocalDate, PfmsExpenditureReport>> groupByStateAndDate(List<PfmsExpenditureReport> reports) {
        return reports.stream()
                .collect(Collectors.groupingBy(PfmsExpenditureReport::getStateId,
                        Collectors.toMap(PfmsExpenditureReport::getReportDate, r -> r, (a, b) -> a)));
    }

    private String resolveStateName(Integer stateId, Map<Integer, States> statesById,
                                     Map<LocalDate, PfmsExpenditureReport> dateMap) {
        States state = statesById.get(stateId);
        if (state != null) {
            return state.getName();
        }
        return dateMap.values().stream()
                .filter(Objects::nonNull)
                .map(PfmsExpenditureReport::getStateName)
                .findFirst()
                .orElse(null);
    }

    private List<PfmsExpenditureSnapshot> buildDailyData(List<LocalDate> dates,
                                                           Map<LocalDate, PfmsExpenditureReport> dateMap) {
        List<PfmsExpenditureSnapshot> data = new ArrayList<>();
        for (LocalDate date : dates) {
            PfmsExpenditureReport report = dateMap.get(date);
            data.add(report != null ? toSnapshot(report) : PfmsExpenditureSnapshot.builder().reportDate(date).build());
        }
        return data;
    }

    private PfmsExpenditureSnapshot toSnapshot(PfmsExpenditureReport report) {
        if (report == null) {
            return null;
        }
        return PfmsExpenditureSnapshot.builder()
                .reportDate(report.getReportDate())
                .totalFundReleased(report.getTotalFundReleased())
                .totalSuccessExpenditure(report.getTotalSuccessExpenditure())
                .balance(report.getBalance())
                .build();
    }

    private List<ParsedRow> parseAndValidate(Workbook workbook, Map<String, States> statesByNormalizedName,
                                              List<States> masterStates, List<String> fuzzyMatchNotes) {
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();

        int headerRow = -1, colState = -1, colFund = -1, colExpenditure = -1, colBalance = -1;
        int maxScanRows = Math.min(sheet.getLastRowNum(), 30);
        for (int r = 0; r <= maxScanRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            int cState = -1, cFund = -1, cExp = -1, cBal = -1;
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                if (c < 0) continue;
                String norm = normalize(readText(row, c, formatter));
                if (norm.equals("state")) cState = c;
                else if (norm.contains("fund") && norm.contains("releas")) cFund = c;
                else if (norm.contains("expenditure")) cExp = c;
                else if (norm.contains("balance")) cBal = c;
            }
            if (cState >= 0 && cFund >= 0 && cExp >= 0 && cBal >= 0) {
                headerRow = r;
                colState = cState;
                colFund = cFund;
                colExpenditure = cExp;
                colBalance = cBal;
                break;
            }
        }

        if (headerRow == -1) {
            throw new GrantException(missingColumnsMessage(sheet, formatter));
        }

        List<String> missingValueIssues = new ArrayList<>();
        List<String> stateNotFoundIssues = new ArrayList<>();
        List<ParsedRow> parsedRows = new ArrayList<>();
        Set<States> matchedStates = new HashSet<>();

        for (int r = headerRow + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            String stateName = readText(row, colState, formatter);
            if (stateName.isEmpty() || normalize(stateName).contains("total")) {
                continue;
            }

            BigDecimal fund = readAmount(row, colFund);
            BigDecimal exp = readAmount(row, colExpenditure);
            BigDecimal bal = readAmount(row, colBalance);

            List<String> rowMissing = new ArrayList<>();
            if (fund == null) rowMissing.add("Total Fund- Released");
            if (exp == null) rowMissing.add("Total Success Expenditure by State");
            if (bal == null) rowMissing.add("Balance");
            if (!rowMissing.isEmpty()) {
                missingValueIssues.add("Row " + (r + 1) + " (" + stateName + "): " + summarize(rowMissing, 3));
                continue;
            }

            States matched = resolveState(stateName, statesByNormalizedName, masterStates, r + 1, fuzzyMatchNotes);
            if (matched == null) {
                stateNotFoundIssues.add("Row " + (r + 1) + ": '" + stateName + "'");
                continue;
            }

            matchedStates.add(matched);
            parsedRows.add(new ParsedRow(matched, fund, exp, bal));
        }

        Set<String> matchedNormalizedNames = matchedStates.stream()
                .map(s -> normalize(s.getName()))
                .collect(Collectors.toSet());
        List<String> missingStates = PFMS_TRACKED_STATES.stream()
                .filter(name -> !matchedNormalizedNames.contains(normalize(name)))
                .collect(Collectors.toList());

        if (parsedRows.isEmpty() && missingValueIssues.isEmpty() && stateNotFoundIssues.isEmpty()
                && missingStates.isEmpty()) {
            throw new GrantException("No valid state data rows found in the uploaded excel");
        }

        if (!missingValueIssues.isEmpty() || !stateNotFoundIssues.isEmpty() || !missingStates.isEmpty()) {
            throw new GrantException(buildValidationMessage(missingValueIssues, stateNotFoundIssues, missingStates));
        }

        return parsedRows;
    }

    // Keeps the thrown error short: instead of one line per bad row (which can balloon to an
    // unreadable wall of text on a large file), groups issues by kind and shows only a few
    // examples with a count of the rest.
    private String buildValidationMessage(List<String> missingValueIssues, List<String> stateNotFoundIssues,
                                           List<String> missingStates) {
        List<String> parts = new ArrayList<>();
        if (!missingValueIssues.isEmpty()) {
            parts.add(missingValueIssues.size() + " row(s) have missing/invalid values, e.g. "
                    + summarize(missingValueIssues, 3));
        }
        if (!stateNotFoundIssues.isEmpty()) {
            parts.add(stateNotFoundIssues.size() + " row(s) have a state not found in the states list, e.g. "
                    + summarize(stateNotFoundIssues, 3));
        }
        if (!missingStates.isEmpty()) {
            parts.add("Missing data for " + missingStates.size() + " state(s): " + summarize(missingStates, 5));
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
        boolean foundState = false, foundFund = false, foundExp = false, foundBal = false;
        for (Row row : sheet) {
            if (row == null) continue;
            for (Cell cell : row) {
                String norm = normalize(readText(row, cell.getColumnIndex(), formatter));
                if (norm.equals("state")) foundState = true;
                else if (norm.contains("fund") && norm.contains("releas")) foundFund = true;
                else if (norm.contains("expenditure")) foundExp = true;
                else if (norm.contains("balance")) foundBal = true;
            }
        }
        List<String> missingCols = new ArrayList<>();
        if (!foundState) missingCols.add("State");
        if (!foundFund) missingCols.add("Total Fund- Released");
        if (!foundExp) missingCols.add("Total Success Expenditure by State");
        if (!foundBal) missingCols.add("Balance");
        if (missingCols.isEmpty()) {
            return "Could not locate a single header row containing all required columns: State, Total Fund- Released, Total Success Expenditure by State, Balance";
        }
        return "Uploaded excel is missing required column(s): " + String.join(", ", missingCols);
    }

    private String readText(Row row, int colIndex, DataFormatter formatter) {
        if (row == null || colIndex < 0) return "";
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    private BigDecimal readAmount(Row row, int colIndex) {
        if (row == null || colIndex < 0) return null;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case FORMULA:
                try {
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return null;
                }
            case STRING:
                String raw = cell.getStringCellValue().trim().replace(",", "");
                if (raw.isEmpty()) return null;
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
        final BigDecimal fund;
        final BigDecimal expenditure;
        final BigDecimal balance;

        ParsedRow(States state, BigDecimal fund, BigDecimal expenditure, BigDecimal balance) {
            this.state = state;
            this.fund = fund;
            this.expenditure = expenditure;
            this.balance = balance;
        }
    }

    public static class UploadResult {
        private final List<PfmsExpenditureReport> saved;
        private final List<String> fuzzyMatchNotes;

        public UploadResult(List<PfmsExpenditureReport> saved, List<String> fuzzyMatchNotes) {
            this.saved = saved;
            this.fuzzyMatchNotes = fuzzyMatchNotes;
        }

        public List<PfmsExpenditureReport> getSaved() {
            return saved;
        }

        public List<String> getFuzzyMatchNotes() {
            return fuzzyMatchNotes;
        }
    }
}
