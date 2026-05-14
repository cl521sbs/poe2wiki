package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.*;
import com.poe2wiki.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-data")
@RequiredArgsConstructor
public class GameDataController {

    private final SkillService skillService;
    private final EquipmentService equipmentService;
    private final ModifierService modifierService;
    private final PassiveService passiveService;
    private final MonsterService monsterService;
    private final CurrencyService currencyService;

    // ==================== Skills ====================

    @GetMapping("/skills")
    public ApiResult<PageResult<Skill>> listSkills(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String damageType,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(skillService.list(page, size, type, damageType, keyword));
    }

    @GetMapping("/skills/{id}")
    public ApiResult<Skill> getSkill(@PathVariable Long id) {
        return ApiResult.success(skillService.getById(id));
    }

    // ==================== Equipment ====================

    @GetMapping("/equipment")
    public ApiResult<PageResult<Equipment>> listEquipment(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(equipmentService.list(page, size, category, rarity, keyword));
    }

    @GetMapping("/equipment/{id}")
    public ApiResult<Equipment> getEquipment(@PathVariable Long id) {
        return ApiResult.success(equipmentService.getById(id));
    }

    // ==================== Modifiers ====================

    @GetMapping("/modifiers")
    public ApiResult<PageResult<Modifier>> listModifiers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(modifierService.list(page, size, type, keyword));
    }

    @GetMapping("/modifiers/{id}")
    public ApiResult<Modifier> getModifier(@PathVariable Long id) {
        return ApiResult.success(modifierService.getById(id));
    }

    // ==================== Passives ====================

    @GetMapping("/passives")
    public ApiResult<PageResult<Passive>> listPassives(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String classRestriction,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(passiveService.list(page, size, type, classRestriction, keyword));
    }

    @GetMapping("/passives/{id}")
    public ApiResult<Passive> getPassive(@PathVariable Long id) {
        return ApiResult.success(passiveService.getById(id));
    }

    // ==================== Monsters ====================

    @GetMapping("/monsters")
    public ApiResult<PageResult<Monster>> listMonsters(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(monsterService.list(page, size, type, keyword));
    }

    @GetMapping("/monsters/{id}")
    public ApiResult<Monster> getMonster(@PathVariable Long id) {
        return ApiResult.success(monsterService.getById(id));
    }

    // ==================== Currency ====================

    @GetMapping("/currency")
    public ApiResult<PageResult<CurrencyEntity>> listCurrency(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(currencyService.list(page, size, type, keyword));
    }

    @GetMapping("/currency/{id}")
    public ApiResult<CurrencyEntity> getCurrency(@PathVariable Long id) {
        return ApiResult.success(currencyService.getById(id));
    }
}
