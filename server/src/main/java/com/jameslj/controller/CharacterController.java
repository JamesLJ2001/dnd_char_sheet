package com.jameslj.controller;

import com.jameslj.entity.DndCharacter;
import com.jameslj.repository.CharacterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

                    DndCharacter updated = characterRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
