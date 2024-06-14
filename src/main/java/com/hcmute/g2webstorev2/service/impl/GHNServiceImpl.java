package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ghn.FeeShipReq;
import com.hcmute.g2webstorev2.dto.response.ghn.CalculateFeeApiRes;
import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopItem;
import com.hcmute.g2webstorev2.enums.PaymentType;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.repository.AddressRepo;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.service.GHNService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
public class GHNServiceImpl implements GHNService {
    @Value("${ghn.token}")
    private String token;
    @Value("${ghn.shop-id}")
    private Integer shopId;
    @Value("${ghn.fee-ship-url}")
    private String feeShipUrl;
    private final CartItemV2Repo cartItemV2Repo;
    private final AddressRepo addressRepo;

    @Override
    public CalculateFeeApiRes calculateFeeShip(Integer addressId, PaymentType paymentType, Long cartItemId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Token", token);
        headers.set("ShopId", shopId.toString());

        CartItemV2 cartItemV2 = cartItemV2Repo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        float chargeableWeight = 0; //unit: kg
        for (ShopItem item : cartItemV2.getShopItems()) {
            Product product = item.getProduct();
            //Recipe which calculate order weight to get the real shipping fee
            chargeableWeight += (product.getLength() * product.getWidth() * product.getHeight()) / 6000;
            chargeableWeight *= item.getQuantity();
        }

        int codValue = 0;
        if (paymentType.equals(PaymentType.COD)) {
            codValue = Math.toIntExact(cartItemV2.getShopSubTotal() - cartItemV2.getShopReduce() - cartItemV2.getFeeShipReduce());
            if (codValue <= 0) codValue = 0;
        }

        FeeShipReq reqBody = FeeShipReq.builder()
                .serviceTypeId(2) //2: Chuyển phát thương mại điện tử
                .fromDistrictId(cartItemV2.getShop().getDistrictId())
//                .fromWardCode(cartItemV2.getShop().getWard())
//                .toWardCode(address.getWard())
                .toDistrictId(address.getDistrictId())
                .weight((int) (chargeableWeight * 1000))    //unit = gram
                .codValue(codValue)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<FeeShipReq> entity = new HttpEntity<>(reqBody, headers);
        return restTemplate.postForObject(feeShipUrl, entity, CalculateFeeApiRes.class);
    }
}
