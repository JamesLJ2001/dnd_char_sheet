package com.jameslj.repository;

import com.jameslj.entity.CharacterResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterResourceRepository extends JpaRepository<CharacterResource, Long> {

    List<CharacterResource> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
