package com.hcmute.g2webstorev2.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.service.ElasticSearchService;
import com.hcmute.g2webstorev2.util.ElasticSearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private final ElasticsearchClient esClient;

    @Override
    public IndexResponse addProduct(Product product) throws IOException {
        IndexRequest<Product> req = IndexRequest.of(i -> i
                .index("product")
                .id(String.valueOf(product.getProductId())));
        IndexResponse res = esClient.index(req);
        log.info("Indexed with version " + res.version());
        return res;
    }

    @Override
    public SearchResponse<Product> fuzzyQueryProducts(String name, Integer startPrice, Integer endPrice) throws IOException {
        Query byName = ElasticSearchUtil.getFuzzyQuery("name", name);
        Query byPriceBetween = ElasticSearchUtil.getRangeQuery("price", startPrice, endPrice);


        if (startPrice != null && endPrice != null)
            return esClient.search(s -> s
                            .index("product")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(byName, byPriceBetween))),
                    Product.class);

        return esClient.search(s -> s
                        .index("product")
                        .query(q -> q
                                .bool(b -> b
                                        .must(byName)))
                ,
                Product.class);
    }
}
