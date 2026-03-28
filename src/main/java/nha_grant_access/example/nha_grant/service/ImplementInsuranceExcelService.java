package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ImplementInsuranceExcelService {

    public ByteArrayInputStream generateExcel(List<ImplementInsuCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Insurance Calculation");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
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
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "Total Population Covered", d.getTotalPopulationCoveredAsPerMou(), "");
            rowIdx = row(sheet, rowIdx, "Eligible SECC Population", d.getEligibleSeccPopulation(), "");
            rowIdx = row(sheet, rowIdx, "Annual Insurance Premium/Family", d.getAnnualInsurancePremiumFamily(), "");
            rowIdx = row(sheet, rowIdx, "Upfront Release by SHA", d.getUpfrontReleaseByShaForPmjay(), "");
            rowIdx = row(sheet, rowIdx, "Payment Tranche No", d.getPaymentTrancheNo(), "");
            rowIdx = row(sheet, rowIdx, "Earlier Released", d.getEarlierAmountReleasedByNha(), "");
            rowIdx = row(sheet, rowIdx, "Unspent Amount", d.getUnspentAmountAsPerUc(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "% Eligible SECC Population",
                    d.getPercentageEligibleSeccPopulation(),
                    "Eligible SECC Population / Total Population");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    d.getMaxGiaImplementationPerFamily(),
                    "1052 × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin per Family",
                    d.getMaxGiaAdminPerFamily(),
                    "Base Admin (150/200/50) × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA",
                    d.getMaxGiaImplementationByNha(),
                    "Eligible Population × Max Implementation per Family");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin by NHA",
                    d.getMaxGiaAdminByNha(),
                    "Eligible Population × Admin per Family (with min cap)");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share of Premium Payable",
                    d.getNhaShareOfPremiumPayable(),
                    "If premium > 1052 → 1052 × Share × Population ELSE Premium × Share × Population");

            rowIdx = row(sheet, rowIdx,
                    "SHA Share of Premium Payable",
                    d.getShaShareOfPremiumPayable(),
                    "If premium > 1052 → (Premium - MaxImpl) × Population ELSE Premium × (1 - Share) × Population");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "If Share=1 → direct ELSE (Upfront × NHA Share Premium / SHA Share)");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "NHA Share Premium × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (NHA Share In Premium, NHA Corresponding to SHA Release, Amount Payable upto Tranche)");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released",
                    d.getAmountProposedToBeReleased(),
                    "Total Payable - Earlier Released - Unspent");
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private int row(Sheet sheet, int i, String f, Object v, String d) {
        Row r = sheet.createRow(i);
        r.createCell(0).setCellValue(f);
        r.createCell(1).setCellValue(v != null ? v.toString() : "");
        r.createCell(2).setCellValue(d);
        return i + 1;
    }
}
