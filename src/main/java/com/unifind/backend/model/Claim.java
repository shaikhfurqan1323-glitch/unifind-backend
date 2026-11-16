package com.unifind.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @Column(length = 1000)
    private String proofDetails;

    // Naya field email ke liye
    private String claimantEmail; 

    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ClaimStatus { PENDING, APPROVED, REJECTED }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
    public String getProofDetails() { return proofDetails; }
    public void setProofDetails(String proofDetails) { this.proofDetails = proofDetails; }
    
    public String getClaimantEmail() { return claimantEmail; }
    public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
}