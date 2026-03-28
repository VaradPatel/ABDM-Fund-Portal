package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.AdministrativeCalc;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class AdministrativeExcelService {

    public ByteArrayInputStream generateExcel(List<AdministrativeCalc> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Administrative");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AdministrativeCalc d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "Total Population Covered", d.getTotalPopulationCoveredAsPerMou(), "");
            rowIdx = row(sheet, rowIdx, "Eligible SECC Population", d.getEligibleSeccPopulation(), "");
            rowIdx = row(sheet, rowIdx, "Total Treatment Cost Paid by SHA", d.getTotalTreatmentCostPaidBySha(), "");
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
                    "Eligible Population × Implementation per Family");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Admin by NHA",
                    d.getMaxGiaAdminByNha(),
                    "Eligible Population × Admin per Family (with min cap)");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (SHA)",
                    d.getCostOfAdministrativeExpenseSha(),
                    "Total Treatment Cost × (1 - NHA Share)");

            rowIdx = row(sheet, rowIdx,
                    "Administrative Expense (NHA)",
                    d.getCostOfAdministrativeExpenseNha(),
                    "Total Treatment Cost × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "Upfront × Share / (1 - Share) OR direct if share = 1");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max GIA Admin × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (Max Admin, NHA Expense, SHA Corresponding, Tranche)");

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