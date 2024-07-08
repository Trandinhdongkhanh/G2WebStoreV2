package com.hcmute.g2webstorev2.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.hcmute.g2webstorev2.es.index.ProductIndex;

import java.io.IOException;

public interface ElasticSearchService {
    SearchResponse<ProductIndex> boolSearchProducts(String name, Integer startPrice, Integer endPrice,
                                                    int page, int size, Integer seed) throws IOException;
}
