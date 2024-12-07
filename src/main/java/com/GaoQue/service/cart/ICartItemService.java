package com.GaoQue.service.cart;

import com.GaoQue.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    CartItem getCartItem(Long cartId, Long productId);
}
