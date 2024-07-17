package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.SellerRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.EmailService;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepo shopRepo;
    private final FileService fileService;
    private final CartItemV2Repo cartItemV2Repo;
    private final ProductRepo productRepo;
    private final EmailService emailService;
    private final SellerRepo sellerRepo;

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
        shop.setWardCode(body.getWardCode());
        shop.setWardName(body.getWardName());
        shop.setDistrictId(body.getDistrictId());
        shop.setDistrictName(body.getDistrictName());
        shop.setProvinceId(body.getProvinceId());
        shop.setProvinceName(body.getProvinceName());
        shop.setStreet(body.getStreet());
        shop.setDistrictId(body.getDistrictId());
        shop.setBankAccHolderName(body.getBankAccHolderName());
        shop.setBankName(body.getBankName());
        shop.setBankAccSeriesNum(body.getBankAccSeriesNum());

        shopRepo.save(shop);
        log.info("Shop with ID = " + shop.getShopId() + " updated successfully");
        return Mapper.toShopResponse(shop);
    }

    @Override
    @Transactional
    public ShopResponse uploadShopImage(MultipartFile file) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Shop shop = seller.getShop();

        List<GCPFile> images = fileService.uploadFiles(new MultipartFile[]{file});
        shop.setImage(images.get(0));
        images.get(0).setShop(shop);

        ShopResponse res = Mapper.toShopResponse(shopRepo.save(shop));
        log.info("Shop with ID = " + res.getShopId() + " upload image successfully");
        return res;
    }

    @Override
    public Page<ShopResponse> getAllShops(int pageNum, int pageSize, boolean isReadyToBanned) {
        if (isReadyToBanned)
            return shopRepo.getReadyToBannedShops(PageRequest.of(pageNum, pageSize)).map(Mapper::toShopResponse);
        return shopRepo.findAll(PageRequest.of(pageNum, pageSize, Sort.by("shopId").descending()))
                .map(Mapper::toShopResponse);
    }

    @Override
    @Transactional
    public ShopResponse lockShop(Integer shopId) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        shop.setIsAllowedToSell(false);
        cartItemV2Repo.deleteAllByShop(shop);
        List<Product> products = productRepo.findAllByShop(shop);
        products.forEach(product -> {
            product.setIsBanned(true);
            product.setIsAvailable(false);
        });
        productRepo.saveAll(products);
        Seller seller = sellerRepo.findByShopAndIsMainAcc(shop)
                        .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        emailService.sendLockShopNotification("Thông báo khóa vĩnh viễn tài khoản shop", seller.getEmail());
        return Mapper.toShopResponse(shop);
    }
}
