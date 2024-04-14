package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepo shopRepo;
    @Override
    public ShopResponse getShopInfo(Integer id) {
        return Mapper.toShopResponse(shopRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ShopResponse updateShopInfo(ShopRequest body) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Shop shop = seller.getShop();
        shop.setImage(body.getImage());
        shop.setName(body.getName());
        shop.setWard(body.getWard());
        shop.setDistrict(body.getDistrict());
        shop.setProvince(body.getProvince());
        shop.setStreet(body.getStreet());
        shop.setDistrictId(body.getDistrictId());

        shopRepo.save(shop);
        log.info("Shop with ID = " + shop.getShopId() + " updated successfully");
        return Mapper.toShopResponse(shop);
    }

    @Override
    public void delShop(Integer id) {
        Shop shop = shopRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + id + " not found"));

        shopRepo.delete(shop);
        log.info("Shop with ID = " + id + " deleted successfully");
    }
}
