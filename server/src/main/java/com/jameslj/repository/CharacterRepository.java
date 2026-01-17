package com.jameslj.repository;

import com.jameslj.entity.DndCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<DndCharacter, Long> {
}
