package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.CustomerProductCompositeKey;
import com.hcmute.g2webstorev2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, CustomerProductCompositeKey> {
    List<CartItem> findAllByCustomer(Customer customer);
    Optional<CartItem> findByProductAndCustomer(Product product, Customer customer);
    void deleteAllByCustomer(Customer customer);
}
