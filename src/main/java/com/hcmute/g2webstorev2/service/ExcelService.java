package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddProductsToExportExcelReq;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ExcelService {
    void exportProductsData(HttpServletResponse res, AddProductsToExportExcelReq body)
            throws IOException;
}
