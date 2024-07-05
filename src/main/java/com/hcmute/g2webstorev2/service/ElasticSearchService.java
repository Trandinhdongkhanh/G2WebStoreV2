//package com.hcmute.g2webstorev2.service;
//
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import com.hcmute.g2webstorev2.entity.Product;
//
//import java.io.IOException;
//
//public interface ElasticSearchService {
//    IndexResponse addProduct(Product product) throws IOException;
//    SearchResponse<Product> fuzzyQueryProducts(String name, Integer startPrice, Integer endPrice) throws IOException;
//}
