package com.inclusivefinance.repository;

import com.inclusivefinance.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByCreditCode(String creditCode);

    List<Enterprise> findByNameContaining(String name);
}
