package com.hcmute.g2webstorev2.util;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.hcmute.g2webstorev2.entity.Category;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopCategory;
import com.hcmute.g2webstorev2.es.index.ProductIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProductUtil {
    public static List<ProductIndex> convertToList(SearchResponse<ProductIndex> res) {
        List<ProductIndex> productIndexList = new LinkedList<>();
        List<Hit<ProductIndex>> hits = res.hits().hits();
        for (Hit<ProductIndex> hit : hits) {
            ProductIndex productIndex = hit.source();
            productIndexList.add(productIndex);
            log.info("Found product " + productIndex.getProductId() + ", score " + hit.score());
        }
        return productIndexList;
    }

    public static List<ProductIndex> filterByPrice(List<ProductIndex> products, Integer startPrice, Integer endPrice) {
        return products.stream()
                .filter(productIndex ->
                        (productIndex.getPrice() >= startPrice && productIndex.getPrice() <= endPrice)
                                && productIndex.getIsAvailable())
                .collect(Collectors.toList());
    }

    public static List<ProductIndex> getPageContent(List<ProductIndex> products, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());

        return products.subList(start, end);
    }

    public static List<Product> getContent(List<Product> products, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());

        return products.subList(start, end);
    }

    public static boolean isBelongToShopCate(Product product, ShopCategory shopCategory) {
        return product.getShopCategory().getPath().startsWith(getPath(shopCategory));
    }

    public static String getPath(Category category) {
        String path;

        if (category.getChildCategories().isEmpty()) path = category.getPath();
        else path = category.getPath() + "/";

        return path;
    }
    public static String getPath(ShopCategory shopCategory){
        String path;

        if (shopCategory.getChildCategories().isEmpty()) path = shopCategory.getPath();
        else path = shopCategory.getPath() + "/";

        return path;
    }
}
