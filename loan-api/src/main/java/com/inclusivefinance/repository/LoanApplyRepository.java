package com.inclusivefinance.repository;

import com.inclusivefinance.entity.LoanApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplyRepository extends JpaRepository<LoanApply, Long> {

    Page<LoanApply> findByEnterpriseIdOrderByApplyDateDesc(Long enterpriseId, Pageable pageable);

    Page<LoanApply> findByEnterpriseIdAndStatusOrderByApplyDateDesc(Long enterpriseId, String status, Pageable pageable);

    List<LoanApply> findByEnterpriseId(Long enterpriseId);

    Page<LoanApply> findAllByOrderByApplyDateDesc(Pageable pageable);

    Page<LoanApply> findByStatusOrderByApplyDateDesc(String status, Pageable pageable);

    @Query(value = "SELECT DATE_FORMAT(apply_date, '%Y-%m') AS month, " +
                   "COUNT(*) AS apply, " +
                   "SUM(CASE WHEN status IN ('APPROVED','GRANTED','REPAID','OVERDUE') THEN 1 ELSE 0 END) AS approved, " +
                   "SUM(CASE WHEN status IN ('GRANTED','REPAID','OVERDUE') THEN 1 ELSE 0 END) AS disbursed " +
                   "FROM loan_apply GROUP BY DATE_FORMAT(apply_date, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Object[]> findMonthlyStats();
}
