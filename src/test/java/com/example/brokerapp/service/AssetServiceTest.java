package com.example.brokerapp.service;

import com.example.brokerapp.model.Asset;
import com.example.brokerapp.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset sampleAsset1;
    private Asset sampleAsset2;

    @BeforeEach
    void setUp() {
        sampleAsset1 = new Asset();
        sampleAsset1.setCustomerId("123");
        sampleAsset1.setAssetName("AAPL");
        sampleAsset1.setSize(20);
        sampleAsset1.setUsableSize(15);

        sampleAsset2 = new Asset();
        sampleAsset2.setCustomerId("123");
        sampleAsset2.setAssetName("GOOGL");
        sampleAsset2.setSize(10);
        sampleAsset2.setUsableSize(5);
    }

    @Test
    void testListAssets() {
        when(assetRepository.findByCustomerId("123")).thenReturn(List.of(sampleAsset1, sampleAsset2));

        List<Asset> assets = assetService.listAssets("123");

        assertNotNull(assets);
        assertEquals(2, assets.size());
        assertEquals("AAPL", assets.get(0).getAssetName());
        assertEquals("GOOGL", assets.get(1).getAssetName());

        verify(assetRepository, times(1)).findByCustomerId("123");
    }

    @Test
    void testListAssetsWhenNoAssetsFound() {
        when(assetRepository.findByCustomerId("123")).thenReturn(List.of());

        List<Asset> assets = assetService.listAssets("123");

        assertNotNull(assets);
        assertTrue(assets.isEmpty());

        verify(assetRepository, times(1)).findByCustomerId("123");
    }
}
