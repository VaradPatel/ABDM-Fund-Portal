package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.AashaHybrid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AashaHybridExcelService {

    public ByteArrayInputStream generateExcel(List<AashaHybrid> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Aasha Hybrid");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AashaHybrid d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            rowIdx = row(sheet, rowIdx, "Insurance Company", d.getNameOfInsuranceCompany(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA (A)", round(d.getNhaShareInGia()), "");
            rowIdx = row(sheet, rowIdx, "Total Eligible Families (B)", d.getTotalEligibleAshaAwwAwhFamilies(), "");

            rowIdx = row(sheet, rowIdx, "Annual Premium per Family (C)", round(d.getAnnualInsurancePremiumFamily()), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    round(d.getMaxGiaImplementationByNhaPerFamily()),
                    "1052 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA (D)",
                    round(d.getMaxGiaImplementationByNha()),
                    "Eligible Families (B) × 1052 * (A)");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share of Premium Payable (E)",
                    round(d.getNhaShareOfPremiumPayable()),
                    "If Premium (C) > 1052 → (1052 × A × B) ELSE (Premium × A × B)");

            rowIdx = row(sheet, rowIdx,
                    "SHA Share of Premium Payable (F)",
                    round(d.getShaShareOfPremiumPayable()),
                    "If Premium (C) > 1052 → (Premium - (1052*A)) × B ELSE (Premium × (1 - A) × B)");

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release by SHA (G)",
                    round(d.getUpfrontReleaseByShaForPmjay()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (H)",
                    round(d.getNhaShareCorrespondingToShaRelease()),
                    "Upfront (G) × (A / (1 - A))");

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (I)",
                    round(d.getTotalAmountPayableTillThisTranche()),
                    "Max GIA Implementation by NHA (D) × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA (J)",
                    round(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (E, H, I)");

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (K)",
                    round(d.getEarlierAmountReleasedByNha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (L)",
                    round(d.getUnspentAmountAsPerUc()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released (M)",
                    round(d.getAmountProposedToBeReleased()),
                    "J - K - L");
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ✅ Common rounding
    private Object round(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
    }

    // 🔥 Common rounding utility (clean + reusable)
    private BigDecimal scale(BigDecimal val) {
        return val != null ? val.setScale(2, RoundingMode.HALF_UP) : null;
    }

    private int row(Sheet sheet, int i, String f, Object v, String d) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);
        r.createCell(1).setCellValue(v != null ? v.toString() : "");
        r.createCell(2).setCellValue(d);
        return i + 1;
    }
}