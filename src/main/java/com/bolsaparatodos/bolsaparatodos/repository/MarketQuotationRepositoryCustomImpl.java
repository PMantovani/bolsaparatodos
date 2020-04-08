package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketQuotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class MarketQuotationRepositoryCustomImpl implements MarketQuotationRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void saveAllMarketQuotations(List<MarketQuotation> quotationList) {
        quotationList.forEach(i -> entityManager.persist(i));
    }

}
