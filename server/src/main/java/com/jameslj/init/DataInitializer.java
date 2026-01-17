package com.jameslj.init;

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
        // 检查数据库是否为空
        if (characterRepository.count() == 0) {
            // 1. 战士 (高力量、高体质，高血量、高护甲)
            DndCharacter warrior = new DndCharacter();
            warrior.setId(1L);
            warrior.setName("雷加");
            warrior.setPlayerName("你");
            warrior.setDndClass("战士");
            warrior.setLevel(3);
            warrior.setMaxHp(45);
            warrior.setCurrentHp(45);
            warrior.setArmorClass(16);
            warrior.setStrength(16);
            warrior.setDexterity(12);
            warrior.setConstitution(16);
            warrior.setIntelligence(10);
            warrior.setWisdom(12);
            warrior.setCharisma(10);

            // 2. 法师 (高智力，低血量，低护甲)
            DndCharacter mage = new DndCharacter();
            mage.setId(2L);
            mage.setName("艾拉");
            mage.setPlayerName("朋友A");
            mage.setDndClass("法师");
            mage.setLevel(3);
            mage.setMaxHp(20);
            mage.setCurrentHp(20);
            mage.setArmorClass(12);
            mage.setStrength(8);
            mage.setDexterity(14);
            mage.setConstitution(12);
            mage.setIntelligence(18);
            mage.setWisdom(14);
            mage.setCharisma(10);

            // 3. 牧师 (高感知，平衡属性)
            DndCharacter cleric = new DndCharacter();
            cleric.setId(3L);
            cleric.setName("瑟拉菲娜");
            cleric.setPlayerName("朋友B");
            cleric.setDndClass("牧师");
            cleric.setLevel(3);
            cleric.setMaxHp(28);
            cleric.setCurrentHp(28);
            cleric.setArmorClass(15);
            cleric.setStrength(12);
            cleric.setDexterity(10);
            cleric.setConstitution(14);
            cleric.setIntelligence(12);
            cleric.setWisdom(17);
            cleric.setCharisma(12);

            // 4. 盗贼 (高敏捷，高爆发)
            DndCharacter rogue = new DndCharacter();
            rogue.setId(4L);
            rogue.setName("影刃");
            rogue.setPlayerName("朋友C");
            rogue.setDndClass("盗贼");
            rogue.setLevel(3);
            rogue.setMaxHp(24);
            rogue.setCurrentHp(24);
            rogue.setArmorClass(14);
            rogue.setStrength(10);
            rogue.setDexterity(18);
            rogue.setConstitution(12);
            rogue.setIntelligence(12);
            rogue.setWisdom(12);
            rogue.setCharisma(14);

            characterRepository.save(warrior);
            characterRepository.save(mage);
            characterRepository.save(cleric);
            characterRepository.save(rogue);

            System.out.println("数据初始化完成：已插入 4 个预设角色");
        } else {
            System.out.println("数据库已有数据，跳过初始化");
        }
    }
}
