package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewsRes {
    @JsonProperty("avg_rate")
    private Double avgRate;
    @JsonProperty("total_rate_count")
    private Long totalRateCount;
    @JsonProperty("five_star_rate_count")
    private Long fiveStarRateCount;
    @JsonProperty("four_star_rate_count")
    private Long fourStarRateCount;
    @JsonProperty("three_star_rate_count")
    private Long threeStarRateCount;
    @JsonProperty("two_star_rate_count")
    private Long twoStarRateCount;
    @JsonProperty("one_star_rate_count")
    private Long oneStarRateCount;
    private Page<ReviewResponse> reviews;
}
