package com.GaoQue.service.cart;

import com.GaoQue.model.Cart;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

    Long initializeNewCart(Long userId);

    Cart getCartByUserId(Long userId);
}

