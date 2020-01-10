package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


}
