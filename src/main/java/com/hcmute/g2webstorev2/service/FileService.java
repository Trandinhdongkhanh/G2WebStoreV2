package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.entity.GCPFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<GCPFile> uploadFiles(MultipartFile[] files);
    void delFile(Long fileId);
}
