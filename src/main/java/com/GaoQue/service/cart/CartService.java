package com.GaoQue.service.cart;

import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.User;
import com.GaoQue.repository.CartItemRepository;
import com.GaoQue.repository.CartRepository;
import com.GaoQue.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.GaoQue.model.Cart;


import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;
    private final AtomicLong cartIdGenerator = new AtomicLong(0);

    @Override
    public Cart getCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng không tồn tại."));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

    @Override
    public Long initializeNewCart(Long userId) {
        Cart existingCart = cartRepository.findByUserId(userId);
        if (existingCart != null) {
            return existingCart.getId();
        }
        Cart newCart = new Cart();
        User user = userService.getUserById(userId);
        if (user != null) {
            newCart.setUser(user);
        }
        Long newCartId = cartIdGenerator.incrementAndGet();
        newCart.setId(newCartId);

        return cartRepository.save(newCart).getId();
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}

