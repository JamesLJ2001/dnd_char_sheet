package com.jameslj;

import com.jameslj.entity.CharacterResource;
import com.jameslj.entity.DndCharacter;
import com.jameslj.repository.CharacterRepository;
import com.jameslj.repository.CharacterResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DndCharacterTest {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private CharacterResourceRepository resourceRepository;

    private DndCharacter paladin;

    @BeforeEach
    void setUp() {
        // 清理数据库
        characterRepository.deleteAll();
        resourceRepository.deleteAll();

        // 创建测试角色
        paladin = new DndCharacter();
        paladin.setName("瓦莱里安");
        paladin.setPlayerName("测试玩家");
        paladin.setDndClass("Paladin");
        paladin.setRace("Human");
        paladin.setLevel(3);
        paladin.setMaxHp(33);
        paladin.setCurrentHp(33);
        paladin.setArmorClass(19);
        paladin.setImageUrl("/images/paladin.jpg");
        paladin.setMainWeapon("+1 锋锐寒铁长剑");
        paladin.setInitiative(4);
        paladin.setSpeed(20);
        paladin.setStrength(16);
        paladin.setDexterity(10);
        paladin.setConstitution(12);
        paladin.setIntelligence(10);
        paladin.setWisdom(13);
        paladin.setCharisma(15);
    }

    @Test
    @Rollback(false)
    void testCreateCharacterWithResources() {
        // 创建资源
        CharacterResource smite = new CharacterResource("Smite Evil", 1, 1, "LONG_REST");
        CharacterResource layOnHands = new CharacterResource("Lay on Hands", 6, 6, "LONG_REST");

        // 添加资源到角色
        paladin.addResource(smite);
        paladin.addResource(layOnHands);

        // 保存角色（级联保存资源）
        DndCharacter saved = characterRepository.save(paladin);

        // 验证角色已保存
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("瓦莱里安");

        // 验证资源已级联保存
        assertThat(saved.getResources()).hasSize(2);

        List<CharacterResource> resources = resourceRepository.findByCharacterId(saved.getId());
        assertThat(resources).hasSize(2);
        assertThat(resources.get(0).getResourceName()).isEqualTo("Smite Evil");
        assertThat(resources.get(1).getResourceName()).isEqualTo("Lay on Hands");
    }

    @Test
    @Rollback(false)
    void testCascadeDelete() {
        // 创建带资源的角色
        CharacterResource smite = new CharacterResource("Smite Evil", 1, 1, "LONG_REST");
        paladin.addResource(smite);

        DndCharacter saved = characterRepository.save(paladin);
        Long characterId = saved.getId();

        // 验证资源存在
        List<CharacterResource> resourcesBefore = resourceRepository.findByCharacterId(characterId);
        assertThat(resourcesBefore).hasSize(1);

        // 删除角色
        characterRepository.deleteById(characterId);

        // 验证资源也被删除
        List<CharacterResource> resourcesAfter = resourceRepository.findByCharacterId(characterId);
        assertThat(resourcesAfter).isEmpty();
    }

    @Test
    @Rollback(false)
    void testResourceRelationship() {
        // 创建并保存角色
        DndCharacter saved = characterRepository.save(paladin);

        // 创建资源并设置关系
        CharacterResource resource = new CharacterResource("Test Resource", 5, 5, "LONG_REST");
        resource.setCharacter(saved);

        // 保存资源
        resourceRepository.save(resource);

        // 验证双向关系
        CharacterResource fetched = resourceRepository.findById(resource.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getCharacter().getId()).isEqualTo(saved.getId());
        assertThat(fetched.getCharacter().getName()).isEqualTo("瓦莱里安");
    }

    @Test
    @Rollback(false)
    void testMultipleCharactersWithResources() {
        // 创建第一个角色
        DndCharacter paladin = characterRepository.save(this.paladin);
        CharacterResource smite = new CharacterResource("Smite Evil", 1, 1, "LONG_REST");
        smite.setCharacter(paladin);
        resourceRepository.save(smite);

        // 创建第二个角色
        DndCharacter bard = new DndCharacter();
        bard.setName("葉月音音");
        bard.setPlayerName("测试玩家2");
        bard.setDndClass("Bard");
        bard.setRace("Human");
        bard.setLevel(3);
        bard.setMaxHp(18);
        bard.setCurrentHp(18);
        bard.setArmorClass(15);
        bard.setImageUrl("/images/bard.jpg");
        bard.setMainWeapon("武士刀");
        bard.setInitiative(7);
        bard.setSpeed(30);
        bard.setStrength(9);
        bard.setDexterity(16);
        bard.setConstitution(11);
        bard.setIntelligence(16);
        bard.setWisdom(13);
        bard.setCharisma(17);

        DndCharacter savedBard = characterRepository.save(bard);
        CharacterResource bardicMusic = new CharacterResource("Bardic Music", 3, 3, "LONG_REST");
        bardicMusic.setCharacter(savedBard);
        resourceRepository.save(bardicMusic);

        // 验证两个角色的资源独立
        List<CharacterResource> paladinResources = resourceRepository.findByCharacterId(paladin.getId());
        List<CharacterResource> bardResources = resourceRepository.findByCharacterId(savedBard.getId());

        assertThat(paladinResources).hasSize(1);
        assertThat(bardResources).hasSize(1);
        assertThat(paladinResources.get(0).getResourceName()).isEqualTo("Smite Evil");
        assertThat(bardResources.get(0).getResourceName()).isEqualTo("Bardic Music");
    }
}
