package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.CartItem;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Repositories.userRepositories;
import com.example.Swp_Project.Repositories.vaccineDetailsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class cartService {
    @Autowired
    private userRepositories userRepo;
    @Autowired
    private vaccineDetailsRepositories vaccineDetailsRepo;
    public CartItem addToCart(UUID userID, UUID vaccineDetailsId, int quantity) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));

        VaccineDetails vaccine = vaccineDetailsRepo.findById(vaccineDetailsId)
                .orElseThrow(() -> new RuntimeException("Vaccine not found, brooo!"));

        // Map VaccineDetails to CartItem
        CartItem cartItem = new CartItem(
                vaccine.getVaccineDetailsId(),
                vaccine.getDoseName(),
                vaccine.getPrice(),
                quantity
        );

        // Check if item already exists in cart
        List<CartItem> cart = user.getCart();
        for (CartItem item : cart) {
            if (item.getItemId().equals(cartItem.getItemId())) {
                item.setQuantity(item.getQuantity() + quantity);
                userRepo.save(user);
                return item;
            }
        }

        // If not found, add new item
        cart.add(cartItem);
        userRepo.save(user);
        return cartItem;
    }
    public List<CartItem> getCart(UUID userID) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));
        return user.getCart();
    }
    public CartItem updateCartItem(UUID userID, UUID itemId, int quantity) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));

        CartItem itemToUpdate = user.getCart().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart, brooo!"));

        itemToUpdate.setQuantity(quantity);
        if (quantity <= 0) {
            user.getCart().remove(itemToUpdate);
        }
        userRepo.save(user);
        return itemToUpdate;
    }
    public void removeFromCart(UUID userID, UUID itemId) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));

        user.getCart().removeIf(item -> item.getItemId().equals(itemId));
        userRepo.save(user);
    }
    public void clearCart(UUID userID) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));

        user.getCart().clear();
        userRepo.save(user);
    }
    public double getCartTotal(UUID userID) {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, brooo!"));

        return user.getCart().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
