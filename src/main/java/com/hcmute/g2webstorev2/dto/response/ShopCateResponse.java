package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopCateResponse {
    @JsonProperty("shop_cate_id")
    private Integer shopCateId;
    private String name;
    @JsonProperty("shop_id")
    private Integer shopId;
    @JsonProperty("child_categories")
    private List<ShopCateResponse> childCategories;
    private String path;
}
