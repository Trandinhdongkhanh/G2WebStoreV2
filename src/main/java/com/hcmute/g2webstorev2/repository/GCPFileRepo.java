package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.GCPFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface GCPFileRepo extends JpaRepository<GCPFile, Long> {
    @Modifying
    @Query("delete from GCPFile gf where gf.id = :id")
    void deleteById(Long id);
}
