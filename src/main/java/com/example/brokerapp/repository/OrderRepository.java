package com.example.brokerapp.repository;

import com.example.brokerapp.model.Order;
import com.example.brokerapp.model.OrderSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween(String customerId, Date startDate, Date endDate);

    Order findByIdAndStatus(Long id, String status);

    List<Order> findByCustomerId(String customerId);

    List<Order> findByAssetNameAndPriceAndStatus(String assetName, Double price, String status);

    @Query(value = "SELECT asset_name AS assetName, order_side AS orderSide, price, " +
            "SUM(price*size) AS totalAmount, COUNT(size) AS totalSize " +
            "FROM orders WHERE status = 'PENDING' " +
            "GROUP BY asset_name, order_side, price",
            nativeQuery = true)
    List<OrderSummaryDTO> getPendingOrderSummary();


}
