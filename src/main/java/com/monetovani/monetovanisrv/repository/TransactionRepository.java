package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.TransactionHeader;
import com.monetovani.monetovanisrv.model.AssetQuantity;
import com.monetovani.monetovanisrv.model.BalanceInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionHeader, Long> {

    List<TransactionHeader> findByUserId(long user);
    Optional<TransactionHeader> findByIdAndUserId(long id, long userId);
    List<TransactionHeader> findByUserIdAndPaymentDateBetweenOrderByPaymentDate
            (long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT " +
            "asset_code as assetCode, SUM(quantity) as quantity " +
            "FROM transaction_header th " +
            "INNER JOIN transaction_item ti ON th.id = ti.header_id " +
            "WHERE user_id = ?1 " +
            "AND th.payment_date < ?2 " +
            "GROUP BY asset_code", nativeQuery = true)
    List<AssetQuantity> findAssetsQuantitiesUntilDate(long userId, String endDate);

    @Query(value = "SELECT " +
            "SUM(md.close_value * ti.quantity) AS balance, " +
            "CAST(md.quotation_datetime AS DATE) AS date " +
            "FROM transaction_header th " +
            "INNER JOIN transaction_item ti ON th.id = ti.header_id " +
            "INNER JOIN market_data md ON ti.asset_code = md.asset_code " +
            "WHERE user_id = ?1 " +
            "AND md.quotation_datetime BETWEEN ?2 AND ?3 " +
            "AND th.payment_date BETWEEN ?2 AND ?3 " +
            "GROUP BY CAST(md.quotation_datetime AS DATE) " +
            "ORDER BY CAST(md.quotation_datetime AS DATE) ASC"
    , nativeQuery = true)
    List<BalanceInterface> findDailyBalancesInRange(long userId, String startDate, String endDate);

}
