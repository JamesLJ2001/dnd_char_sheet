package com.jameslj.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_usages")
public class ResourceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer usedAmount;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private CharacterResource resource;

    public ResourceUsage() {}

    public ResourceUsage(Integer usedAmount, CharacterResource resource) {
        this.usedAmount = usedAmount;
        this.timestamp = LocalDateTime.now();
        this.resource = resource;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUsedAmount() { return usedAmount; }
    public void setUsedAmount(Integer usedAmount) { this.usedAmount = usedAmount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public CharacterResource getResource() { return resource; }
    public void setResource(CharacterResource resource) { this.resource = resource; }
}
