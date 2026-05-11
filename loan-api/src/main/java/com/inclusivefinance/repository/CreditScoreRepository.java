package com.inclusivefinance.repository;

import com.inclusivefinance.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {

    Optional<CreditScore> findTopByEnterpriseIdOrderByEvaluatedAtDesc(Long enterpriseId);

    List<CreditScore> findByEnterpriseIdOrderByEvaluatedAtDesc(Long enterpriseId);
}
