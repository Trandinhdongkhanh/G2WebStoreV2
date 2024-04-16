package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Integer> {

}
