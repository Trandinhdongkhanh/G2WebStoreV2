package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ghn.CreateOrderReq;
import com.hcmute.g2webstorev2.dto.request.ghn.ExpectedDeliveryDateReq;
import com.hcmute.g2webstorev2.dto.request.ghn.FeeShipReq;
import com.hcmute.g2webstorev2.dto.response.ghn.*;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.PaymentType;
import com.hcmute.g2webstorev2.exception.GHNException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.repository.AddressRepo;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.GHNService;
import com.hcmute.g2webstorev2.util.UnixTimestampConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @Value("${ghn.expected-delivery-url}")
    private String expectedDeliveryDateUrl;
    @Value("${ghn.create-order-url}")
    private String createOrderUrl;
    @Value("${ghn.print-order.url}")
    private String printOrderUrl;
    @Value("${ghn.print-order.return-url.printA5}")
    private String printA5Url;
    @Value("${ghn.print-order.return-url.print80x80}")
    private String print80x80Url;
    @Value("${ghn.print-order.return-url.print50x72}")
    private String print50x72Url;
    private final CartItemV2Repo cartItemV2Repo;
    private final AddressRepo addressRepo;
    private final ProductRepo productRepo;

    private float getChargeableWeight(CartItemV2 cartItemV2) {
        float calWeight = 0; //unit: kg
        float realWeight = 0;
        for (ShopItem item : cartItemV2.getShopItems()) {
            Product product = item.getProduct();
            //Recipe which calculate order weight to get the real shipping fee
            calWeight += ((product.getLength() * product.getWidth() * product.getHeight()) / 6000) * item.getQuantity();
            realWeight += product.getWeight() * item.getQuantity();
        }
        return Math.max(calWeight, realWeight / 1000);
    }

    private float getChargeableWeight(Order order) {
        float calWeight = 0; //unit: kg
        float realWeight = 0;
        for (OrderItem item : order.getOrderItems()) {
            //Recipe which calculate order weight to get the real shipping fee
            calWeight += ((item.getLength() * item.getWidth() * item.getHeight()) / 6000) * item.getQuantity();
            realWeight += item.getWeight() * item.getQuantity();
        }
        return Math.max(calWeight, realWeight / 1000);
    }

    @Override
    public CalculateFeeApiRes calculateFeeShip(Integer addressId, PaymentType paymentType, Long cartItemId) {
        HttpHeaders headers = setUpGhnHeaders();

        CartItemV2 cartItemV2 = cartItemV2Repo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));


        int codValue = 0;
        if (paymentType.equals(PaymentType.COD)) {
            codValue = Math.toIntExact(cartItemV2.getShopSubTotal() - cartItemV2.getShopReduce() - cartItemV2.getFeeShipReduce());
            if (codValue <= 0) codValue = 0;
        }

        FeeShipReq reqBody = FeeShipReq.builder()
                .serviceTypeId(2) //2: Chuyển phát thương mại điện tử
                .fromDistrictId(cartItemV2.getShop().getDistrictId())
                .fromWardCode(cartItemV2.getShop().getWardCode())
                .toWardCode(address.getWardCode())
                .toDistrictId(address.getDistrictId())
                .weight((int) (getChargeableWeight(cartItemV2) * 1000))    //unit = gram
                .codValue(codValue)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<FeeShipReq> entity = new HttpEntity<>(reqBody, headers);
        return restTemplate.postForObject(feeShipUrl, entity, CalculateFeeApiRes.class);
    }

    @Override
    public LocalDateTime calculateExceptedDeliveryDate(ExpectedDeliveryDateReq body) {
        HttpHeaders headers = setUpGhnHeaders();
        ExpectedDeliveryDateReq reqBody = ExpectedDeliveryDateReq.builder()
                .fromDistrictId(body.getFromDistrictId())
                .fromWardCode(body.getFromWardCode())
                .toDistrictId(body.getToDistrictId())
                .toWardCode(body.getToWardCode())
                .serviceId(53320)   //53320: Chuyển phát thương mại điện tử
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ExpectedDeliveryDateReq> entity = new HttpEntity<>(reqBody, headers);
        ExpectedDeliveryDateApiRes res = restTemplate.postForObject(expectedDeliveryDateUrl, entity, ExpectedDeliveryDateApiRes.class);
        if (res != null && res.getCode() == 200) return UnixTimestampConverter.covert(res.getData().getLeadTime());
        throw new GHNException(res.getMessage());
    }

    @Override
    public CreateOrderApiRes createOrder(Order order) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int codAmount = 0;
        if (order.getPaymentType().equals(PaymentType.COD)) codAmount += order.getGrandTotal() - order.getFeeShip();
        List<CreateOrderItemData> items = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> {
            CreateOrderItemData item = CreateOrderItemData.builder()
                    .name(orderItem.getName())
                    .quantity(orderItem.getQuantity())
                    .code(String.valueOf(orderItem.getProductId()))
                    .price(orderItem.getPrice())
                    .build();
            items.add(item);
        });

        String fromAddress = order.getShop().getStreet() + ", " + order.getShop().getWardName() + ", " +
                order.getShop().getDistrictName() + ", " + order.getShop().getProvinceName();
        String toAddress = order.getAddress().getOrderReceiveAddress() + ", " + order.getAddress().getWardName() + ", " +
                order.getAddress().getDistrictName() + ", " + order.getAddress().getProvinceName();

        HttpHeaders headers = setUpGhnHeaders();
        CreateOrderReq reqBody = CreateOrderReq.builder()
                .fromPhone(seller.getPhoneNo())
                .fromName(order.getShop().getName())
                .fromAddress(fromAddress)
                .fromWardName(order.getShop().getWardName())
                .fromDistrictName(order.getShop().getDistrictName())
                .fromProvinceName(order.getShop().getProvinceName())
                .toName(order.getAddress().getReceiverName())
                .toPhone(order.getAddress().getReceiverPhoneNo())
                .toAddress(toAddress)
                .toWardName(order.getAddress().getWardName())
                .toDistrictName(order.getAddress().getDistrictName())
                .toProvinceName(order.getAddress().getProvinceName())
                .codAmount(codAmount)
                .weight((int) (getChargeableWeight(order) * 1000)) //unit = gram
                .serviceTypeId(2) //2: Chuyển phát thương mại điện tử
                .paymentTypeId(2) //2: Người mua/Người nhận thanh toán phí dịch vụ
                .requiredNote("KHONGCHOXEMHANG")
                .items(items)
                .build();

        HttpEntity<CreateOrderReq> entity = new HttpEntity<>(reqBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        CreateOrderApiRes res = restTemplate.postForObject(createOrderUrl, entity, CreateOrderApiRes.class);
        if (res != null && res.getCode() == 200) return res;
        throw new GHNException(res.getMessage());
    }

    @Override
    public String printOrder(String ghnOrderCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);

        Map<String, List<String>> body = new HashMap<>();
        body.put("order_codes", List.of(ghnOrderCode));

        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PrintOrderApiRes> response = restTemplate.exchange(
                printOrderUrl,
                HttpMethod.POST,
                entity,
                PrintOrderApiRes.class
        );

        PrintOrderApiRes res = response.getBody();
        if (res != null && res.getCode() == 200) return printA5Url + res.getData().getToken();
        throw new GHNException(res.getMessage());
    }

    private HttpHeaders setUpGhnHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Token", token);
        headers.set("ShopId", shopId.toString());
        return headers;
    }
}
