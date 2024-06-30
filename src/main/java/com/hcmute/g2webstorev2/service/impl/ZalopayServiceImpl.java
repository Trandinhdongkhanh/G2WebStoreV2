package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.zalopay.CreateOrderReq;
import com.hcmute.g2webstorev2.dto.request.zalopay.EmbedDataReq;
import com.hcmute.g2webstorev2.dto.request.zalopay.ItemData;
import com.hcmute.g2webstorev2.dto.response.zalopay.CreateOrderRes;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.service.ZalopayService;
import com.hcmute.g2webstorev2.util.zalopay.HMACUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZalopayServiceImpl implements ZalopayService {
    @Value("${zalopay.app-id}")
    private Integer appId;
    @Value("${zalopay.key1}")
    private String key1;
    @Value("${zalopay.key2}")
    private String key2;
    @Value("${zalopay.api.create-order}")
    private String createOrderApi;

    @Override
    public CreateOrderRes createOrder(long amount, List<Order> orders) {
        Random rand = new Random();
        int random_id = rand.nextInt(1000000);

        EmbedDataReq embed_data = EmbedDataReq.builder()
                .preferredPaymentMethod(List.of("international_card"))
                .redirectUrl("http://localhost:8002")
                .build();

        List<ItemData> items = new ArrayList<>();
        orders.forEach(order -> order.getOrderItems().forEach(orderItem -> {
            ItemData itemData = ItemData.builder()
                    .itemId(orderItem.getProductId())
                    .itemName(orderItem.getName())
                    .itemPrice(Long.valueOf(orderItem.getPrice()))
                    .itemQuantity(orderItem.getQuantity())
                    .build();
            items.add(itemData);
        }));

        CreateOrderReq req = CreateOrderReq.builder()
                .appId(appId)
                .appTransId(getCurrentTimeString("yyMMdd") + "_" + random_id)
                .appTime(System.currentTimeMillis())
                .appUser("G2WebStore")
                .amount(amount)
                .description("G2WebStore - Payment for the order #" + random_id)
                .bankCode("")   //Use Sandbox payment credentials (VISA) so the bank code must be empty
                .item(items)
                .embedData(embed_data)
                .build();

        // app_id +”|”+ app_trans_id +”|”+ appuser +”|”+ amount +"|" + app_time +”|”+ embed_data +"|" +item
        String data = req.getAppId() + "|" + req.getAppTransId() + "|" + req.getAppUser() + "|" + req.getAmount()
                + "|" + req.getAppTime() + "|" + req.getEmbedData() + "|" + req.getItem();
        req.setMac(HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data));

        // Set up headers and body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<CreateOrderReq> entity = new HttpEntity<>(req, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(createOrderApi, entity, CreateOrderRes.class);
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}