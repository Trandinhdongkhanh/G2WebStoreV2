package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address, Integer> {
    List<Address> findAllByCustomer(Customer customer);

    Address findByReceiverPhoneNo(String phoneNo);
}
