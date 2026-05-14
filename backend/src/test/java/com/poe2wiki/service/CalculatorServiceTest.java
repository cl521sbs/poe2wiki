package com.poe2wiki.service;

import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    @Test
    void calculate_shouldReturnPositiveDps() {
        DpsRequest req = buildSimpleRequest();
        DpsResponse result = calculatorService.calculate(req);
        assertThat(result.getDps()).isPositive();
        assertThat(result.getAvgDamage()).isPositive();
        assertThat(result.getBreakdown()).isNotNull();
    }

    @Test
    void calculate_shouldAccountForResistances() {
        DpsRequest lowRes = buildSimpleRequest();
        lowRes.getTarget().setLightningRes(0.0);

        DpsRequest highRes = buildSimpleRequest();
        highRes.getTarget().setLightningRes(75.0);

        DpsResponse r1 = calculatorService.calculate(lowRes);
        DpsResponse r2 = calculatorService.calculate(highRes);
        assertThat(r1.getEffectiveDps()).isGreaterThan(r2.getEffectiveDps());
    }

    @Test
    void compare_shouldReturnBothBuilds() {
        DpsRequest buildA = buildSimpleRequest();
        DpsRequest buildB = buildSimpleRequest();
        buildB.getEquipment().getWeapon().setPhysMax(200);

        var result = calculatorService.compare(buildA, buildB);
        assertThat(result).containsKeys("buildA", "buildB");
        assertThat(result.get("buildB").getDps()).isGreaterThan(result.get("buildA").getDps());
    }

    private DpsRequest buildSimpleRequest() {
        DpsRequest req = new DpsRequest();

        DpsRequest.SkillConfig skill = new DpsRequest.SkillConfig();
        skill.setSkillId(1L);
        skill.setLevel(20);
        skill.setSupportGems(List.of());
        req.setSkills(List.of(skill));

        DpsRequest.EquipmentConfig equip = new DpsRequest.EquipmentConfig();
        DpsRequest.WeaponConfig weapon = new DpsRequest.WeaponConfig();
        weapon.setPhysMin(50);
        weapon.setPhysMax(100);
        weapon.setAttackSpeed(1.5);
        weapon.setCritChance(6.0);
        weapon.setCritMultiplier(150.0);
        equip.setWeapon(weapon);
        req.setEquipment(equip);

        DpsRequest.PassiveConfig passives = new DpsRequest.PassiveConfig();
        passives.setDamageInc(100.0);
        passives.setAttackSpeedInc(10.0);
        passives.setCritChanceInc(50.0);
        passives.setCritMultiInc(30.0);
        passives.setPenetration(10.0);
        req.setPassives(passives);

        DpsRequest.TargetResistances target = new DpsRequest.TargetResistances();
        target.setFireRes(30.0);
        target.setColdRes(30.0);
        target.setLightningRes(30.0);
        target.setChaosRes(10.0);
        req.setTarget(target);

        req.setBuffs(List.of());
        return req;
    }
}
