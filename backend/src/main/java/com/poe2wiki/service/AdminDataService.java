package com.poe2wiki.service;

import com.poe2wiki.entity.*;
import com.poe2wiki.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDataService {

    private final SkillMapper skillMapper;
    private final EquipmentMapper equipmentMapper;
    private final ModifierMapper modifierMapper;
    private final PassiveMapper passiveMapper;
    private final MonsterMapper monsterMapper;
    private final CurrencyMapper currencyMapper;

    // ==================== Skills ====================
    public Skill createSkill(Skill skill) { skillMapper.insert(skill); return skill; }
    public Skill updateSkill(Skill skill) { skillMapper.updateById(skill); return skill; }
    public void deleteSkill(Long id) { skillMapper.deleteById(id); }

    // ==================== Equipment ====================
    public Equipment createEquipment(Equipment eq) { equipmentMapper.insert(eq); return eq; }
    public Equipment updateEquipment(Equipment eq) { equipmentMapper.updateById(eq); return eq; }
    public void deleteEquipment(Long id) { equipmentMapper.deleteById(id); }

    // ==================== Modifiers ====================
    public Modifier createModifier(Modifier m) { modifierMapper.insert(m); return m; }
    public Modifier updateModifier(Modifier m) { modifierMapper.updateById(m); return m; }
    public void deleteModifier(Long id) { modifierMapper.deleteById(id); }

    // ==================== Passives ====================
    public Passive createPassive(Passive p) { passiveMapper.insert(p); return p; }
    public Passive updatePassive(Passive p) { passiveMapper.updateById(p); return p; }
    public void deletePassive(Long id) { passiveMapper.deleteById(id); }

    // ==================== Monsters ====================
    public Monster createMonster(Monster m) { monsterMapper.insert(m); return m; }
    public Monster updateMonster(Monster m) { monsterMapper.updateById(m); return m; }
    public void deleteMonster(Long id) { monsterMapper.deleteById(id); }

    // ==================== Currency ====================
    public CurrencyEntity createCurrency(CurrencyEntity c) { currencyMapper.insert(c); return c; }
    public CurrencyEntity updateCurrency(CurrencyEntity c) { currencyMapper.updateById(c); return c; }
    public void deleteCurrency(Long id) { currencyMapper.deleteById(id); }
}
