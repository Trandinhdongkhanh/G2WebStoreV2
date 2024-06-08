package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemV2Repo extends JpaRepository<CartItemV2, Long> {
    Optional<CartItemV2> findByShopAndCustomer(Shop shop, Customer customer);

    List<CartItemV2> findAllByCustomer(Customer customer);
    int deleteAllByShop(Shop shop);
}
