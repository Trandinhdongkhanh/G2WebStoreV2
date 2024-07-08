package com.hcmute.g2webstorev2.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.hcmute.g2webstorev2.es.index.ProductIndex;
import com.hcmute.g2webstorev2.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private final ElasticsearchClient esClient;

    @Override
    public SearchResponse<ProductIndex> boolSearchProducts(String name, Integer startPrice, Integer endPrice,
                                                           int page, int size, Integer seed) throws IOException {
        MatchQuery matchQuery = MatchQuery.of(m -> m
                .field("name")
                .query(name)
                .fuzziness("2"));

        MatchPhraseQuery matchPhraseQuery = MatchPhraseQuery.of(mp -> mp
                .field("name")
                .query(name));

        WildcardQuery wildcardQuery = WildcardQuery.of(w -> w
                .field("name")
                .value(name + "*"));

        RangeQuery rangeQuery = RangeQuery.of(r -> r
                .field("price")
                .gte(JsonData.of(startPrice))
                .lte(JsonData.of(endPrice)));

        List<Query> queries = new ArrayList<>();
        queries.add(matchQuery._toQuery());
        queries.add(matchPhraseQuery._toQuery());
        queries.add(wildcardQuery._toQuery());

        BoolQuery boolQuery;
        if (startPrice != null && endPrice != null)
            boolQuery = BoolQuery.of(b -> b
                    .should(queries)
                    .filter(rangeQuery._toQuery()));
        else boolQuery = BoolQuery.of(b -> b.should(queries));
        Query query;
        if (seed != null)
            query = Query.of(q -> q
                            .functionScore(f -> f
                                    .query(q1 -> q1.bool(boolQuery))
                                    .functions(fs -> fs.randomScore(rd -> rd.seed(String.valueOf(seed))))));
        else query = Query.of(q -> q.bool(boolQuery));

        return esClient.search(s -> s
                        .index("product_index")
                        .query(query)
                        .from(page * size)     //number of hits to skip
                        .size(size),    //number of hits to return
                ProductIndex.class);
    }
}
