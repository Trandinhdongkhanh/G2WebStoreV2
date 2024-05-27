package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddProductsToExportExcelReq;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.ExcelService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {
    private final XSSFSheet sheet;
    private final XSSFWorkbook workbook;
    private final ProductRepo productRepo;

    @Autowired
    public ExcelServiceImpl(ProductRepo productRepo) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Products");
        this.productRepo = productRepo;
    }

    private void writeHeaderLine() {
        Row header = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(header, 0, "Product ID", style);
        createCell(header, 1, "Product Name", style);
        createCell(header, 2, "Description", style);
        createCell(header, 3, "Price", style);
        createCell(header, 4, "Stock Quantity", style);
        createCell(header, 5, "Height", style);
        createCell(header, 6, "Width", style);
        createCell(header, 7, "Length", style);
        createCell(header, 8, "Weight", style);
    }

    private void createCell(Row row, int col, Object value, CellStyle style) {
        sheet.autoSizeColumn(col);
        Cell cell = row.createCell(col);
        if (value instanceof Double) cell.setCellValue((Double) value);
        if (value instanceof Boolean) cell.setCellValue((Boolean) value);
        if (value instanceof String) cell.setCellValue((String) value);
        if (value instanceof Date) cell.setCellValue((Date) value);
        if (value instanceof Integer) cell.setCellValue((Integer) value);
        if (value instanceof Long) cell.setCellValue((Long) value);
        cell.setCellStyle(style);
    }

    private void writeDataLines(List<Product> products) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        CellStyle productIdCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        productIdCellStyle.setFont(font);
        productIdCellStyle.setLocked(true);

        for (Product product : products) {
            Row row = sheet.createRow(rowCount++);
            int col = 0;

            createCell(row, col++, product.getProductId(), productIdCellStyle);
            createCell(row, col++, product.getName(), style);
            createCell(row, col++, product.getDescription(), style);
            createCell(row, col++, product.getPrice(), style);
            createCell(row, col++, product.getStockQuantity(), style);
            createCell(row, col++, product.getHeight(), style);
            createCell(row, col++, product.getWidth(), style);
            createCell(row, col++, product.getLength(), style);
            createCell(row, col++, product.getWeight(), style);

        }
    }

    @Override
    public void exportProductsData(HttpServletResponse res, AddProductsToExportExcelReq body) throws IOException {
        List<Product> products;
        if (body.isAllProducts()) products = productRepo.findAll();
        else {
            if (body.getProductIds() == null || body.getProductIds().isEmpty())
                throw new NullPointerException("Products IDs must not be empty or null");
            products = productRepo.findAllById(body.getProductIds());
            products.sort(Comparator.comparing(Product::getProductId));
        }

        sheet.createFreezePane(0, 1);
        sheet.protectSheet("password");
        writeHeaderLine();
        writeDataLines(products);

        ServletOutputStream outputStream = res.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}