package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.es.index.ProductIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductESRepo extends ElasticsearchRepository<ProductIndex, Integer> {

}
