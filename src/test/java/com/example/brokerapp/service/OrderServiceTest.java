package com.example.brokerapp.service;

import com.example.brokerapp.model.Asset;
import com.example.brokerapp.model.Order;
import com.example.brokerapp.repository.AssetRepository;
import com.example.brokerapp.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private OrderService orderService;

    private Order sampleBuyOrder;
    private Order sampleSellOrder;
    private Asset sampleAsset;

    @BeforeEach
    void setUp() {
        sampleBuyOrder = new Order();
        sampleBuyOrder.setId(1L);
        sampleBuyOrder.setCustomerId("123");
        sampleBuyOrder.setAssetName("AAPL");
        sampleBuyOrder.setOrderSide("BUY");
        sampleBuyOrder.setSize(10);
        sampleBuyOrder.setPrice(150.0);
        sampleBuyOrder.setStatus("PENDING");

        sampleSellOrder = new Order();
        sampleSellOrder.setId(2L);
        sampleSellOrder.setCustomerId("123");
        sampleSellOrder.setAssetName("AAPL");
        sampleSellOrder.setOrderSide("SELL");
        sampleSellOrder.setSize(10);
        sampleSellOrder.setPrice(150.0);
        sampleSellOrder.setStatus("PENDING");

        sampleAsset = new Asset();
        sampleAsset.setCustomerId("123");
        sampleAsset.setAssetName("AAPL");
        sampleAsset.setSize(20);
        sampleAsset.setUsableSize(20);
    }

    @Test
    void testCreateBuyOrderSuccessfully() throws Exception {
        when(orderRepository.save(any(Order.class))).thenReturn(sampleBuyOrder);

        Order createdOrder = orderService.createOrder(sampleBuyOrder);

        assertNotNull(createdOrder);
        assertEquals("PENDING", createdOrder.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateSellOrderWithSufficientAssets() throws Exception {
        when(assetRepository.findByCustomerIdAndAssetName("123", "AAPL"))
                .thenReturn(List.of(sampleAsset));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleSellOrder);

        Order createdOrder = orderService.createOrder(sampleSellOrder);

        assertNotNull(createdOrder);
        assertEquals("PENDING", createdOrder.getStatus());
        assertEquals(10, sampleAsset.getUsableSize());
        verify(assetRepository, times(1)).save(sampleAsset);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateSellOrderWithInsufficientAssets() {
        sampleAsset.setUsableSize(5);
        when(assetRepository.findByCustomerIdAndAssetName("123", "AAPL"))
                .thenReturn(List.of(sampleAsset));

        Exception exception = assertThrows(Exception.class, () -> {
            orderService.createOrder(sampleSellOrder);
        });

        assertEquals("Not Enough Size", exception.getMessage());
    }

    @Test
    void testCancelOrderSuccessfully() throws Exception {
        when(orderRepository.findByIdAndStatus(1L, "PENDING")).thenReturn(sampleBuyOrder);

        orderService.cancelOrder(1L);

        assertEquals("CANCELED", sampleBuyOrder.getStatus());
        verify(orderRepository, times(1)).save(sampleBuyOrder);
    }

    @Test
    void testCancelOrderThatDoesNotExist() {
        when(orderRepository.findByIdAndStatus(1L, "PENDING")).thenReturn(null);

        assertDoesNotThrow(() -> orderService.cancelOrder(1L));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testMatcher() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleBuyOrder, sampleSellOrder));
        when(orderRepository.findByAssetNameAndPriceAndStatus("AAPL", 150.0, "PENDING"))
                .thenReturn(List.of(sampleSellOrder));

        orderService.matcher();

        assertEquals("MATCHED", sampleBuyOrder.getStatus());
        assertEquals("MATCHED", sampleSellOrder.getStatus());

        verify(orderRepository, times(2)).save(any(Order.class));
    }
}
