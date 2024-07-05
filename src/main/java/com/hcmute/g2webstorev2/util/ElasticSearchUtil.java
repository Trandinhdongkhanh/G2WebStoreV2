//package com.hcmute.g2webstorev2.util;
//
//import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
//import co.elastic.clients.elasticsearch._types.query_dsl.Query;
//import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
//import co.elastic.clients.json.JsonData;
//
//public class ElasticSearchUtil {
//    public static Query getFuzzyQuery(String fieldName, String value) {
//        return FuzzyQuery.of(f -> f
//                        .field(fieldName)
//                        .value(value))
//                ._toQuery();
//    }
//
//    public static Query getRangeQuery(String fieldName, Integer start, Integer end) {
//        return RangeQuery.of(r -> r
//                        .field(fieldName)
//                        .gte(JsonData.of(start))
//                        .lte(JsonData.of(end)))
//                ._toQuery();
//    }
//}
