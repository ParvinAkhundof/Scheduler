package com.akbank.wm.middleware.core.service;

import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import java.util.List;

@Service
public class ExcelService {

    public Workbook createTableWithData(Workbook workbook, Sheet sheet, int rowNumber, List<String> headerList,
            List<List<String>> rows, String tableName) {

        // Create sample data (you can replace this with your own data)
        Row headerRow = sheet.createRow(rowNumber);
        for (int i = 0; i < headerList.size(); i++) {
            headerRow.createCell(i).setCellValue(headerList.get(i));
        }

        Row dataRow;
        int startRowNumber = rowNumber + 1;

        for (int i = 0; i < rows.size(); i++) {
            rowNumber++;
            dataRow = sheet.createRow(rowNumber);
            for (int j = 0; j < rows.get(i).size(); j++) {
                try {
                    dataRow.createCell(j).setCellValue(Double.parseDouble(rows.get(i).get(j)));
                } catch (Exception e) {
                    dataRow.createCell(j).setCellValue(rows.get(i).get(j));
                }

            }
        }

        AreaReference area = new AreaReference(
                "A" + startRowNumber + ":" + ((char) (64 + headerList.size())) + (rowNumber + 1),
                workbook.getSpreadsheetVersion());

        XSSFTable table = ((XSSFSheet) sheet).createTable(area);
        table.setName(tableName);
        table.setDisplayName(tableName);
        table.getCTTable().addNewAutoFilter();
        table.setStyleName("TableStyleLight15");
        XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();

        style.setShowColumnStripes(false);
        style.setShowRowStripes(true);

        return workbook;

    }

}
