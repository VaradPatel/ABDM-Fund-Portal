package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.AashaAdmin;
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
public class AashaAdminExcelService {

    public ByteArrayInputStream generateExcel(List<AashaAdmin> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Aasha Admin");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AashaAdmin d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Proposal Type", "Administration", "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share in GIA (A)",
                    d.getNhaShareInGia(),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Total Eligible Families (B)",
                    d.getTotalEligibleAshaAwwAwhFamilies(),
                    "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    scale(d.getMaxGiaImplementationByNhaPerFamily()),
                    "1052 × NHA Share (A)");

            StringBuilder descAdminFamily = new StringBuilder();
            descAdminFamily.append("-States/UTs with up to 1 lakh beneficiary families, the amount will be ₹200 per family or ₹1 crore, whichever is higher;\n");
            descAdminFamily.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descAdminFamily.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descAdminFamily.append("x NHA Share in GIA (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin per Family",
                    scale(d.getMaxGiaAdminByNhaPerFamily()),
                    descAdminFamily.toString());

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA",
                    scale(d.getMaxGiaImplementationByNha()),
                    "Eligible Families (B) × 1052 * A");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin by NHA (C)",
                    scale(d.getMaxGiaAdminByNha()),
                    "Eligible Families (B) × Admin per Family (with minimum cap)");

            rowIdx = row(sheet, rowIdx,
                    "Total Admin Cost Paid by SHA (D)",
                    scale(d.getTotalTreatmentCostPaidBySha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (SHA)",
                    scale(d.getCostOfAdministrativeExpenseSha()),
                    "Total Admin Cost (D) × (1 - NHA Share (A))");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense NHA (E)",
                    scale(d.getCostOfAdministrativeExpenseNha()),
                    "Total Admin Cost (D) × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release by SHA (F)",
                    scale(d.getUpfrontReleaseByShaForPmjay()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (G)",
                    scale(d.getNhaShareCorrespondingToShaRelease()),
                    "Upfront (F) × (A / (1 - A))");

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No (H)",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (I)",
                    scale(d.getTotalAmountPayableTillThisTranche()),
                    "Max GIA Admin by NHA (C) × Tranche No (H)");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA(M)",
                    scale(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (C, E, G, I)");

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (J)",
                    scale(d.getEarlierAmountReleasedByNha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (K)",
                    scale(d.getUnspentAmountAsPerUc()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released",
                    scale(d.getAmountProposedToBeReleased()),
                    "M - Earlier Released (J) - Unspent (K)");
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
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