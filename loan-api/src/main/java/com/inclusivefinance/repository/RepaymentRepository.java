package com.inclusivefinance.repository;

import com.inclusivefinance.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    List<Repayment> findByLoanIdOrderByPeriodNo(Long loanId);

    List<Repayment> findByLoanIdAndStatus(Long loanId, String status);
}
