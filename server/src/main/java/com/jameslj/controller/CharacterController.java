package com.jameslj.controller;

import com.jameslj.entity.CharacterResource;
import com.jameslj.entity.DndCharacter;
import com.jameslj.repository.CharacterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterRepository characterRepository;

    public CharacterController(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    // GET /api/characters - 返回所有角色列表
    @GetMapping
    public List<DndCharacter> getAllCharacters() {
        return characterRepository.findAll();
    }

    // GET /api/characters/{id} - 返回单个角色详情
    @GetMapping("/{id}")
    public ResponseEntity<DndCharacter> getCharacterById(@PathVariable Long id) {
        return characterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/characters/{id} - 更新角色（部分更新）
    @PutMapping("/{id}")
    public ResponseEntity<DndCharacter> updateCharacter(@PathVariable Long id, @RequestBody DndCharacter character) {
        return characterRepository.findById(id)
                .map(existing -> {
                    // 只更新非 null 的字段
                    if (character.getName() != null) existing.setName(character.getName());
                    if (character.getPlayerName() != null) existing.setPlayerName(character.getPlayerName());
                    if (character.getDndClass() != null) existing.setDndClass(character.getDndClass());
                    if (character.getLevel() != null) existing.setLevel(character.getLevel());
                    if (character.getMaxHp() != null) existing.setMaxHp(character.getMaxHp());
                    if (character.getCurrentHp() != null) existing.setCurrentHp(character.getCurrentHp());
                    if (character.getArmorClass() != null) existing.setArmorClass(character.getArmorClass());
                    if (character.getStrength() != null) existing.setStrength(character.getStrength());
                    if (character.getDexterity() != null) existing.setDexterity(character.getDexterity());
                    if (character.getConstitution() != null) existing.setConstitution(character.getConstitution());
                    if (character.getIntelligence() != null) existing.setIntelligence(character.getIntelligence());
                    if (character.getWisdom() != null) existing.setWisdom(character.getWisdom());
                    if (character.getCharisma() != null) existing.setCharisma(character.getCharisma());
                    if (character.getRace() != null) existing.setRace(character.getRace());
                    if (character.getImageUrl() != null) existing.setImageUrl(character.getImageUrl());
                    if (character.getMainWeapon() != null) existing.setMainWeapon(character.getMainWeapon());
                    if (character.getInitiative() != null) existing.setInitiative(character.getInitiative());
                    if (character.getSpeed() != null) existing.setSpeed(character.getSpeed());

                    DndCharacter updated = characterRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/characters/{id}/hp - 调整 HP
    @PatchMapping("/{id}/hp")
    public ResponseEntity<DndCharacter> adjustHp(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer delta = request.get("delta");
        if (delta == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<DndCharacter> optCharacter = characterRepository.findById(id);
        if (optCharacter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DndCharacter character = optCharacter.get();
        int newHp = character.getCurrentHp() + delta;
        newHp = Math.max(0, Math.min(newHp, character.getMaxHp()));
        character.setCurrentHp(newHp);
        DndCharacter updated = characterRepository.save(character);
        return ResponseEntity.ok(updated);
    }

    // PATCH /api/characters/{id}/resources/{resourceId} - 调整资源值
    @PatchMapping("/{id}/resources/{resourceId}")
    public ResponseEntity<DndCharacter> adjustResource(
            @PathVariable Long id,
            @PathVariable Long resourceId,
            @RequestBody Map<String, Integer> request) {

        Integer delta = request.get("delta");
        if (delta == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<DndCharacter> optCharacter = characterRepository.findById(id);
        if (optCharacter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DndCharacter character = optCharacter.get();
        CharacterResource resource = character.getResources().stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElse(null);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        int newValue = resource.getCurrentValue() + delta;
        newValue = Math.max(0, Math.min(newValue, resource.getMaxValue()));
        resource.setCurrentValue(newValue);

        DndCharacter updated = characterRepository.save(character);
        return ResponseEntity.ok(updated);
    }

    // POST /api/characters/{id}/long-rest - 执行长休
    @PostMapping("/{id}/long-rest")
    public ResponseEntity<DndCharacter> longRest(@PathVariable Long id) {
        return characterRepository.findById(id)
                .map(character -> {
                    // 恢复 HP 到最大值
                    character.setCurrentHp(character.getMaxHp());

                    // 恢复所有资源到最大值
                    character.getResources().forEach(resource -> {
                        if ("LONG_REST".equals(resource.getRecoverType())) {
                            resource.setCurrentValue(resource.getMaxValue());
                        }
                    });

                    DndCharacter updated = characterRepository.save(character);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
