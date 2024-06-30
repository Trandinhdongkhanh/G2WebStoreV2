package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.exception.FilesUploadException;
import com.hcmute.g2webstorev2.exception.GCPFileUploadException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.repository.GCPFileRepo;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.util.GCPDataBucketUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final GCPDataBucketUtil gcpDataBucketUtil;
    private final GCPFileRepo gcpFileRepo;

    @Override
    public List<GCPFile> uploadFiles(MultipartFile[] files) {
        List<GCPFile> gcpFiles = new ArrayList<>();

        Arrays.asList(files).forEach(file -> {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new FilesUploadException("Original file name is null");
            }
            Path path = new File(originalFileName).toPath();

            try {
                String contentType = Files.probeContentType(path);
                GCPFile res = gcpDataBucketUtil.uploadFile(file, originalFileName, contentType);

                if (res != null) {
                    gcpFiles.add(res);
                    log.debug("File uploaded successfully, file name: {} and url: {}", res.getFileName(), res.getFileUrl());
                }
            } catch (Exception e) {
                log.error("Error occurred while uploading. Error: ", e);
                throw new GCPFileUploadException("Error occurred while uploading");
            }
        });

        return gcpFiles;
    }

    @Override
    @Transactional
    public void delFile(Long fileId) {
        GCPFile gcpFile = gcpFileRepo.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File with ID = " + fileId + " not found"));

        if (gcpDataBucketUtil.delFile(gcpFile.getFileName())) {
            gcpFileRepo.deleteById(fileId);
            log.info("File with ID = " + fileId + " deleted successfully");
        }
    }
}
