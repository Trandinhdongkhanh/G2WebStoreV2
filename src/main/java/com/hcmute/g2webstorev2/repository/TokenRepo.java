package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {

    @Query("select t from Token t inner join " +
            "Customer c on t.customer.customerId = c.customerId " +
            "where c.customerId = :cusId and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByCustomer(Integer cusId);

    @Query("select t from Token t inner join " +
            "Seller s on t.seller.sellerId = s.sellerId " +
            "where s.sellerId = :sellerId and (t.expired = false or t.revoked = false )")
    List<Token> findAllValidTokenBySeller(Integer sellerId);

    @Query("select t from Token t inner join " +
            "Admin a on t.admin.adminId = a.adminId " +
            "where a.adminId = :adminId and (t.expired = false or t.revoked = false )")
    List<Token> findAllValidTokenByAdmin(Integer adminId);

    Optional<Token> findByToken(String token);


}
