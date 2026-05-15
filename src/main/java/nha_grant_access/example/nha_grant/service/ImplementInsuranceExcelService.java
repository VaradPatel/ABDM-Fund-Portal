package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
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
public class ImplementInsuranceExcelService {

    public ByteArrayInputStream generateExcel(List<ImplementInsuCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Insurance Calculation");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (ImplementInsuCalc d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Insurance Company", d.getNameOfInsuranceCompany(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA (A)", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "Total Population Covered (B)", d.getTotalPopulationCoveredAsPerMou(), "");
            rowIdx = row(sheet, rowIdx, "Eligible SECC Population (C)", d.getEligibleSeccPopulation(), "As per the OM No-S12018/130/2021 , Dated-12th jan 2023, the beneficiary base under the scheme has been increased.");
            rowIdx = row(sheet, rowIdx, "Annual Insurance Premium/Family", d.getAnnualInsurancePremiumFamily(), "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "% Eligible SECC Population (D)",
                    scale(d.getPercentageEligibleSeccPopulation()),
                    "Eligible SECC Population(C) / Total Population(B)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family (E)",
                    scale(d.getMaxGiaImplementationPerFamily()),
                    "1052 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin per Family (F)",
                    scale(d.getMaxGiaAdminPerFamily()),
                    "Base Admin (150/200/50) × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA (G)",
                    scale(d.getMaxGiaImplementationByNha()),
                    "Eligible Population(C) × Max Implementation per Family(E)");

            StringBuilder descH = new StringBuilder();
            descH.append("Eligible Population(C) × Admin per Family(F) with min cap + Base Admin (150/200/50 based on population) × NHA Share in GIA\n");
            descH.append("-States/UTs with up to 1 lakh beneficiary families, the amount will be ₹200 per family or ₹1 crore, whichever is higher;\n");
            descH.append("-states with more than 1 lakh but less than 10 lakh beneficiary families, the amount will be ₹150 per family or ₹2 crore, whichever is higher,\n");
            descH.append("-states with more than 10 lakh beneficiary families, the amount will be ₹50 per family or ₹15 crore, whichever is higher.\n");
            descH.append("x NHA Share in GIA (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin by NHA (H)",
                    scale(d.getMaxGiaAdminByNha()),
                    descH.toString());

            rowIdx = row(sheet, rowIdx,
                    "NHA Share of Premium Payable (I)",
                    scale(d.getNhaShareOfPremiumPayable()),
                    "If premium > 1052 → 1052 × A × C ELSE Premium × A × C");

            rowIdx = row(sheet, rowIdx,
                    "SHA Share of Premium Payable (J)",
                    scale(d.getShaShareOfPremiumPayable()),
                    "If premium > 1052 → (Premium - (1052*A)) × C ELSE Premium × (1-A) × C");

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release by SHA (K)",
                    scale(d.getUpfrontReleaseByShaForPmjay()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (L)",
                    scale(d.getNhaShareCorrespondingToShaRelease()),
                    "If (k × A /(1-A)");

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No (M)",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (N)",
                    scale(d.getTotalAmountPayableTillThisTranche()),
                    "NHA Share Premium(I) × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA (O)",
                    scale(d.getTotalAmountPayableByNhaAsOnDate()),
                    "Minimum of (I, L, N)");

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (P)",
                    scale(d.getEarlierAmountReleasedByNha()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (Q)",
                    scale(d.getUnspentAmountAsPerUc()),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released (R)",
                    scale(d.getAmountProposedToBeReleased()),
                    "O - P - Q");
        }

        // Auto-size columns
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

    private int row(Sheet sheet, int i, String f, Object v, String d) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);
        r.createCell(1).setCellValue(v != null ? v.toString() : "");
        r.createCell(2).setCellValue(d);
        return i + 1;
    }
}