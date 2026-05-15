package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.AdministrativeCalc;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AdministrativeExcelService {

    public ByteArrayInputStream generateExcel(List<AdministrativeCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Administrative");

        // Create a reusable style for text wrapping on description column
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AdministrativeCalc d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Proposal Type", "Administration", "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "", wrapStyle);

            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA (A)", d.getNhaShareInGia(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Total Population Covered (B)", d.getTotalPopulationCoveredAsPerMou(), "", wrapStyle);
            rowIdx = row(sheet, rowIdx, "Eligible SECC Population (C)", d.getEligibleSeccPopulation(), "As per the OM No-S12018/130/2021 , Dated-12th jan 2023, the beneficiary base under the scheme has been increased.", wrapStyle);

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "% Eligible SECC Population (D)",
                    scale(d.getPercentageEligibleSeccPopulation()),
                    "Eligible SECC Population (C) / Total Population (B)", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family (E)",
                    scale(d.getMaxGiaImplementationPerFamily()),
                    "1052 × NHA Share in GIA (A)", wrapStyle);

            // Build description using StringBuilder for "Max GIA Admin per Family (F)"
            StringBuilder descF = new StringBuilder();
            descF.append("-States/UTs with up to 1 lakh beneficiary families, the amount will be ₹200 per family or ₹1 crore, whichever is higher;\n");
            descF.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descF.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descF.append("× NHA Share in GIA (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin per Family (F)",
                    scale(d.getMaxGiaAdminPerFamily()),
                    descF.toString(), wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA (G)",
                    scale(d.getMaxGiaImplementationByNha()),
                    "Eligible Population (C) × Implementation per Family (E)", wrapStyle);

            // Build description using StringBuilder for "Max GIA Admin by NHA (H)"
            StringBuilder descH = new StringBuilder();
            descH.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descH.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descH.append("× NHA Share in GIA (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin by NHA (H)",
                    scale(d.getMaxGiaAdminByNha()),
                    descH.toString(), wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Total Admin Cost Paid by SHA (I)",
                    scale(d.getTotalTreatmentCostPaidBySha()),
                    "With reference to DO No. S-12018/410/2025-NHA, the State Health Agency (SHA) has submitted the State Annual Action Plan for administrative expenses for release of the first tranche.", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (SHA) (J)",
                    scale(d.getCostOfAdministrativeExpenseSha()),
                    "Total Admin Cost (I) × (1 - A)", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense NHA (K)",
                    scale(d.getCostOfAdministrativeExpenseNha()),
                    "Total Admin Cost (I) × A", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release by SHA (L)",
                    scale(d.getUpfrontReleaseByShaForPmjay()),
                    "", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (M)",
                    scale(d.getNhaShareCorrespondingToShaRelease()),
                    "Upfront (L) × (A / (1 - A))", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (N)",
                    scale(d.getTotalAmountPayableTillThisTranche()),
                    "Max GIA Admin (H) × Tranche No", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA (O)",
                    scale(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (H, K, M, N)", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (P)",
                    scale(d.getEarlierAmountReleasedByNha()),
                    "", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (Q)",
                    scale(d.getUnspentAmountAsPerUc()),
                    "", wrapStyle);

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released (R)",
                    scale(d.getAmountProposedToBeReleased()),
                    "O - P - Q", wrapStyle);
        }

        // Auto size
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // 🔥 Common rounding utility (clean + reusable)
    private BigDecimal scale(BigDecimal val) {
        return val != null ? val.setScale(2, RoundingMode.HALF_UP) : null;
    }

    private int row(Sheet sheet, int i, String f, Object v, String d, CellStyle wrapStyle) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);
        r.createCell(1).setCellValue(v != null ? v.toString() : "");
        Cell descCell = r.createCell(2);
        descCell.setCellValue(d);
        descCell.setCellStyle(wrapStyle);  // Apply wrapping to description column
        return i + 1;
    }
}