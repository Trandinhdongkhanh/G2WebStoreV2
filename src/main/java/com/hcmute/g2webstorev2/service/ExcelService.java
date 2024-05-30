package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddProductsToExportExcelReq;
import com.hcmute.g2webstorev2.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExcelService {
    void exportToExcel(HttpServletResponse response, AddProductsToExportExcelReq body) throws IOException;

    List<Product> readProductsData(MultipartFile file) throws IOException;
}
