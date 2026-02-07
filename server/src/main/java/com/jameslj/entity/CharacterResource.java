package com.jameslj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "character_resources")
public class CharacterResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;
    private Integer currentValue;
    private Integer maxValue;
    private String recoverType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    @JsonBackReference
    private DndCharacter character;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceUsage> usages = new ArrayList<>();

    public CharacterResource() {}

    public CharacterResource(String resourceName, Integer currentValue, Integer maxValue, String recoverType) {
        this.resourceName = resourceName;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
        this.recoverType = recoverType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public Integer getCurrentValue() { return currentValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }

    public Integer getMaxValue() { return maxValue; }
    public void setMaxValue(Integer maxValue) { this.maxValue = maxValue; }

    public String getRecoverType() { return recoverType; }
    public void setRecoverType(String recoverType) { this.recoverType = recoverType; }

    public DndCharacter getCharacter() { return character; }
    public void setCharacter(DndCharacter character) { this.character = character; }

    public List<ResourceUsage> getUsages() { return usages; }
    public void setUsages(List<ResourceUsage> usages) { this.usages = usages; }
}
