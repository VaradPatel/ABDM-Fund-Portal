package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ImplementTrustExcelService {

    public ByteArrayInputStream generateExcel(List<ImplementTrustCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Implement Trust");

        // Create a reusable style for text wrapping on description column
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (ImplementTrustCalc d : list) {

            // ---------- NON CALCULATED FIELDS (Description = blank) ----------

            rowIdx = createRow(sheet, rowIdx, "State Name", d.getStateName(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Scheme Name", "AB PM-JAY", "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "NHA Share in GIA (A) ", d.getNhaShareInGia(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Total Population Covered (MOU) (B)", d.getTotalPopulationCoveredAsPerMou(), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Eligible SECC Population (C)", d.getEligibleSeccPopulation(), "As per the OM No-S12018/130/2021 , Dated-12th jan 2023, the beneficiary base under the scheme has been increased.", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "", wrapStyle);

            // ---------- CALCULATED FIELDS (WITH DESCRIPTION) ----------

            rowIdx = createRow(sheet, rowIdx,
                    "% Eligible SECC Population (D)",
                    scale(d.getPercentageEligibleSeccPopulation()),
                    "Eligible SECC Population(C) / Total Population Covered (B)", wrapStyle);
            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    scale(d.getMaxGiaImplementationPerFamily()),
                    "1052 × NHA Share in GIA (A)", wrapStyle);

            StringBuilder descE = new StringBuilder();
            descE.append("-States/UTs with up to 1 lakh beneficiary families, the amount will be ₹200 per family or ₹1 crore, whichever is higher;\n");
            descE.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descE.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descE.append("x NHA Share in GIA (A)");

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Admin per Family(E)",
                    scale(d.getMaxGiaAdminPerFamily()),
                    descE.toString(), wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Implementation by NHA (F) ",
                    scale(d.getMaxGiaImplementationByNha()),
                    "Eligible SECC Population(C) × Max GIA Implementation per Family(1052 *A)", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "Max GIA Admin by NHA(G)",
                    scale(d.getMaxGiaAdminByNha()),
                    "Eligible SECC Population × Max GIA Admin per Family(E) (with minimum cap)", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Total Treatment Cost Paid by SHA(H)", scale(d.getTotalTreatmentCostPaidBySha()), "As per SHA Claim paid Sheet ", wrapStyle);


            rowIdx = createRow(sheet, rowIdx,
                    "Treatment Cost for PMJAY(I)",
                    scale(d.getTreatmentCostForPmjayBeneficiaries()),
                    "Total Treatment Cost Paid by SHA (H)× % Eligible SECC Population (D)", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "NHA Share in PMJAY Treatment Cost (K)",
                    scale(d.getNhaShareInPmjayTreatmentCost()),
                    "Treatment Cost(I) × NHA Share(A)", wrapStyle);

            rowIdx = createRow(sheet, rowIdx, "Upfront Release by SHA(L)", scale(d.getUpfrontReleaseByShaForPmjay()), "", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (M)",
                    scale(d.getNhaShareCorrespondingToShaRelease()),
                    "Upfront SHA Release(L) × (A / (1 -A))", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Payment Tranche No", d.getPaymentTrancheNo(), "(0.5/0.75/1.0)", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (O)",
                    scale(d.getTotalAmountPayableTillThisTranche()),
                    "Max GIA Implementation by NHA(F) × Payment Tranche No", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "Total Amount Payable by NHA (P)",
                    scale(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (F, K, M, O)", wrapStyle);

            rowIdx = createRow(sheet, rowIdx, "Earlier Amount Released by NHA(Q)", scale(d.getEarlierAmountReleasedByNha()), "", wrapStyle);
            rowIdx = createRow(sheet, rowIdx, "Unspent Amount as per UC(R)", scale(d.getUnspentAmountAsPerUc()), "", wrapStyle);

            rowIdx = createRow(sheet, rowIdx,
                    "Amount Proposed to be Released(S)",
                    scale(d.getAmountProposedToBeReleased()),
                    "P-Q-R", wrapStyle);
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

    // 🔥 Common rounding utility (clean + reusable)
    private BigDecimal scale(BigDecimal val) {
        return val != null ? val.setScale(2, RoundingMode.HALF_UP) : null;
    }

    // Helper method
    private int createRow(Sheet sheet, int rowIdx, String field, Object value, String desc, CellStyle wrapStyle) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(field);
        row.createCell(1).setCellValue(value != null ? value.toString() : "");
        Cell descCell = row.createCell(2);
        descCell.setCellValue(desc);
        descCell.setCellStyle(wrapStyle);
        return rowIdx + 1;
    }
}