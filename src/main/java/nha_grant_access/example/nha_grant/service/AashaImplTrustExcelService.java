package nha_grant_access.example.nha_grant.service;

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

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Particulars");
        header.createCell(1).setCellValue("Information");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (AashaImplTrust d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Date of Implementation", d.getDateOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "Benefit Cover", d.getBenefitCover(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share in GIA (A)", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "Total Eligible Families (B)", d.getTotalEligibleAshaAwwAwhFamilies(), "");
            rowIdx = row(sheet, rowIdx, "Policy Period", d.getPolicyPeriod(), "");

            // -------- CALCULATED --------

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation per Family",
                    d.getMaxGiaImplementationByNhaPerFamily(),
                    "1052 × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Max GIA Implementation by NHA (C)",
                    d.getMaxGiaImplementationByNha(),
                    "Eligible Families (B) × 1052 * A");

            rowIdx = row(sheet, rowIdx,
                    "Total Treatment Cost Paid by SHA (D)",
                    d.getTotalTreatmentCostPaidBySha(),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share in Cost (E)",
                    d.getNhaShareInCostForAshaAwsAww(),
                    "Total Treatment Cost (D) × NHA Share (A)");

            rowIdx = row(sheet, rowIdx,
                    "Upfront Release by SHA (F)",
                    d.getUpfrontReleaseByShaForPmjay(),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "NHA Share Corresponding to SHA Release (G)",
                    d.getNhaShareCorrespondingToShaRelease(),
                    "Upfront Release (F) × (A / (1 - A))");

            rowIdx = row(sheet, rowIdx,
                    "Payment Tranche No",
                    d.getPaymentTrancheNo(),
                    "(0.5/0.75/1.0)");


            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable Till Tranche (J)",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max GIA Implementation (C) × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Amount Payable by NHA (K)",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (C, E, G, J)");

            rowIdx = row(sheet, rowIdx,
                    "Earlier Released (H)",
                    d.getEarlierAmountReleasedByNha(),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Unspent Amount (I)",
                    d.getUnspentAmountAsPerUc(),
                    "");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed to be Released (L)",
                    d.getAmountProposedToBeReleased(),
                    "K - H - I");
        }

        // Auto-size
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