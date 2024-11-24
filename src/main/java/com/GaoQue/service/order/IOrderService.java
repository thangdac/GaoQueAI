package com.GaoQue.service.order;

import com.GaoQue.dto.OrderDto;
import com.GaoQue.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);
}
