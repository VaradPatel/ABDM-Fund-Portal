package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
import nha_grant_access.example.nha_grant.entity.VvsHybrid;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import nha_grant_access.example.nha_grant.entity.AashaAdmin;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
@Service
public class VVSHybridExcelService {

    public ByteArrayInputStream generateExcel(List<VvsHybrid> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("VVS Hybrid");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (VvsHybrid d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "New Beneficiaries", d.getNewBeneficiaryInstate(), "");
            rowIdx = row(sheet, rowIdx, "Old Beneficiaries", d.getOldBeneficiaryInState(), "");
            rowIdx = row(sheet, rowIdx, "Insurance Company", d.getNameOfInsuranceCompany(), "");
            rowIdx = row(sheet, rowIdx, "Annual Premium", d.getAnnualInsurancePremiumFamily(), "");
            rowIdx = row(sheet, rowIdx, "Upfront Release by SHA", d.getUpfrontReleaseByShaForPmjay(), "");
            rowIdx = row(sheet, rowIdx, "Payment Tranche No", d.getPaymentTrancheNo(), "");
            rowIdx = row(sheet, rowIdx, "Earlier Released", d.getEarlierAmountReleasedByNha(), "");
            rowIdx = row(sheet, rowIdx, "Unspent Amount", d.getUnspentAmountAsPerUc(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (New)",
                    d.getMaxGiaImplementationPerFamilyNew(),
                    "1052 × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation per Family (Old)",
                    d.getMaxGiaImplementationPerFamilyOld(),
                    "75.70 × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "Max Implementation by NHA",
                    d.getMaxGiaImplementationByNha(),
                    "(New × New Rate) + (Old × Old Rate)");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share of Premium",
                    d.getNhaShareOfPremiumPayable(),
                    "Min(1052, Premium) × NHA Share × Old Beneficiaries");

            rowIdx = row(sheet, rowIdx,
                    "SHA Share of Premium",
                    d.getShaShareOfPremiumPayable(),
                    "Remaining Premium × Old Beneficiaries");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "Upfront × ratio OR direct if share = 1");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max Implementation × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (NhaShareOfPremiumPayable , NHA Share Corresponding to SHA Release, Total Amount Payable Till Tranche)");

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
