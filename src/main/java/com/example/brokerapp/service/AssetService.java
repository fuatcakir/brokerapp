package com.example.brokerapp.service;

import com.example.brokerapp.model.Asset;
import com.example.brokerapp.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
    @Autowired
    AssetRepository assetRepository;

    public List<Asset> listAssets(String customerId) {
        return assetRepository.findByCustomerId(customerId);
    }
}
