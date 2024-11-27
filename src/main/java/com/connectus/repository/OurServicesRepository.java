package com.connectus.repository;

import com.connectus.entity.OurServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OurServicesRepository extends JpaRepository<OurServices, Long> {
}
