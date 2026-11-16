package com.unifind.backend.controller;

import com.unifind.backend.model.Item;
import com.unifind.backend.model.User;
import com.unifind.backend.repository.ItemRepository;
import com.unifind.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;

    // 1. Get All Items
    @GetMapping
    public List<Item> getAllItems(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        }
        return itemRepository.findAll();
    }

    // 2. Report a new Item
    @PostMapping("/report/{userId}")
    public ResponseEntity<?> reportItem(@PathVariable Long userId, @RequestBody Item item) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        item.setReportedBy(userOpt.get());
        item.setStatus(Item.ItemStatus.OPEN);
        
        // YEH LINE OWNER KI EMAIL SAVE KAREGI (Naya add kiya hai)
        item.setOwnerEmail(userOpt.get().getEmail()); 
        
        Item savedItem = itemRepository.save(item);
        
        return ResponseEntity.ok(savedItem);
    }

    // 3. Get My Reported Items
    @GetMapping("/my-items/{userId}")
    public List<Item> getMyItems(@PathVariable Long userId) {
        return itemRepository.findByReportedById(userId);
    }
}