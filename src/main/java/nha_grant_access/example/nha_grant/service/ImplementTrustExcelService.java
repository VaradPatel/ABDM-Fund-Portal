package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ImplementTrustExcelService {

    public ByteArrayInputStream generateExcel(List<ImplementTrustCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Implement Trust");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (ImplementTrustCalc d : list) {

            // ---------- NON CALCULATED FIELDS (Description = blank) ----------

            rowIdx = createRow(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = createRow(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = createRow(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = createRow(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = createRow(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = createRow(sheet, rowIdx, "NHA Share in GIA", d.getNhaShareInGia(), "");
            rowIdx = createRow(sheet, rowIdx, "Total Population Covered (MOU)", d.getTotalPopulationCoveredAsPerMou(), "");
            rowIdx = createRow(sheet, rowIdx, "Eligible SECC Population", d.getEligibleSeccPopulation(), "");
            rowIdx = createRow(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");
            rowIdx = createRow(sheet, rowIdx, "Total Treatment Cost Paid by SHA", d.getTotalTreatmentCostPaidBySha(), "");
            rowIdx = createRow(sheet, rowIdx, "Upfront Release by SHA", d.getUpfrontReleaseByShaForPmjay(), "");
            rowIdx = createRow(sheet, rowIdx, "Payment Tranche No", d.getPaymentTrancheNo(), "");
            rowIdx = createRow(sheet, rowIdx, "Earlier Amount Released by NHA", d.getEarlierAmountReleasedByNha(), "");
            rowIdx = createRow(sheet, rowIdx, "Unspent Amount as per UC", d.getUnspentAmountAsPerUc(), "");

            // ---------- CALCULATED FIELDS (WITH DESCRIPTION) ----------

            rowIdx = createRow(sheet, rowIdx,
                    "% Eligible SECC Population",
                    d.getPercentageEligibleSeccPopulation(),
                    "Eligible SECC Population / Total Population Covered");

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    d.getMaxGiaImplementationPerFamily(),
                    "1052 × NHA Share in GIA");

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Admin per Family",
                    d.getMaxGiaAdminPerFamily(),
                    "Base Admin (150/200/50 based on population) × NHA Share");

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Implementation by NHA",
                    d.getMaxGiaImplementationByNha(),
                    "Eligible SECC Population × Max GIA Implementation per Family");

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Admin by NHA",
                    d.getMaxGiaAdminByNha(),
                    "Eligible SECC Population × Max GIA Admin per Family (with minimum cap)");

            rowIdx = createRow(sheet, rowIdx,
                    "Treatment Cost for PMJAY",
                    d.getTreatmentCostForPmjayBeneficiaries(),
                    "Total Treatment Cost Paid by SHA × % Eligible SECC Population");

            rowIdx = createRow(sheet, rowIdx,
                    "NHA Share in PMJAY Treatment Cost",
                    d.getNhaShareInPmjayTreatmentCost(),
                    "Treatment Cost × NHA Share");

            rowIdx = createRow(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "Upfront SHA Release × (NHA Share / (1 - NHA Share)) OR direct if share = 1");

            rowIdx = createRow(sheet, rowIdx,
                    "Total Amount Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max GIA Implementation by NHA × Payment Tranche No");

            rowIdx = createRow(sheet, rowIdx,
                    "Total Amount Payable by NHA (As On Date)",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (Max Amount for Implementation, NHA Share in Treatment ,  Nha Share Corresponding to share Release , Amount Payable upto tranche)");

            rowIdx = createRow(sheet, rowIdx,
                    "Amount Proposed to be Released",
                    d.getAmountProposedToBeReleased(),
                    "Total Payable - Earlier Released - Unspent Amount");
        }

        // Auto size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Helper method
    private int createRow(Sheet sheet, int rowIdx, String field, Object value, String desc) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(field);
        row.createCell(1).setCellValue(value != null ? value.toString() : "");
        row.createCell(2).setCellValue(desc);
        return rowIdx + 1;
    }
}