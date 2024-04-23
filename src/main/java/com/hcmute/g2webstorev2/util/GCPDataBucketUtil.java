package com.hcmute.g2webstorev2.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.exception.GCPFileUploadException;
import com.hcmute.g2webstorev2.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class GCPDataBucketUtil {
    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private String gcpBucketId;

    private static final String PUBLIC_GG_STORAGE_PREFIX = "https://storage.googleapis.com/";

    public GCPFile uploadFile(MultipartFile multipartFile, String fileName, String contentType) {

        try {

            log.debug("Start file uploading process on GCS");
            checkFileExtension(fileName);

            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();

            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();

            Storage storage = options.getService();
            Bucket bucket = storage.get(gcpBucketId, Storage.BucketGetOption.fields());

            String id = UUID.randomUUID().toString();
            Blob blob = bucket.create(id + "-" + fileName, multipartFile.getBytes(), contentType);


            if (blob != null) {
                log.debug("File successfully uploaded to GCS");
                return GCPFile.builder()
                        .fileName(fileName)
                        .fileUrl(PUBLIC_GG_STORAGE_PREFIX + gcpBucketId + "/" + blob.getName())
                        .fileType(blob.getContentType())
                        .build();
            }

        } catch (Exception e) {
            log.error("An error occurred while uploading data. Exception: ", e);
            throw new GCPFileUploadException("An error occurred while storing data to GCS");
        }
        throw new GCPFileUploadException("An error occurred while storing data to GCS");
    }

    private void checkFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            String[] extensionList = {".png", ".jpeg", ".pdf", ".doc", ".mp3", ".jpg", ".mp4"};

            for (String extension : extensionList) {
                if (fileName.endsWith(extension)) {
                    log.debug("Accepted file type : {}", extension);
                    return;
                }
            }
        }
        log.error("Not a permitted file type");
        throw new InvalidFileTypeException("Not a permitted file type");
    }
}
