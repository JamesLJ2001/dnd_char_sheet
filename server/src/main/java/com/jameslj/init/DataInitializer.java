package com.jameslj.init;

import com.jameslj.entity.CharacterResource;
import com.jameslj.entity.DndCharacter;
import com.jameslj.repository.CharacterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CharacterRepository characterRepository;

    public DataInitializer(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 清除旧数据
        characterRepository.deleteAll();

        // ==================== 角色 1: 瓦莱里安 (Paladin) ====================
        DndCharacter paladin = new DndCharacter();
        paladin.setId(1L);
        paladin.setName("瓦莱里安");
        paladin.setPlayerName("你");
        paladin.setDndClass("Paladin");
        paladin.setRace("Human");
        paladin.setLevel(3);
        paladin.setMaxHp(33);
        paladin.setCurrentHp(33);
        paladin.setArmorClass(19);
        paladin.setImageUrl("/images/paladin.jpg");
        paladin.setMainWeapon("+1 锋锐寒铁长剑 (+8 to hit, 1d8+4 damage)");
        paladin.setInitiative(4);
        paladin.setSpeed(20);
        paladin.setStrength(16);
        paladin.setDexterity(10);
        paladin.setConstitution(12);
        paladin.setIntelligence(10);
        paladin.setWisdom(13);
        paladin.setCharisma(15);

        // Paladin 资源
        CharacterResource smiteEvil = new CharacterResource("Smite Evil (破邪斩)", 1, 1, "LONG_REST");
        CharacterResource layOnHands = new CharacterResource("Lay on Hands (圣疗点数)", 6, 6, "LONG_REST");
        CharacterResource turnUndead = new CharacterResource("Turn Undead (驱散不死生物)", 0, 0, "LONG_REST");

        paladin.addResource(smiteEvil);
        paladin.addResource(layOnHands);
        paladin.addResource(turnUndead);

        // ==================== 角色 2: 维涅斯贝雅德 (Duskblade) ====================
        DndCharacter duskblade = new DndCharacter();
        duskblade.setId(2L);
        duskblade.setName("维涅斯贝雅德");
        duskblade.setPlayerName("朋友A");
        duskblade.setDndClass("Duskblade");
        duskblade.setRace("Half-Elf");
        duskblade.setLevel(3);
        duskblade.setMaxHp(33);
        duskblade.setCurrentHp(33);
        duskblade.setArmorClass(15);
        duskblade.setImageUrl("/images/duskblade.jpg");
        duskblade.setMainWeapon("+1 锋锐寒铁长剑 (+8 命中, 1d8+4 伤害)");
        duskblade.setInitiative(2);
        duskblade.setSpeed(30);
        duskblade.setStrength(17);
        duskblade.setDexterity(15);
        duskblade.setConstitution(16);
        duskblade.setIntelligence(16);
        duskblade.setWisdom(15);
        duskblade.setCharisma(11);

        // Duskblade 资源
        CharacterResource arcaneAttunement = new CharacterResource("奥能同调 (Arcane Attunement)", 6, 6, "LONG_REST");
        CharacterResource spellsLv0 = new CharacterResource("0环法术位 (Spells Lv0)", 5, 5, "LONG_REST");
        CharacterResource spellsLv1 = new CharacterResource("1环法术位 (Spells Lv1)", 5, 5, "LONG_REST");

        duskblade.addResource(arcaneAttunement);
        duskblade.addResource(spellsLv0);
        duskblade.addResource(spellsLv1);

        // ==================== 角色 3: ame^^ (Warlock) ====================
        DndCharacter warlock = new DndCharacter();
        warlock.setId(3L);
        warlock.setName("ame^^");
        warlock.setPlayerName("朋友B");
        warlock.setDndClass("Warlock");
        warlock.setRace("Half-Orc");
        warlock.setLevel(3);
        warlock.setMaxHp(27);
        warlock.setCurrentHp(27);
        warlock.setArmorClass(14);
        warlock.setImageUrl("/images/warlock.jpg");
        warlock.setMainWeapon("魔焰 (Eldritch Blast) (远程接触 +4, 2d6 伤害)");
        warlock.setInitiative(6);
        warlock.setSpeed(30);
        warlock.setStrength(14);
        warlock.setDexterity(14);
        warlock.setConstitution(14);
        warlock.setIntelligence(10);
        warlock.setWisdom(10);
        warlock.setCharisma(16);

        // Warlock 资源
        CharacterResource eldritchBlast = new CharacterResource("魔焰 (Eldritch Blast)", 999, 999, "NONE");
        CharacterResource dr = new CharacterResource("伤害减免 (DR)", 1, 1, "NONE");

        warlock.addResource(eldritchBlast);
        warlock.addResource(dr);

        // ==================== 角色 4: 葉月音音 (Bard) ====================
        DndCharacter bard = new DndCharacter();
        bard.setId(4L);
        bard.setName("葉月音音");
        bard.setPlayerName("朋友C");
        bard.setDndClass("Bard");
        bard.setRace("Human");
        bard.setLevel(3);
        bard.setMaxHp(18);
        bard.setCurrentHp(18);
        bard.setArmorClass(15);
        bard.setImageUrl("/images/bard.jpg");
        bard.setMainWeapon("武士刀 (天照十字切) (+7 命中, 2d3+1d4+3 伤害)");
        bard.setInitiative(7);
        bard.setSpeed(30);
        bard.setStrength(9);
        bard.setDexterity(16);
        bard.setConstitution(11);
        bard.setIntelligence(16);
        bard.setWisdom(13);
        bard.setCharisma(17);

        // Bard 资源
        CharacterResource bardicMusic = new CharacterResource("吟唱次数 (Bardic Music)", 3, 3, "LONG_REST");
        CharacterResource bardSpellsLv0 = new CharacterResource("0环法术位 (Spells Lv0)", 3, 3, "LONG_REST");
        CharacterResource bardSpellsLv1 = new CharacterResource("1环法术位 (Spells Lv1)", 2, 2, "LONG_REST");
        CharacterResource bardSpellsLv2 = new CharacterResource("2环法术位 (Spells Lv2)", 1, 1, "LONG_REST");

        bard.addResource(bardicMusic);
        bard.addResource(bardSpellsLv0);
        bard.addResource(bardSpellsLv1);
        bard.addResource(bardSpellsLv2);

        // 保存所有角色（级联保存资源）
        characterRepository.save(paladin);
        characterRepository.save(duskblade);
        characterRepository.save(warlock);
        characterRepository.save(bard);

        System.out.println("✅ 数据初始化完成：已插入 4 个真实角色及资源");
        System.out.println("   - 瓦莱里安 (Paladin) - 3 资源");
        System.out.println("   - 维涅斯贝雅德 (Duskblade) - 3 资源");
        System.out.println("   - ame^^ (Warlock) - 2 资源");
        System.out.println("   - 葉月音音 (Bard) - 4 资源");
    }
}
