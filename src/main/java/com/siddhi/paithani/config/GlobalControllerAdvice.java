package com.siddhi.paithani.config;

import com.siddhi.paithani.service.CartService;
import com.siddhi.paithani.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final WishlistService wishlistService;

    @Autowired
    public GlobalControllerAdvice(CartService cartService, WishlistService wishlistService) {
        this.cartService = cartService;
        this.wishlistService = wishlistService;
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount() {
        return cartService != null ? cartService.getItemCount() : 0;
    }

    @ModelAttribute("wishlistCount")
    public int getWishlistCount() {
        return wishlistService != null ? wishlistService.getWishlistCount() : 0;
    }

    @ModelAttribute("wishlistService")
    public WishlistService getWishlistService() {
        return wishlistService;
    }
}
