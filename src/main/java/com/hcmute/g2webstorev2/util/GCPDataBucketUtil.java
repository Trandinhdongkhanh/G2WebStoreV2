package com.hcmute.g2webstorev2.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
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
    @Value("${gcp.gcs.url-prefix}")
    private String gcsUrlPrefix;
    @Value("${firebase.key}")
    private String firebaseKey;
    @Value("${firebase.storage.bucket}")
    private String firebaseBucket;
    @Value("${firebase.storage.url}")
    private String fbUrl;
    @Value("${firebase.project}")
    private String fbPrjId;
    public GCPFile uploadFile(MultipartFile multipartFile, String fileName, String contentType) {

        try {
            log.info("Start file uploading process on GCS");
            checkFileExtension(fileName);
            String formattedFileName = fileName.trim().replaceAll("\\s+", "");  //Remove whitespaces

            InputStream inputStream = new ClassPathResource(firebaseKey).getInputStream();
            StorageOptions options = StorageOptions.newBuilder().setProjectId(fbPrjId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            Storage storage = options.getService();
            Bucket bucket = storage.get(firebaseBucket, Storage.BucketGetOption.fields());

            String id = UUID.randomUUID().toString();
            String gcpFileName = id + "-" + formattedFileName;
            Blob blob = bucket.create(gcpFileName, multipartFile.getBytes(), contentType);

            String objUrl = fbUrl + firebaseBucket + "/o/" + blob.getName() + "?alt=media";

            if (blob != null) {
                log.debug("File successfully uploaded to GCS");
                return GCPFile.builder()
                        .fileName(gcpFileName)
                        .fileUrl(objUrl)
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
            String[] extensionList = {".png", ".jpeg", ".pdf", ".doc", ".mp3", ".jpg", ".mp4", ".jfif"};

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
