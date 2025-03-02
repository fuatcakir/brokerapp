package com.example.brokerapp.repository;

import com.example.brokerapp.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Long> {
    List<Asset> findByCustomerId(String customerId);
    List<Asset> findByCustomerIdAndAssetName(String customerId,String assetName);
}
