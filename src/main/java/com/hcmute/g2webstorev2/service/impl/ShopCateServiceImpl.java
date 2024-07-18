package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ShopCateRequest;
import com.hcmute.g2webstorev2.dto.response.ShopCateResponse;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.ShopCategory;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.ShopCateRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.ShopCateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopCateServiceImpl implements ShopCateService {
    private final ShopCateRepo shopCateRepo;
    private final ShopRepo shopRepo;
    @Override
    public ShopCateResponse getShopCategory(Integer id) {
        return Mapper.toShopCateResponse(shopCateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop category with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public void updateShopCategory(ShopCateRequest body, Integer id) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (shopCateRepo.existsByNameAndShop_ShopId(body.getName(), seller.getShop().getShopId()))
            throw new ResourceNotUniqueException("Duplicate category name");

        ShopCategory category = shopCateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        ShopCategory parentCategory = null;
        if (body.getParentId() != null) {
            parentCategory = shopCateRepo.findById(body.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent category with ID = " + body.getParentId() + " not found"));
        }

        if (!Objects.equals(category.getShop().getShopId(), seller.getShop().getShopId()))
            throw new AccessDeniedException("Access denied, seller not belong to this shop");

        category.setParentCategory(parentCategory);
        category.setName(body.getName());

        if (parentCategory == null) category.setPath(category.getId().toString());
        else category.setPath(parentCategory.getPath() + "/" + category.getId().toString());

        log.info("Update category with ID = " + id + " successfully");
    }

    @Override
    @Transactional
    public void delShopCategory(Integer id) {
        ShopCategory category = shopCateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        if (category.getParentCategory() != null) {
            category.getParentCategory().getChildCategories().remove(category);
            category.setParentCategory(null);
        }

        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(category.getShop().getShopId(), seller.getShop().getShopId()))
            throw new AccessDeniedException("Access denied, seller not belong to this shop");

        shopCateRepo.delete(category);

        log.info("Category with ID = " + id + " deleted successfully");
    }

    @Override
    @Transactional
    public ShopCateResponse addShopCategory(ShopCateRequest body) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (shopCateRepo.existsByNameAndShop_ShopId(body.getName(), seller.getShop().getShopId()))
            throw new ResourceNotUniqueException("Duplicate category name");

        ShopCategory parentCategory = null;
        if (body.getParentId() != null) {
            parentCategory = shopCateRepo.findById(body.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent category with ID = " + body.getParentId() + " not found"));
        }
        ShopCategory shopCategory = shopCateRepo.save(ShopCategory.builder()
                .name(body.getName())
                .parentCategory(parentCategory)
                .shop(seller.getShop())
                .build());

        if (parentCategory == null) shopCategory.setPath(shopCategory.getId().toString());
        else shopCategory.setPath(parentCategory.getPath() + "/" + shopCategory.getId().toString());

        ShopCateResponse res = Mapper.toShopCateResponse(shopCategory);
        log.info("Category created successfully");
        return res;
    }

    @Override
    public List<ShopCateResponse> getAllShopCategoriesByShopId(Integer id) {
        if (!shopRepo.existsByShopId(id)) throw new ResourceNotFoundException("Shop with ID = " + id + " not found");
        return shopCateRepo.findAllShopCategoriesByShop(id)
                .stream().map(Mapper::toShopCateResponse)
                .collect(Collectors.toList());
    }
}
