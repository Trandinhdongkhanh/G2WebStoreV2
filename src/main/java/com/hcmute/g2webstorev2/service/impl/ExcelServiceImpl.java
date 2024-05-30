package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddProductsToExportExcelReq;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.exception.InvalidFileTypeException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.ExcelService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final ProductRepo productRepo;

    private void newReportExcel() {
        workbook = new XSSFWorkbook();
    }

    private void writeTableHeaderExcel(String sheetName, String[] headers) {

        // sheet
        sheet = workbook.createSheet(sheetName);
        sheet.createFreezePane(0, 1);

        //header style
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setLocked(true);

        // header
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }
    }

    private CellStyle getFontContentExcel() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        return style;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof String)
            cell.setCellValue((String) value);
        cell.setCellStyle(style);
    }

    private HttpServletResponse initResponseForExportExcel(HttpServletResponse response, String fileName) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        String headerKey = HttpHeaders.CONTENT_DISPOSITION;
        String headerValue = "attachment; filename=" + fileName + ".xlsx";
        response.setHeader(headerKey, headerValue);
        return response;
    }

    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return (contentType != null && (contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) &&
                (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")));
    }

    private void writeTableData(AddProductsToExportExcelReq body) {
        // data
        List<Product> products;
        if (body.isAllProducts()) products = productRepo.findAll();
        else {
            if (body.getProductIds() == null || body.getProductIds().isEmpty())
                throw new NullPointerException("Product Ids must not null or empty");
            products = productRepo.findAllById(body.getProductIds());
        }

        // font style content
        CellStyle unlockedStyle = getFontContentExcel();
        CellStyle lockedStyle = getFontContentExcel();
        lockedStyle.setLocked(true);
        unlockedStyle.setLocked(false);

        // starting write on row
        int startRow = 1;

        // write content
        for (Product product : products) {
            Row row = sheet.createRow(startRow++);
            int columnCount = 0;
            createCell(row, columnCount++, product.getProductId(), lockedStyle);
            createCell(row, columnCount++, product.getName(), unlockedStyle);
            createCell(row, columnCount++, product.getDescription(), unlockedStyle);
            createCell(row, columnCount++, product.getPrice(), unlockedStyle);
            createCell(row, columnCount++, product.getStockQuantity(), unlockedStyle);
            createCell(row, columnCount++, product.getHeight(), unlockedStyle);
            createCell(row, columnCount++, product.getWidth(), unlockedStyle);
            createCell(row, columnCount++, product.getLength(), unlockedStyle);
            createCell(row, columnCount++, product.getWeight(), unlockedStyle);
        }
    }

    @Override
    public List<Product> readProductsData(MultipartFile file) throws IOException {
        if (!isValidExcelFile(file)) throw new InvalidFileTypeException("File type must be excel");
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Product> products = new ArrayList<>();
        XSSFWorkbook reqWorkbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet reqSheet = reqWorkbook.getSheetAt(0);

        for (int i = 1; i < reqSheet.getPhysicalNumberOfRows(); i++) {
            Row row = reqSheet.getRow(i);

            Integer productId = (int) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            String description = row.getCell(2).getStringCellValue();
            Integer price = (int) row.getCell(3).getNumericCellValue();
            Integer stockQuantity = (int) row.getCell(4).getNumericCellValue();
            Float height = (float) row.getCell(5).getNumericCellValue();
            Float width = (float) row.getCell(6).getNumericCellValue();
            Float length = (float) row.getCell(7).getNumericCellValue();
            Float weight = (float) row.getCell(8).getNumericCellValue();

            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!Objects.equals(name, product.getName()) &&
                    productRepo.existsByNameAndShop(name, seller.getShop()))
                throw new ResourceNotUniqueException("Duplicate product name");

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setHeight(height);
            product.setWidth(width);
            product.setLength(length);
            product.setWeight(weight);
            products.add(product);
        }
        return products;
    }

    public void exportToExcel(HttpServletResponse response, AddProductsToExportExcelReq body) throws IOException {
        newReportExcel();

        // response  writer to excel
        HttpServletResponse excelResponse = initResponseForExportExcel(response, "products");
        ServletOutputStream outputStream = excelResponse.getOutputStream();

        // write sheet & header
        String[] headers = new String[]{
                "Product ID",
                "Product Name",
                "Description",
                "Price",
                "Stock Quantity",
                "Height",
                "Width",
                "Length",
                "Weight"};
        writeTableHeaderExcel("Products", headers);

        // write content row
        writeTableData(body);
        sheet.protectSheet("password");

        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}