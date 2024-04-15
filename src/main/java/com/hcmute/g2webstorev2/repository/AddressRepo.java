package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Integer> {
    List<Address> findAllByCustomer(Customer customer);

    @Query("select a from Address a where a.isDefault = true")
    Optional<Address> findByDefault(boolean isDefault);
}
