package com.connectus.repository;

import com.connectus.entity.AbouthUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbouthUsRepository extends JpaRepository<AbouthUs, Long> {
    Optional<AbouthUs> findFirstByOrderByIdAsc();

}
