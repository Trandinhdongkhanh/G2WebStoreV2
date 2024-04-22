package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.util.FilesUploadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files-upload")
public class FilesUploadingController {
    @Autowired
    private FilesUploadingService filesUploadingService;
    @PostMapping
    public String upload(@RequestParam("file")MultipartFile multipartFile){
        return filesUploadingService.upload(multipartFile);
    }
}
