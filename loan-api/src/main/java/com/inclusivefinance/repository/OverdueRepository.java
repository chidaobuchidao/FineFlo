package com.inclusivefinance.repository;

import com.inclusivefinance.entity.Overdue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverdueRepository extends JpaRepository<Overdue, Long> {

    List<Overdue> findByEnterpriseId(Long enterpriseId);

    Page<Overdue> findAllByOrderByStartDateDesc(Pageable pageable);
}
