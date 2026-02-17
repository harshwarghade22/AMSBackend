package com.harshwarghade.project.repository;

import com.harshwarghade.project.entity.TransactionCopy;

import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.domain.*;

public interface TransactionCopyRepository extends JpaRepository<TransactionCopy, Long> {
    org.springframework.data.domain.Page<TransactionCopy> findByAccountId(Long accountId, org.springframework.data.domain.Pageable pageable);
}

