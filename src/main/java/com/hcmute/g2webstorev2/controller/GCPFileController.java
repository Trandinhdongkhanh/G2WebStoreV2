package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.service.FileService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/gcp-storage")
public class GCPFileController {
    @Autowired
    private FileService fileService;

    @DeleteMapping("/product/object/{fileId}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> delImageFromProduct(
            @PathVariable("fileId")
            @NotNull(message = "File ID must not be null")
            @Min(value = 1, message = "File ID must not be less than 1")
            Long fileId) throws IOException {
        fileService.delFile(fileId);
        return ResponseEntity.ok("File with ID = " + fileId + " deleted successfully");
    }
}
