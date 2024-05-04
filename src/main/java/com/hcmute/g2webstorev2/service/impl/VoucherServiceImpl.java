package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToProductsReq;
import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.Voucher;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.exception.VoucherException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.repository.VoucherRepo;
import com.hcmute.g2webstorev2.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hcmute.g2webstorev2.enums.DiscountType.*;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    private VoucherRepo voucherRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ShopRepo shopRepo;

    @Override
    @Transactional
    public VoucherResponse addVoucher(VoucherRequest body) {
        if (voucherRepo.existsByName(body.getName()))
            throw new ResourceNotUniqueException("Duplicate voucher name");

        if (body.getReducePrice() == null && body.getReducePercent() == null)
            throw new VoucherException("Reduce price and Reduce percent can't both be null");

        if (body.getDiscountType().equals(PERCENTAGE) && (body.getReducePercent() == null || body.getReducePrice() != null))
            throw new VoucherException("PERCENTAGE discount required non null " +
                    "'reduce_percent' value and null 'reduce_price' value");

        if (body.getDiscountType().equals(PRICE) && (body.getReducePrice() == null || body.getReducePercent() != null))
            throw new VoucherException("PRICE discount required null " +
                    "'reduce_percent' value and non null 'reduce_price' value");

        if (body.getStartDate().isAfter(body.getEndDate()))
            throw new VoucherException("Start date can't be after end date");

        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Shop shop = shopRepo.findById(seller.getShop().getShopId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shop with ID = " + seller.getShop().getShopId() + " not found"));

        VoucherResponse res = Mapper.toVoucherResponse(voucherRepo.save(Voucher.builder()
                .name(body.getName())
                .startDate(body.getStartDate())
                .endDate(body.getEndDate())
                .discountType(body.getDiscountType())
                .voucherType(body.getVoucherType())
                .minSpend(body.getMinSpend())
                .reducePrice(body.getReducePrice())
                .reducePercent(body.getReducePercent())
                .quantity(body.getQuantity())
                .maxUsePerCus(body.getMaxUsePerCus())
                .shop(shop)
                .build()));

        log.info("Voucher with ID = " + res.getId() + " created successfully");
        return res;
    }

    @Override
    public VoucherResponse getVoucher(String id) {
        return Mapper.toVoucherResponse(voucherRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher with ID = " + id + " not found")));
    }

    @Override
    public List<VoucherResponse> getVouchersByProduct(Integer id) {
        if (!productRepo.existsById(id)) throw new ResourceNotFoundException("Product with ID = " + id + " not found");
        return voucherRepo.findAllByProductId(id)
                .stream().filter(voucher -> voucher.getEndDate().isAfter(LocalDate.now())
                        && voucher.getStartDate().isBefore(LocalDate.now()))
                .map(Mapper::toVoucherResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addVoucherToProducts(AddVoucherToProductsReq body, String voucherId) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Voucher voucher = voucherRepo
                .findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher with ID = " + voucherId + " not found"));

        if (voucher.getEndDate().isBefore(LocalDate.now()))
            throw new VoucherException("Voucher is expired");

        List<Product> products = new ArrayList<>();

        body.getProductIds().forEach(id -> {
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

            if (!Objects.equals(seller.getShop().getShopId(), product.getShop().getShopId()))
                throw new AccessDeniedException("You don't have permission on product with ID = " + id);

            products.add(product);
        });

        voucher.setProducts(products);
        log.info("Add voucher to products successfully");
    }
}
