package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ghn.FeeShipReq;
import com.hcmute.g2webstorev2.dto.response.ghn.CalculateFeeApiRes;
import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.ShopItem;
import com.hcmute.g2webstorev2.service.GHNService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class GHnServiceImpl implements GHNService {
    @Value("${ghn.token}")
    private String token;
    @Value("${ghn.shop-id}")
    private Integer shopId;
    @Value("${ghn.fee-ship-url}")
    private String feeShipUrl;

    @Override
    public CalculateFeeApiRes calculateFeeShip(Shop shop, Address address, List<ShopItem> shopItems, Integer codValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        headers.set("Token", token);
        headers.set("ShopId", shopId.toString());

        int chargeableWeight = 0; //unit = kilogram
        for (ShopItem item : shopItems) {
            Product product = item.getProduct();
            //Recipe which calculate order weight to get the real shipping fee
            chargeableWeight += (int) (product.getLength() * product.getWidth() * product.getHeight()) / 6000;
            chargeableWeight *= item.getQuantity();
        }

        FeeShipReq reqBody = FeeShipReq.builder()
                .serviceTypeId(2) //2: Chuyển phát thương mại điện tử
                .fromDistrictId(shop.getDistrictId())
                .fromWardCode(shop.getWard())
                .toWardCode(address.getWard())
                .toDistrictId(address.getDistrictId())
                .weight(chargeableWeight * 1000)    //unit = gram
                .codValue(codValue)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<FeeShipReq> entity = new HttpEntity<>(reqBody, headers);
        return restTemplate.postForObject(feeShipUrl, entity, CalculateFeeApiRes.class);
    }
}
