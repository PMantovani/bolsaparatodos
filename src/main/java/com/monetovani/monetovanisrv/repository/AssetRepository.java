package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, String> {
}
