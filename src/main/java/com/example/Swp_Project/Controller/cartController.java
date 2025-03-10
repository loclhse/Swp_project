package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.CartItem;
import com.example.Swp_Project.Service.cartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class cartController {
    @Autowired
    private cartService cartService;
    @PostMapping("/{userID}")
    public CartItem addToCart(
            @PathVariable UUID userID,
            @RequestParam("vaccineDetailsId") UUID vaccineDetailsId,
            @RequestParam("quantity") int quantity) {
        return cartService.addToCart(userID, vaccineDetailsId, quantity);
    }

    @GetMapping("/{userID}")
    public List<CartItem> getCart(@PathVariable UUID userID) {
        return cartService.getCart(userID);
    }

    @PutMapping("/{userID}/{itemId}")
    public CartItem updateCartItem(
            @PathVariable UUID userID,
            @PathVariable UUID itemId,
            @RequestParam("quantity") int quantity) {
        return cartService.updateCartItem(userID, itemId, quantity);
    }
    @DeleteMapping("/{userID}/{itemId}")
    public void removeFromCart(@PathVariable UUID userID, @PathVariable UUID itemId) {
        cartService.removeFromCart(userID, itemId);
    }

    @DeleteMapping("/{userID}")
    public void clearCart(@PathVariable UUID userID) {
        cartService.clearCart(userID);
    }

    @GetMapping("/{userID}/total")
    public double getCartTotal(@PathVariable UUID userID) {
        return cartService.getCartTotal(userID);
    }
}
