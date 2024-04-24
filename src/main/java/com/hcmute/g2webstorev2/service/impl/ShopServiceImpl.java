package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepo shopRepo;
    @Autowired
    private FileService fileService;

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

    @Override
    @Transactional
    public ShopResponse uploadShopImage(MultipartFile file) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Shop shop = seller.getShop();

        if (shop.getImage() != null) fileService.delFile(shop.getImage().getId());

        List<GCPFile> images = fileService.uploadFiles(new MultipartFile[]{file});
        shop.setImage(images.get(0));
        images.get(0).setShop(shop);

        ShopResponse res = Mapper.toShopResponse(shopRepo.save(shop));
        log.info("Shop with ID = " + res.getShopId() + " upload image successfully");
        return res;
    }
}
