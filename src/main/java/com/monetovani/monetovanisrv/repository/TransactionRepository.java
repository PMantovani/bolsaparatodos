package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.Transaction;
import com.monetovani.monetovanisrv.model.Balance;
import com.monetovani.monetovanisrv.model.BalanceInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT " +
            "SUM(case when to_account_id is null and to_position_id is null then -value else value end) AS balance, " +
            "CAST(transaction_date AS DATE) AS date " +
            "FROM transaction " +
            "GROUP BY CAST(transaction_date AS DATE) " +
            "ORDER BY CAST(transaction_date AS DATE) ASC"
            , nativeQuery = true)
    List<BalanceInterface> findDailyBalance();

}
