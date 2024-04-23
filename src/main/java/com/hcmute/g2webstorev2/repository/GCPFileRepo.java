package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.GCPFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GCPFileRepo extends JpaRepository<GCPFile, Integer> {

}
