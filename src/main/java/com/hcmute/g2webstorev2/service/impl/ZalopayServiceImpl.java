package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.config.ZaloPayConfig;
import com.hcmute.g2webstorev2.dto.request.zalopay.CreateOrderReq;
import com.hcmute.g2webstorev2.dto.request.zalopay.EmbedDataReq;
import com.hcmute.g2webstorev2.dto.request.zalopay.ItemData;
import com.hcmute.g2webstorev2.dto.response.zalopay.CallBackData;
import com.hcmute.g2webstorev2.dto.response.zalopay.CallBackRes;
import com.hcmute.g2webstorev2.dto.response.zalopay.CreateOrderRes;
import com.hcmute.g2webstorev2.dto.response.zalopay.ZaloPayServerRes;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.service.ZalopayService;
import com.hcmute.g2webstorev2.util.zalopay.HMACUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZalopayServiceImpl implements ZalopayService {
    private final ZaloPayConfig config;
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
                .appId(config.getAppId())
                .appTransId(getCurrentTimeString("yyMMdd") + "_" + random_id)
                .appTime(System.currentTimeMillis())
                .appUser("G2WebStore")
                .amount(amount)
                .description("G2WebStore - Payment for the order #" + random_id)
                .bankCode("")   //Use Sandbox payment credentials (VISA) so the bank code must be empty
                .item(items)
                .callbackUrl(config.getCallBackUrl())
                .embedData(embed_data)
                .build();

        // app_id +”|”+ app_trans_id +”|”+ appuser +”|”+ amount +"|" + app_time +”|”+ embed_data +"|" +item
        String data = req.getAppId() + "|" + req.getAppTransId() + "|" + req.getAppUser() + "|" + req.getAmount()
                + "|" + req.getAppTime() + "|" + req.getEmbedData() + "|" + req.getItem();
        req.setMac(HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.getKey1(), data));

        // Set up headers and body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<CreateOrderReq> entity = new HttpEntity<>(req, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(config.getCreateOrderApi(), entity, CreateOrderRes.class);
    }

    @Override
    public ZaloPayServerRes handleCallBackData(CallBackRes cbRes) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(config.getKey2().getBytes(), "HmacSHA256"));
        ZaloPayServerRes result;

        try {
            CallBackData cbData = cbRes.getData();
            String reqMac = cbRes.getMac();

            byte[] hashBytes = HmacSHA256.doFinal(cbData.toString().getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // kiểm tra callback hợp lệ (đến từ ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback không hợp lệ
                result = ZaloPayServerRes.builder()
                        .returnCode(-1)
                        .returnMessage("mac not equal")
                        .build();
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng
                log.info("update order's status = success where app_trans_id = " + cbData.getAppTransId());
                result = ZaloPayServerRes.builder()
                        .returnCode(1)
                        .returnMessage("success")
                        .build();
            }
        } catch (Exception ex) {
            result = ZaloPayServerRes.builder()
                    .returnCode(0) // ZaloPay server sẽ callback lại (tối đa 3 lần)
                    .returnMessage(ex.getMessage())
                    .build();
        }

        // thông báo kết quả cho ZaloPay server
        return result;
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}