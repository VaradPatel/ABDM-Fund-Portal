package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.VvsAdmin;
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
public class VVSAdminExcelService {

    public ByteArrayInputStream generateExcel(List<VvsAdmin> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("VVS Admin");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (VvsAdmin d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Proposal Type ", "Administration", "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            rowIdx = row(sheet, rowIdx, "NHA Share (A)", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "New Beneficiaries (B)", d.getNewBeneficiaryInstate(), "");
            rowIdx = row(sheet, rowIdx, "Old Beneficiaries (C)", d.getOldBeneficiaryInState(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (New)",
                    scale(d.getMaxGiaImplementationPerFamilyNew()),
                    "1052 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (Old)",
                    scale(d.getMaxGiaImplementationPerFamilyOld()),
                    "75.70 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation by NHA (D)",
                    scale(d.getMaxGiaImplementationByNha()),
                    "(B × 1052 * A) + (C × 75.70 * A)");

            StringBuilder descAdmin = new StringBuilder();
            descAdmin.append("-States/UTs with up to 1 lakh beneficiary families, the amount will be ₹200 per family or ₹1 crore, whichever is higher;\n");
            descAdmin.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descAdmin.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descAdmin.append("x NHA Share in GIA (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max Admin by NHA (E)",
                    scale(d.getMaxGiaAdminByNha()),
                    descAdmin.toString());
            rowIdx = row(sheet, rowIdx,
                    "Admin Cost Paid by SHA (F)",
                    scale(d.getTotalTreatmentCostPaidBySha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (SHA)",
                    scale(d.getCostOfAdministrativeExpenseSha()),
                    "F × (1 - A)");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (NHA) (G)",
                    scale(d.getCostOfAdministrativeExpenseNha()),
                    "F × A");

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release (H)",
                    scale(d.getUpfrontReleaseByShaForPmjay()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (I)",
                    scale(d.getNhaShareCorrespondingToShaRelease()),
                    "H × (A / (1 - A))");

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)");


            rowIdx = row(sheet, rowIdx,
                    "Total Payable Till Tranche (J)",
                    scale(d.getTotalAmountPayableTillThisTranche()),
                    "E × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Payable by NHA(M)",
                    scale(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (E, G, I, J)");

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (K)",
                    scale(d.getEarlierAmountReleasedByNha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (L)",
                    scale(d.getUnspentAmountAsPerUc()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed",
                    scale(d.getAmountProposedToBeReleased()),
                    "M - K - L");
        }

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

    // 🔥 CENTRALIZED ROUNDING HERE
    private int row(Sheet sheet, int i, String f, Object v, String d) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);

        if (v instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal) v).setScale(2, RoundingMode.HALF_UP);
            r.createCell(1).setCellValue(bd.toString());
        } else {
            r.createCell(1).setCellValue(v != null ? v.toString() : "");
        }

        r.createCell(2).setCellValue(d);
        return i + 1;
    }
}