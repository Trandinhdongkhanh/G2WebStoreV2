package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    @JsonProperty("review_id")
    private Integer reviewId;
    private String content;
    private String images;
    private Integer rate;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("shop_feed_back")
    private String shopFeedBack;
}
