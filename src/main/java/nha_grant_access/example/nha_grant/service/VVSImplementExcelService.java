package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
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
public class VVSImplementExcelService {

    public ByteArrayInputStream generateExcel(List<VVSImplementNew> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("VVS Implement Trust");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (VVSImplementNew d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA (A)", round(d.getNhaShareInGia()), "");
            rowIdx = row(sheet, rowIdx, "New Beneficiaries (B)", d.getNewBeneficiaryInstate(), "");
            rowIdx = row(sheet, rowIdx, "Old Beneficiaries (C)", d.getOldBeneficiaryInState(), "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (New)",
                    round(d.getMaxGiaImplementationPerFamilyNew()),
                    "1052 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (Old)",
                    round(d.getMaxGiaImplementationPerFamilyOld()),
                    "75.70 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation by NHA (D)",
                    round(d.getMaxGiaImplementationByNha()),
                    "(B × 1052 * A) + (C × 75.70 * A)");

            rowIdx = row(sheet, rowIdx,
                    "Total Treatment Cost Paid by SHA (E)",
                    round(d.getTotalTreatmentCostPaidBySha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share in Treatment Cost (F)",
                    round(d.getNhaShareInPmjayTreatmentCost()),
                    "Total Treatment Cost (E) × NHA Share (A)");

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
                    "Max Implementation by NHA (D) × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA (J)",
                    round(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (D, F, H, I)");

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

        // Auto size
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

    // Helper
    private int row(Sheet sheet, int i, String f, Object v, String d) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);
        r.createCell(1).setCellValue(v != null ? v.toString() : "");
        r.createCell(2).setCellValue(d);
        return i + 1;
    }
}