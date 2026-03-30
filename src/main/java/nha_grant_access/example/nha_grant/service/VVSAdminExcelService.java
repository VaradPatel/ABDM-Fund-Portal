package nha_grant_access.example.nha_grant.service;
import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
import nha_grant_access.example.nha_grant.entity.VvsAdmin;
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
public class VVSAdminExcelService {

    public ByteArrayInputStream generateExcel(List<VvsAdmin> list) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("VVS Admin");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Field Name");
        header.createCell(1).setCellValue("Value");
        header.createCell(2).setCellValue("Description");

        int rowIdx = 1;

        for (VvsAdmin d : list) {

            // -------- NON CALCULATED --------
            rowIdx = row(sheet, rowIdx, "State Name", d.getStateName(), "");
            rowIdx = row(sheet, rowIdx, "Mode of Implementation", d.getModeOfImplementation(), "");
            rowIdx = row(sheet, rowIdx, "Scheme Name", d.getSchemeName(), "");
            rowIdx = row(sheet, rowIdx, "NHA Share", d.getNhaShareInGia(), "");
            rowIdx = row(sheet, rowIdx, "New Beneficiaries", d.getNewBeneficiaryInstate(), "");
            rowIdx = row(sheet, rowIdx, "Old Beneficiaries", d.getOldBeneficiaryInState(), "");
            rowIdx = row(sheet, rowIdx, "Treatment Cost Paid by SHA", d.getTotalTreatmentCostPaidBySha(), "");
            rowIdx = row(sheet, rowIdx, "Upfront Release", d.getUpfrontReleaseByShaForPmjay(), "");
            rowIdx = row(sheet, rowIdx, "Payment Tranche", d.getPaymentTrancheNo(), "");
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
                    "Max Admin by NHA",
                    d.getMaxGiaAdminByNha(),
                    "Population-based admin cost with min cap");

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
                    "Upfront × Share / (1 - Share)");

            rowIdx = row(sheet, rowIdx,
                    "Total Payable Till Tranche",
                    d.getTotalAmountPayableTillThisTranche(),
                    "Max Admin × Tranche No");

            rowIdx = row(sheet, rowIdx,
                    "Total Payable by NHA",
                    d.getTotalAmountPayableByNhaAsOnDate(),
                    "Minimum of (MAX Admin By NHA ,CostOfAdministrativeExpenseNha, NHA Share Corresponding to SHA Release, Total Payable Till Tranche)");

            rowIdx = row(sheet, rowIdx,
                    "Amount Proposed",
                    d.getAmountProposedToBeReleased(),
                    "Total - Earlier - Unspent");
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
