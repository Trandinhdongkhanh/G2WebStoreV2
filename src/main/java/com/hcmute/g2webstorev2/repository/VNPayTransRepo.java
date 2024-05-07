package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.VNPAYTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VNPayTransRepo extends JpaRepository<VNPAYTransaction, String> {
    @Query("select t from VNPAYTransaction t where " +
            "t.vnp_TxnRef like :vnp_TxnRef")
    List<VNPAYTransaction> findAllByVnp_TxnRef(String vnp_TxnRef);

    int deleteAllByOrder(Order order);
}
