package com.unifind.backend.controller;

import com.unifind.backend.model.Claim;
import com.unifind.backend.model.Item;
import com.unifind.backend.model.User;
import com.unifind.backend.repository.ClaimRepository;
import com.unifind.backend.repository.ItemRepository;
import com.unifind.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*")
public class ClaimController {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;

    // 1. Submit a Claim (Student ke liye)
    @PostMapping("/submit")
    public ResponseEntity<?> submitClaim(@RequestBody ClaimRequest request) {
        Optional<Item> itemOpt = itemRepository.findById(request.itemId);
        Optional<User> userOpt = userRepository.findById(request.claimantId);

        if (itemOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item or User not found");
        }

        if (itemOpt.get().getStatus() != Item.ItemStatus.OPEN) {
            return ResponseEntity.badRequest().body("This item is no longer available for claiming.");
        }

        Claim claim = new Claim();
        claim.setItem(itemOpt.get());
        claim.setClaimant(userOpt.get());
        claim.setProofDetails(request.proofDetails);
        
        // YEH LINE EMAIL SAVE KAREGI
        claim.setClaimantEmail(userOpt.get().getEmail()); 
        
        claim.setStatus(Claim.ClaimStatus.PENDING);
        
        claimRepository.save(claim);
        
        Item item = itemOpt.get();
        item.setStatus(Item.ItemStatus.CLAIMED);
        itemRepository.save(item);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Claim submitted successfully");
        return ResponseEntity.ok(response);
    }

    // 2. Get Pending Claims (Admin Dashboard ke liye)
    @GetMapping("/pending")
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus(Claim.ClaimStatus.PENDING);
    }

    // 3. Approve / Reject Claim (Admin ke liye)
    @PutMapping("/{claimId}/status")
    public ResponseEntity<?> updateClaimStatus(@PathVariable Long claimId, @RequestParam String status) {
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        if (claimOpt.isEmpty()) return ResponseEntity.badRequest().body("Claim not found");

        Claim claim = claimOpt.get();
        claim.setStatus(Claim.ClaimStatus.valueOf(status));
        claimRepository.save(claim);

        Item item = claim.getItem();
        if (status.equals("APPROVED")) {
            item.setStatus(Item.ItemStatus.RETURNED);
        } else if (status.equals("REJECTED")) {
            item.setStatus(Item.ItemStatus.OPEN);
        }
        itemRepository.save(item);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Claim " + status);
        return ResponseEntity.ok(response);
    }

    // Helper DTO (Data Transfer Object) request ke liye
    public static class ClaimRequest {
        public Long itemId;
        public Long claimantId;
        public String proofDetails;
    }
}