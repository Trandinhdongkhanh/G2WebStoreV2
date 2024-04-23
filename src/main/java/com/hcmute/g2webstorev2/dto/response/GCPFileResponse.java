package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GCPFileResponse {
    private Long id;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_url")
    private String fileUrl;
    @JsonProperty("file_type")
    private String fileType;
}
