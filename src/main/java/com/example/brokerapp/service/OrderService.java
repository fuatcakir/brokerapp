package com.example.brokerapp.service;

import com.example.brokerapp.model.Asset;
import com.example.brokerapp.model.Customer;
import com.example.brokerapp.model.Order;
import com.example.brokerapp.model.OrderSummaryDTO;
import com.example.brokerapp.repository.AssetRepository;
import com.example.brokerapp.repository.CustomerRepository;
import com.example.brokerapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    CustomerRepository customerRepository;

    public Order createOrder(Order order) throws Exception {
        order.setStatus("PENDING");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Customer byUsername = customerRepository.findByUsername(currentPrincipalName);

        if (order.getCustomerId() == null) {
            order.setCustomerId(String.valueOf(byUsername.getId()));
        }
        if ("SELL".equals(order.getOrderSide())) {
            List<Asset> byCustomerIdAndAssetName = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
            if (!byCustomerIdAndAssetName.isEmpty()) {
                if (byCustomerIdAndAssetName.get(0).getUsableSize() >= order.getSize()) { // 999 is admin
                    Asset asset = byCustomerIdAndAssetName.get(0);
                    asset.setUsableSize(asset.getUsableSize() - order.getSize());
                    assetRepository.save(asset);
                } else {
                    throw new Exception("Not Enough Size");
                }
            }
        }
        return orderRepository.save(order);
    }

    private void saveAsset(Order order) {
        List<Asset> byCustomerIdAndAssetName = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
        if (byCustomerIdAndAssetName.isEmpty()) {
            Asset asset = new Asset();
            asset.setCustomerId(order.getCustomerId());
            asset.setAssetName(order.getAssetName());
            asset.setSize(order.getSize());
            asset.setUsableSize(order.getSize());
            assetRepository.save(asset);
        } else {
            Asset currentasset = byCustomerIdAndAssetName.get(0);
            if ("BUY".equals(order.getOrderSide()))
                currentasset.setSize(currentasset.getSize() + order.getSize());
            if ("SELL".equals(order.getOrderSide()))
                currentasset.setSize(currentasset.getSize() - order.getSize());
            assetRepository.save(currentasset);
        }
    }

    private void updateAsset(Order order) {
        List<Asset> byCustomerIdAndAssetName = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
        if (!byCustomerIdAndAssetName.isEmpty()) {
            Asset asset = new Asset();
            asset.setCustomerId(order.getCustomerId());
            asset.setAssetName(order.getAssetName());
            asset.setSize(order.getSize());
            asset.setUsableSize(order.getSize());
            assetRepository.save(asset);
        }
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> listByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> listOrders(String customerId, Date startDate, Date endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }

    public void cancelOrder(Long orderId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Customer byUsername = customerRepository.findByUsername(currentPrincipalName);


        Order order = orderRepository.findByIdAndStatus(orderId, "PENDING");

        if (!order.getCustomerId().equals(String.valueOf(byUsername.getId()))) {
            throw new Exception("You cannot cancel someone else's order.");
        }

        if ("SELL".equals(order.getOrderSide())) {
            List<Asset> byCustomerIdAndAssetName = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
            if (!byCustomerIdAndAssetName.isEmpty()) {
                if (byCustomerIdAndAssetName.get(0).getUsableSize() >= order.getSize()) { // 999 is admin
                    Asset asset = byCustomerIdAndAssetName.get(0);
                    asset.setUsableSize(asset.getUsableSize() + order.getSize());
                    assetRepository.save(asset);
                } else {
                    throw new Exception("Not Enough Size");
                }
            }
        }

        order.setStatus("CANCELED");
        orderRepository.save(order);
    }

    public void matcher() {

        for (Order currentOrder : orderRepository.findAll()) {
            if ("BUY".equals(currentOrder.getOrderSide()) && "PENDING".equals(currentOrder.getStatus())) {
                List<Order> pendingOrders = orderRepository.findByAssetNameAndPriceAndStatus(currentOrder.getAssetName(), currentOrder.getPrice(), "PENDING");
                for (Order pendingOrder : pendingOrders) {
                    if (Objects.equals(pendingOrder.getId(), currentOrder.getId())) continue;
                    if ("SELL".equals(pendingOrder.getOrderSide())) {
                        if (currentOrder.getSize() == pendingOrder.getSize()) {
                            currentOrder.setStatus("MATCHED");
                            saveAsset(currentOrder);
                            pendingOrder.setStatus("MATCHED");
                            saveAsset(pendingOrder);
                        } else if (currentOrder.getSize() > pendingOrder.getSize()) {
                            currentOrder.setStatus("PENDING");
                            currentOrder.setSize(currentOrder.getSize() - pendingOrder.getSize());
                            updateAsset(currentOrder);
                            pendingOrder.setStatus("MATCHED");
                            saveAsset(pendingOrder);
                        } else {
                            currentOrder.setStatus("MATCHED");
                            saveAsset(currentOrder);
                            pendingOrder.setStatus("PENDING");
                            pendingOrder.setSize(pendingOrder.getSize() - currentOrder.getSize());
                            updateAsset(pendingOrder);
                        }

                        orderRepository.save(currentOrder);
                        orderRepository.save(pendingOrder);
                    }
                }
            }


        }

    }
}

