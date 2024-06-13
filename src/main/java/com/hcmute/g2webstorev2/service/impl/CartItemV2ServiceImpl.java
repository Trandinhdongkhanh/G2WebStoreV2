package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.entity.composite_key.CartItemVoucherKey;
import com.hcmute.g2webstorev2.exception.InvalidVoucherException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.*;
import com.hcmute.g2webstorev2.service.CartItemV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemV2ServiceImpl implements CartItemV2Service {
    private final CartItemV2Repo cartItemV2Repo;
    private final ProductRepo productRepo;
    private final ShopItemRepo shopItemRepo;

    @Override
    @Transactional
    public void addItem(CartItemRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItemV2 cartItemV2 = cartItemV2Repo.findByShopAndCustomer(product.getShop(), customer)
                .orElse(null);
        LocalDate now = LocalDate.now();

        //If there is no items of shop appear in cart
        if (cartItemV2 == null) {
            cartItemV2 = CartItemV2.builder()
                    .customer(customer)
                    .shop(product.getShop())
                    .shopItems(null)
                    .cartItemVouchers(null)
                    .build();

            ShopItem shopItem = ShopItem.builder()
                    .cartItemV2(cartItemV2)
                    .quantity(body.getQuantity())
                    .product(product)
                    .build();

            List<CartItemVoucher> cartItemVouchers = new LinkedList<>();
            for (Voucher voucher : product.getVouchers()) {
                if (!isValidVoucher(voucher, now)) continue;
                CartItemVoucherKey key = new CartItemVoucherKey(cartItemV2.getCartItemId(), voucher.getVoucherId());
                CartItemVoucher cartItemVoucher = CartItemVoucher.builder()
                        .key(key)
                        .cartItemV2(cartItemV2)
                        .voucher(voucher)
                        .isSelected(false)
                        .isEligible(null)
                        .build();
                cartItemVouchers.add(cartItemVoucher);
            }
            cartItemV2.setCartItemVouchers(cartItemVouchers);
            cartItemV2.setShopItems(List.of(shopItem));
            cartItemV2Repo.save(cartItemV2);
            log.info("Cart item saved successfully");
            return;
        }

        ShopItem shopItem = cartItemV2.getShopItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getProductId(), product.getProductId()))
                .findFirst().orElse(null);

        if (shopItem == null) {
            shopItem = ShopItem.builder()
                    .cartItemV2(cartItemV2)
                    .quantity(body.getQuantity())
                    .product(product)
                    .build();

            for (Voucher voucher : product.getVouchers()) {
                if (!isValidVoucher(voucher, now)) continue;
                CartItemVoucher cartItemVoucher = cartItemV2.getCartItemVouchers().stream()
                        .filter(v -> Objects.equals(v.getKey().getVoucherId(), voucher.getVoucherId()))
                        .findFirst().orElse(null);
                if (cartItemVoucher == null) {
                    cartItemVoucher = CartItemVoucher.builder()
                            .key(new CartItemVoucherKey(cartItemV2.getCartItemId(), voucher.getVoucherId()))
                            .voucher(voucher)
                            .cartItemV2(cartItemV2)
                            .isSelected(false)
                            .isEligible(null)
                            .build();
                    cartItemV2.getCartItemVouchers().add(cartItemVoucher);
                }
            }
            cartItemV2.getShopItems().add(shopItem);
            cartItemV2Repo.save(cartItemV2);
            log.info("Cart item saved successfully");
            return;
        }
        shopItem.setQuantity(shopItem.getQuantity() + body.getQuantity());
        cartItemV2.getShopItems().add(shopItem);
        shopItemRepo.save(shopItem);
    }

    private boolean isValidVoucher(Voucher voucher, LocalDate now) {
        return (!voucher.getIsPaused() && voucher.getStartDate().isBefore(now) && voucher.getEndDate().isAfter(now));
    }

    @Override
    @Transactional
    public List<CartItemV2Res> getCartItems() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDate now = LocalDate.now();
        List<CartItemV2> cartItemV2List = cartItemV2Repo.findAllByCustomer(customer);
        for (CartItemV2 cartItemV2 : cartItemV2List) {
            Set<CartItemVoucher> cartItemVouchers = cartItemV2.getCartItemVouchers().stream()
                    .filter(itemVoucher -> isVoucherExisted(cartItemV2.getShopItems(), itemVoucher))
                    .filter(itemVoucher -> isValidVoucher(itemVoucher.getVoucher(), now))
                    .collect(Collectors.toSet());

            cartItemVouchers.forEach(cartItemVoucher ->
                cartItemVoucher.setIsEligible(cartItemV2.getShopSubTotal() >= cartItemVoucher.getVoucher().getMinSpend()));

            Set<CartItemVoucher> sortedSet = cartItemVouchers.stream()
                    .sorted(Comparator.comparingInt(cartItemVoucher -> cartItemVoucher.getVoucher().getReducePrice()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            cartItemV2.setCartItemVouchers(new LinkedList<>(sortedSet));
        }
        return cartItemV2Repo.saveAll(cartItemV2List).stream().map(Mapper::toCartItemv2Res).toList();
    }

    private boolean isVoucherExisted(List<ShopItem> shopItems, CartItemVoucher cartItemVoucher) {
        for (ShopItem shopItem : shopItems)
            for (Voucher voucher : shopItem.getProduct().getVouchers())
                if (Objects.equals(cartItemVoucher.getVoucher().getVoucherId(), voucher.getVoucherId()))
                    return true;
        return false;
    }

    @Override
    @Transactional
    public void selectVoucher(Long cartItemV2Id, String voucherId) {
        CartItemV2 cartItemV2 = cartItemV2Repo.findById(cartItemV2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found"));

        CartItemVoucher cartItemVoucher = cartItemV2.getCartItemVouchers().stream()
                .filter(v -> Objects.equals(v.getVoucher().getVoucherId(), voucherId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        if (cartItemVoucher.getIsEligible()) {
            for (CartItemVoucher v : cartItemV2.getCartItemVouchers()) {
                if (Objects.equals(v.getVoucher().getVoucherId(), cartItemVoucher.getVoucher().getVoucherId())) {
                    v.setIsSelected(!v.getIsSelected());
                    continue;
                }
                if (Objects.equals(v.getVoucher().getVoucherType(), cartItemVoucher.getVoucher().getVoucherType()))
                    v.setIsSelected(false);
            }
        }
        throw new InvalidVoucherException("Can't apply voucher");
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

    @Override
    @Transactional
    public void delAllItem() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartItemV2Repo.deleteAllByCustomer(customer);
        log.info("All items deleted");
    }
}
