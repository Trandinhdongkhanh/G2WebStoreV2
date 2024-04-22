package com.hcmute.g2webstorev2.util;

import org.springframework.web.multipart.MultipartFile;


public interface FilesUploadingService {
    public String upload(MultipartFile multipartFile);
}
