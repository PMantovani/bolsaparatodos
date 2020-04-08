package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, String> {
}
