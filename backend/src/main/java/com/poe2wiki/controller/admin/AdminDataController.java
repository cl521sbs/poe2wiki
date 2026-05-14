package com.poe2wiki.controller.admin;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.entity.*;
import com.poe2wiki.service.AdminDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
public class AdminDataController {

    private final AdminDataService adminDataService;

    // ==================== Skills ====================
    @PostMapping("/skills")
    public ApiResult<Skill> createSkill(@Valid @RequestBody Skill skill) {
        return ApiResult.success(adminDataService.createSkill(skill));
    }

    @PutMapping("/skills/{id}")
    public ApiResult<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        return ApiResult.success(adminDataService.updateSkill(skill));
    }

    @DeleteMapping("/skills/{id}")
    public ApiResult<Void> deleteSkill(@PathVariable Long id) {
        adminDataService.deleteSkill(id);
        return ApiResult.success();
    }

    // ==================== Equipment ====================
    @PostMapping("/equipment")
    public ApiResult<Equipment> createEquipment(@RequestBody Equipment eq) {
        return ApiResult.success(adminDataService.createEquipment(eq));
    }

    @PutMapping("/equipment/{id}")
    public ApiResult<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment eq) {
        eq.setId(id);
        return ApiResult.success(adminDataService.updateEquipment(eq));
    }

    @DeleteMapping("/equipment/{id}")
    public ApiResult<Void> deleteEquipment(@PathVariable Long id) {
        adminDataService.deleteEquipment(id);
        return ApiResult.success();
    }

    // ==================== Modifiers ====================
    @PostMapping("/modifiers")
    public ApiResult<Modifier> createModifier(@RequestBody Modifier m) {
        return ApiResult.success(adminDataService.createModifier(m));
    }

    @PutMapping("/modifiers/{id}")
    public ApiResult<Modifier> updateModifier(@PathVariable Long id, @RequestBody Modifier m) {
        m.setId(id);
        return ApiResult.success(adminDataService.updateModifier(m));
    }

    @DeleteMapping("/modifiers/{id}")
    public ApiResult<Void> deleteModifier(@PathVariable Long id) {
        adminDataService.deleteModifier(id);
        return ApiResult.success();
    }

    // ==================== Passives ====================
    @PostMapping("/passives")
    public ApiResult<Passive> createPassive(@RequestBody Passive p) {
        return ApiResult.success(adminDataService.createPassive(p));
    }

    @PutMapping("/passives/{id}")
    public ApiResult<Passive> updatePassive(@PathVariable Long id, @RequestBody Passive p) {
        p.setId(id);
        return ApiResult.success(adminDataService.updatePassive(p));
    }

    @DeleteMapping("/passives/{id}")
    public ApiResult<Void> deletePassive(@PathVariable Long id) {
        adminDataService.deletePassive(id);
        return ApiResult.success();
    }

    // ==================== Monsters ====================
    @PostMapping("/monsters")
    public ApiResult<Monster> createMonster(@RequestBody Monster m) {
        return ApiResult.success(adminDataService.createMonster(m));
    }

    @PutMapping("/monsters/{id}")
    public ApiResult<Monster> updateMonster(@PathVariable Long id, @RequestBody Monster m) {
        m.setId(id);
        return ApiResult.success(adminDataService.updateMonster(m));
    }

    @DeleteMapping("/monsters/{id}")
    public ApiResult<Void> deleteMonster(@PathVariable Long id) {
        adminDataService.deleteMonster(id);
        return ApiResult.success();
    }

    // ==================== Currency ====================
    @PostMapping("/currency")
    public ApiResult<CurrencyEntity> createCurrency(@RequestBody CurrencyEntity c) {
        return ApiResult.success(adminDataService.createCurrency(c));
    }

    @PutMapping("/currency/{id}")
    public ApiResult<CurrencyEntity> updateCurrency(@PathVariable Long id, @RequestBody CurrencyEntity c) {
        c.setId(id);
        return ApiResult.success(adminDataService.updateCurrency(c));
    }

    @DeleteMapping("/currency/{id}")
    public ApiResult<Void> deleteCurrency(@PathVariable Long id) {
        adminDataService.deleteCurrency(id);
        return ApiResult.success();
    }
}
