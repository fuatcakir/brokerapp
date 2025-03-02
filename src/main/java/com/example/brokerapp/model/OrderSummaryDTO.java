package com.example.brokerapp.model;


import java.math.BigDecimal;

public interface OrderSummaryDTO {
    String getAssetName();

    String getOrderSide();

    BigDecimal getPrice();

    BigDecimal getTotalAmount();

    Integer getTotalSize();
}
