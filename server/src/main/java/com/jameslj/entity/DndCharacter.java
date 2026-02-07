package com.jameslj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dnd_characters")
public class DndCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String playerName;
    private String dndClass;
    private String race;
    private Integer level;
    private Integer maxHp;
    private Integer currentHp;
    private Integer armorClass;
    private String imageUrl;
    private String mainWeapon;
    private Integer initiative;
    private Integer speed;

    // 六维属性
    private Integer strength;
    private Integer dexterity;
    private Integer constitution;
    private Integer intelligence;
    private Integer wisdom;
    private Integer charisma;

    // 资源列表（一对多关系）
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CharacterResource> resources = new ArrayList<>();

    // Default constructor
    public DndCharacter() {}

    // Constructor with fields
    public DndCharacter(Long id, String name, String playerName, String dndClass, String race, Integer level,
                        Integer maxHp, Integer currentHp, Integer armorClass, String imageUrl, String mainWeapon,
                        Integer initiative, Integer speed, Integer strength, Integer dexterity,
                        Integer constitution, Integer intelligence, Integer wisdom, Integer charisma) {
        this.id = id;
        this.name = name;
        this.playerName = playerName;
        this.dndClass = dndClass;
        this.race = race;
        this.level = level;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.armorClass = armorClass;
        this.imageUrl = imageUrl;
        this.mainWeapon = mainWeapon;
        this.initiative = initiative;
        this.speed = speed;
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getDndClass() { return dndClass; }
    public void setDndClass(String dndClass) { this.dndClass = dndClass; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getMaxHp() { return maxHp; }
    public void setMaxHp(Integer maxHp) { this.maxHp = maxHp; }

    public Integer getCurrentHp() { return currentHp; }
    public void setCurrentHp(Integer currentHp) { this.currentHp = currentHp; }

    public Integer getArmorClass() { return armorClass; }
    public void setArmorClass(Integer armorClass) { this.armorClass = armorClass; }

    public Integer getStrength() { return strength; }
    public void setStrength(Integer strength) { this.strength = strength; }

    public Integer getDexterity() { return dexterity; }
    public void setDexterity(Integer dexterity) { this.dexterity = dexterity; }

    public Integer getConstitution() { return constitution; }
    public void setConstitution(Integer constitution) { this.constitution = constitution; }

    public Integer getIntelligence() { return intelligence; }
    public void setIntelligence(Integer intelligence) { this.intelligence = intelligence; }

    public Integer getWisdom() { return wisdom; }
    public void setWisdom(Integer wisdom) { this.wisdom = wisdom; }

    public Integer getCharisma() { return charisma; }
    public void setCharisma(Integer charisma) { this.charisma = charisma; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getMainWeapon() { return mainWeapon; }
    public void setMainWeapon(String mainWeapon) { this.mainWeapon = mainWeapon; }

    public Integer getInitiative() { return initiative; }
    public void setInitiative(Integer initiative) { this.initiative = initiative; }

    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }

    public List<CharacterResource> getResources() { return resources; }
    public void setResources(List<CharacterResource> resources) { this.resources = resources; }

    public void addResource(CharacterResource resource) {
        resources.add(resource);
        resource.setCharacter(this);
    }

    public void removeResource(CharacterResource resource) {
        resources.remove(resource);
        resource.setCharacter(null);
    }
}
