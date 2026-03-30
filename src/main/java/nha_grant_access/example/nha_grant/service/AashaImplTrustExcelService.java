package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.entity.AashaAdmin;
import nha_grant_access.example.nha_grant.entity.AashaImplTrust;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
@Service
public class AashaImplTrustExcelService {

    public ByteArrayInputStream generateExcel(List<AashaImplTrust> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Aasha Impl Trust");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AashaImplTrust d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "Total Eligible Families", d.getTotalEligibleAshaAwwAwhFamilies(), "");
            rowIdx = row(sheet, rowIdx, "Upfront Release by SHA", d.getUpfrontReleaseByShaForPmjay(), "");
            rowIdx = row(sheet, rowIdx, "Payment Tranche No", d.getPaymentTrancheNo(), "");
            rowIdx = row(sheet, rowIdx, "Earlier Released", d.getEarlierAmountReleasedByNha(), "");
            rowIdx = row(sheet, rowIdx, "Unspent Amount", d.getUnspentAmountAsPerUc(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    d.getMaxGiaImplementationByNhaPerFamily(),
                    "1052 × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA",
                    d.getMaxGiaImplementationByNha(),
                    "Eligible Families × Max per family");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share in Cost",
                    d.getNhaShareInCostForAshaAwsAww(),
                    "Total Treatment Cost × NHA Share");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "Upfront × (NHA Share / (1 - NHA Share))");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max GIA × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (MaxGiaImplementationByNha, NhaShareInCostForAshaAwsAww , NhaShareCorrespondingToShaRelease, TotalAmountPayableTillThisTranche)");

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