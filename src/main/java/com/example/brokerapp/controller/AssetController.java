package com.example.brokerapp.controller;

import com.example.brokerapp.model.Asset;
import com.example.brokerapp.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {
    @Autowired
    AssetService assetService;

    @GetMapping("/list")
    public ResponseEntity<List<Asset>> listAssets(@RequestParam String customerId) {
        try {
            List<Asset> assets = assetService.listAssets(customerId);
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
