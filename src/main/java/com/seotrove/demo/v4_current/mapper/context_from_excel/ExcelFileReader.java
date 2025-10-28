package com.seotrove.demo.v4_current.mapper.context_from_excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelFileReader {

    public static ContextRow readContextFromExcel(File file, String contextLabel) throws IOException {

        try (var wb = WorkbookFactory.create(file)) {
            var sheet = wb.getSheet("Context Structure Table");
            var headerRow = sheet.getRow(0);

            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                colIndex.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row == null) continue;

                if (contextLabel.equalsIgnoreCase(row.getCell(colIndex.get("Label")).getStringCellValue())) {
                    ContextRow ctx = new ContextRow();
                    ctx.seqNum = (int) row.getCell(colIndex.get("Seq Num")).getNumericCellValue();
                    ctx.label = row.getCell(colIndex.get("Label")).getStringCellValue();
                    ctx.startDate = row.getCell(colIndex.get("Start/Instant Date")).getStringCellValue();
                    ctx.endDate = row.getCell(colIndex.get("End Date")).getStringCellValue();
                    ctx.periodType = row.getCell(colIndex.get("Period Type")).getStringCellValue();
                    ctx.identifierScheme = row.getCell(colIndex.get("Identifier Scheme")).getStringCellValue();
                    ctx.identifierValue = row.getCell(colIndex.get("Identifier Value")).getStringCellValue();

                    // Handle Dimensions
                    for (int d = 1; d <= 3; d++) {
                        String prefixCol = "Dimension " + d + ": Namespace Prefix";
                        if (!colIndex.containsKey(prefixCol)) continue;

                        var dim = new ContextRow.Dimension();
                        dim.nsPrefix = row.getCell(colIndex.get(prefixCol)).getStringCellValue();
                        dim.name = row.getCell(colIndex.get("Dimension " + d + ": Name")).getStringCellValue();
                        dim.type = row.getCell(colIndex.get("Dimension " + d + ": Type")).getStringCellValue();
                        dim.value = row.getCell(colIndex.get("Dimension " + d + ": Value")).getStringCellValue();
                        dim.element = row.getCell(colIndex.get("Dimension " + d + ": Element")).getStringCellValue();
                        ctx.dimensions.add(dim);
                    }
                    return ctx;
                }

            }
            return null;
        }
    }

}
