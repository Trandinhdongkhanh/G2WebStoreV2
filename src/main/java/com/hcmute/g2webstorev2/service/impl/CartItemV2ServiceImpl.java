package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToCartItemReq;
import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.exception.InvalidVoucherException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.VoucherRepo;
import com.hcmute.g2webstorev2.service.CartItemV2Service;
import com.hcmute.g2webstorev2.service.ShopItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static com.hcmute.g2webstorev2.enums.DiscountType.*;
import static com.hcmute.g2webstorev2.enums.VoucherType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemV2ServiceImpl implements CartItemV2Service {
    private final CartItemV2Repo cartItemV2Repo;
    private final ProductRepo productRepo;
    private final VoucherRepo voucherRepo;
    private final ShopItemService shopItemService;

    @Override
    @Transactional
    public CartItemV2Res addItem(CartItemRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItemV2 cartItemV2 = cartItemV2Repo.findByShopAndCustomer(product.getShop(), customer)
                .orElse(null);

        if (cartItemV2 == null) {
            cartItemV2 = CartItemV2.builder()
                    .customer(customer)
                    .shop(product.getShop())
                    .shopItems(null)
                    .vouchers(null)
                    .build();
        }

        ShopItem shopItem = shopItemService.addItem(cartItemV2, product, body.getQuantity());
        if (cartItemV2.getShopItems() != null && !cartItemV2.getShopItems().isEmpty())
            cartItemV2.getShopItems().add(shopItem);
        else cartItemV2.setShopItems(Set.of(shopItem));

        CartItemV2Res res = Mapper.toCartItemv2Res(cartItemV2Repo.save(cartItemV2));
        log.info("Cart item saved successfully");
        return res;
    }

    @Override
    public Set<CartItemV2Res> getCartItems() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartItemV2Repo.findAllByCustomer(customer)
                .stream().map(Mapper::toCartItemv2Res)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CartItemV2Res addVoucher(AddVoucherToCartItemReq body) {
        Voucher voucher = voucherRepo.findById(body.getVoucherId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        CartItemV2 cartItemV2 = cartItemV2Repo.findById(body.getCartItemV2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found"));

        if (cartItemV2.getShopSubTotal() < voucher.getMinSpend())
            throw new InvalidVoucherException("Tổng chi tiêu chưa đủ để áp dụng voucher");

        Voucher voucherInCart = cartItemV2.getVouchers().stream()
                .filter(v -> v.getId().equals(voucher.getId()))
                .findFirst()
                .orElse(null);

        if (voucherInCart != null) throw new InvalidVoucherException("Voucher đã được áp dụng");
        cartItemV2.getVouchers().add(voucher);
        return Mapper.toCartItemv2Res(cartItemV2Repo.save(cartItemV2));
    }

    @Override
    public void delItem(Long cartItemId) {
        CartItemV2 cartItemV2 = cartItemV2Repo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItemV2.getShopItems().forEach(shopItem -> shopItem.setCartItemV2(null));
        cartItemV2.setShopItems(null);

        cartItemV2Repo.delete(cartItemV2);
        log.info("Cart item deleted successfully");
    }
}
