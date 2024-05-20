package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;
import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopItem;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ShopItemRepo;
import com.hcmute.g2webstorev2.service.CartItemV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
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
    public CartItemV2Res addItem(CartItemRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItemV2 cartItemV2 = cartItemV2Repo.findByShopAndCustomer(product.getShop(), customer).orElse(null);
        if (cartItemV2 == null) {
            cartItemV2 = CartItemV2.builder()
                    .customer(customer)
                    .shop(product.getShop())
                    .vouchers(null)
                    .build();

            ShopItem shopItem = ShopItem.builder()
                    .cartItemV2(cartItemV2)
                    .price(Long.valueOf(product.getPrice()))
                    .name(product.getName())
                    .quantity(body.getQuantity())
                    .product(product)
                    .build();

            cartItemV2.setShopItems(Set.of(shopItem));
            cartItemV2.setShopSubTotal(shopItem.getSubTotal());
        } else {
            ShopItem shopItem = shopItemRepo.findByCartItemV2AndProduct(cartItemV2, product)
                    .orElse(null);

            if (shopItem != null) shopItem.setQuantity(shopItem.getQuantity() + body.getQuantity());
            else
                shopItem = ShopItem.builder()
                        .cartItemV2(cartItemV2)
                        .price(Long.valueOf(product.getPrice()))
                        .name(product.getName())
                        .quantity(body.getQuantity())
                        .product(product)
                        .build();


            cartItemV2.getShopItems().add(shopItem);
            cartItemV2.setShopSubTotal(cartItemV2.getShopSubTotal() + shopItem.getSubTotal());
        }

        CartItemV2Res res = Mapper.toCartItemv2Res(cartItemV2Repo.save(cartItemV2));
        log.info("Add item successfully");
        return res;
    }

    @Override
    @Transactional
    public Set<CartItemV2Res> getCartItems() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<ShopItem> shopItems = new HashSet<>();
        Set<CartItemV2> cartItemV2Set = cartItemV2Repo.findAllByCustomer(customer);

        if (cartItemV2Set != null && !cartItemV2Set.isEmpty()) {
            cartItemV2Set.forEach(cartItemV2 -> {
                Long shopSubTotal = 0L;
                for (ShopItem shopItem : cartItemV2.getShopItems()){
                    Product product = shopItem.getProduct();
                    shopItem.setName(product.getName());
                    shopItem.setPrice(Long.valueOf(product.getPrice()));
                    shopSubTotal += shopItem.getSubTotal();
                    shopItems.add(shopItem);
                }
                cartItemV2.setShopSubTotal(shopSubTotal);
            });

            shopItemRepo.saveAll(shopItems);
            cartItemV2Repo.saveAll(cartItemV2Set);
            return cartItemV2Set.stream().map(Mapper::toCartItemv2Res).collect(Collectors.toSet());
        }
        return null;
    }
}
